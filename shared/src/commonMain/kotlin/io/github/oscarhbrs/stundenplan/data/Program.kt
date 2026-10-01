package io.github.oscarhbrs.stundenplan.data

import io.github.oscarhbrs.stundenplan.schedule.Program

val GROUP_NUMBERS = 1..6

data class GroupSelection(val program: Program, val group: Int)

fun List<GroupSelection>.sortedForDisplay(): List<GroupSelection> =
    distinct().sortedWith(compareBy({ it.program.ordinal }, { it.group }))

fun List<GroupSelection>.toggle(selection: GroupSelection): List<GroupSelection> =
    (if (selection in this) this - selection else this + selection).sortedForDisplay()

/** e.g. "BCSP Gr. 1, 2 · BI Gr. 3", or "BCSP · Gruppe 1" for a single group. */
fun List<GroupSelection>.summary(): String {
    singleOrNull()?.let { return "${it.program.displayName} · Gruppe ${it.group}" }
    return groupBy { it.program }.entries.joinToString(" · ") { (program, groups) ->
        "${program.displayName} Gr. " + groups.joinToString(", ") { it.group.toString() }
    }
}
