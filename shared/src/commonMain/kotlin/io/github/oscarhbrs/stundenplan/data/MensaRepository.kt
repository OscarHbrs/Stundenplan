package io.github.oscarhbrs.stundenplan.data

import io.github.oscarhbrs.stundenplan.mensa.MensaJson
import io.github.oscarhbrs.stundenplan.mensa.MensaPlan
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private const val KEY_MENSA_PLAN = "mensa_plan"
private val REFRESH_INTERVAL = 30.minutes

/** Loads the menu via [fetchPlan] and keeps the last result so it is still shown offline. */
@OptIn(ExperimentalTime::class)
class MensaRepository(
    private val storage: KeyValueStorage,
    private val fetchPlan: suspend () -> MensaPlan
) {
    private var lastRefresh: Instant? = null

    fun cached(): MensaPlan? = storage.getString(KEY_MENSA_PLAN)?.let { json ->
        runCatching { MensaJson.decodeFromString(MensaPlan.serializer(), json) }.getOrNull()
    }

    fun needsRefresh(): Boolean = lastRefresh?.let { Clock.System.now() - it > REFRESH_INTERVAL } ?: true

    suspend fun refresh(): MensaPlan {
        val plan = fetchPlan()
        storage.putString(KEY_MENSA_PLAN, MensaJson.encodeToString(MensaPlan.serializer(), plan))
        lastRefresh = Clock.System.now()
        return plan
    }
}
