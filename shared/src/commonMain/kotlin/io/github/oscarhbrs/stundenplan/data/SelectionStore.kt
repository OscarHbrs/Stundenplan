package io.github.oscarhbrs.stundenplan.data

private const val KEY_PROGRAM = "selected_program"
private const val KEY_GROUP = "selected_group"

/** Minimal persistent key-value storage, implemented per platform. */
interface KeyValueStorage {
    fun getString(key: String): String?
    fun getInt(key: String): Int?
    fun putString(key: String, value: String)
    fun putInt(key: String, value: Int)
}

class SelectionStore(private val storage: KeyValueStorage) {

    fun load(): GroupSelection? {
        val programName = storage.getString(KEY_PROGRAM) ?: return null
        val group = storage.getInt(KEY_GROUP) ?: return null
        if (group !in 1..6) return null
        val program = runCatching { Program.valueOf(programName) }.getOrNull() ?: return null
        return GroupSelection(program, group)
    }

    fun save(selection: GroupSelection) {
        storage.putString(KEY_PROGRAM, selection.program.name)
        storage.putInt(KEY_GROUP, selection.group)
    }
}
