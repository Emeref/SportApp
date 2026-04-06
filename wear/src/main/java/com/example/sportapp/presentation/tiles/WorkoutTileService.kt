package com.example.sportapp.presentation.tiles

import android.content.Context
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.LayoutElement
import androidx.wear.protolayout.ModifiersBuilders.Clickable
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material.Button
import androidx.wear.protolayout.material.ButtonColors
import androidx.wear.protolayout.material.CompactChip
import androidx.wear.protolayout.material.ChipColors
import androidx.wear.protolayout.material.Colors
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.MultiButtonLayout
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import com.example.sportapp.R
import com.example.sportapp.data.db.WorkoutDefinitionDao
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.presentation.MainActivity
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalHorologistApi::class)
class WorkoutTileService : SuspendingTileService() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface TileServiceEntryPoint {
        fun workoutDefinitionDao(): WorkoutDefinitionDao
    }

    override suspend fun tileRequest(requestParams: RequestBuilders.TileRequest): TileBuilders.Tile {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            TileServiceEntryPoint::class.java
        )
        val dao = entryPoint.workoutDefinitionDao()
        val allDefinitions = dao.getAllDefinitions().first()
        val definitions = allDefinitions.take(6)

        val lastSelectedId = requestParams.currentState.lastClickableId
        val selectedWorkoutId = if (lastSelectedId.startsWith("ID_")) {
            lastSelectedId.removePrefix("ID_").toLongOrNull() ?: -1L
        } else {
            -1L
        }

        val selectedWorkout = definitions.find { it.id == selectedWorkoutId }

        // Wersja zasobów zależna od zestawu aktywności i ich ikon
        val resourcesVersion = if (definitions.isEmpty()) "empty" 
            else definitions.joinToString("_") { "${it.id}_${it.iconName}" }

        return TileBuilders.Tile.Builder()
            .setResourcesVersion(resourcesVersion)
            .setTileTimeline(
                TimelineBuilders.Timeline.fromLayoutElement(
                    createLayout(requestParams.deviceConfiguration, definitions, selectedWorkout)
                )
            )
            .build()
    }

    override suspend fun resourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ResourceBuilders.Resources {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            TileServiceEntryPoint::class.java
        )
        val dao = entryPoint.workoutDefinitionDao()
        val definitions = dao.getAllDefinitions().first().take(6)

        val resourcesVersion = if (definitions.isEmpty()) "empty" 
            else definitions.joinToString("_") { "${it.id}_${it.iconName}" }
            
        val resourcesBuilder = ResourceBuilders.Resources.Builder()
            .setVersion(resourcesVersion)

        definitions.forEach { def ->
            resourcesBuilder.addIdToImageMapping(
                def.iconName,
                ResourceBuilders.ImageResource.Builder()
                    .setAndroidResourceByResId(
                        ResourceBuilders.AndroidImageResourceByResId.Builder()
                            .setResourceId(getResIdForIconName(def.iconName))
                            .build()
                    ).build()
            )
        }

        return resourcesBuilder.build()
    }

    private fun getResIdForIconName(name: String): Int {
        return when (name) {
            "DirectionsRun" -> R.drawable.directionsrun
            "DirectionsWalk" -> R.drawable.directionswalk
            "DirectionsBike" -> R.drawable.directionsbike
            "Pool" -> R.drawable.pool
            "Fitness" -> R.drawable.fitness
            "SelfImprovement" -> R.drawable.selfimprovement
            "Mountain" -> R.drawable.mountain
            "SportsTennis" -> R.drawable.sportstennis
            else -> R.drawable.ic_launcher_foreground
        }
    }

    private fun createLayout(
        deviceParameters: DeviceParameters,
        definitions: List<WorkoutDefinition>,
        selectedWorkout: WorkoutDefinition?
    ): LayoutElement {
        val colors = Colors.DEFAULT
        
        val content: LayoutElement = if (definitions.isEmpty()) {
            Text.Builder(this, "Brak aktywności")
                .setTypography(Typography.TYPOGRAPHY_BODY1)
                .setColor(argb(colors.onSurface))
                .build()
        } else {
            val multiButtonBuilder = MultiButtonLayout.Builder()
            definitions.forEach { item ->
                val isSelected = item.id == selectedWorkout?.id
                val buttonColors = if (isSelected) {
                    ButtonColors.primaryButtonColors(colors)
                } else {
                    ButtonColors.secondaryButtonColors(colors)
                }
                
                val clickable = Clickable.Builder()
                    .setId("ID_${item.id}")
                    .setOnClick(ActionBuilders.LoadAction.Builder().build())
                    .build()

                multiButtonBuilder.addButtonContent(
                    Button.Builder(this, clickable)
                        .setIconContent(item.iconName)
                        .setButtonColors(buttonColors)
                        .build()
                )
            }
            multiButtonBuilder.build()
        }

        val headerColumn = LayoutElementBuilders.Column.Builder()
            .addContent(
                Text.Builder(this, "Wybierz trening")
                    .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                    .setColor(argb(0x80FFFFFF.toInt()))
                    .build()
            )

        if (selectedWorkout != null) {
            headerColumn.addContent(
                Text.Builder(this, selectedWorkout.name)
                    .setTypography(Typography.TYPOGRAPHY_CAPTION2)
                    .setColor(argb(colors.primary))
                    .build()
            )
        }

        val startClickable = launchActivityClickable(this, selectedWorkout?.id ?: -1L)

        return PrimaryLayout.Builder(deviceParameters)
            .setResponsiveContentInsetEnabled(true)
            .setPrimaryLabelTextContent(headerColumn.build())
            .setContent(content)
            .setPrimaryChipContent(
                CompactChip.Builder(this, "START", startClickable, deviceParameters)
                    .setChipColors(ChipColors.primaryChipColors(colors))
                    .build()
            )
            .build()
    }

    private fun launchActivityClickable(context: Context, workoutId: Long): Clickable {
        val launchAction = ActionBuilders.LaunchAction.Builder()
            .setAndroidActivity(
                ActionBuilders.AndroidActivity.Builder()
                    .setPackageName(context.packageName)
                    .setClassName(MainActivity::class.java.name)
                    .addKeyToExtraMapping("EXTRA_DEFINITION_ID", ActionBuilders.AndroidLongExtra.Builder().setValue(workoutId).build())
                    .addKeyToExtraMapping("START_IMMEDIATELY", ActionBuilders.AndroidBooleanExtra.Builder().setValue(true).build())
                    .build()
            )
            .build()

        return Clickable.Builder()
            .setOnClick(launchAction)
            .build()
    }
}
