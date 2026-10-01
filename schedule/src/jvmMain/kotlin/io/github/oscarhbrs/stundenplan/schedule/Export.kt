package io.github.oscarhbrs.stundenplan.schedule

import java.io.File

/** Writes the built-in timetable as JSON, e.g. `./gradlew :schedule:jvmRun --args=path/to/schedule.json`. */
fun main(args: Array<String>) {
    val output = File(args.firstOrNull() ?: "schedule.json")
    output.absoluteFile.parentFile.mkdirs()
    output.writeText(ScheduleJson.encodeToString(Schedule.serializer(), ScheduleData.schedule))
    println("Wrote ${ScheduleData.schedule.courses.values.sumOf { it.size }} courses to $output")
}
