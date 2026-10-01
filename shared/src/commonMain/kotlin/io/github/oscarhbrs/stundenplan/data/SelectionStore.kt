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

/** Stores the selected groups as e.g. "BCSP:1,BI:3,MI" (a program without groups has no number). */
class SelectionStore(private val storage: KeyValueStorage) {

    fun load(): List<GroupSelection> {
        val stored = storage.getString(KEY_GROUPS) ?: return loadLegacy()
        return stored.split(',').filter { it.isNotBlank() }.mapNotNull { entry ->
            val program = entry.substringBefore(':')
            if (':' !in entry) return@mapNotNull GroupSelection(program, null)
            entry.substringAfter(':').toIntOrNull()?.let { GroupSelection(program, it) }
        }.sortedForDisplay()
    }

    fun save(selections: List<GroupSelection>) {
        storage.putString(
            KEY_GROUPS,
            selections.joinToString(",") { selection -> selection.group?.let { "${selection.program}:$it" } ?: selection.program }
        )
    }

    private fun loadLegacy(): List<GroupSelection> {
        val program = storage.getString(KEY_LEGACY_PROGRAM) ?: return emptyList()
        val group = storage.getInt(KEY_LEGACY_GROUP) ?: return emptyList()
        return listOf(GroupSelection(program, group))
    }
}
