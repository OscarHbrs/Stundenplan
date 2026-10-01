package io.github.oscarhbrs.stundenplan.data

import io.github.oscarhbrs.stundenplan.schedule.Course
import io.github.oscarhbrs.stundenplan.schedule.GroupSpec
import io.github.oscarhbrs.stundenplan.schedule.Schedule
import io.github.oscarhbrs.stundenplan.schedule.Weekday
import io.github.oscarhbrs.stundenplan.schedule.displayLabel
import io.github.oscarhbrs.stundenplan.schedule.hasCourseOn
import io.github.oscarhbrs.stundenplan.schedule.matches
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

val orderedWeekdays = listOf(
    Weekday.MONTAG, Weekday.DIENSTAG, Weekday.MITTWOCH, Weekday.DONNERSTAG, Weekday.FREITAG
)

@OptIn(ExperimentalTime::class)
fun nowLocal(): LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

fun currentWeekday(date: LocalDate = nowLocal().date): Weekday? = when (date.dayOfWeek) {
    DayOfWeek.MONDAY -> Weekday.MONTAG
    DayOfWeek.TUESDAY -> Weekday.DIENSTAG
    DayOfWeek.WEDNESDAY -> Weekday.MITTWOCH
    DayOfWeek.THURSDAY -> Weekday.DONNERSTAG
    DayOfWeek.FRIDAY -> Weekday.FREITAG
    else -> null
}

/** Monday of the week to show: the current one, or the next one on weekends. */
fun displayedMonday(today: LocalDate): LocalDate {
    val monday = today.minus(today.dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)
    return if (today.dayOfWeek >= DayOfWeek.SATURDAY) monday.plus(1, DateTimeUnit.WEEK) else monday
}

fun weekDates(monday: LocalDate): Map<Weekday, LocalDate> =
    orderedWeekdays.associateWith { day -> monday.plus(day.ordinal, DateTimeUnit.DAY) }

fun Course.startTime(): LocalTime = LocalTime.parse(start)
fun Course.endTime(): LocalTime = LocalTime.parse(end)

/**
 * A course as shown in the timetable. Identical courses of several programs (e.g. a shared lecture)
 * are merged into one entry; [groups] are the selected groups that attend it.
 */
data class ScheduledCourse(val course: Course, val groups: List<GroupSelection>, val groupLabel: String?)

/** The courses of [selections] that take place in the week starting on [monday]. */
fun scheduleFor(
    schedule: Schedule,
    selections: List<GroupSelection>,
    monday: LocalDate
): Map<Weekday, List<ScheduledCourse>> {
    val dates = weekDates(monday)
    val merged = LinkedHashMap<Course, Pair<Course, MutableList<GroupSelection>>>()
    selections.sortedForDisplay().forEach { selection ->
        val group = selection.group
        schedule.program(selection.program)?.courses.orEmpty()
            .filter { (group == null || it.groups.matches(group)) && schedule.term.hasCourseOn(it, dates.getValue(it.day)) }
            .forEach { course ->
                val entry = merged.getOrPut(course.copy(groups = GroupSpec.All)) { course to mutableListOf() }
                entry.second += selection
            }
    }
    val scheduled = merged.values.map { (course, groups) ->
        ScheduledCourse(course, groups, groupLabel(course, groups, selections))
    }
    return orderedWeekdays.associateWith { day ->
        scheduled.filter { it.course.day == day }.sortedBy { it.course.startTime() }
    }
}

private fun groupLabel(course: Course, groups: List<GroupSelection>, selections: List<GroupSelection>): String? {
    if (selections.size <= 1) return course.groups.displayLabel()
    val byProgram = groups.groupBy { it.program }
    val singleProgram = selections.map { it.program }.distinct().size == 1
    return byProgram.entries.joinToString(" · ") { (program, attending) ->
        val numbers = attending.mapNotNull { it.group }.joinToString("+")
        when {
            numbers.isEmpty() -> program
            singleProgram -> "Gr. $numbers"
            else -> "$program $numbers"
        }
    }
}
