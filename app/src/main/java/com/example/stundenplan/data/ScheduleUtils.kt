package com.example.stundenplan.data

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

val orderedWeekdays = listOf(
    Weekday.MONTAG, Weekday.DIENSTAG, Weekday.MITTWOCH, Weekday.DONNERSTAG, Weekday.FREITAG
)

fun currentWeekday(date: LocalDate = LocalDate.now()): Weekday? = when (date.dayOfWeek) {
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
