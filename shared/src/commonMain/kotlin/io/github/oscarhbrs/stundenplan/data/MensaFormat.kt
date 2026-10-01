package io.github.oscarhbrs.stundenplan.data

import io.github.oscarhbrs.stundenplan.mensa.Meal
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

private const val LABEL_VEGAN = "Vegan"
private const val LABEL_MEATLESS = "fleischlose Kost"
const val LABEL_VEGETARIAN = "Vegetarisch"

fun LocalDate.weekdayShort(): String = when (dayOfWeek) {
    DayOfWeek.MONDAY -> "Mo"
    DayOfWeek.TUESDAY -> "Di"
    DayOfWeek.WEDNESDAY -> "Mi"
    DayOfWeek.THURSDAY -> "Do"
    DayOfWeek.FRIDAY -> "Fr"
    DayOfWeek.SATURDAY -> "Sa"
    DayOfWeek.SUNDAY -> "So"
}

fun LocalDate.dayMonth(): String =
    "${day.toString().padStart(2, '0')}.${month.number.toString().padStart(2, '0')}."

@OptIn(ExperimentalTime::class)
fun formatFetchedAt(epochMillis: Long, today: LocalDate = nowLocal().date): String {
    val dateTime = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault())
    val time = "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
    val day = when (dateTime.date) {
        today -> "heute"
        today.minus(1, DateTimeUnit.DAY) -> "gestern"
        else -> "${dateTime.date.weekdayShort()} ${dateTime.date.dayMonth()}"
    }
    return "$day, $time"
}

/** Vegan dishes are also tagged "fleischlose Kost" on the website; show only the stronger label. */
fun Meal.displayLabels(): List<String> {
    val vegan = LABEL_VEGAN in labels
    return labels.distinct().mapNotNull { label ->
        when {
            label == LABEL_MEATLESS && vegan -> null
            label == LABEL_MEATLESS -> LABEL_VEGETARIAN
            else -> label
        }
    }
}

fun isDietLabel(label: String): Boolean = label == LABEL_VEGAN || label == LABEL_VEGETARIAN
