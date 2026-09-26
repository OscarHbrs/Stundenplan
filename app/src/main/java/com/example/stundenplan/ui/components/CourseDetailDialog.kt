package com.example.stundenplan.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.stundenplan.data.Course
import com.example.stundenplan.data.displayLabel
import com.example.stundenplan.data.displayTitle
import com.example.stundenplan.data.effectiveNote
import com.example.stundenplan.ui.theme.SubjectColors
import com.example.stundenplan.ui.theme.TimeTextStyle

@Composable
fun CourseDetailDialog(course: Course, onDismiss: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val subjectColor = SubjectColors.colorFor(course.title)
    val accent = if (isDark) subjectColor.dark else subjectColor.light

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${course.day.shortLabel} · ${course.start} – ${course.end}",
                        style = TimeTextStyle,
                        color = accent
                    )
                    Surface(shape = RoundedCornerShape(6.dp), color = accent) {
                        Text(
                            text = course.type.fullLabel,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.size(10.dp))

                Text(
                    text = course.displayTitle(),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.size(16.dp))

                DetailRow(icon = "📍", label = "Raum", value = course.room)
                DetailRow(icon = "👤", label = "Dozent", value = course.lecturer)
                course.groups.displayLabel()?.let { label ->
                    DetailRow(icon = "👥", label = "Gruppe", value = label)
                }

                course.effectiveNote()?.let { note ->
                    Spacer(modifier = Modifier.size(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Row(modifier = Modifier.padding(10.dp)) {
                            Text(text = "⚠", fontSize = 14.sp)
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = note,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.size(18.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Schließen")
                }
            }
        }
    }
}

@Composable
private fun DetailRow(icon: String, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(text = icon, fontSize = 15.sp)
        Spacer(modifier = Modifier.size(10.dp))
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
