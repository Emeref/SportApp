package com.example.sportapp.presentation.stats

import android.text.Layout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
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
import kotlin.math.ceil

@Composable
fun CommonChartSection(
    title: String,
    producer: ChartEntryModelProducer,
    unit: String = "",
    overallRawData: List<Map<String, String>>? = null,
    detailTimes: List<String>? = null,
    isScrollEnabled: Boolean = false
) {
    val axisValuesOverrider = remember {
        object : AxisValuesOverrider<ChartEntryModel> {
            override fun getMaxY(model: ChartEntryModel): Float {
                val max = model.maxY
                if (max.isNaN() || max <= 0f) return 8f
                val ceiling = ceil(max.toDouble()).toInt()
                val remainder = ceiling % 8
                val finalMax = if (remainder == 0) ceiling else ceiling + (8 - remainder)
                return finalMax.toFloat()
            }
            override fun getMinY(model: ChartEntryModel): Float = 0f
            override fun getMinX(model: ChartEntryModel): Float = model.minX
            override fun getMaxX(model: ChartEntryModel): Float = model.maxX
        }
    }

    val marker = rememberMarkerCustom(overallRawData, detailTimes, unit)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        
        val symbols = DecimalFormatSymbols(Locale.US).apply { groupingSeparator = ' ' }
        val formatter = DecimalFormat("#,###.#", symbols)
        val orangeColor = Color(0xFFFF9800)

        Chart(
            chart = lineChart(
                lines = listOf(
                    lineSpec(
                        lineColor = orangeColor,
                        lineBackgroundShader = DynamicShaders.fromBrush(
                            Brush.verticalGradient(
                                colors = listOf(orangeColor.copy(alpha = 0.4f), Color.Transparent)
                            )
                        )
                    )
                ),
                axisValuesOverrider = axisValuesOverrider,
            ),
            chartModelProducer = producer,
            marker = marker,
            startAxis = rememberStartAxis(
                label = axisLabelComponent(color = MaterialTheme.colorScheme.onSurface),
                valueFormatter = { value, _ -> formatter.format(value.toLong()) },
                itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 9),
                guideline = null
            ),
            bottomAxis = rememberBottomAxis(
                label = axisLabelComponent(color = MaterialTheme.colorScheme.onSurface),
                valueFormatter = { value, _ -> 
                    if (value.isNaN()) "" else (value.toInt() + 1).toString() 
                },
                guideline = null
            ),
            chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = isScrollEnabled),
            modifier = Modifier.fillMaxWidth().height(if (overallRawData != null) 320.dp else 200.dp)
        )
    }
}

@Composable
fun rememberMarkerCustom(
    overallRawData: List<Map<String, String>>?,
    detailTimes: List<String>?,
    unit: String
): Marker {
    val labelBackgroundColor = MaterialTheme.colorScheme.surface
    val labelTextColor = MaterialTheme.colorScheme.onSurface
    
    val symbols = DecimalFormatSymbols(Locale.US).apply { groupingSeparator = ' ' }
    val formatter = DecimalFormat("#,###.#", symbols)
    val inputSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    val outputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    val isDetail = detailTimes != null
    val lineCount = if (isDetail) 2 else 3

    val label = textComponent(
        color = labelTextColor,
        background = shapeComponent(
            shape = MarkerCorneredShape(Corner.FullyRounded),
            color = labelBackgroundColor,
            strokeColor = Color.Green,
            strokeWidth = 2.dp
        ),
        padding = MutableDimensions(horizontalDp = 12f, verticalDp = 8f),
        textAlignment = Layout.Alignment.ALIGN_CENTER,
        lineCount = lineCount
    )
    val indicator = shapeComponent(shape = Shapes.pillShape, color = Color.Green)
    val guideline = lineComponent(
        color = Color.Green.copy(alpha = 0.5f),
        thickness = 2.dp
    )
    return remember(label, indicator, guideline, overallRawData, detailTimes, unit) {
        object : MarkerComponent(label, indicator, guideline) {
            override fun getInsets(
                context: com.patrykandpatrick.vico.core.context.MeasureContext,
                outInsets: com.patrykandpatrick.vico.core.chart.insets.Insets,
                horizontalDimensions: com.patrykandpatrick.vico.core.chart.dimensions.HorizontalDimensions
            ) {
                with(context) {
                    val height = label.getHeight(context, text = if (isDetail) "100.0 unit\n00:00:00" else "Activity\n2024-01-01\n100.0 unit")
                    outInsets.top = height + (density * 24f)
                }
            }
        }.apply {
            labelFormatter = MarkerLabelFormatter { markedEntries, _ ->
                val entry = markedEntries.firstOrNull() ?: return@MarkerLabelFormatter ""
                val index = entry.entry.x.toInt()
                val value = formatter.format(entry.entry.y)

                if (isDetail && detailTimes != null) {
                    if (index in detailTimes.indices) {
                        val time = detailTimes[index]
                        "$value $unit\n$time"
                    } else ""
                } else if (overallRawData != null) {
                    if (index in overallRawData.indices) {
                        val data = overallRawData[index]
                        val activityName = data["nazwa aktywnosci"] ?: "Aktywność"
                        val rawDate = data["data"] ?: ""
                        val formattedDate = try {
                            inputSdf.parse(rawDate)?.let { outputSdf.format(it) } ?: rawDate
                        } catch (e: Exception) {
                            rawDate
                        }
                        "$activityName\n$formattedDate\n$value $unit"
                    } else ""
                } else ""
            }
        }
    }
}
