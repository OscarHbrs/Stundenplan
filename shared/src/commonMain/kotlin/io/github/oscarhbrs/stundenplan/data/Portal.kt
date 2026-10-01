package io.github.oscarhbrs.stundenplan.data

enum class PortalLogin(val label: String) {
    MIA("MIA-Login"),
    FB02("FB02-Kennung"),
    NONE("Ohne Login")
}

data class Portal(
    val name: String,
    val description: String,
    val url: String,
    val login: PortalLogin? // null = not confirmed, show no badge
)

const val MIA_URL = "https://mia.h-brs.de/"

// Logins per https://faq.infcs.de/passworter/
val portals = listOf(
    Portal(
        name = "LEA",
        description = "Vorlesungsfolien, Übungsblätter, Abgaben und Ankündigungen zu deinen Kursen",
        url = "https://lea.hochschule-bonn-rhein-sieg.de/",
        login = PortalLogin.MIA
    ),
    Portal(
        name = "Apollo",
        description = "Prüfungen an- und abmelden, Noten und Studienfortschritt einsehen",
        url = "https://apollo.h-brs.de/",
        login = PortalLogin.MIA
    ),
    Portal(
        name = "EVA",
        description = "Kurse belegen und sehen, welcher Übungsgruppe du zugeteilt bist",
        url = "https://eva.inf.h-brs.de/",
        login = PortalLogin.FB02
    ),
    Portal(
        name = "EVA2",
        description = "Offizielle Stundenpläne, Studienpläne, Fachbereichs-Ticker und Lernraum-Reservierung",
        url = "https://eva2.inf.h-brs.de/",
        login = PortalLogin.FB02
    ),
    Portal(
        name = "OWA",
        description = "Deine Hochschul-E-Mails im Browser (Outlook)",
        url = "https://owa.stud.h-brs.de/",
        login = PortalLogin.MIA
    ),
    Portal(
        name = "Nextcloud",
        description = "Cloud-Speicher des Fachbereichs Informatik",
        url = "https://cloud.inf.h-brs.de/",
        login = null
    ),
    Portal(
        name = "FAQ",
        description = "Hilfe des Fachbereichs Informatik zu Accounts, Passwörtern, WLAN und mehr. " +
            "Mit FB02-Kennung angemeldet siehst du alle Inhalte.",
        url = "https://faq.inf.h-brs.de/",
        login = PortalLogin.NONE
    )
)
