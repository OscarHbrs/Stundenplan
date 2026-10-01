package io.github.oscarhbrs.stundenplan.schedule

/** The timetable built into the apps, generated from `schedule/schedules.json`. */
val builtInSchedule: Schedule by lazy { ScheduleJson.decodeFromString(Schedule.serializer(), BUILT_IN_SCHEDULE_JSON) }
