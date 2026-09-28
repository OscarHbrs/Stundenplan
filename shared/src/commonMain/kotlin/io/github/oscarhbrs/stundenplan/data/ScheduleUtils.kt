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

fun scheduleFor(selection: GroupSelection): Map<Weekday, List<Course>> {
    val all = ScheduleData.coursesFor(selection.program)
    return orderedWeekdays.associateWith { day ->
        all.filter { it.day == day && it.groups.matches(selection.group) }
            .sortedBy { it.startTime() }
    }
}
