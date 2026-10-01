package io.github.oscarhbrs.stundenplan.schedule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.datetime.LocalDate

class Eva2ParserTest {

    private fun resource(name: String): String =
        checkNotNull(javaClass.classLoader.getResource(name)) { "Missing test resource $name" }.readText()

    private val index = Eva2Parser.parseIndex(resource("index.html"))
    private val term = Eva2Parser.term(index.weeks, 2026)

    private fun program(label: String): Program {
        val semester = index.semesters.first { it.label == label }
        val rows = Eva2Parser.parseRows(resource(label.lowercase().replace(' ', '-') + ".html"))
        return Eva2Parser.program(semester, index.abbreviations, rows, term)
    }

    private fun Program.course(title: String, day: Weekday, start: String) =
        courses.single { it.title == title && it.day == day && it.start == start }

    @Test
    fun parsesIndex() {
        assertEquals("c32ef58d2e1df421b3b48ef959d32758", index.termId)
        assertEquals(listOf(40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 54, 55, 56), index.weeks)
        assertEquals(30, index.semesters.size)
        assertEquals(
            listOf("BCSP 1", "BI 1", "BWI 1"),
            index.semesters.filter { Eva2Scraper.isBachelorFirstSemester(index, it) }.map { it.label }
        )
        assertEquals("Bachelor Informatik", index.abbreviations["BI"])
    }

    @Test
    fun termWithChristmasBreak() {
        assertEquals(LocalDate(2026, 9, 28), term.start)
        assertEquals(LocalDate(2027, 1, 22), term.end)
        assertEquals(listOf(DateRange(LocalDate(2026, 12, 21), LocalDate(2027, 1, 3))), term.breaks)
    }

    @Test
    fun parsesBachelorProgram() {
        val bi = program("BI 1")
        assertEquals("BI", bi.id)
        assertEquals("Bachelor Informatik", bi.name)
        assertEquals(6, bi.groups)
        assertEquals(22, bi.courses.size)

        val exercise = bi.course("Mathematische Grundlagen und Lineare Algebra", Weekday.MONTAG, "10:00")
        assertEquals(CourseType.UEBUNG, exercise.type)
        assertEquals(GroupSpec.Numbers(listOf(2, 5)), exercise.groups)
        assertEquals(LocalDate(2026, 10, 5), exercise.from)
        assertEquals("ab KW 41 (41–04)", exercise.note)

        val lecture = bi.course("Programmierung 1", Weekday.MONTAG, "08:15")
        assertEquals("09:45", lecture.end)
        assertEquals("St-HS1/2", lecture.room)
        assertEquals(GroupSpec.All, lecture.groups)

        assertEquals("Böhmer J., Rademacher", bi.course("Netze", Weekday.DONNERSTAG, "08:15").lecturer)

        val practical = bi.course("Technische Informatik P1", Weekday.MITTWOCH, "09:00")
        assertEquals(GroupSpec.AllWithNote, practical.groups)
        assertEquals("Gruppeneinteilung P1/P2 lt. Aushang · ab KW 42 (42–04)", practical.note)
    }

    @Test
    fun parsesEvenAndOddWeeks() {
        val bcsp = program("BCSP 1")
        val even = bcsp.courses.single { it.title == "Informationssicherheit" && it.groups == GroupSpec.Numbers(listOf(1, 6)) }
        assertEquals(WeekParity.EVEN, even.weeks)
        assertEquals("nur gerade KW (40–04)", even.note)
        assertNull(even.from)

        val odd = bcsp.courses.single { it.title == "Informationssicherheit" && it.groups == GroupSpec.Numbers(listOf(3, 4)) }
        assertEquals(WeekParity.ODD, odd.weeks)
        assertEquals("nur ungerade KW (41–03)", odd.note)
        assertEquals(LocalDate(2027, 1, 14), odd.until)
        assertFalse(term.hasCourseOn(odd, LocalDate(2027, 1, 7)))
        assertTrue(term.hasCourseOn(odd, LocalDate(2027, 1, 14)))
    }

    @Test
    fun parsesEnglishGroups() {
        val english = program("BWI 1").courses.filter { it.title == "Business English for BIS" }
        assertEquals(listOf("A", "B", "C", "D", "E", "F"), english.map { (it.groups as GroupSpec.EnglishGroup).letter }.sorted())
        assertTrue(english.all { it.type == CourseType.SEMINARISTISCHER_UNTERRICHT })
    }

    @Test
    fun parsesMasterProgramWithoutGroups() {
        val mi = program("MI 1")
        assertEquals("MI", mi.id)
        assertEquals("Master Informatik", mi.name)
        assertEquals(0, mi.groups)
        val ml = mi.course("Machine Learning", Weekday.DIENSTAG, "13:30")
        assertEquals(GroupSpec.Letter("A"), ml.groups)
        assertEquals(CourseType.VORLESUNG_UEBUNG, ml.type)
        assertEquals(CourseType.VORLESUNG_UEBUNG_PRAKTIKUM, mi.course("Embedded Systems Design", Weekday.DONNERSTAG, "13:30").type)
        assertEquals(CourseType.SEMINAR, mi.course("Introduction to Scientific Work", Weekday.MONTAG, "13:30").type)
    }

    @Test
    fun namesMastersMissingFromLegend() {
        val mwi = program("MWI 1")
        assertEquals("Master Wirtschaftsinformatik", mwi.name)
        assertTrue(mwi.courses.any { it.type == CourseType.PROJEKT })
    }

    @Test
    fun jsonRoundTrip() {
        val schedule = Schedule(term, listOf(program("BI 1"), program("MI 1")))
        val json = ScheduleJson.encodeToString(Schedule.serializer(), schedule)
        assertEquals(schedule, ScheduleJson.decodeFromString(Schedule.serializer(), json))
    }

    @Test
    fun legacyJsonOnlyUsesOldTypes() {
        val schedule = Schedule(term, listOf("BCSP 1", "BI 1", "BWI 1", "MI 1").map(::program))
        val json = legacyJson(schedule)
        assertTrue(json.startsWith("""{"term":{"start":"2026-09-28""""))
        assertTrue(""""BCSP":[""" in json && """"BI":[""" in json && """"BWI":[""" in json)
        assertFalse("\"MI\"" in json)
        assertFalse("VORLESUNG_UEBUNG" in json || "\"type\":\"letter\"" in json || "SONSTIGE" in json)
    }
}
