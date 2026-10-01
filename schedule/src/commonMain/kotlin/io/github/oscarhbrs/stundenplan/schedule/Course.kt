package io.github.oscarhbrs.stundenplan.schedule

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CourseType(val shortLabel: String, val fullLabel: String) {
    VORLESUNG("V", "Vorlesung"),
    UEBUNG("Ü", "Übung"),
    UEBUNG_PRAKTIKUM("ÜP", "Übung im Pool/Praktikum"),
    PRAKTIKUM("P", "Praktikum"),
    SEMINARISTISCHER_UNTERRICHT("SU", "Seminaristischer Unterricht"),
    VORLESUNG_UEBUNG("VÜ", "Vorlesung mit Übung"),
    VORLESUNG_UEBUNG_PRAKTIKUM("VÜP", "Vorlesung, Übung und Praktikum"),
    SEMINAR("S", "Seminar"),
    PROJEKT("Projekt", "Projekt"),
    SONSTIGE("?", "Sonstige")
}

@Serializable
sealed class GroupSpec {
    @Serializable
    @SerialName("all")
    data object All : GroupSpec()

    @Serializable
    @SerialName("numbers")
    data class Numbers(val numbers: List<Int>) : GroupSpec()

    @Serializable
    @SerialName("english")
    data class EnglishGroup(val letter: String) : GroupSpec()

    @Serializable
    @SerialName("allWithNote")
    data object AllWithNote : GroupSpec()

    @Serializable
    @SerialName("letter")
    data class Letter(val letter: String) : GroupSpec()
}

fun GroupSpec.matches(group: Int): Boolean = when (this) {
    GroupSpec.All -> true
    is GroupSpec.Numbers -> group in numbers
    GroupSpec.AllWithNote -> true
    is GroupSpec.EnglishGroup -> true
    is GroupSpec.Letter -> true
}

fun GroupSpec.displayLabel(): String? = when (this) {
    GroupSpec.All -> null
    is GroupSpec.Numbers -> "Gr. " + numbers.joinToString("+")
    GroupSpec.AllWithNote -> null
    is GroupSpec.EnglishGroup -> "Gr. $letter"
    is GroupSpec.Letter -> "Gr. $letter"
}

/** Every week, or every other week counted from the start of the term (see [Term.weekNumber]). */
@Serializable
enum class WeekParity { ALL, EVEN, ODD }

@Serializable
data class Course(
    val day: Weekday,
    val start: String,
    val end: String,
    val title: String,
    val type: CourseType,
    val room: String,
    val lecturer: String,
    val groups: GroupSpec,
    val note: String? = null,
    val weeks: WeekParity = WeekParity.ALL,
    val from: LocalDate? = null,
    val until: LocalDate? = null
)

fun Course.displayTitle(): String = when (val g = groups) {
    is GroupSpec.EnglishGroup -> "$title (Gr. ${g.letter})"
    is GroupSpec.Letter -> "$title (Gr. ${g.letter})"
    else -> title
}

fun Course.effectiveNote(): String? = when (groups) {
    is GroupSpec.EnglishGroup -> note ?: "Englischgruppe – eigene Einteilung beachten"
    else -> note
}

@Serializable
enum class Weekday(val label: String, val shortLabel: String) {
    MONTAG("Montag", "Mo"),
    DIENSTAG("Dienstag", "Di"),
    MITTWOCH("Mittwoch", "Mi"),
    DONNERSTAG("Donnerstag", "Do"),
    FREITAG("Freitag", "Fr")
}
