package io.github.oscarhbrs.stundenplan.data

import io.github.oscarhbrs.stundenplan.schedule.Schedule

/** A group of a program, or the whole program ([group] `null`) if it is not split into groups. */
data class GroupSelection(val program: String, val group: Int?)

fun List<GroupSelection>.sortedForDisplay(): List<GroupSelection> =
    distinct().sortedWith(compareBy({ it.program }, { it.group ?: 0 }))

fun List<GroupSelection>.toggle(selection: GroupSelection): List<GroupSelection> =
    (if (selection in this) this - selection else this + selection).sortedForDisplay()

/** The selections that still exist in [schedule], e.g. after a new term changed the programs. */
fun List<GroupSelection>.validIn(schedule: Schedule): List<GroupSelection> = filter { selection ->
    val program = schedule.program(selection.program) ?: return@filter false
    val group = selection.group
    if (group == null) program.groups == 0 else group in 1..program.groups
}

/** e.g. "BCSP Gr. 1, 2 · BI Gr. 3 · MI", or "BCSP · Gruppe 1" for a single group. */
fun List<GroupSelection>.summary(): String {
    singleOrNull()?.let { selection ->
        return selection.group?.let { "${selection.program} · Gruppe $it" } ?: selection.program
    }
    return groupBy { it.program }.entries.joinToString(" · ") { (program, selections) ->
        val groups = selections.mapNotNull { it.group }
        if (groups.isEmpty()) program else "$program Gr. " + groups.joinToString(", ")
    }
}
