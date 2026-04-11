package com.example.sportapp.healthconnect

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.sportapp.data.IUserHealthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.reflect.KClass

sealed class HealthDataSyncResult {
    data class Success(
        val sex: String?,
        val age: Int?,
        val weightKg: Double?,
        val heightCm: Double?,
        val restingHeartRate: Int?,
        val maxHeartRate: Int?,
        val vo2max: Double?
    ) : HealthDataSyncResult()
    object PermissionDenied : HealthDataSyncResult()
    data class Error(val message: String) : HealthDataSyncResult()
}

class HealthDataSyncUseCase @Inject constructor(
    private val healthConnectClient: HealthConnectClient,
    private val userHealthRepository: IUserHealthRepository
) {
    suspend fun readHealthDataFromHC(): HealthDataSyncResult {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("HealthDataSync", "--- START SYNCHRONIZACJI ---")
                
                // 1. Waga (Ostatnie 5 lat) - zaokrąglamy do 2 miejsc
                val weightRecord = readLatestRecord(WeightRecord::class, 1825)
                val weight = weightRecord?.weight?.inKilograms?.let { (it * 100.0).roundToInt() / 100.0 }
                Log.d("HealthDataSync", "Wynik Waga: $weight kg")

                // 2. Wzrost (Ostatnie 40 lat) - zaokrąglamy do 1 miejsca
                val heightRecord = readLatestRecord(HeightRecord::class, 15000)
                val height = heightRecord?.height?.inMeters?.times(100)?.let { (it * 10.0).roundToInt() / 10.0 }
                Log.d("HealthDataSync", "Wynik Wzrost: $height cm")

                // 3. Tętno spoczynkowe (Ostatnie 2 lata)
                val restingHRRecord = readLatestRecord(RestingHeartRateRecord::class, 730)
                val restingHR = restingHRRecord?.beatsPerMinute?.toInt()
                Log.d("HealthDataSync", "Wynik Tętno spocz.: $restingHR bpm")

                // 4. Tętno maksymalne (Agregacja z treningów)
                val maxHR = readMaxHeartRate()
                Log.d("HealthDataSync", "Wynik Tętno max: $maxHR bpm")

                // 5. VO2 Max (Ostatnie 10 lat) - zaokrąglamy do 2 miejsc
                val vo2maxRecord = readLatestRecord(Vo2MaxRecord::class, 3650)
                val vo2max = vo2maxRecord?.vo2MillilitersPerMinuteKilogram?.let { (it * 100.0).roundToInt() / 100.0 }
                Log.d("HealthDataSync", "Wynik VO2 Max: $vo2max")

                HealthDataSyncResult.Success(
                    sex = null, 
                    age = null,
                    weightKg = weight,
                    heightCm = height,
                    restingHeartRate = restingHR,
                    maxHeartRate = maxHR,
                    vo2max = vo2max
                )
            } catch (e: SecurityException) {
                Log.e("HealthDataSync", "Brak uprawnień do Health Connect", e)
                HealthDataSyncResult.PermissionDenied
            } catch (e: Exception) {
                Log.e("HealthDataSync", "Błąd synchronizacji", e)
                HealthDataSyncResult.Error(e.message ?: "Wystąpił błąd")
            }
        }
    }

    private suspend fun readMaxHeartRate(): Int? {
        return try {
            val endTime = Instant.now().plus(30, ChronoUnit.DAYS)
            val startTime = endTime.minus(365, ChronoUnit.DAYS)
            val response = healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(HeartRateRecord.BPM_MAX),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response[HeartRateRecord.BPM_MAX]?.toInt()
        } catch (e: Exception) {
            Log.w("HealthDataSync", "Błąd agregacji HR Max: ${e.message}")
            null
        }
    }

    private suspend fun <T : Record> readLatestRecord(recordType: KClass<T>, lookbackDays: Long): T? {
        return try {
            val endTime = Instant.now().plus(30, ChronoUnit.DAYS)
            val startTime = endTime.minus(lookbackDays + 30, ChronoUnit.DAYS)
            
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = recordType,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    ascendingOrder = false,
                    pageSize = 10 // Pobieramy więcej, aby ręcznie wybrać najnowszy
                )
            )
            
            val records = response.records
            if (records.isEmpty()) return null
            
            // Sortujemy po czasie pomiaru
            records.maxByOrNull { record ->
                when (record) {
                    is WeightRecord -> record.time
                    is HeightRecord -> record.time
                    is RestingHeartRateRecord -> record.time
                    is Vo2MaxRecord -> record.time
                    else -> Instant.EPOCH
                }
            } as? T
        } catch (e: Exception) {
            Log.w("HealthDataSync", "Błąd odczytu ${recordType.simpleName}: ${e.message}")
            null
        }
    }
}
