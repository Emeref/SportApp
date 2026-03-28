package com.example.sportapp.presentation.stats

import android.graphics.Paint
import android.graphics.RectF
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.cornered.Corner
import com.patrykandpatrick.vico.core.component.shape.cornered.MarkerCorneredShape
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.math.atan2

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
    private val paint = Paint().apply {
        style = Paint.Style.FILL
    }

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
    isTimestampX: Boolean = false
) {
    val axisValuesOverrider = remember(hrZoneResult, unit) {
        object : AxisValuesOverrider<ChartEntryModel> {
            override fun getMaxY(model: ChartEntryModel): Float {
                val max = model.maxY
                if (max.isNaN() || max <= 0f) return if (unit == "bpm") 180f else 8f
                
                if (unit == "bpm") {
                    return max + 5f
                }

                if (hrZoneResult != null) return hrZoneResult.zones.last().maxBpm.toFloat() + 5f
                
                if (unit == "hPa") {
                    val c = ceil(max.toDouble()).toFloat()
                    val f = floor(model.minY.toDouble()).toFloat()
                    return if (c == f) c + 1f else c
                }
                
                val ceiling = ceil(max.toDouble()).toInt()
                val remainder = ceiling % 8
                val finalMax = if (remainder == 0) ceiling else ceiling + (8 - remainder)
                return finalMax.toFloat()
            }
            override fun getMinY(model: ChartEntryModel): Float {
                val minDataValue = if (model.minY.isNaN()) 0f else model.minY
                
                if (unit == "hPa") {
                    return floor(minDataValue.toDouble()).toFloat()
                }

                if (unit == "bpm") {
                    return (minDataValue - 5f).coerceAtLeast(0f)
                }

                return (minDataValue - 4f).coerceAtLeast(0f)
            }
            override fun getMinX(model: ChartEntryModel): Float = model.minX
            override fun getMaxX(model: ChartEntryModel): Float = model.maxX
        }
    }

    val marker = rememberMarkerCustom(overallRawData, detailTimes, unit, lineColors, isTimestampX)

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp)) {
        if (title.isNotEmpty()) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        val symbols = DecimalFormatSymbols(Locale.US).apply { groupingSeparator = ' ' }
        val formatter = remember(unit) {
            if (unit == "hPa") DecimalFormat("#,###.##", symbols)
            else DecimalFormat("#,###.#", symbols)
        }
        val primaryColor = MaterialTheme.colorScheme.primary

        val totalPoints = detailTimes?.size ?: overallRawData?.size ?: 0
        
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

        val lines = lineColors?.map { color ->
            lineSpec(
                lineColor = color,
                lineBackgroundShader = null
            )
        } ?: listOf(
            lineSpec(
                lineColor = primaryColor,
                lineBackgroundShader = DynamicShaders.fromBrush(
                    Brush.verticalGradient(
                        colors = listOf(primaryColor.copy(alpha = 0.4f), Color.Transparent)
                    )
                )
            )
        )

        val startPadding = 0.dp
        val endPadding = 24.dp
        val chartEndPadding = 50.dp

        BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(if (overallRawData != null || isTimestampX) 320.dp else 200.dp)) {
            val chartWidthDp = this.maxWidth - chartEndPadding
            val totalPointsForSpacing = if (totalPoints > 1) totalPoints else 2
            
            // Zmniejszamy spacing dla timestampów, aby uniknąć gigantycznych szerokości wykresu
            val calculatedSpacing = if (isTimestampX) 20.dp else (chartWidthDp - startPadding - endPadding) / (totalPointsForSpacing - 1).toFloat()

            val horizontalItemPlacer = remember(totalPoints, isTimestampX) {
                if (isTimestampX) {
                    // Wyświetlamy etykietę co 7 dni (tydzień), aby uniknąć renderowania tysięcy tekstów
                    AxisItemPlacer.Horizontal.default(
                        spacing = 7,
                        offset = 0,
                        shiftExtremeTicks = true,
                        addExtremeLabelPadding = true
                    )
                } else {
                    AxisItemPlacer.Horizontal.default(
                        spacing = if (totalPoints > 1) ((totalPoints - 1) / 5).coerceAtLeast(1) else 1,
                        offset = 0,
                        shiftExtremeTicks = true,
                        addExtremeLabelPadding = true
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
                        if (unit == "hPa") formatter.format(value)
                        else formatter.format(value.toLong())
                    },
                    itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 9),
                    guideline = null
                ),
                bottomAxis = rememberBottomAxis(
                    label = axisLabelComponent(
                        color = MaterialTheme.colorScheme.onSurface,
                        textSize = 8.sp,
                ).apply { ellipsize = null }, 
                valueFormatter = { value, _ -> 
                    if (isTimestampX) {
                        val msInDay = 86400000L
                        SimpleDateFormat("dd.MM", Locale.US).format(Date(value.toLong() * msInDay))
                    } else {
                        val index = value.toInt()
                        if (!detailTimes.isNullOrEmpty()) {
                            if (index in detailTimes.indices) {
                                formatToRelativeTime(index, detailTimes)
                            } else ""
                        } else if (!overallRawData.isNullOrEmpty()) {
                            if (index in overallRawData.indices) {
                                val timestamp = when (val data = overallRawData[index]) {
                                    is WorkoutEntity -> data.startTime
                                    is Map<*, *> -> {
                                        val rawDate = data["data"]?.toString() ?: ""
                                        try { 
                                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(rawDate)?.time 
                                        } catch (_: Exception) { null }
                                    }
                                    else -> null
                                }
                                if (timestamp != null) {
                                    SimpleDateFormat("MM-dd", Locale.US).format(Date(timestamp))
                                } else ""
                            } else ""
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
                unscalableStartPadding = startPadding,
                unscalableEndPadding = endPadding
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
        return when {
            hours > 0 -> String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
            else -> String.format(Locale.US, "%d:%02d", minutes, seconds)
        }
    } catch (_: Exception) {
        return "0:00"
    }
}

@Composable
fun rememberMarkerCustom(
    overallRawData: List<Any>?,
    detailTimes: List<String>?,
    unit: String,
    compareColors: List<Color>? = null,
    isTimestampX: Boolean = false
): Marker {
    val labelBackgroundColor = MaterialTheme.colorScheme.surface
    val labelTextColor = MaterialTheme.colorScheme.onSurface
    val primaryColor = MaterialTheme.colorScheme.primary
    
    val symbols = DecimalFormatSymbols(Locale.US).apply { groupingSeparator = ' ' }
    val formatter = DecimalFormat("#,###.#", symbols)
    val outputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
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
    val guideline = lineComponent(
        color = primaryColor.copy(alpha = 0.5f),
        thickness = 2.dp
    )
    return remember(label, indicator, guideline, overallRawData, detailTimes, unit, compareColors, isTimestampX) {
        object : MarkerComponent(label, indicator, guideline) {
            override fun getInsets(
                context: com.patrykandpatrick.vico.core.context.MeasureContext,
                outInsets: com.patrykandpatrick.vico.core.chart.insets.Insets,
                horizontalDimensions: com.patrykandpatrick.vico.core.chart.dimensions.HorizontalDimensions
            ) {
                with(context) {
                    val height = label.getHeight(context, text = if (isDetail || isTimestampX) "100.0 unit\n00:00:00" else "Activity\n2024-01-01\n100.0 unit")
                    outInsets.top = height + (density * 24f)
                }
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
                    if (detailTimes != null && index in detailTimes.indices) {
                        sb.append(detailTimes[index])
                    }
                    sb
                } else {
                    val entry = markedEntries.firstOrNull() ?: return@MarkerLabelFormatter ""
                    val value = formatter.format(entry.entry.y)

                    if (isTimestampX) {
                        val msInDay = 86400000L
                        val date = outputSdf.format(Date(entry.entry.x.toLong() * msInDay))
                        "$date\n$value $unit"
                    } else if (isDetail) {
                        val index = entry.entry.x.toInt()
                        if (detailTimes != null && index in detailTimes.indices) {
                            val time = detailTimes[index]
                            "$value $unit\n$time"
                        } else ""
                    } else if (overallRawData != null) {
                        val index = entry.entry.x.toInt()
                        if (index in overallRawData.indices) {
                            when (val data = overallRawData[index]) {
                                is WorkoutEntity -> {
                                    val name = data.activityName
                                    val date = outputSdf.format(Date(data.startTime))
                                    "$name\n$date\n$value $unit"
                                }
                                is Map<*, *> -> {
                                    val name = data["nazwa aktywnosci"]?.toString() ?: "Aktywność"
                                    val rawDate = data["data"]?.toString() ?: ""
                                    val formattedDate = try {
                                        inputSdf.parse(rawDate)?.let { outputSdf.format(it) } ?: rawDate
                                    } catch (_: Exception) {
                                        rawDate
                                    }
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

                    // Sprawdzenie czy kliknięto w koło (łatwiejsze klikanie w pełny kształt)
                    if (distance <= radius) {
                        var touchAngle = Math.toDegrees(atan2(y.toDouble(), x.toDouble())).toFloat()
                        if (touchAngle < 0) touchAngle += 360f

                        // Dostosowanie kąta, bo rysujemy od -90 stopni
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
                    // Zmiana na wypełniony wykres kołowy (useCenter = true)
                    drawArc(
                        color = if (isSelected) stat.zone.color.copy(alpha = 0.8f) else stat.zone.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        size = Size(size.width, size.height)
                    )
                    
                    // Biała obwódka dla zaznaczonej strefy
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
    return if (h > 0) {
        String.format(Locale.US, "%d:%02d:%02d", h, m, s)
    } else {
        String.format(Locale.US, "%02d:%02d", m, s)
    }
}
