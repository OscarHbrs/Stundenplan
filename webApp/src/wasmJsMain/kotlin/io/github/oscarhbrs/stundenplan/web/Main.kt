@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.oscarhbrs.stundenplan.web

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import io.github.oscarhbrs.stundenplan.App
import io.github.oscarhbrs.stundenplan.data.KeyValueStorage
import io.github.oscarhbrs.stundenplan.data.SelectionStore
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.events.Event

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val store = SelectionStore(LocalStorage)
    ComposeViewport(document.getElementById("app")!!) {
        var insets by remember { mutableStateOf(readSafeAreaInsets()) }
        DisposableEffect(Unit) {
            val listener: (Event) -> Unit = { insets = readSafeAreaInsets() }
            window.addEventListener("resize", listener)
            hideLoadingScreen()
            onDispose { window.removeEventListener("resize", listener) }
        }
        App(store = store, safeAreaInsets = insets)
    }
}

/** 1 CSS px equals 1 dp in Compose for Web. */
private fun readSafeAreaInsets(): WindowInsets = WindowInsets(
    left = safeAreaInset("left").dp,
    top = safeAreaInset("top").dp,
    right = safeAreaInset("right").dp,
    bottom = safeAreaInset("bottom").dp
)

/** Reads `env(safe-area-inset-*)` via the probe element defined in index.html. */
private fun safeAreaInset(side: String): Int = js(
    "parseInt(getComputedStyle(document.getElementById('safe-area-probe')).getPropertyValue('padding-' + side)) || 0"
)

private fun hideLoadingScreen() {
    document.getElementById("loading")?.remove()
}

private object LocalStorage : KeyValueStorage {
    override fun getString(key: String): String? = localStorageGet(key)
    override fun getInt(key: String): Int? = localStorageGet(key)?.toIntOrNull()
    override fun putString(key: String, value: String) = localStorageSet(key, value)
    override fun putInt(key: String, value: Int) = localStorageSet(key, value.toString())
}

// localStorage throws in some private-browsing modes; treat that like "nothing stored".
private fun localStorageGet(key: String): String? =
    js("(() => { try { return localStorage.getItem(key); } catch (e) { return null; } })()")

private fun localStorageSet(key: String, value: String) {
    js("(() => { try { localStorage.setItem(key, value); } catch (e) {} })()")
}
