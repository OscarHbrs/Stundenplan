package com.example.stundenplan.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.stundenplan.data.GroupSelection
import com.example.stundenplan.data.SelectionStore

private enum class AppTab(val label: String, val icon: String) {
    STUNDENPLAN("Stundenplan", "📅"),
    STUDIENGANG("Studiengang", "🎓")
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    var selection by remember { mutableStateOf(SelectionStore.load(context)) }
    var currentTab by remember {
        mutableStateOf(if (selection == null) AppTab.STUDIENGANG else AppTab.STUNDENPLAN)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = { Text(text = tab.icon, fontSize = 20.sp) },
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
                        SelectionStore.save(context, newSelection)
                        currentTab = AppTab.STUNDENPLAN
                    }
                )
            }
        }
    }
}
