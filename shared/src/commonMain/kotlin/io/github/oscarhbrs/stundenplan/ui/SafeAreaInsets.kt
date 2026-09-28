package io.github.oscarhbrs.stundenplan.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Safe-area insets supplied by the platform when Compose cannot detect them itself
 * (web: `env(safe-area-inset-*)` for the iPhone notch and home indicator).
 * `null` means: use the Material defaults, which read the system bars (Android).
 */
val LocalSafeAreaInsets = staticCompositionLocalOf<WindowInsets?> { null }

/** Insets for a top app bar, or `null` for the Material default. */
@Composable
fun topBarInsets(): WindowInsets? =
    LocalSafeAreaInsets.current?.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)

/** Insets for a bottom navigation bar, or `null` for the Material default. */
@Composable
fun bottomBarInsets(): WindowInsets? =
    LocalSafeAreaInsets.current?.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
