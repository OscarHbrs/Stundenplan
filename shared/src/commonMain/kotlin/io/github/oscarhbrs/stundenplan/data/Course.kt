package io.github.oscarhbrs.stundenplan.data

enum class CourseType(val shortLabel: String, val fullLabel: String) {
    VORLESUNG("V", "Vorlesung"),
    UEBUNG("Ü", "Übung"),
    UEBUNG_PRAKTIKUM("ÜP", "Übung im Pool/Praktikum"),
    PRAKTIKUM("P", "Praktikum"),
    SEMINARISTISCHER_UNTERRICHT("SU", "Seminaristischer Unterricht")
}

sealed class GroupSpec {
    object All : GroupSpec()

    data class Numbers(val numbers: List<Int>) : GroupSpec()

    data class EnglishGroup(val letter: String) : GroupSpec()

    object AllWithNote : GroupSpec()
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

data class Course(
    val day: Weekday,
    val start: String,
    val end: String,
    val title: String,
    val type: CourseType,
    val room: String,
    val lecturer: String,
    val groups: GroupSpec,
    val note: String? = null
)

fun Course.displayTitle(): String {
    val g = groups
    return if (g is GroupSpec.EnglishGroup) "$title (Gr. ${g.letter})" else title
}

fun Course.effectiveNote(): String? = when (groups) {
    is GroupSpec.EnglishGroup -> note ?: "Englischgruppe – eigene Einteilung beachten"
    else -> note
}

enum class Weekday(val label: String, val shortLabel: String) {
    MONTAG("Montag", "Mo"),
    DIENSTAG("Dienstag", "Di"),
    MITTWOCH("Mittwoch", "Mi"),
    DONNERSTAG("Donnerstag", "Do"),
    FREITAG("Freitag", "Fr")
}
