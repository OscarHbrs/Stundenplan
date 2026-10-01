package io.github.oscarhbrs.stundenplan.data

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
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

fun Course.startTime(): LocalTime = LocalTime.parse(start)
fun Course.endTime(): LocalTime = LocalTime.parse(end)

/**
 * A course as shown in the timetable. Identical courses of several programs (e.g. a shared lecture)
 * are merged into one entry; [groups] are the selected groups that attend it.
 */
data class ScheduledCourse(val course: Course, val groups: List<GroupSelection>, val groupLabel: String?)

fun scheduleFor(selections: List<GroupSelection>): Map<Weekday, List<ScheduledCourse>> {
    val merged = LinkedHashMap<Course, Pair<Course, MutableList<GroupSelection>>>()
    selections.sortedForDisplay().forEach { selection ->
        ScheduleData.coursesFor(selection.program)
            .filter { it.groups.matches(selection.group) }
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
        val numbers = attending.joinToString("+") { it.group.toString() }
        if (singleProgram) "Gr. $numbers" else "${program.displayName} $numbers"
    }
}
