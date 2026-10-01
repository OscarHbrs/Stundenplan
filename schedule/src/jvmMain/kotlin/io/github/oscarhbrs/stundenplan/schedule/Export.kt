package io.github.oscarhbrs.stundenplan.schedule

import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

/** Programs the Android app up to v1.4.0 knows; it reads them from `schedule.json`. */
private val LEGACY_PROGRAMS = listOf("BCSP", "BI", "BWI")

// Readable diffs for the built-in copy in schedule/schedules.json.
private val PrettyJson = Json(ScheduleJson) { prettyPrint = true }

/**
 * Loads the timetables from eva2 and writes them as JSON, e.g.
 * `./gradlew :schedule:jvmRun --args="$PWD/schedule/schedules.json"` to update the built-in timetable.
 * The optional second file gets the format of older app versions (`schedule.json`).
 */
fun main(args: Array<String>) {
    val output = File(args.getOrNull(0) ?: "schedules.json")
    val legacyOutput = args.getOrNull(1)?.let(::File)
    val client = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(20))
        .build()

    val schedule = runBlocking {
        Eva2Scraper.loadSchedule(fetch = { url ->
            val request = HttpRequest.newBuilder(URI(url)).timeout(Duration.ofSeconds(30)).build()
            val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).await()
            check(response.statusCode() == 200) { "HTTP ${response.statusCode()} for $url" }
            response.body()
        })
    }
    schedule.programs.forEach { check(it.courses.isNotEmpty()) { "No courses found for ${it.id}" } }
    val legacy = legacyOutput?.let { legacyJson(schedule) }

    output.absoluteFile.parentFile.mkdirs()
    output.writeText(PrettyJson.encodeToString(Schedule.serializer(), schedule) + "\n")
    println("Wrote ${schedule.programs.joinToString { "${it.id} (${it.courses.size})" }} to $output")
    if (legacyOutput != null && legacy != null) {
        legacyOutput.absoluteFile.parentFile.mkdirs()
        legacyOutput.writeText(legacy)
        println("Wrote ${LEGACY_PROGRAMS.joinToString()} in the old format to $legacyOutput")
    }
}

/** `{"term": …, "courses": {"BCSP": […], …}}` with only the course types and groups older apps can read. */
fun legacyJson(schedule: Schedule): String {
    val courses = LEGACY_PROGRAMS.mapNotNull { id ->
        schedule.program(id)?.let { program -> id to JsonArray(program.courses.map(::legacyCourse)) }
    }
    check(courses.size == LEGACY_PROGRAMS.size) { "Missing programs for schedule.json" }
    return JsonObject(
        mapOf(
            "term" to ScheduleJson.encodeToJsonElement(Term.serializer(), schedule.term),
            "courses" to JsonObject(courses.toMap())
        )
    ).toString()
}

private fun legacyCourse(course: Course) = ScheduleJson.encodeToJsonElement(
    Course.serializer(),
    course.copy(
        type = when (course.type) {
            CourseType.VORLESUNG_UEBUNG, CourseType.VORLESUNG_UEBUNG_PRAKTIKUM, CourseType.SONSTIGE -> CourseType.VORLESUNG
            CourseType.SEMINAR, CourseType.PROJEKT -> CourseType.SEMINARISTISCHER_UNTERRICHT
            else -> course.type
        },
        groups = when (val groups = course.groups) {
            is GroupSpec.Letter -> GroupSpec.EnglishGroup(groups.letter)
            else -> groups
        },
        note = if (course.groups is GroupSpec.Letter) course.note ?: "Gruppeneinteilung beachten" else course.note
    )
)
