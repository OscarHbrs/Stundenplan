package io.github.oscarhbrs.stundenplan.data

private const val KEY_GROUPS = "selected_groups"
private const val KEY_LEGACY_PROGRAM = "selected_program"
private const val KEY_LEGACY_GROUP = "selected_group"

/** Minimal persistent key-value storage, implemented per platform. */
interface KeyValueStorage {
    fun getString(key: String): String?
    fun getInt(key: String): Int?
    fun putString(key: String, value: String)
    fun putInt(key: String, value: Int)
}

/** Stores the selected groups as e.g. "BCSP:1,BI:3". */
class SelectionStore(private val storage: KeyValueStorage) {

    fun load(): List<GroupSelection> {
        val stored = storage.getString(KEY_GROUPS) ?: return loadLegacy()
        return stored.split(',').mapNotNull { entry ->
            val program = runCatching { Program.valueOf(entry.substringBefore(':')) }.getOrNull()
            val group = entry.substringAfter(':', "").toIntOrNull()
            if (program != null && group != null && group in GROUP_NUMBERS) GroupSelection(program, group) else null
        }.sortedForDisplay()
    }

    fun save(selections: List<GroupSelection>) {
        storage.putString(KEY_GROUPS, selections.joinToString(",") { "${it.program.name}:${it.group}" })
    }

    private fun loadLegacy(): List<GroupSelection> {
        val program = storage.getString(KEY_LEGACY_PROGRAM)
            ?.let { runCatching { Program.valueOf(it) }.getOrNull() } ?: return emptyList()
        val group = storage.getInt(KEY_LEGACY_GROUP)?.takeIf { it in GROUP_NUMBERS } ?: return emptyList()
        return listOf(GroupSelection(program, group))
    }
}
