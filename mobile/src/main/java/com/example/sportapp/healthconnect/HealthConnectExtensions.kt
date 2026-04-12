package com.example.sportapp.healthconnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.request.ReadRecordsRequest
import kotlin.reflect.KClass

suspend fun <T : Record> HealthConnectClient.readAllRecords(
    recordType: KClass<T>,
    request: ReadRecordsRequest<T>
): List<T> {
    val allRecords = mutableListOf<T>()
    var pageToken: String? = null
    do {
        val response = this.readRecords(
            ReadRecordsRequest(
                recordType = recordType,
                timeRangeFilter = request.timeRangeFilter,
                dataOriginFilter = request.dataOriginFilter,
                ascendingOrder = request.ascendingOrder,
                pageSize = request.pageSize,
                pageToken = pageToken
            )
        )
        allRecords.addAll(response.records)
        pageToken = response.pageToken
    } while (pageToken != null)
    return allRecords
}
