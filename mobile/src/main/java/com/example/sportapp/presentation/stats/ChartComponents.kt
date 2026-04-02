package com.example.sportapp.presentation.stats

import android.graphics.Paint
import android.graphics.RectF
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.model.HeartRateZoneResult
import com.example.sportapp.data.model.ZoneStat
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.layout.fullWidth
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.decoration.Decoration
import com.patrykandpatrick.vico.core.chart.draw.ChartDrawContext
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.Component
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.cornered.Corner
import com.patrykandpatrick.vico.core.component.shape.cornered.MarkerCorneredShape
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.context.DrawContext
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter
import com.patrykandpatrick.vico.core.marker.MarkerVisibilityChangeListener
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.math.atan2
import kotlin.math.roundToLong

class ThresholdLineDecoration(
    private val thresholdValue: Float,
    private val lineComponent: Component
) : Decoration {
    override fun onDrawAboveChart(context: ChartDrawContext, bounds: RectF) {
        val chartValues = context.chartValuesProvider.getChartValues()
        val minY = chartValues.minY
        val maxY = chartValues.maxY
        val yRange = maxY - minY
        if (yRange <= 0f) return
        
        val relativeY = (thresholdValue - minY) / yRange
        val drawY = bounds.bottom - (relativeY * bounds.height())
        
        if (drawY >= bounds.top && drawY <= bounds.bottom) {
            lineComponent.draw(context, bounds.left, drawY, bounds.right, drawY)
        }
    }
}

class ZoneBackgroundDecoration(
    private val minYValue: Float,
    private val maxYValue: Float,
    private val zoneColor: Color
) : Decoration {
    private val paint = Paint().apply { style = Paint.Style.FILL }

    override fun onDrawBehindChart(context: ChartDrawContext, bounds: RectF) {
        val chartValues = context.chartValuesProvider.getChartValues()
        val minY = chartValues.minY
        val maxY = chartValues.maxY
        val yRange = maxY - minY
        if (yRange <= 0f) return
        
        val relativeMinY = (minYValue - minY) / yRange
        val relativeMaxY = (maxYValue - minY) / yRange
        
        val top = (bounds.bottom - (relativeMaxY * bounds.height())).coerceAtLeast(bounds.top)
        val bottom = (bounds.bottom - (relativeMinY * bounds.height())).coerceAtMost(bounds.bottom)
        
        if (top < bottom) {
            paint.color = zoneColor.toArgb()
            context.canvas.drawRect(bounds.left, top, bounds.right, bottom, paint)
        }
    }
}

@Composable
fun CommonChartSection(
    title: String,
    producer: ChartEntryModelProducer,
    unit: String = "",
    overallRawData: List<Any>? = null,
    detailTimes: List<String>? = null,
    isScrollEnabled: Boolean = true,
    isZoomEnabled: Boolean = true,
    hrZoneResult: HeartRateZoneResult? = null,
    lineColors: List<Color>? = null,
    isTimestampX: Boolean = false,
    onMarkerShown: ((Int?) -> Unit)? = null
) {
    val totalPoints = detailTimes?.size ?: overallRawData?.size ?: 0
    val axisValuesOverrider = remember(hrZoneResult, unit, totalPoints) {
        object : AxisValuesOverrider<ChartEntryModel> {
            override fun getMaxY(model: ChartEntryModel): Float {
                val max = if (model.maxY.isNaN()) 0f else model.maxY
                if (max <= 0f) return if (unit == "bpm") 180f else if (unit == "hPa") 1025f else 8f
                if (unit == "bpm") return max + 5f
                if (hrZoneResult != null) return hrZoneResult.zones.last().maxBpm.toFloat() + 5f
                if (unit == "hPa") {
                    val currentMin = if (model.minY.isNaN()) max - 2f else model.minY
                    val c = ceil(max.toDouble()).toFloat()
                    return if (c <= floor(currentMin.toDouble()).toFloat()) c + 2f else c + 1f
                }
                if (unit == "m" && title.contains("Wysokość", ignoreCase = true)) {
                    return floor(max.toDouble() + 1.0).toFloat()
                }
                return (ceil(max.toDouble() / 8.0) * 8.0).toFloat().coerceAtLeast(1f)
            }
            override fun getMinY(model: ChartEntryModel): Float {
                val minDataValue = if (model.minY.isNaN()) 0f else model.minY
                if (unit == "hPa") return floor(minDataValue.toDouble()).toFloat() - 1f
                if (unit == "bpm") return (minDataValue - 5f).coerceAtLeast(0f)
                if (unit == "m" && title.contains("Wysokość", ignoreCase = true)) {
                    return floor(minDataValue.toDouble() - 0.001).toFloat().coerceAtLeast(0f)
                }
                return 0f
            }
            override fun getMinX(model: ChartEntryModel): Float = if (totalPoints == 1) model.minX - 0.5f else model.minX
            override fun getMaxX(model: ChartEntryModel): Float = if (totalPoints == 1) model.maxX + 0.5f else model.maxX
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val lines = remember(lineColors, primaryColor, totalPoints) {
        lineColors?.map { color ->
            lineSpec(
                lineColor = color,
                lineBackgroundShader = null,
                point = if (totalPoints == 1) ShapeComponent(shape = Shapes.pillShape, color = color.toArgb()) else null,
                pointSize = if (totalPoints == 1) 4.dp else 0.dp
            )
        } ?: listOf(
            lineSpec(
                lineColor = primaryColor,
                lineBackgroundShader = DynamicShaders.fromBrush(
                    Brush.verticalGradient(colors = listOf(primaryColor.copy(alpha = 0.4f), Color.Transparent))
                ),
                point = if (totalPoints == 1) ShapeComponent(shape = Shapes.pillShape, color = primaryColor.toArgb()) else null,
                pointSize = if (totalPoints == 1) 4.dp else 0.dp
            )
        )
    }

    val thresholdLineComponent = lineComponent(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 1.dp)
    
    val chartDecorations = remember(hrZoneResult, thresholdLineComponent) {
        if (hrZoneResult == null) {
            emptyList()
        } else {
            val decorations = mutableListOf<Decoration>()
            hrZoneResult.zones.forEach { stat ->
                decorations.add(ZoneBackgroundDecoration(stat.minBpm.toFloat(), stat.maxBpm.toFloat(), stat.zone.color.copy(alpha = 0.5f)))
                decorations.add(ThresholdLineDecoration(stat.maxBpm.toFloat(), thresholdLineComponent))
            }
            decorations
        }
    }

    val timestampSdf = remember(isTimestampX) { 
        SimpleDateFormat("dd.MM", Locale.getDefault()).apply {
            if (isTimestampX) timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    val symbols = DecimalFormatSymbols(Locale.US).apply { groupingSeparator = ' ' }
    val formatter = remember(unit) {
        if (unit == "hPa") DecimalFormat("#,###.##", symbols)
        else DecimalFormat("#,###.#", symbols)
    }

    val currentOnMarkerShown by rememberUpdatedState(onMarkerShown)
    val marker = rememberMarkerCustom(overallRawData, detailTimes, unit, lineColors, isTimestampX, currentOnMarkerShown)

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp)) {
        if (title.isNotEmpty()) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
        }
        
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(if (overallRawData != null || isTimestampX) 320.dp else 200.dp)) {
            val chartWidthDp = this.maxWidth - 50.dp
            val totalPointsForSpacing = if (totalPoints > 1) totalPoints else 2
            val calculatedSpacing = if (isTimestampX) 20.dp else (chartWidthDp - 24.dp) / (totalPointsForSpacing - 1).toFloat()

            val horizontalItemPlacer = remember(totalPoints, isTimestampX) {
                if (isTimestampX) {
                    AxisItemPlacer.Horizontal.default(spacing = 1, offset = 0, shiftExtremeTicks = true, addExtremeLabelPadding = true)
                } else {
                    AxisItemPlacer.Horizontal.default(
                        spacing = if (totalPoints > 1) ((totalPoints - 1) / 5).coerceAtLeast(1) else 1,
                        offset = 0, shiftExtremeTicks = true, addExtremeLabelPadding = true
                    )
                }
            }

            Chart(
                chart = lineChart(
                    lines = lines,
                    axisValuesOverrider = axisValuesOverrider,
                    spacing = calculatedSpacing,
                    decorations = chartDecorations
                ),
                chartModelProducer = producer,
                marker = marker,
                startAxis = rememberStartAxis(
                    label = axisLabelComponent(color = MaterialTheme.colorScheme.onSurface),
                    valueFormatter = { value, _ -> 
                        if (unit == "hPa" || unit == "km/h" || unit == "kcal") formatter.format(value)
                        else formatter.format(value.toLong())
                    },
                    itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 9),
                    guideline = null
                ),
                bottomAxis = rememberBottomAxis(
                    label = axisLabelComponent(color = MaterialTheme.colorScheme.onSurface, textSize = 8.sp).apply { ellipsize = null }, 
                    valueFormatter = { value, _ -> 
                        if (isTimestampX) {
                            val timestamp = value.toDouble().roundToLong() * 86400000L
                            timestampSdf.format(Date(timestamp))
                        } else {
                            val index = value.toInt()
                            if (!detailTimes.isNullOrEmpty() && index in detailTimes.indices) formatToRelativeTime(index, detailTimes)
                            else if (!overallRawData.isNullOrEmpty() && index in overallRawData.indices) {
                                val timestamp = when (val data = overallRawData[index]) {
                                    is WorkoutEntity -> data.startTime
                                    is Map<*, *> -> {
                                        try { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(data["data"]?.toString() ?: "")?.time } catch (_: Exception) { null }
                                    }
                                    else -> null
                                }
                                if (timestamp != null) SimpleDateFormat("MM-dd", Locale.US).format(Date(timestamp)) else ""
                            } else ""
                        }
                    },
                    itemPlacer = horizontalItemPlacer,
                    guideline = null
                ),
                chartScrollSpec = rememberChartScrollSpec<ChartEntryModel>(isScrollEnabled = isScrollEnabled),
                isZoomEnabled = isZoomEnabled,
                autoScaleUp = AutoScaleUp.Full,
                horizontalLayout = HorizontalLayout.fullWidth(
                    unscalableStartPadding = if (totalPoints == 1) 16.dp else 0.dp,
                    unscalableEndPadding = 24.dp
                ),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private fun formatToRelativeTime(index: Int, allTimes: List<String>): String {
    if (allTimes.isEmpty()) return "0:00"
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
    try {
        val startTime = sdf.parse(allTimes[0])?.time ?: 0L
        val currentTime = sdf.parse(allTimes[index])?.time ?: 0L
        val diffSeconds = (currentTime - startTime) / 1000
        val hours = diffSeconds / 3600
        val minutes = (diffSeconds % 3600) / 60
        val seconds = diffSeconds % 60
        return if (hours > 0) String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
        else String.format(Locale.US, "%d:%02d", minutes, seconds)
    } catch (_: Exception) { return "0:00" }
}

@Composable
fun rememberMarkerCustom(
    overallRawData: List<Any>?,
    detailTimes: List<String>?,
    unit: String,
    compareColors: List<Color>? = null,
    isTimestampX: Boolean = false,
    onMarkerShown: ((Int?) -> Unit)? = null
): Marker {
    val labelBackgroundColor = MaterialTheme.colorScheme.surface
    val labelTextColor = MaterialTheme.colorScheme.onSurface
    val primaryColor = MaterialTheme.colorScheme.primary
    
    val symbols = DecimalFormatSymbols(Locale.US).apply { groupingSeparator = ' ' }
    val formatter = DecimalFormat("#,###.#", symbols)
    val outputSdf = remember(isTimestampX) { 
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            if (isTimestampX) timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    val inputSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    val isDetail = detailTimes != null
    val lineCount = if (compareColors != null) compareColors.size + 1 else if (isDetail) 2 else if (isTimestampX) 2 else 3

    val label = textComponent(
        color = labelTextColor,
        background = shapeComponent(
            shape = MarkerCorneredShape(Corner.FullyRounded),
            color = labelBackgroundColor,
            strokeColor = primaryColor,
            strokeWidth = 2.dp
        ),
        padding = MutableDimensions(horizontalDp = 12f, verticalDp = 8f),
        textAlignment = Layout.Alignment.ALIGN_CENTER,
        lineCount = lineCount
    )
    val indicator = shapeComponent(shape = Shapes.pillShape, color = primaryColor)
    val guideline = lineComponent(color = primaryColor.copy(alpha = 0.5f), thickness = 2.dp)
    
    return remember(label, indicator, guideline, overallRawData, detailTimes, unit, compareColors, isTimestampX, onMarkerShown) {
        object : MarkerComponent(label, indicator, guideline) {
            override fun draw(
                context: DrawContext,
                bounds: RectF,
                markedEntries: List<Marker.EntryModel>,
                chartValuesProvider: com.patrykandpatrick.vico.core.chart.values.ChartValuesProvider
            ) {
                super.draw(context, bounds, markedEntries, chartValuesProvider)
                val index = markedEntries.firstOrNull()?.entry?.x?.toInt()
                onMarkerShown?.invoke(index)
            }
        }.apply {
            labelFormatter = MarkerLabelFormatter { markedEntries, _ ->
                if (compareColors != null && markedEntries.size >= 2) {
                    val sb = SpannableStringBuilder()
                    markedEntries.forEachIndexed { i, entry ->
                        val color = compareColors.getOrNull(i) ?: labelTextColor
                        val start = sb.length
                        val value = formatter.format(entry.entry.y)
                        sb.append("$value $unit\n")
                        sb.setSpan(ForegroundColorSpan(color.toArgb()), start, sb.length, 0)
                    }
                    val index = markedEntries.first().entry.x.toInt()
                    if (detailTimes != null && index in detailTimes.indices) sb.append(detailTimes[index])
                    sb
                } else {
                    val entry = markedEntries.firstOrNull() ?: return@MarkerLabelFormatter ""
                    val value = formatter.format(entry.entry.y)
                    if (isTimestampX) {
                        val timestamp = entry.entry.x.toDouble().roundToLong() * 86400000L
                        "${outputSdf.format(Date(timestamp))}\n$value $unit"
                    } else if (isDetail) {
                        val index = entry.entry.x.toInt()
                        if (detailTimes != null && index in detailTimes.indices) "$value $unit\n${detailTimes[index]}" else ""
                    } else if (overallRawData != null) {
                        val index = entry.entry.x.toInt()
                        if (index in overallRawData.indices) {
                            when (val data = overallRawData[index]) {
                                is WorkoutEntity -> "${data.activityName}\n${outputSdf.format(Date(data.startTime))}\n$value $unit"
                                is Map<*, *> -> {
                                    val name = data["nazwa aktywnosci"]?.toString() ?: "Aktywność"
                                    val rawDate = data["data"]?.toString() ?: ""
                                    val formattedDate = try { inputSdf.parse(rawDate)?.let { outputSdf.format(it) } ?: rawDate } catch (_: Exception) { rawDate }
                                    "$name\n$formattedDate\n$value $unit"
                                }
                                else -> "$value $unit"
                            }
                        } else ""
                    } else ""
                }
            }
        }
    }
}

@Composable
fun DonutChart(stats: List<ZoneStat>, modifier: Modifier = Modifier) {
    var selectedZone by remember { mutableStateOf<ZoneStat?>(null) }
    val density = LocalDensity.current

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(stats) {
                detectTapGestures { offset ->
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val x = offset.x - center.x
                    val y = offset.y - center.y
                    val radius = size.width / 2f
                    val distance = sqrt((x * x + y * y).toDouble())

                    if (distance <= radius) {
                        var touchAngle = Math.toDegrees(atan2(y.toDouble(), x.toDouble())).toFloat()
                        if (touchAngle < 0) touchAngle += 360f
                        val adjustedAngle = (touchAngle + 90f) % 360f
                        var currentAngle = 0f
                        var found = false
                        stats.forEach { stat ->
                            val sweepAngle = (stat.percentage / 100f) * 360f
                            if (adjustedAngle >= currentAngle && adjustedAngle < currentAngle + sweepAngle) {
                                selectedZone = if (selectedZone == stat) null else stat
                                found = true
                            }
                            currentAngle += sweepAngle
                        }
                        if (!found) selectedZone = null
                    } else {
                        selectedZone = null
                    }
                }
            }
        ) {
            var startAngle = -90f
            stats.forEach { stat ->
                val sweepAngle = (stat.percentage / 100f) * 360f
                if (sweepAngle > 0) {
                    val isSelected = selectedZone == stat
                    drawArc(
                        color = if (isSelected) stat.zone.color.copy(alpha = 0.8f) else stat.zone.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        size = Size(size.width, size.height)
                    )
                    if (isSelected) {
                        drawArc(
                            color = Color.White.copy(alpha = 0.5f),
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            style = Stroke(width = 4f),
                            size = Size(size.width, size.height)
                        )
                    }
                    startAngle += sweepAngle
                }
            }
        }

        selectedZone?.let { stat ->
            val yOffset = with(density) { -70.dp.roundToPx() }
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, yOffset),
                onDismissRequest = { selectedZone = null }
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 6.dp,
                    shadowElevation = 6.dp
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = stat.zone.displayName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = stat.zone.color
                        )
                        Text(
                            text = "${formatDuration(stat.durationSeconds)}: ${stat.zone.description}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) String.format(Locale.US, "%d:%02d:%02d", h, m, s)
    else String.format(Locale.US, "%02d:%02d", m, s)
}
