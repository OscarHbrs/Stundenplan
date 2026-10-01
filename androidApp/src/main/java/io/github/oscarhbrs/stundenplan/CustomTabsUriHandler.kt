package io.github.oscarhbrs.stundenplan

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.platform.UriHandler

// Custom Tabs share the browser's cookies and password manager, so portal logins persist.
class CustomTabsUriHandler(private val context: Context) : UriHandler {
    override fun openUri(uri: String) {
        try {
            CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(uri))
        } catch (e: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
        }
    }
}
