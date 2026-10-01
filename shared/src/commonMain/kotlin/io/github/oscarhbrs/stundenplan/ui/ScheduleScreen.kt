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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import io.github.oscarhbrs.stundenplan.data.dayMonth
import io.github.oscarhbrs.stundenplan.data.displayedMonday
import io.github.oscarhbrs.stundenplan.data.nowLocal
import io.github.oscarhbrs.stundenplan.data.scheduleFor
import io.github.oscarhbrs.stundenplan.data.summary
import io.github.oscarhbrs.stundenplan.schedule.Schedule
import io.github.oscarhbrs.stundenplan.schedule.Term
import io.github.oscarhbrs.stundenplan.ui.components.GridScheduleView
import io.github.oscarhbrs.stundenplan.ui.components.LegendCard
import io.github.oscarhbrs.stundenplan.ui.theme.AppIcons
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    schedule: Schedule,
    selection: List<GroupSelection>,
    onOpenGroupPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    var now by remember { mutableStateOf(nowLocal()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000)
            now = nowLocal()
        }
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
                actions = {
                    IconButton(onClick = onOpenGroupPicker) {
                        Icon(
                            imageVector = AppIcons.Settings,
                            contentDescription = "Gruppen wählen",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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
            EmptySelectionNotice(onOpenGroupPicker = onOpenGroupPicker, modifier = Modifier.padding(innerPadding))
        } else {
            val monday = displayedMonday(now.date)
            val week = remember(schedule, selection, monday) { scheduleFor(schedule, selection, monday) }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 9.dp)
            ) {
                if (week.values.all { it.isEmpty() }) {
                    Text(
                        text = "Diese Woche finden keine Veranstaltungen statt.",
                        modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                GridScheduleView(schedule = week, monday = monday, now = now)
                Spacer(modifier = Modifier.size(9.dp))
                Text(
                    text = "Vorlesungszeit: ${schedule.term.label()}",
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

private fun Term.label(): String = "${start.dayMonth()}${start.year} – ${end.dayMonth()}${end.year}"

@Composable
private fun EmptySelectionNotice(onOpenGroupPicker: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Wähle zuerst mindestens eine Gruppe aus.",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(onClick = onOpenGroupPicker) {
            Text("Gruppen wählen")
        }
    }
}
