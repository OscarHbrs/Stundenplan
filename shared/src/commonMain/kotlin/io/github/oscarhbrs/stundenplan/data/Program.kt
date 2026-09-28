package io.github.oscarhbrs.stundenplan.data

enum class Program(val displayName: String, val fullName: String, val termRange: String) {
    BCSP("BCSP", "Cyber Security", "28.09.2026 – 22.01.2027"),
    BI("BI", "Informatik", "28.09.2026 – 22.01.2027"),
    BWI("BWI", "Wirtschaftsinformatik", "28.09.2026 – 22.01.2027")
}

data class GroupSelection(val program: Program, val group: Int)
