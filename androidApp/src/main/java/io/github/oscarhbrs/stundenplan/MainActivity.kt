package io.github.oscarhbrs.stundenplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import io.github.oscarhbrs.stundenplan.data.MensaRepository
import io.github.oscarhbrs.stundenplan.data.ScheduleRepository
import io.github.oscarhbrs.stundenplan.data.SelectionStore
import io.github.oscarhbrs.stundenplan.mensa.MensaScraper
import io.github.oscarhbrs.stundenplan.network.httpGet
import io.github.oscarhbrs.stundenplan.storage.SharedPreferencesStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Published with the web app, see deploy-web.yml.
private const val SCHEDULE_URL = "https://oscarhbrs.github.io/Stundenplan/schedule.json"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val storage = SharedPreferencesStorage(this)
        val store = SelectionStore(storage)
        val schedules = ScheduleRepository(storage) {
            withContext(Dispatchers.IO) { httpGet(SCHEDULE_URL) }
        }
        val mensa = MensaRepository(storage) {
            withContext(Dispatchers.IO) { MensaScraper.loadPlan(::httpGet) }
        }
        val uriHandler = CustomTabsUriHandler(this)
        setContent {
            CompositionLocalProvider(LocalUriHandler provides uriHandler) {
                App(store = store, schedules = schedules, mensa = mensa)
            }
        }
    }
}
