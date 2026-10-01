package io.github.oscarhbrs.stundenplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import io.github.oscarhbrs.stundenplan.data.MensaRepository
import io.github.oscarhbrs.stundenplan.data.SelectionStore
import io.github.oscarhbrs.stundenplan.mensa.MensaScraper
import io.github.oscarhbrs.stundenplan.network.httpGet
import io.github.oscarhbrs.stundenplan.storage.SharedPreferencesStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val storage = SharedPreferencesStorage(this)
        val store = SelectionStore(storage)
        val mensa = MensaRepository(storage) {
            withContext(Dispatchers.IO) { MensaScraper.loadPlan(::httpGet) }
        }
        val uriHandler = CustomTabsUriHandler(this)
        setContent {
            CompositionLocalProvider(LocalUriHandler provides uriHandler) {
                App(store = store, mensa = mensa)
            }
        }
    }
}
