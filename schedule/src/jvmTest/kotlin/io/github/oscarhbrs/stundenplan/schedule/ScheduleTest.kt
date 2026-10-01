package io.github.oscarhbrs.stundenplan.schedule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.datetime.LocalDate

class ScheduleTest {

    private val term = Term(
        start = LocalDate(2026, 9, 28),
        end = LocalDate(2027, 1, 22),
        breaks = listOf(DateRange(LocalDate(2026, 12, 21), LocalDate(2027, 1, 1)))
    )

    private fun course(weeks: WeekParity = WeekParity.ALL, from: LocalDate? = null) = Course(
        Weekday.DONNERSTAG, "15:15", "16:45", "Test", CourseType.UEBUNG, "St-C119", "X", GroupSpec.All,
        weeks = weeks, from = from
    )

    @Test
    fun isoWeekNumbers() {
        assertEquals(40, LocalDate(2026, 9, 28).isoWeekNumber())
        assertEquals(40, LocalDate(2026, 10, 4).isoWeekNumber())
        assertEquals(41, LocalDate(2026, 10, 5).isoWeekNumber())
        assertEquals(53, LocalDate(2026, 12, 31).isoWeekNumber())
        assertEquals(53, LocalDate(2027, 1, 3).isoWeekNumber())
        assertEquals(1, LocalDate(2027, 1, 4).isoWeekNumber())
    }

    @Test
    fun weekParity() {
        val kw40 = LocalDate(2026, 10, 1)
        val kw41 = LocalDate(2026, 10, 8)
        assertTrue(term.hasCourseOn(course(WeekParity.EVEN), kw40))
        assertFalse(term.hasCourseOn(course(WeekParity.EVEN), kw41))
        assertFalse(term.hasCourseOn(course(WeekParity.ODD), kw40))
        assertTrue(term.hasCourseOn(course(WeekParity.ODD), kw41))
    }

    @Test
    fun startDate() {
        val from = LocalDate(2026, 10, 5)
        assertFalse(term.hasCourseOn(course(from = from), LocalDate(2026, 10, 1)))
        assertTrue(term.hasCourseOn(course(from = from), LocalDate(2026, 10, 8)))
    }

    @Test
    fun onlyDuringLectureTime() {
        assertFalse(term.hasCourseOn(course(), LocalDate(2026, 9, 24)))
        assertFalse(term.hasCourseOn(course(), LocalDate(2026, 12, 24)))
        assertTrue(term.hasCourseOn(course(), LocalDate(2027, 1, 7)))
        assertFalse(term.hasCourseOn(course(), LocalDate(2027, 1, 28)))
    }

    @Test
    fun jsonRoundTrip() {
        val json = ScheduleJson.encodeToString(Schedule.serializer(), ScheduleData.schedule)
        assertEquals(ScheduleData.schedule, ScheduleJson.decodeFromString(Schedule.serializer(), json))
    }
}
