package io.github.oscarhbrs.stundenplan.schedule

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
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

@Serializable
data class Schedule(val term: Term, val courses: Map<Program, List<Course>>) {
    fun coursesFor(program: Program): List<Course> = courses[program].orEmpty()
}

fun LocalDate.isoWeekNumber(): Int {
    val thursday = plus(4 - dayOfWeek.isoDayNumber, DateTimeUnit.DAY)
    return (thursday.dayOfYear - 1) / 7 + 1
}

/** Whether [course] takes place on [date], which must fall on the course's weekday. */
fun Term.hasCourseOn(course: Course, date: LocalDate): Boolean {
    if (!isLectureDay(date)) return false
    if (course.from != null && date < course.from) return false
    return when (course.weeks) {
        WeekParity.ALL -> true
        WeekParity.EVEN -> date.isoWeekNumber() % 2 == 0
        WeekParity.ODD -> date.isoWeekNumber() % 2 == 1
    }
}

val ScheduleJson = Json { ignoreUnknownKeys = true }
