package io.github.oscarhbrs.stundenplan.schedule

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class DateRange(val start: LocalDate, val end: LocalDate) {
    operator fun contains(date: LocalDate): Boolean = date in start..end
}

/** Lecture period; no courses take place outside it or during [breaks]. */
@Serializable
data class Term(val start: LocalDate, val end: LocalDate, val breaks: List<DateRange> = emptyList()) {
    fun isLectureDay(date: LocalDate): Boolean = date in start..end && breaks.none { date in it }
}

/**
 * A study program in one semester, e.g. "BI" (Bachelor Informatik, 1st semester).
 * [groups] is the number of numbered groups (1..groups), 0 if the program is not split into groups.
 */
@Serializable
data class Program(val id: String, val name: String, val groups: Int, val courses: List<Course>)

@Serializable
data class Schedule(val term: Term, val programs: List<Program>) {
    fun program(id: String): Program? = programs.firstOrNull { it.id == id }
}

fun LocalDate.isoWeekNumber(): Int {
    val thursday = plus(4 - dayOfWeek.isoDayNumber, DateTimeUnit.DAY)
    return (thursday.dayOfYear - 1) / 7 + 1
}

fun LocalDate.monday(): LocalDate = minus(dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)

/**
 * Calendar week of [date], counted on from the first week of the term without restarting at New Year
 * (KW 53 is followed by 54, not 1), as eva2 does for its even/odd week rhythm.
 */
fun Term.weekNumber(date: LocalDate): Int =
    start.isoWeekNumber() + start.monday().daysUntil(date.monday()) / 7

/** Whether [course] takes place on [date], which must fall on the course's weekday. */
fun Term.hasCourseOn(course: Course, date: LocalDate): Boolean {
    if (!isLectureDay(date)) return false
    if (course.from != null && date < course.from) return false
    if (course.until != null && date > course.until) return false
    return when (course.weeks) {
        WeekParity.ALL -> true
        WeekParity.EVEN -> weekNumber(date) % 2 == 0
        WeekParity.ODD -> weekNumber(date) % 2 == 1
    }
}

val ScheduleJson = Json { ignoreUnknownKeys = true }
