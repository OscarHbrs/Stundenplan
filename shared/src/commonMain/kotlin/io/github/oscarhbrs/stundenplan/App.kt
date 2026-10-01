package io.github.oscarhbrs.stundenplan

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import io.github.oscarhbrs.stundenplan.data.MensaRepository
import io.github.oscarhbrs.stundenplan.data.ScheduleRepository
import io.github.oscarhbrs.stundenplan.data.SelectionStore
import io.github.oscarhbrs.stundenplan.ui.AppRoot
import io.github.oscarhbrs.stundenplan.ui.LocalSafeAreaInsets
import io.github.oscarhbrs.stundenplan.ui.theme.StundenplanTheme

/**
 * Entry point shared by all platforms.
 *
 * @param safeAreaInsets insets to keep content clear of (notch, home indicator) on platforms
 *   where Compose cannot detect them itself, e.g. the web. `null` uses the Material defaults.
 */
@Composable
fun App(
    store: SelectionStore,
    schedules: ScheduleRepository,
    mensa: MensaRepository,
    safeAreaInsets: WindowInsets? = null
) {
    CompositionLocalProvider(LocalSafeAreaInsets provides safeAreaInsets) {
        StundenplanTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AppRoot(store = store, schedules = schedules, mensa = mensa)
            }
        }
    }
}
