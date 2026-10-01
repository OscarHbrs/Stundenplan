package io.github.oscarhbrs.stundenplan.schedule

object Eva2Scraper {
    const val INDEX_URL = "https://eva2.inf.h-brs.de/stundenplan/"

    private val FIRST_SEMESTER = Regex("""^\S+ 1$""")

    /** Bachelor programs in their 1st semester (BCSP 1, BI 1, BWI 1), according to eva2's legend. */
    fun isBachelorFirstSemester(index: Eva2Index, semester: Eva2Semester): Boolean =
        FIRST_SEMESTER.matches(semester.label) &&
            index.abbreviations[semester.label.substringBefore(' ')].orEmpty().startsWith("Bachelor ")

    suspend fun loadSchedule(
        fetch: suspend (url: String) -> String,
        include: (Eva2Index, Eva2Semester) -> Boolean = ::isBachelorFirstSemester
    ): Schedule {
        val index = Eva2Parser.parseIndex(fetch(INDEX_URL))
        val semesters = index.semesters.filter { include(index, it) }
        check(semesters.isNotEmpty()) { "No matching programs on $INDEX_URL" }
        val rows = semesters.associateWith { Eva2Parser.parseRows(fetch(tableUrl(index, it))) }
        val year = rows.values.flatten().mapNotNull { Eva2Parser.parsePeriod(it.period)?.start }.minOrNull()?.year
            ?: error("No dates found in the timetables")
        val term = Eva2Parser.term(index.weeks, year)
        return Schedule(term, semesters.map { Eva2Parser.program(it, index.abbreviations, rows.getValue(it), term) })
    }

    private fun tableUrl(index: Eva2Index, semester: Eva2Semester): String {
        val params = listOf(
            "weeks" to index.weeks.joinToString(";"),
            "days" to "1-7",
            "mode" to "table",
            "identifier_semester" to semester.id,
            "show_semester" to "",
            "term" to index.termId
        )
        return INDEX_URL + "anzeigen/?" + params.joinToString("&") { (key, value) -> "$key=${encode(value)}" }
    }

    private fun encode(value: String): String = buildString {
        value.encodeToByteArray().forEach { byte ->
            val c = (byte.toInt() and 0xFF).toChar()
            if (c.isLetterOrDigit() && c.code < 128 || c in "-_.~") append(c)
            else append('%').append((byte.toInt() and 0xFF).toString(16).uppercase().padStart(2, '0'))
        }
    }
}
