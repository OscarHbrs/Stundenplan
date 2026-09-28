package com.example.stundenplan.ui

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
import com.example.stundenplan.data.GroupSelection
import com.example.stundenplan.data.SelectionStore
import com.example.stundenplan.ui.theme.AppIcons

private enum class AppTab(val label: String, val icon: ImageVector) {
    STUNDENPLAN("Stundenplan", AppIcons.Calendar),
    STUDIENGANG("Studiengang", AppIcons.School)
}

@Composable
fun AppRoot(store: SelectionStore) {
    var selection by remember { mutableStateOf(store.load()) }
    var currentTab by remember {
        mutableStateOf(if (selection == null) AppTab.STUDIENGANG else AppTab.STUNDENPLAN)
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
                    currentSelection = selection,
                    onSelect = { newSelection: GroupSelection ->
                        selection = newSelection
                        store.save(newSelection)
                        currentTab = AppTab.STUNDENPLAN
                    }
                )
            }
        }
    }
}
