package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.data.LapManager
import com.example.sportapp.data.SessionData
import com.example.sportapp.data.SessionRepository
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.presentation.settings.MobileSettingsManager
import com.example.sportapp.presentation.settings.MobileSettingsState
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ActivityDetailViewModelTest {

    private val context = mockk<Context>(relaxed = true)
    private val repository = mockk<IWorkoutRepository>()
    private val sessionRepository = mockk<SessionRepository>()
    private val workoutDao = mockk<WorkoutDao>(relaxed = true)
    private val lapManager = mockk<LapManager>()
    private val mobileSettingsManager = mockk<MobileSettingsManager>()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Mocking flows to prevent NoSuchElementException and provide initial state
        every { repository.getAllDefinitions() } returns flowOf(emptyList())
        every { repository.getAllWorkouts() } returns flowOf(emptyList())
        every { mobileSettingsManager.settingsFlow } returns MutableStateFlow(MobileSettingsState())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialization loads session data correctly`() = runTest {
        val activityId = 1L
        val sessionData = SessionData(activityName = "Running", charts = emptyMap())
        coEvery { sessionRepository.getSessionData(activityId) } returns sessionData
        coEvery { workoutDao.getLapsForWorkout(activityId) } returns emptyList()
        coEvery { workoutDao.getWorkoutById(activityId) } returns null
        
        val savedStateHandle = SavedStateHandle(mapOf("activityId" to activityId.toString()))
        val viewModel = ActivityDetailViewModel(
            context, repository, sessionRepository, workoutDao, lapManager, mobileSettingsManager, savedStateHandle
        )
        
        advanceUntilIdle()
        
        viewModel.sessionData.test {
            assertEquals("Running", awaitItem()?.activityName)
        }
    }

    @Test
    fun `error in session repository updates error state`() = runTest {
        val activityId = 1L
        val errorMsg = "Database error"
        val errorData = SessionData(activityName = "", error = errorMsg)
        
        coEvery { sessionRepository.getSessionData(activityId) } returns errorData
        coEvery { workoutDao.getLapsForWorkout(activityId) } returns emptyList()
        
        val savedStateHandle = SavedStateHandle(mapOf("activityId" to activityId.toString()))
        val vm = ActivityDetailViewModel(
            context, repository, sessionRepository, workoutDao, lapManager, mobileSettingsManager, savedStateHandle
        )
        
        advanceUntilIdle()

        vm.error.test {
            assertEquals(errorMsg, awaitItem())
        }
    }

    @Test
    fun `selectLap updates selectedLap state`() = runTest {
        val activityId = 1L
        val sessionData = SessionData(activityName = "Running", charts = emptyMap())
        coEvery { sessionRepository.getSessionData(activityId) } returns sessionData
        coEvery { workoutDao.getLapsForWorkout(activityId) } returns emptyList()

        val savedStateHandle = SavedStateHandle(mapOf("activityId" to activityId.toString()))
        val viewModel = ActivityDetailViewModel(
            context, repository, sessionRepository, workoutDao, lapManager, mobileSettingsManager, savedStateHandle
        )
        
        advanceUntilIdle()
        
        val mockLap = mockk<com.example.sportapp.data.model.WorkoutLap>()
        viewModel.selectLap(mockLap)
        
        viewModel.selectedLap.test {
            assertEquals(mockLap, awaitItem())
        }
    }
}
