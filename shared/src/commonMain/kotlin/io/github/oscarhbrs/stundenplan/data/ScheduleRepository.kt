package io.github.oscarhbrs.stundenplan.data

import io.github.oscarhbrs.stundenplan.schedule.Schedule
import io.github.oscarhbrs.stundenplan.schedule.ScheduleData
import io.github.oscarhbrs.stundenplan.schedule.ScheduleJson
import kotlinx.coroutines.CancellationException

private const val KEY_SCHEDULE = "schedule"

/**
 * The timetable built into the app, or a newer one loaded via [fetchJson] (the `schedule.json` published
 * with the web app), so changes reach installed apps without an update. Without [fetchJson], only the
 * built-in timetable is used.
 */
class ScheduleRepository(
    private val storage: KeyValueStorage,
    private val fetchJson: (suspend () -> String)? = null
) {
    fun current(): Schedule = if (fetchJson == null) ScheduleData.schedule else cached() ?: ScheduleData.schedule

    /** Returns the downloaded timetable, or `null` if it could not be loaded. */
    suspend fun refresh(): Schedule? {
        val fetch = fetchJson ?: return null
        val json = try {
            fetch()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            return null
        }
        val schedule = decode(json) ?: return null
        storage.putString(KEY_SCHEDULE, json)
        return schedule
    }

    private fun cached(): Schedule? = storage.getString(KEY_SCHEDULE)?.let(::decode)

    private fun decode(json: String): Schedule? =
        runCatching { ScheduleJson.decodeFromString(Schedule.serializer(), json) }.getOrNull()
}
