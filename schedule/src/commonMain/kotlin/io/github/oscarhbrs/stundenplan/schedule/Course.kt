package io.github.oscarhbrs.stundenplan.schedule

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Program(val displayName: String, val fullName: String) {
    BCSP("BCSP", "Cyber Security"),
    BI("BI", "Informatik"),
    BWI("BWI", "Wirtschaftsinformatik")
}

@Serializable
enum class CourseType(val shortLabel: String, val fullLabel: String) {
    VORLESUNG("V", "Vorlesung"),
    UEBUNG("Ü", "Übung"),
    UEBUNG_PRAKTIKUM("ÜP", "Übung im Pool/Praktikum"),
    PRAKTIKUM("P", "Praktikum"),
    SEMINARISTISCHER_UNTERRICHT("SU", "Seminaristischer Unterricht")
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
}

fun GroupSpec.matches(group: Int): Boolean = when (this) {
    GroupSpec.All -> true
    is GroupSpec.Numbers -> group in numbers
    GroupSpec.AllWithNote -> true
    is GroupSpec.EnglishGroup -> true
}

fun GroupSpec.displayLabel(): String? = when (this) {
    GroupSpec.All -> null
    is GroupSpec.Numbers -> "Gr. " + numbers.joinToString("+")
    GroupSpec.AllWithNote -> null
    is GroupSpec.EnglishGroup -> "Gr. $letter"
}

/** Calendar weeks (ISO) a course takes place in. */
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
    val from: LocalDate? = null
)

fun Course.displayTitle(): String {
    val g = groups
    return if (g is GroupSpec.EnglishGroup) "$title (Gr. ${g.letter})" else title
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
