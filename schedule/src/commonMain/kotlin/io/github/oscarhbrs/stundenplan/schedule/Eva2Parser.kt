package io.github.oscarhbrs.stundenplan.schedule

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/** The selection form on https://eva2.inf.h-brs.de/stundenplan/. */
data class Eva2Index(
    val termId: String,
    val weeks: List<Int>,
    val semesters: List<Eva2Semester>,
    val abbreviations: Map<String, String>
)

/** An entry of "Studiengang / Semester", e.g. id "#SPLUS5A456B", label "BI 1". */
data class Eva2Semester(val id: String, val label: String)

data class Eva2Row(
    val day: String,
    val start: String,
    val end: String,
    val room: String,
    val event: String,
    val period: String,
    val lecturer: String
)

data class Eva2Period(val start: LocalDate, val end: LocalDate, val parity: WeekParity, val firstWeek: String, val lastWeek: String)

object Eva2Parser {

    private val WEEKDAYS = mapOf(
        "Mo" to Weekday.MONTAG,
        "Di" to Weekday.DIENSTAG,
        "Mi" to Weekday.MITTWOCH,
        "Do" to Weekday.DONNERSTAG,
        "Fr" to Weekday.FREITAG
    )
    private val TYPE = Regex("""\s*\(([^()]+)\)$""")
    private val GROUP = Regex("""\s+Gr\.\s+(\S+)$""")
    private val PRACTICAL_PART = Regex("""^(.+) (P\d+)$""")
    private val PERIOD = Regex("""(\d{2})\.(\d{2})\.(\d{4})-(\d{2})\.(\d{2})\.(\d{4}) \(([gu]?)KW (\d+)-(\d+)\)""")
    private val INITIAL_AFTER_COMMA = Regex("""(\p{L}[\p{L}-]*), (\p{Lu}\.)""")

    fun parseIndex(html: String): Eva2Index {
        val doc = Ksoup.parse(html)
        val weekOptions = doc.select("select[name=weeks] option")
        val wholeTerm = weekOptions.firstOrNull { it.hasAttr("selected") } ?: weekOptions.firstOrNull()
        return Eva2Index(
            termId = doc.selectFirst("input[name=term]")?.attr("value").orEmpty(),
            weeks = wholeTerm?.attr("value")?.split(';')?.mapNotNull { it.trim().toIntOrNull() }.orEmpty(),
            semesters = doc.select("select[name=identifier_semester] option")
                .filter { it.attr("value").isNotBlank() }
                .map { Eva2Semester(it.attr("value"), it.text().trim()) },
            abbreviations = doc.select("span.configlegende li")
                .map { it.text().split(" = ", limit = 2) }
                .filter { it.size == 2 }
                .associate { (abbreviation, name) -> abbreviation.trim() to name.trim() }
        )
    }

    fun parseRows(html: String): List<Eva2Row> {
        var day = ""
        return Ksoup.parse(html).select("table tr").mapNotNull { row ->
            row.selectFirst("td.liste-wochentag")?.let { day = it.text().trim() }
            val start = row.cell("liste-startzeit") ?: return@mapNotNull null
            Eva2Row(
                day = day,
                start = start,
                end = row.cell("liste-endzeit").orEmpty(),
                room = row.cell("liste-raum").orEmpty(),
                event = row.cell("liste-veranstaltung").orEmpty(),
                period = row.cell("liste-beginn").orEmpty(),
                lecturer = row.cell("liste-wer").orEmpty()
            )
        }
    }

    private fun Element.cell(cssClass: String): String? = selectFirst("td.$cssClass")?.text()?.trim()

    fun parsePeriod(text: String): Eva2Period? {
        val m = PERIOD.find(text)?.groupValues ?: return null
        return Eva2Period(
            start = LocalDate(m[3].toInt(), m[2].toInt(), m[1].toInt()),
            end = LocalDate(m[6].toInt(), m[5].toInt(), m[4].toInt()),
            parity = when (m[7]) {
                "g" -> WeekParity.EVEN
                "u" -> WeekParity.ODD
                else -> WeekParity.ALL
            },
            firstWeek = m[8],
            lastWeek = m[9]
        )
    }

    /**
     * eva2 numbers the weeks of a term on past New Year (…, 53, 54 = KW 1, …); weeks missing from
     * [weeks] are breaks. [year] is the year the term starts in.
     */
    fun term(weeks: List<Int>, year: Int): Term {
        require(weeks.isNotEmpty()) { "No weeks found" }
        val jan4 = LocalDate(year, 1, 4)
        val firstMonday = jan4.monday().plus(weeks.first() - 1, DateTimeUnit.WEEK)
        fun monday(week: Int) = firstMonday.plus(week - weeks.first(), DateTimeUnit.WEEK)
        val breaks = weeks.zipWithNext()
            .filter { (a, b) -> b > a + 1 }
            .map { (a, b) -> DateRange(monday(a + 1), monday(b).minus(1, DateTimeUnit.DAY)) }
        return Term(start = monday(weeks.first()), end = monday(weeks.last()).plus(4, DateTimeUnit.DAY), breaks = breaks)
    }

    fun program(semester: Eva2Semester, abbreviations: Map<String, String>, rows: List<Eva2Row>, term: Term): Program {
        val abbreviation = semester.label.substringBefore(' ')
        val id = semester.label.removeSuffix(" 1")
        val courses = markPracticalParts(rows.mapNotNull { course(it, abbreviation, term) })
        return Program(
            id = id,
            name = programName(abbreviation, abbreviations),
            groups = courses.maxOfOrNull { (it.groups as? GroupSpec.Numbers)?.numbers?.maxOrNull() ?: 0 } ?: 0,
            courses = courses
        )
    }

    // The legend lacks some masters (e.g. MWI), but has the matching bachelor (BWI).
    private fun programName(abbreviation: String, abbreviations: Map<String, String>): String =
        abbreviations[abbreviation]
            ?: abbreviation.takeIf { it.startsWith("M") }
                ?.let { abbreviations["B" + it.drop(1)] }
                ?.takeIf { it.startsWith("Bachelor ") }
                ?.replaceFirst("Bachelor ", "Master ")
            ?: abbreviation

    fun course(row: Eva2Row, programAbbreviation: String, term: Term): Course? {
        val day = WEEKDAYS[row.day] ?: return null
        var title = row.event.trim()

        val typeLabel = TYPE.find(title)?.groupValues?.get(1)
        val type = CourseType.entries.firstOrNull { it.shortLabel == typeLabel && it != CourseType.SONSTIGE }
            ?: CourseType.SONSTIGE
        if (typeLabel != null) title = title.replace(TYPE, "")

        val groupToken = GROUP.find(title)?.groupValues?.get(1)
        if (groupToken != null) title = title.replace(GROUP, "")
        title = title.removeSuffix(" ($programAbbreviation)").trim()
        val groups = when {
            groupToken == null -> GroupSpec.All
            groupToken.split('+').all { part -> part.isNotEmpty() && part.all { it.isDigit() } } ->
                GroupSpec.Numbers(groupToken.split('+').map { it.toInt() })
            "English" in title -> GroupSpec.EnglishGroup(groupToken)
            else -> GroupSpec.Letter(groupToken)
        }

        val period = parsePeriod(row.period)
        val firstDay = term.start.plus(day.ordinal, DateTimeUnit.DAY)
        val lastDay = term.end.monday().plus(day.ordinal, DateTimeUnit.DAY)
        val from = period?.start?.takeIf { it > firstDay }
        val until = period?.end?.takeIf { it < lastDay }
        val weekNote = period?.let { p ->
            val range = "${p.firstWeek}–${p.lastWeek}"
            when {
                p.parity == WeekParity.EVEN -> "nur gerade KW ($range)"
                p.parity == WeekParity.ODD -> "nur ungerade KW ($range)"
                from != null && until != null -> "nur KW $range"
                from != null -> "ab KW ${p.firstWeek} ($range)"
                until != null -> "bis KW ${p.lastWeek} ($range)"
                else -> null
            }
        }
        val typeNote = typeLabel.takeIf { type == CourseType.SONSTIGE }?.let { "Art: $it" }

        return Course(
            day = day,
            start = row.start.padStart(5, '0'),
            end = row.end.padStart(5, '0'),
            title = title,
            type = type,
            room = row.room,
            lecturer = row.lecturer.replace(INITIAL_AFTER_COMMA, "$1 $2"),
            groups = groups,
            note = listOfNotNull(typeNote, weekNote).joinToString(" · ").ifEmpty { null },
            weeks = period?.parity ?: WeekParity.ALL,
            from = from,
            until = until
        )
    }

    /** Practicals split into parts without group numbers ("… P1", "… P2") are assigned separately. */
    private fun markPracticalParts(courses: List<Course>): List<Course> {
        val partsByTitle = courses
            .filter { it.groups == GroupSpec.All }
            .mapNotNull { PRACTICAL_PART.find(it.title)?.groupValues }
            .groupBy({ it[1] }, { it[2] })
            .mapValues { (_, parts) -> parts.distinct().sorted() }
            .filterValues { it.size > 1 }
        return courses.map { course ->
            val match = PRACTICAL_PART.find(course.title)?.groupValues
            val parts = match?.let { partsByTitle[it[1]] }
            if (course.groups != GroupSpec.All || parts == null) {
                course
            } else {
                course.copy(
                    groups = GroupSpec.AllWithNote,
                    note = listOfNotNull("Gruppeneinteilung ${parts.joinToString("/")} lt. Aushang", course.note)
                        .joinToString(" · ")
                )
            }
        }
    }
}
