package io.github.oscarhbrs.stundenplan.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.oscarhbrs.stundenplan.data.ScheduledCourse
import io.github.oscarhbrs.stundenplan.data.currentWeekday
import io.github.oscarhbrs.stundenplan.data.endTime
import io.github.oscarhbrs.stundenplan.data.orderedWeekdays
import io.github.oscarhbrs.stundenplan.data.startTime
import io.github.oscarhbrs.stundenplan.data.weekDates
import io.github.oscarhbrs.stundenplan.schedule.Weekday
import io.github.oscarhbrs.stundenplan.schedule.displayTitle
import io.github.oscarhbrs.stundenplan.schedule.effectiveNote
import io.github.oscarhbrs.stundenplan.ui.theme.AppIcons
import io.github.oscarhbrs.stundenplan.ui.theme.CurrentTimeLineColor
import io.github.oscarhbrs.stundenplan.ui.theme.SubjectColors
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

private const val GRID_START_HOUR = 8
private const val GRID_END_HOUR = 19
private val DP_PER_MINUTE = 1.05.dp
private val TIME_AXIS_WIDTH = 38.dp

// German abbreviated month names, as java.time printed them for "MMM" with Locale.GERMAN.
private val GERMAN_MONTHS_SHORT = listOf(
    "Jan.", "Feb.", "März", "Apr.", "Mai", "Juni", "Juli", "Aug.", "Sept.", "Okt.", "Nov.", "Dez."
)

@Composable
fun GridScheduleView(
    schedule: Map<Weekday, List<ScheduledCourse>>,
    monday: LocalDate,
    now: LocalDateTime,
    modifier: Modifier = Modifier
) {
    val dates = remember(monday) { weekDates(monday) }
    val today = currentWeekday(now.date)
    val totalMinutes = (GRID_END_HOUR - GRID_START_HOUR) * 60
    val totalHeight = DP_PER_MINUTE * totalMinutes

    Column(modifier = modifier.fillMaxWidth()) {
        WeekHeaderRow(dates = dates, today = today)
        Spacer(modifier = Modifier.size(4.dp))

        Row(modifier = Modifier.fillMaxWidth().height(totalHeight)) {
            TimeAxis(totalHeight = totalHeight)
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                HourGridLines(totalHeight = totalHeight)
                Row(modifier = Modifier.fillMaxSize()) {
                    orderedWeekdays.forEachIndexed { index, weekday ->
                        DayColumn(
                            courses = schedule[weekday].orEmpty(),
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        if (index != orderedWeekdays.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            )
                        }
                    }
                }
                if (today != null) {
                    CurrentTimeLine(
                        now = now.time,
                        today = today,
                        visibleDays = orderedWeekdays,
                        totalHeight = totalHeight
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekHeaderRow(dates: Map<Weekday, LocalDate>, today: Weekday?) {
    val monthLabel = remember(dates) {
        GERMAN_MONTHS_SHORT[dates.getValue(Weekday.MONTAG).month.ordinal]
    }
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier.width(TIME_AXIS_WIDTH),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = monthLabel,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        orderedWeekdays.forEach { weekday ->
            val isToday = weekday == today
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = weekday.shortLabel,
                    fontSize = 12.sp,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = dates.getValue(weekday).day.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun HourGridLines(totalHeight: Dp) {
    Box(modifier = Modifier.fillMaxWidth().height(totalHeight)) {
        for (hour in GRID_START_HOUR..GRID_END_HOUR) {
            val offset = DP_PER_MINUTE * ((hour - GRID_START_HOUR) * 60)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(top = offset)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
            )
        }
    }
}

@Composable
private fun TimeAxis(totalHeight: Dp) {
    Box(modifier = Modifier.width(TIME_AXIS_WIDTH).height(totalHeight)) {
        for (hour in GRID_START_HOUR..GRID_END_HOUR) {
            val offset = DP_PER_MINUTE * ((hour - GRID_START_HOUR) * 60)
            Text(
                text = "${hour.toString().padStart(2, '0')}:00",
                modifier = Modifier
                    .padding(top = offset)
                    .offset(y = (-7).dp),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DayColumn(courses: List<ScheduledCourse>, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier) {
        val columnWidth = maxWidth
        val laidOut = remember(courses) { layoutOverlaps(courses) }
        laidOut.forEach { (scheduled, lane, laneCount) ->
            val course = scheduled.course
            val startMinutes = course.startTime().hour * 60 + course.startTime().minute
            val endMinutes = course.endTime().hour * 60 + course.endTime().minute
            val topOffset = DP_PER_MINUTE * (startMinutes - GRID_START_HOUR * 60)
            val blockHeight = DP_PER_MINUTE * (endMinutes - startMinutes)
            val laneWidth = columnWidth / laneCount
            val laneStart = laneWidth * lane

            CourseBlock(
                scheduled = scheduled,
                modifier = Modifier
                    .padding(top = topOffset)
                    .offset(x = laneStart)
                    .width(laneWidth)
                    .padding(horizontal = 1.dp)
                    .height(blockHeight)
            )
        }
    }
}

private fun layoutOverlaps(courses: List<ScheduledCourse>): List<Triple<ScheduledCourse, Int, Int>> {
    val sorted = courses.sortedWith(compareBy({ it.course.startTime() }, { it.course.endTime() }))
    val result = mutableListOf<Triple<ScheduledCourse, Int, Int>>()
    var cluster = mutableListOf<ScheduledCourse>()
    var clusterEnd: LocalTime? = null

    fun flushCluster() {
        if (cluster.isEmpty()) return
        val laneEnds = mutableListOf<LocalTime>()
        val laneOf = LinkedHashMap<ScheduledCourse, Int>()
        for (scheduled in cluster) {
            val c = scheduled.course
            var lane = laneEnds.indexOfFirst { it <= c.startTime() }
            if (lane == -1) {
                laneEnds.add(c.endTime())
                lane = laneEnds.size - 1
            } else {
                laneEnds[lane] = c.endTime()
            }
            laneOf[scheduled] = lane
        }
        val laneCount = laneEnds.size
        cluster.forEach { c -> result.add(Triple(c, laneOf.getValue(c), laneCount)) }
        cluster = mutableListOf()
    }

    for (scheduled in sorted) {
        val c = scheduled.course
        val end = clusterEnd
        if (cluster.isEmpty() || end == null || c.startTime() < end) {
            cluster.add(scheduled)
            clusterEnd = if (end == null || c.endTime() > end) c.endTime() else end
        } else {
            flushCluster()
            cluster.add(scheduled)
            clusterEnd = c.endTime()
        }
    }
    flushCluster()
    return result
}

@Composable
private fun CourseBlock(scheduled: ScheduledCourse, modifier: Modifier = Modifier) {
    val course = scheduled.course
    val isDark = isSystemInDarkTheme()
    val subjectColor = SubjectColors.colorFor(course.title)
    val accent = if (isDark) subjectColor.dark else subjectColor.light
    val backgroundTint = accent.copy(alpha = if (isDark) 0.22f else 0.14f)
    var showDetails by remember { mutableStateOf(false) }

    Surface(
        onClick = { showDetails = true },
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = backgroundTint
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(accent)
            )
            Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 3.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = course.displayTitle(),
                        modifier = Modifier.weight(1f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = course.type.shortLabel,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = accent
                    )
                }
                Text(
                    text = course.room,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = course.lecturer,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                scheduled.groupLabel?.let { label ->
                    Text(
                        text = label,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = accent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                course.effectiveNote()?.let { note ->
                    Row {
                        Icon(
                            imageVector = AppIcons.Warning,
                            contentDescription = null,
                            modifier = Modifier.padding(top = 1.dp, end = 2.dp).size(9.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = note,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.error,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

    if (showDetails) {
        CourseDetailDialog(course = course, groupLabel = scheduled.groupLabel, onDismiss = { showDetails = false })
    }
}

@Composable
private fun CurrentTimeLine(
    now: LocalTime,
    today: Weekday,
    visibleDays: List<Weekday>,
    totalHeight: Dp
) {
    val gridStart = LocalTime(GRID_START_HOUR, 0)
    val gridEnd = LocalTime(GRID_END_HOUR, 0)
    if (now < gridStart || now > gridEnd) return

    val totalMinutes = (GRID_END_HOUR - GRID_START_HOUR) * 60
    val nowMinutes = (now.hour - GRID_START_HOUR) * 60 + now.minute
    val fraction = nowMinutes.toFloat() / totalMinutes.toFloat()
    val todayIndex = visibleDays.indexOf(today).takeIf { it >= 0 }

    Canvas(modifier = Modifier.fillMaxWidth().height(totalHeight)) {
        val y = size.height * fraction
        val thinStroke = 2.dp.toPx()
        drawLine(
            color = CurrentTimeLineColor.copy(alpha = 0.85f),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = thinStroke
        )
        if (todayIndex != null) {
            val columnWidth = size.width / visibleDays.size
            val xStart = columnWidth * todayIndex
            val xEnd = xStart + columnWidth
            drawLine(
                color = CurrentTimeLineColor,
                start = Offset(xStart, y),
                end = Offset(xEnd, y),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}
