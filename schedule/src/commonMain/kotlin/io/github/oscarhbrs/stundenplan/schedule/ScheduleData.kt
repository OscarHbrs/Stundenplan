package io.github.oscarhbrs.stundenplan.schedule

import kotlinx.datetime.LocalDate

object ScheduleData {

    private val KW_41 = LocalDate(2026, 10, 5)

    private val bcsp: List<Course> = listOf(
        Course(Weekday.MONTAG, "08:15", "09:45", "Netze", CourseType.UEBUNG_PRAKTIKUM, "St-C055, St-C115", "Schneider M.", GroupSpec.Numbers(listOf(1, 6))),
        Course(Weekday.MONTAG, "08:15", "09:45", "Datenschutz, IT-Recht und Privatheit", CourseType.UEBUNG, "St-B060", "Haferkorn", GroupSpec.Numbers(listOf(2, 5))),
        Course(Weekday.MONTAG, "10:00", "11:30", "Algebraische Strukturen", CourseType.VORLESUNG, "St-HS3", "Müller", GroupSpec.All),
        Course(Weekday.MONTAG, "11:45", "13:15", "Datenschutz, IT-Recht und Privatheit", CourseType.UEBUNG, "St-B060", "Haferkorn", GroupSpec.Numbers(listOf(3, 4))),

        Course(Weekday.DIENSTAG, "10:00", "11:30", "Programmierung 1", CourseType.VORLESUNG, "St-HS8", "Kless", GroupSpec.All),
        Course(Weekday.DIENSTAG, "11:45", "14:00", "Programmierung 1", CourseType.UEBUNG, "St-C063", "Kless", GroupSpec.Numbers(listOf(3, 4))),
        Course(Weekday.DIENSTAG, "13:30", "15:00", "Netze", CourseType.UEBUNG_PRAKTIKUM, "St-C055, St-C117", "Rademacher", GroupSpec.Numbers(listOf(2, 5))),
        Course(Weekday.DIENSTAG, "13:30", "15:00", "Datenschutz, IT-Recht und Privatheit", CourseType.UEBUNG, "St-C118", "Haferkorn", GroupSpec.Numbers(listOf(1, 6))),
        Course(Weekday.DIENSTAG, "15:15", "16:45", "Datenschutz, IT-Recht und Privatheit", CourseType.VORLESUNG, "St-C116", "Haferkorn", GroupSpec.All),

        Course(Weekday.MITTWOCH, "08:15", "09:45", "Algebraische Strukturen", CourseType.UEBUNG, "St-B060", "Müller", GroupSpec.Numbers(listOf(3, 4))),
        Course(Weekday.MITTWOCH, "10:00", "11:30", "Algebraische Strukturen", CourseType.VORLESUNG, "St-HS3", "Müller", GroupSpec.All),
        Course(Weekday.MITTWOCH, "11:45", "13:15", "Netze", CourseType.UEBUNG_PRAKTIKUM, "St-C115", "Jansen", GroupSpec.Numbers(listOf(4))),

        Course(Weekday.DONNERSTAG, "08:15", "09:45", "Netze", CourseType.VORLESUNG, "St-HS1/2", "Böhmer J., Rademacher", GroupSpec.All),
        Course(Weekday.DONNERSTAG, "10:00", "11:30", "Netze", CourseType.UEBUNG_PRAKTIKUM, "St-C055, St-C063", "Böhmer J.", GroupSpec.Numbers(listOf(3))),
        Course(Weekday.DONNERSTAG, "13:30", "15:00", "Informationssicherheit", CourseType.VORLESUNG, "St-HS8", "Schiffner", GroupSpec.All),
        Course(Weekday.DONNERSTAG, "15:15", "16:45", "Informationssicherheit", CourseType.UEBUNG, "St-C119", "Schiffner", GroupSpec.Numbers(listOf(1, 6)), "nur gerade KW (40–04)", weeks = WeekParity.EVEN),
        Course(Weekday.DONNERSTAG, "15:15", "16:45", "Informationssicherheit", CourseType.UEBUNG, "St-C119", "Schiffner", GroupSpec.Numbers(listOf(3, 4)), "nur ungerade KW (41–03)", weeks = WeekParity.ODD),
        Course(Weekday.DONNERSTAG, "15:15", "16:45", "Informationssicherheit", CourseType.UEBUNG, "St-C063", "Fuchs", GroupSpec.Numbers(listOf(2, 5)), "nur ungerade KW (41–03)", weeks = WeekParity.ODD),

        Course(Weekday.FREITAG, "08:15", "09:45", "Programmierung 1", CourseType.VORLESUNG, "St-HS8", "Kless", GroupSpec.All),
        Course(Weekday.FREITAG, "10:00", "11:30", "Algebraische Strukturen", CourseType.UEBUNG, "St-B062", "Marcov", GroupSpec.Numbers(listOf(2, 5))),
        Course(Weekday.FREITAG, "10:00", "12:15", "Programmierung 1", CourseType.UEBUNG, "St-C063, St-C181", "Kless", GroupSpec.Numbers(listOf(1, 6))),
        Course(Weekday.FREITAG, "13:30", "15:00", "Algebraische Strukturen", CourseType.UEBUNG, "St-B062", "Marcov", GroupSpec.Numbers(listOf(1, 6))),
        Course(Weekday.FREITAG, "13:30", "15:45", "Programmierung 1", CourseType.UEBUNG, "St-C063", "Kless", GroupSpec.Numbers(listOf(2, 5)))
    )

    private val bi: List<Course> = listOf(
        Course(Weekday.MONTAG, "08:15", "09:45", "Programmierung 1", CourseType.VORLESUNG, "St-HS1/2", "Mersmann", GroupSpec.All),
        Course(Weekday.MONTAG, "10:00", "11:30", "Mathematische Grundlagen und Lineare Algebra", CourseType.UEBUNG, "St-C118", "Hülsmann", GroupSpec.Numbers(listOf(2, 5))),
        Course(Weekday.MONTAG, "10:00", "11:30", "Netze", CourseType.UEBUNG_PRAKTIKUM, "St-C055, St-C119", "Schneider M.", GroupSpec.Numbers(listOf(3))),
        Course(Weekday.MONTAG, "11:45", "13:15", "Technische Informatik", CourseType.UEBUNG, "St-C118", "Schneider M.", GroupSpec.Numbers(listOf(2, 5))),
        Course(Weekday.MONTAG, "13:30", "15:00", "Mathematische Grundlagen und Lineare Algebra", CourseType.UEBUNG, "St-B060", "Hülsmann", GroupSpec.Numbers(listOf(3, 4))),

        Course(Weekday.DIENSTAG, "08:15", "09:45", "Programmierung 1", CourseType.VORLESUNG, "St-HS1/2", "Mersmann", GroupSpec.All),
        Course(Weekday.DIENSTAG, "10:00", "11:30", "Mathematische Grundlagen und Lineare Algebra", CourseType.UEBUNG, "St-C118", "Hülsmann", GroupSpec.Numbers(listOf(1, 6))),
        Course(Weekday.DIENSTAG, "13:30", "15:00", "Mathematische Grundlagen und Lineare Algebra", CourseType.VORLESUNG, "St-HS1/2", "Hülsmann", GroupSpec.All),
        Course(Weekday.DIENSTAG, "15:15", "16:45", "Netze", CourseType.UEBUNG_PRAKTIKUM, "St-C055, St-C117", "Rademacher", GroupSpec.Numbers(listOf(2, 4))),

        Course(Weekday.MITTWOCH, "09:00", "11:15", "Technische Informatik P1", CourseType.PRAKTIKUM, "St-C117, St-C119", "Schauer, Schneider M.", GroupSpec.AllWithNote, "Gruppeneinteilung P1/P2 lt. Aushang"),
        Course(Weekday.MITTWOCH, "10:00", "11:30", "Netze", CourseType.UEBUNG_PRAKTIKUM, "St-C055, St-C115", "Jansen", GroupSpec.Numbers(listOf(5))),
        Course(Weekday.MITTWOCH, "10:00", "11:30", "Technische Informatik", CourseType.UEBUNG, "St-C063", "Breuer", GroupSpec.Numbers(listOf(3, 4))),
        Course(Weekday.MITTWOCH, "11:45", "13:15", "Technische Informatik", CourseType.VORLESUNG, "St-HS1/2", "Breuer", GroupSpec.All),
        Course(Weekday.MITTWOCH, "13:30", "15:00", "Technische Informatik", CourseType.UEBUNG, "St-C175", "Schneider M.", GroupSpec.Numbers(listOf(1, 6))),
        Course(Weekday.MITTWOCH, "15:15", "17:30", "Technische Informatik P2", CourseType.PRAKTIKUM, "St-C117, St-C119", "Schauer, Schneider M.", GroupSpec.AllWithNote, "Gruppeneinteilung P1/P2 lt. Aushang"),

        Course(Weekday.DONNERSTAG, "08:15", "09:45", "Netze", CourseType.VORLESUNG, "St-HS1/2", "Böhmer J., Rademacher", GroupSpec.All),
        Course(Weekday.DONNERSTAG, "10:00", "12:15", "Programmierung 1", CourseType.PRAKTIKUM, "St-C016", "Mersmann", GroupSpec.Numbers(listOf(1, 6))),
        Course(Weekday.DONNERSTAG, "10:00", "12:15", "Programmierung 1", CourseType.PRAKTIKUM, "St-C177", "Razzaq", GroupSpec.Numbers(listOf(2, 5))),
        Course(Weekday.DONNERSTAG, "13:30", "15:00", "Mathematische Grundlagen und Lineare Algebra", CourseType.VORLESUNG, "St-HS1/2", "Hülsmann", GroupSpec.All),
        Course(Weekday.DONNERSTAG, "15:15", "16:45", "Netze", CourseType.UEBUNG_PRAKTIKUM, "St-C055, St-C175", "Jansen", GroupSpec.Numbers(listOf(1, 6))),

        Course(Weekday.FREITAG, "10:00", "12:15", "Programmierung 1", CourseType.PRAKTIKUM, "St-C016", "Mersmann", GroupSpec.Numbers(listOf(3))),
        Course(Weekday.FREITAG, "10:00", "12:15", "Programmierung 1", CourseType.PRAKTIKUM, "St-C177", "Razzaq", GroupSpec.Numbers(listOf(4)))
    )

    private val bwi: List<Course> = listOf(
        Course(Weekday.MONTAG, "08:15", "09:45", "Programmierung 1", CourseType.VORLESUNG, "St-HS8", "Balg", GroupSpec.All),
        Course(Weekday.MONTAG, "10:00", "11:30", "Mathematische Grundlagen und Lineare Algebra", CourseType.UEBUNG, "St-B060", "Lanzerath", GroupSpec.Numbers(listOf(4)), "ab KW 41 (41–04)", from = KW_41),
        Course(Weekday.MONTAG, "10:00", "11:30", "Mathematische Grundlagen und Lineare Algebra", CourseType.UEBUNG, "St-B062", "Weil", GroupSpec.Numbers(listOf(3)), "ab KW 41 (41–04)", from = KW_41),
        Course(Weekday.MONTAG, "10:00", "11:30", "Einführung in die Wirtschaftsinformatik", CourseType.UEBUNG, "St-C115", "Bergmann", GroupSpec.Numbers(listOf(1, 6))),
        Course(Weekday.MONTAG, "11:45", "13:15", "Einführung in die Wirtschaftsinformatik", CourseType.VORLESUNG, "St-HS8", "Bergmann", GroupSpec.All),
        Course(Weekday.MONTAG, "15:15", "16:45", "Mathematische Grundlagen und Lineare Algebra", CourseType.UEBUNG, "St-B062", "Weil", GroupSpec.Numbers(listOf(6)), "ab KW 41 (41–04)", from = KW_41),

        Course(Weekday.DIENSTAG, "08:15", "09:45", "Programmierung 1", CourseType.VORLESUNG, "St-HS8", "Balg", GroupSpec.All),
        Course(Weekday.DIENSTAG, "10:00", "11:30", "Mathematische Grundlagen und Lineare Algebra", CourseType.UEBUNG, "St-B060", "Lanzerath", GroupSpec.Numbers(listOf(2)), "ab KW 41 (41–04)", from = KW_41),
        Course(Weekday.DIENSTAG, "10:00", "12:15", "Programmierung 1", CourseType.PRAKTIKUM, "St-C175", "Balg", GroupSpec.Numbers(listOf(1, 6))),
        Course(Weekday.DIENSTAG, "10:00", "11:30", "Einführung in die Wirtschaftsinformatik", CourseType.UEBUNG, "St-C018", "Bergmann", GroupSpec.Numbers(listOf(3, 4))),
        Course(Weekday.DIENSTAG, "11:45", "13:15", "Einführung in die Wirtschaftsinformatik", CourseType.UEBUNG, "St-C018", "Bergmann", GroupSpec.Numbers(listOf(2, 5))),
        Course(Weekday.DIENSTAG, "14:15", "16:30", "Programmierung 1", CourseType.PRAKTIKUM, "St-C175", "Balg", GroupSpec.Numbers(listOf(2, 5))),

        Course(Weekday.MITTWOCH, "13:30", "15:00", "Mathematische Grundlagen und Lineare Algebra", CourseType.UEBUNG, "St-C175", "Weil", GroupSpec.Numbers(listOf(1)), "ab KW 41 (41–04)", from = KW_41),
        Course(Weekday.MITTWOCH, "15:15", "16:45", "Mathematische Grundlagen und Lineare Algebra", CourseType.VORLESUNG, "St-HS8", "Weil", GroupSpec.All),
        Course(Weekday.MITTWOCH, "17:00", "18:30", "Mathematische Grundlagen und Lineare Algebra", CourseType.UEBUNG, "St-B060", "Lanzerath", GroupSpec.Numbers(listOf(5)), "ab KW 41 (41–04)", from = KW_41),

        Course(Weekday.DONNERSTAG, "09:15", "11:30", "Programmierung 1", CourseType.PRAKTIKUM, "St-C175", "Balg", GroupSpec.Numbers(listOf(3, 4))),
        Course(Weekday.DONNERSTAG, "09:15", "11:30", "Business English for BIS", CourseType.SEMINARISTISCHER_UNTERRICHT, "St-B060", "Müller-SPZ", GroupSpec.EnglishGroup("A")),
        Course(Weekday.DONNERSTAG, "09:15", "11:30", "Business English for BIS", CourseType.SEMINARISTISCHER_UNTERRICHT, "St-C118", "Werthwein-SPZ", GroupSpec.EnglishGroup("B")),
        Course(Weekday.DONNERSTAG, "09:15", "11:30", "Business English for BIS", CourseType.SEMINARISTISCHER_UNTERRICHT, "St-C018", "Seibert-SPZ", GroupSpec.EnglishGroup("C")),
        Course(Weekday.DONNERSTAG, "11:45", "13:15", "Mathematische Grundlagen und Lineare Algebra", CourseType.VORLESUNG, "St-HS8", "Weil", GroupSpec.All),
        Course(Weekday.DONNERSTAG, "14:00", "16:15", "Business English for BIS", CourseType.SEMINARISTISCHER_UNTERRICHT, "St-C117", "Seibert-SPZ", GroupSpec.EnglishGroup("D")),
        Course(Weekday.DONNERSTAG, "14:00", "16:15", "Business English for BIS", CourseType.SEMINARISTISCHER_UNTERRICHT, "St-B060", "Müller-SPZ", GroupSpec.EnglishGroup("E")),
        Course(Weekday.DONNERSTAG, "14:00", "16:15", "Business English for BIS", CourseType.SEMINARISTISCHER_UNTERRICHT, "St-C116", "Lieder", GroupSpec.EnglishGroup("F")),

        Course(Weekday.FREITAG, "10:00", "11:30", "Einführung in die Betriebswirtschaftslehre", CourseType.VORLESUNG, "St-HS8", "Bonne", GroupSpec.All),
        Course(Weekday.FREITAG, "11:45", "13:15", "Einführung in die Betriebswirtschaftslehre", CourseType.UEBUNG, "St-HS8", "Bonne", GroupSpec.All)
    )

    val schedule = Schedule(
        term = Term(start = LocalDate(2026, 9, 28), end = LocalDate(2027, 1, 22)),
        courses = mapOf(Program.BCSP to bcsp, Program.BI to bi, Program.BWI to bwi)
    )
}
