package io.github.oscarhbrs.stundenplan.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.oscarhbrs.stundenplan.data.MensaRepository
import io.github.oscarhbrs.stundenplan.data.SelectionStore
import io.github.oscarhbrs.stundenplan.data.toggle
import io.github.oscarhbrs.stundenplan.ui.theme.AppIcons

private enum class AppTab(val label: String, val icon: ImageVector) {
    STUNDENPLAN("Stundenplan", AppIcons.Calendar),
    STUDIENGANG("Studiengang", AppIcons.School),
    MENSA("Mensa", AppIcons.Restaurant),
    PORTALE("Portale", AppIcons.Link)
}

@Composable
fun AppRoot(store: SelectionStore, mensa: MensaRepository) {
    var selection by remember { mutableStateOf(store.load()) }
    var currentTab by remember {
        mutableStateOf(if (selection.isEmpty()) AppTab.STUDIENGANG else AppTab.STUNDENPLAN)
    }

    Scaffold(
        bottomBar = {
            NavigationBar(windowInsets = bottomBarInsets() ?: NavigationBarDefaults.windowInsets) {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = { Icon(imageVector = tab.icon, contentDescription = null) },
                        label = { Text(text = tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (currentTab) {
                AppTab.STUNDENPLAN -> ScheduleScreen(selection = selection)
                AppTab.STUDIENGANG -> ProgramSelectionScreen(
                    selected = selection,
                    onToggle = { group ->
                        selection = selection.toggle(group)
                        store.save(selection)
                    }
                )
                AppTab.MENSA -> MensaScreen(repository = mensa)
                AppTab.PORTALE -> PortalsScreen()
            }
        }
    }
}
