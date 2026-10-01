package io.github.oscarhbrs.stundenplan.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.github.oscarhbrs.stundenplan.data.displayLabels
import io.github.oscarhbrs.stundenplan.data.isDietLabel
import io.github.oscarhbrs.stundenplan.mensa.Co2Info
import io.github.oscarhbrs.stundenplan.mensa.Co2Rating
import io.github.oscarhbrs.stundenplan.mensa.Meal
import io.github.oscarhbrs.stundenplan.mensa.MensaNotice
import io.github.oscarhbrs.stundenplan.mensa.MensaScraper
import io.github.oscarhbrs.stundenplan.mensa.OpeningHours
import io.github.oscarhbrs.stundenplan.ui.theme.AppIcons
import io.github.oscarhbrs.stundenplan.ui.theme.Co2Green
import io.github.oscarhbrs.stundenplan.ui.theme.Co2Orange
import io.github.oscarhbrs.stundenplan.ui.theme.Co2Red

private val cardShape = RoundedCornerShape(14.dp)

private fun Co2Rating.color(fallback: Color): Color = when (this) {
    Co2Rating.GREEN -> Co2Green
    Co2Rating.ORANGE -> Co2Orange
    Co2Rating.RED -> Co2Red
    Co2Rating.UNKNOWN -> fallback
}

// The web build's font has no subscript digits, so "₂" is drawn as a shifted "2".
private fun co2Text(prefix: String, suffix: String = ""): AnnotatedString = buildAnnotatedString {
    append(prefix)
    append("CO")
    withStyle(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 0.75.em)) { append("2") }
    append(suffix)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MealCard(meal: Meal, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = meal.name,
                    modifier = Modifier.weight(1f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                meal.studentPrice?.let { price ->
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = price,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            val labels = meal.displayLabels()
            if (labels.isNotEmpty() || meal.co2 != null) {
                Spacer(modifier = Modifier.size(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    itemVerticalAlignment = Alignment.CenterVertically
                ) {
                    labels.forEach { LabelChip(it) }
                    meal.co2?.let { Co2Chip(it) }
                }
            }
        }
    }
}

@Composable
private fun LabelChip(label: String) {
    val tint = if (isDietLabel(label)) Co2Green else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(shape = RoundedCornerShape(6.dp), color = tint.copy(alpha = 0.14f)) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = tint
        )
    }
}

@Composable
private fun Co2Chip(co2: Co2Info) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(9.dp),
            shape = CircleShape,
            color = co2.rating.color(MaterialTheme.colorScheme.outline)
        ) {}
        Text(
            text = co2Text("${co2.grams} g "),
            modifier = Modifier.padding(start = 5.dp),
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MealDetailDialog(meal: Meal, category: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = category,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = meal.name,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                meal.studentPrice?.let { price ->
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "$price für Studierende",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val labels = meal.displayLabels()
                if (labels.isNotEmpty()) {
                    Spacer(modifier = Modifier.size(12.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        labels.forEach { LabelChip(it) }
                    }
                }

                meal.co2?.let { co2 ->
                    Spacer(modifier = Modifier.size(16.dp))
                    Co2Box(co2)
                }

                Spacer(modifier = Modifier.size(16.dp))
                DetailList(
                    title = "Allergene",
                    entries = meal.allergens,
                    emptyText = "Keine Allergene angegeben"
                )
                if (meal.additives.isNotEmpty()) {
                    Spacer(modifier = Modifier.size(12.dp))
                    DetailList(title = "Zusatzstoffe", entries = meal.additives, emptyText = "")
                }

                Spacer(modifier = Modifier.size(12.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Schließen")
                }
            }
        }
    }
}

@Composable
private fun Co2Box(co2: Co2Info) {
    val color = co2.rating.color(MaterialTheme.colorScheme.outline)
    Surface(shape = RoundedCornerShape(10.dp), color = color.copy(alpha = 0.12f)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Surface(
                modifier = Modifier.padding(top = 4.dp).size(12.dp),
                shape = CircleShape,
                color = color
            ) {}
            Spacer(modifier = Modifier.size(10.dp))
            Column {
                Text(
                    text = co2Text("${co2.grams} g ", "-Äquivalent"),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                co2.headline?.let {
                    Text(text = it, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                }
                co2.description?.let {
                    Text(text = it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun DetailList(title: String, entries: List<String>, emptyText: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.size(4.dp))
    if (entries.isEmpty()) {
        Text(text = emptyText, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    } else {
        entries.forEach { entry ->
            Text(
                text = "• $entry",
                modifier = Modifier.padding(vertical = 1.dp),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun NoticeCard(notice: MensaNotice, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = cardShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = AppIcons.Info,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(10.dp))
            Column {
                Text(
                    text = notice.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                notice.text?.let {
                    Text(text = it, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun MensaInfoCard(openingHours: List<OpeningHours>, address: String?, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            if (openingHours.isNotEmpty()) {
                InfoRow(icon = AppIcons.Schedule) {
                    Text(
                        text = "Öffnungszeiten",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    openingHours.forEach { hours ->
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = hours.place,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${hours.days} · ${hours.time} Uhr",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            address?.let {
                Spacer(modifier = Modifier.size(12.dp))
                InfoRow(icon = AppIcons.Place) {
                    Text(text = it, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            TextButton(
                onClick = { uriHandler.openUri(MensaScraper.PAGE_URL) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Auf der Webseite ansehen")
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, content: @Composable () -> Unit) {
    Row {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(top = 1.dp).size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.size(10.dp))
        Column { content() }
    }
}
