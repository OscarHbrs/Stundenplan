package io.github.oscarhbrs.stundenplan.ui.theme

import androidx.compose.ui.graphics.Color

val LightPrimary = Color(0xFF00695C)
val LightBackground = Color(0xFFFAFAFA)
val LightSurface = Color(0xFFFFFFFF)

val DarkPrimary = Color(0xFF4DB6AC)
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)

val CurrentTimeLineColor = Color(0xFFFF6A3D)

val Co2Green = Color(0xFF43A047)
val Co2Orange = Color(0xFFFB8C00)
val Co2Red = Color(0xFFE53935)

data class SubjectColor(val light: Color, val dark: Color)

object SubjectColors {
    private val programmierung1 = SubjectColor(Color(0xFF00897B), Color(0xFF4DB6AC))
    private val mathematik = SubjectColor(Color(0xFF0091EA), Color(0xFF64B5F6))
    private val netze = SubjectColor(Color(0xFFF4511E), Color(0xFFFF8A65))
    private val datenschutz = SubjectColor(Color(0xFF7C4DFF), Color(0xFFB388FF))
    private val infosec = SubjectColor(Color(0xFFC2185B), Color(0xFFF06292))
    private val techinf = SubjectColor(Color(0xFFF9A825), Color(0xFFFFD54F))
    private val einfWi = SubjectColor(Color(0xFF3949AB), Color(0xFF7986CB))
    private val einfBwl = SubjectColor(Color(0xFF2E7D32), Color(0xFF81C784))
    private val businessEnglish = SubjectColor(Color(0xFF6D4C41), Color(0xFFBCAAA4))
    private val fallback = SubjectColor(Color(0xFF546E7A), Color(0xFF90A4AE))

    private val bySubject = linkedMapOf(
        "Programmierung 1" to programmierung1,
        "Algebraische Strukturen" to mathematik,
        "Mathematische Grundlagen und Lineare Algebra" to mathematik,
        "Netze" to netze,
        "Datenschutz, IT-Recht und Privatheit" to datenschutz,
        "Informationssicherheit" to infosec,
        "Technische Informatik" to techinf,
        "Technische Informatik P1" to techinf,
        "Technische Informatik P2" to techinf,
        "Einführung in die Wirtschaftsinformatik" to einfWi,
        "Einführung in die Betriebswirtschaftslehre" to einfBwl,
        "Business English for BIS" to businessEnglish
    )

    val legendEntries: List<Pair<String, SubjectColor>> = listOf(
        "Programmierung 1" to programmierung1,
        "Algebraische Strukturen" to mathematik,
        "Mathematische Grundlagen und Lineare Algebra" to mathematik,
        "Netze" to netze,
        "Datenschutz, IT-Recht und Privatheit" to datenschutz,
        "Informationssicherheit" to infosec,
        "Technische Informatik" to techinf,
        "Einführung in die Wirtschaftsinformatik" to einfWi,
        "Einführung in die Betriebswirtschaftslehre" to einfBwl,
        "Business English for BIS" to businessEnglish
    )

    fun colorFor(subjectTitle: String): SubjectColor = bySubject[subjectTitle] ?: fallback
}
