package io.github.oscarhbrs.stundenplan.data

import io.github.oscarhbrs.stundenplan.schedule.Schedule
import io.github.oscarhbrs.stundenplan.schedule.ScheduleJson
import io.github.oscarhbrs.stundenplan.schedule.builtInSchedule
import kotlinx.coroutines.CancellationException

private const val KEY_SCHEDULE = "schedules"

/**
 * The timetable built into the app, or a newer one from eva2 loaded via [fetchJson] (the `schedules.json`
 * the deploy workflow publishes with the web app), so changes arrive without an app update.
 */
class ScheduleRepository(
    private val storage: KeyValueStorage,
    private val builtIn: Schedule = builtInSchedule,
    private val fetchJson: suspend () -> String
) {
    fun current(): Schedule = cached()?.takeIf(::isCurrent) ?: builtIn

    /** Returns the downloaded timetable, or `null` if it could not be loaded. */
    suspend fun refresh(): Schedule? {
        val json = try {
            fetchJson()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            return null
        }
        val schedule = decode(json)?.takeIf(::isCurrent) ?: return null
        storage.putString(KEY_SCHEDULE, json)
        return schedule
    }

    // After an app update with a new term, a timetable stored for the previous term is outdated.
    private fun isCurrent(schedule: Schedule): Boolean = schedule.term.start >= builtIn.term.start

    private fun cached(): Schedule? = storage.getString(KEY_SCHEDULE)?.let(::decode)

    private fun decode(json: String): Schedule? =
        runCatching { ScheduleJson.decodeFromString(Schedule.serializer(), json) }.getOrNull()
}
