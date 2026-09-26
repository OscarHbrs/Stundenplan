package com.example.stundenplan.data

import android.content.Context

private const val PREFS_NAME = "stundenplan_prefs"
private const val KEY_PROGRAM = "selected_program"
private const val KEY_GROUP = "selected_group"

object SelectionStore {

    fun load(context: Context): GroupSelection? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val programName = prefs.getString(KEY_PROGRAM, null) ?: return null
        val group = prefs.getInt(KEY_GROUP, -1)
        if (group !in 1..6) return null
        val program = runCatching { Program.valueOf(programName) }.getOrNull() ?: return null
        return GroupSelection(program, group)
    }

    fun save(context: Context, selection: GroupSelection) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PROGRAM, selection.program.name)
            .putInt(KEY_GROUP, selection.group)
            .apply()
    }
}
