package io.github.oscarhbrs.stundenplan.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.oscarhbrs.stundenplan.data.GroupSelection
import io.github.oscarhbrs.stundenplan.schedule.Program
import io.github.oscarhbrs.stundenplan.schedule.Schedule
import io.github.oscarhbrs.stundenplan.ui.theme.AppIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupPickerSheet(
    schedule: Schedule,
    selected: List<GroupSelection>,
    onToggle: (GroupSelection) -> Unit,
    onDismiss: () -> Unit
) {
    val safeArea = LocalSafeAreaInsets.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = { safeArea ?: BottomSheetDefaults.windowInsets }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Gruppen wählen",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Wähle eine oder mehrere Gruppen, auch aus verschiedenen Studiengängen. Der Stundenplan zeigt alle Kurse dieser Gruppen." +
                        if (schedule.programs.any { it.groups == 0 }) " Studiengänge ohne Gruppeneinteilung wählst du als Ganzes." else "",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            items(schedule.programs, key = { it.id }) { program ->
                ProgramSection(
                    program = program,
                    selected = selected,
                    onToggle = onToggle
                )
            }

            item {
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Fertig")
                }
            }
        }
    }
}

@Composable
private fun ProgramSection(
    program: Program,
    selected: List<GroupSelection>,
    onToggle: (GroupSelection) -> Unit
) {
    val selectedCount = selected.count { it.program == program.id }
    val options = if (program.groups == 0) listOf(null) else (1..program.groups).toList()
    var expanded by remember(program.id) {
        mutableStateOf(selected.isEmpty() || selectedCount > 0)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 6.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = program.id,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = when {
                            selectedCount == 0 -> program.name
                            program.groups == 0 -> "${program.name} · gewählt"
                            selectedCount == 1 -> "${program.name} · 1 Gruppe gewählt"
                            else -> "${program.name} · $selectedCount Gruppen gewählt"
                        },
                        fontSize = 12.sp,
                        color = if (selectedCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = if (expanded) AppIcons.ExpandLess else AppIcons.ExpandMore,
                    contentDescription = if (expanded) "Einklappen" else "Ausklappen",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    options.forEach { group ->
                        val selection = GroupSelection(program.id, group)
                        GroupRow(
                            label = group?.let { "Gruppe $it" } ?: "Alle Veranstaltungen",
                            isSelected = selection in selected,
                            onClick = { onToggle(selection) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupRow(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
