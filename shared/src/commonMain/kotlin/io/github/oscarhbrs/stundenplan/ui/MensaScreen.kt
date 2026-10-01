package io.github.oscarhbrs.stundenplan.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.oscarhbrs.stundenplan.data.MensaRepository
import io.github.oscarhbrs.stundenplan.data.dayMonth
import io.github.oscarhbrs.stundenplan.data.formatFetchedAt
import io.github.oscarhbrs.stundenplan.data.nowLocal
import io.github.oscarhbrs.stundenplan.data.weekdayShort
import io.github.oscarhbrs.stundenplan.mensa.Meal
import io.github.oscarhbrs.stundenplan.mensa.MensaDay
import io.github.oscarhbrs.stundenplan.mensa.MensaPlan
import io.github.oscarhbrs.stundenplan.mensa.MensaScraper
import io.github.oscarhbrs.stundenplan.ui.components.MealCard
import io.github.oscarhbrs.stundenplan.ui.components.MealDetailDialog
import io.github.oscarhbrs.stundenplan.ui.components.MensaInfoCard
import io.github.oscarhbrs.stundenplan.ui.components.NoticeCard
import io.github.oscarhbrs.stundenplan.ui.theme.AppIcons
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MensaScreen(repository: MensaRepository, modifier: Modifier = Modifier) {
    var plan by remember { mutableStateOf(repository.cached()) }
    var loading by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun refresh() {
        if (loading) return
        scope.launch {
            loading = true
            failed = false
            try {
                plan = repository.refresh()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                failed = true
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        if (plan == null || repository.needsRefresh()) refresh()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mensa", fontWeight = FontWeight.Bold)
                        Text(
                            text = plan?.let { "Sankt Augustin · Stand: ${formatFetchedAt(it.fetchedAtEpochMillis)}" }
                                ?: "Sankt Augustin",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 16.dp).size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = ::refresh) {
                            Icon(imageVector = AppIcons.Refresh, contentDescription = "Aktualisieren")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                windowInsets = topBarInsets() ?: TopAppBarDefaults.windowInsets
            )
        }
    ) { innerPadding ->
        val currentPlan = plan
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                currentPlan != null -> PlanContent(plan = currentPlan, showOfflineHint = failed)
                loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                else -> LoadError(onRetry = ::refresh)
            }
        }
    }
}

@Composable
private fun PlanContent(plan: MensaPlan, showOfflineHint: Boolean) {
    val today = remember { nowLocal().date }
    val days = remember(plan, today) { plan.days.filter { it.date >= today } }
    var selectedDate by remember(days) {
        mutableStateOf(days.firstOrNull { it.date == today }?.date ?: days.firstOrNull()?.date)
    }
    val selectedDay = days.firstOrNull { it.date == selectedDate }
    var openMeal by remember { mutableStateOf<Pair<Meal, String>?>(null) }
    val listState = rememberLazyListState()

    LaunchedEffect(selectedDate) { listState.scrollToItem(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (days.isNotEmpty()) {
            DaySelector(
                days = days,
                selected = selectedDate,
                today = today,
                onSelect = { selectedDate = it }
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (showOfflineHint) {
                item { OfflineHint() }
            }

            when {
                selectedDay == null -> item {
                    EmptyNotice("Aktuell ist kein Speiseplan verfügbar.")
                }
                selectedDay.categories.isEmpty() -> item {
                    EmptyNotice("Für diesen Tag gibt es keinen Speiseplan – die Mensa hat wahrscheinlich geschlossen.")
                }
                else -> dayItems(selectedDay, onMealClick = { meal, category -> openMeal = meal to category })
            }

            item {
                Spacer(modifier = Modifier.size(6.dp))
                MensaInfoCard(openingHours = plan.openingHours, address = plan.address)
            }
        }
    }

    openMeal?.let { (meal, category) ->
        MealDetailDialog(meal = meal, category = category, onDismiss = { openMeal = null })
    }
}

private fun LazyListScope.dayItems(
    day: MensaDay,
    onMealClick: (Meal, String) -> Unit
) {
    items(day.notices) { NoticeCard(it) }
    day.categories.forEach { category ->
        item(key = "header-${category.name}") {
            Text(
                text = category.name,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        items(category.meals) { meal ->
            MealCard(meal = meal, onClick = { onMealClick(meal, category.name) })
        }
    }
}

@Composable
private fun DaySelector(
    days: List<MensaDay>,
    selected: LocalDate?,
    today: LocalDate,
    onSelect: (LocalDate) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(days, key = { it.date.toString() }) { day ->
            val isSelected = day.date == selected
            val isToday = day.date == today
            Surface(
                onClick = { onSelect(day.date) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isToday) "Heute" else day.date.weekdayShort(),
                        fontSize = 12.sp,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            isToday -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Text(
                        text = day.date.dayMonth(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun OfflineHint() {
    Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.errorContainer) {
        Text(
            text = "Aktualisieren fehlgeschlagen – angezeigt wird der zuletzt geladene Stand.",
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Composable
private fun EmptyNotice(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 32.dp),
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun LoadError(onRetry: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Der Speiseplan konnte nicht geladen werden. Bitte prüfe deine Internetverbindung.",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.size(16.dp))
        Button(onClick = onRetry) { Text("Erneut versuchen") }
        TextButton(onClick = { uriHandler.openUri(MensaScraper.PAGE_URL) }) {
            Text("Auf der Webseite ansehen")
        }
    }
}
