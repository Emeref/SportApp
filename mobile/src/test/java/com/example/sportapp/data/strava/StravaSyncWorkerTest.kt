package com.example.sportapp.data.strava

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.example.sportapp.data.GpxGenerator
import com.example.sportapp.data.db.SyncMetadataDao
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.strava.api.StravaUploadApi
import com.example.sportapp.data.strava.api.StravaUploadResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
class StravaSyncWorkerTest {
    private lateinit var context: Context
    private val workoutDao = mockk<WorkoutDao>()
    private val syncMetadataDao = mockk<SyncMetadataDao>()
    private val stravaUploadApi = mockk<StravaUploadApi>()
    private val stravaStorage = mockk<StravaStorage>()

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `doWork returns success when upload is successful`() = runBlocking {
        val workoutId = 1L
        val workout = WorkoutEntity(
            id = workoutId,
            activityName = "Running",
            baseType = "run",
            startTime = System.currentTimeMillis(),
            durationFormatted = "01:00:00",
            durationSeconds = 3600
        )

        coEvery { workoutDao.getWorkoutById(workoutId) } returns workout
        coEvery { workoutDao.getPointsForWorkout(workoutId) } returns emptyList()
        coEvery { stravaUploadApi.uploadActivity(any(), any(), any(), any(), any()) } returns Response.success(
            StravaUploadResponse(id = 123L, status = "ready", error = null)
        )
        coEvery { syncMetadataDao.getByLocalId(workoutId, "EXERCISE") } returns null
        coEvery { syncMetadataDao.insert(any()) } returns Unit

        val worker = TestListenableWorkerBuilder<StravaSyncWorker>(context)
            .setInputData(workDataOf(StravaSyncWorker.EXTRA_WORKOUT_ID to workoutId))
            .setWorkerFactory(object : androidx.work.WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: androidx.work.WorkerParameters
                ): ListenableWorker? {
                    return StravaSyncWorker(
                        appContext, 
                        workerParameters, 
                        workoutDao, 
                        syncMetadataDao, 
                        stravaUploadApi, 
                        stravaStorage
                    )
                }
            })
            .build()

        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.success(), result)
    }
}
