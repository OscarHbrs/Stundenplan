package io.github.oscarhbrs.stundenplan.mensa

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class MensaPlan(
    val fetchedAtEpochMillis: Long,
    val openingHours: List<OpeningHours>,
    val address: String?,
    val days: List<MensaDay>
)

@Serializable
data class OpeningHours(val place: String, val days: String, val time: String)

@Serializable
data class MensaDay(
    val date: LocalDate,
    val notices: List<MensaNotice>,
    val categories: List<MealCategory>
)

@Serializable
data class MensaNotice(val title: String, val text: String?)

@Serializable
data class MealCategory(val name: String, val meals: List<Meal>)

@Serializable
data class Meal(
    val name: String,
    val studentPrice: String?,
    val labels: List<String>,
    val allergens: List<String>,
    val additives: List<String>,
    val co2: Co2Info?
)

@Serializable
data class Co2Info(
    val grams: Int,
    val rating: Co2Rating,
    val headline: String?,
    val description: String?
)

@Serializable
enum class Co2Rating { GREEN, ORANGE, RED, UNKNOWN }

val MensaJson = Json { ignoreUnknownKeys = true }
