package io.github.oscarhbrs.stundenplan.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.oscarhbrs.stundenplan.data.MIA_URL
import io.github.oscarhbrs.stundenplan.data.Portal
import io.github.oscarhbrs.stundenplan.data.PortalLogin
import io.github.oscarhbrs.stundenplan.data.portals
import io.github.oscarhbrs.stundenplan.ui.theme.AppIcons
import io.github.oscarhbrs.stundenplan.ui.theme.Fb02Dark
import io.github.oscarhbrs.stundenplan.ui.theme.Fb02Light

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalsScreen(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Portale", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Hochschule & Fachbereich Informatik",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                windowInsets = topBarInsets() ?: TopAppBarDefaults.windowInsets
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(portals, key = { it.name }) { portal ->
                PortalCard(portal = portal, onClick = { uriHandler.openUri(portal.url) })
            }
            item {
                Spacer(modifier = Modifier.size(6.dp))
                LoginInfoCard(onOpenMia = { uriHandler.openUri(MIA_URL) })
            }
        }
    }
}

@Composable
private fun PortalCard(portal: Portal, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = portal.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    portal.login?.let {
                        Spacer(modifier = Modifier.size(8.dp))
                        LoginBadge(it)
                    }
                }
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = portal.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = AppIcons.OpenInNew,
                contentDescription = "Öffnen",
                modifier = Modifier.padding(start = 12.dp).size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun LoginBadge(login: PortalLogin) {
    val (container, content) = loginColors(login)
    Surface(shape = RoundedCornerShape(6.dp), color = container) {
        Text(
            text = login.label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = content
        )
    }
}

@Composable
private fun loginColors(login: PortalLogin): Pair<Color, Color> {
    val accent = when (login) {
        PortalLogin.MIA -> MaterialTheme.colorScheme.primary
        PortalLogin.FB02 -> if (isSystemInDarkTheme()) Fb02Dark else Fb02Light
        PortalLogin.NONE -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    return accent.copy(alpha = 0.15f) to accent
}

@Composable
private fun LoginInfoCard(onOpenMia: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Welcher Login?",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.size(10.dp))
            LoginExplanation(
                login = PortalLogin.MIA,
                text = "Hochschulweiter Zugang. Das Passwort legst du bei der Aktivierung in MIA selbst fest."
            )
            Spacer(modifier = Modifier.size(10.dp))
            LoginExplanation(
                login = PortalLogin.FB02,
                text = "Zugang vom Fachbereich Informatik mit Standardpasswort. " +
                    "Kennung: 1. Buchstabe Vorname + 5 Buchstaben Nachname + 2s, z. B. mmuste2s."
            )
            TextButton(onClick = onOpenMia, contentPadding = PaddingValues(horizontal = 0.dp)) {
                Text("MIA öffnen (Passwort setzen/ändern)")
            }
        }
    }
}

@Composable
private fun LoginExplanation(login: PortalLogin, text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        LoginBadge(login)
        Text(
            text = text,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
