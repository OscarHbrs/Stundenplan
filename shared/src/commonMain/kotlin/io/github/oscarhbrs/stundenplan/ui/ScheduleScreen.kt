package io.github.oscarhbrs.stundenplan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.oscarhbrs.stundenplan.data.GroupSelection
import io.github.oscarhbrs.stundenplan.data.summary
import io.github.oscarhbrs.stundenplan.data.currentWeekday
import io.github.oscarhbrs.stundenplan.data.scheduleFor
import io.github.oscarhbrs.stundenplan.ui.components.GridScheduleView
import io.github.oscarhbrs.stundenplan.ui.components.LegendCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(selection: List<GroupSelection>, modifier: Modifier = Modifier) {
    var today by remember { mutableStateOf(currentWeekday()) }
    LaunchedEffect(Unit) {
        today = currentWeekday()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Stundenplan", fontWeight = FontWeight.Bold)
                        Text(
                            text = if (selection.isNotEmpty()) selection.summary() else "Keine Gruppe gewählt",
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
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
        if (selection.isEmpty()) {
            EmptySelectionNotice(modifier = Modifier.padding(innerPadding))
        } else {
            val schedule = remember(selection) { scheduleFor(selection) }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 9.dp)
            ) {
                GridScheduleView(schedule = schedule, today = today)
                Spacer(modifier = Modifier.size(9.dp))
                Text(
                    text = selection.map { it.program.termRange }.distinct().joinToString(" / "),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.size(16.dp))
                LegendCard()
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun EmptySelectionNotice(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bitte wähle zuerst mindestens eine Gruppe im Tab \"Studiengang\" aus.",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
