package com.example.sportapp.presentation.workout

import app.cash.turbine.test
import com.example.sportapp.data.db.WorkoutDefinitionDao
import com.example.sportapp.data.model.WorkoutDefinition
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DynamicWorkoutViewModelTest {

    private val dao = mockk<WorkoutDefinitionDao>()
    private lateinit var viewModel: DynamicWorkoutViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DynamicWorkoutViewModel(dao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDefinition updates state when definition exists`() = runTest {
        // Given
        val definitionId = 1L
        val mockDefinition = WorkoutDefinition(
            id = definitionId,
            name = "Test Workout",
            iconName = "test_icon",
            baseType = "Running",
            sensors = emptyList()
        )
        coEvery { dao.getDefinitionById(definitionId) } returns mockDefinition

        // When
        viewModel.loadDefinition(definitionId)
        
        advanceUntilIdle()

        // Then
        viewModel.definition.test {
            val item = awaitItem()
            assertEquals("Test Workout", item?.name)
        }
    }

    @Test
    fun `loadDefinition sets null when definition does not exist`() = runTest {
        // Given
        val definitionId = 99L
        coEvery { dao.getDefinitionById(definitionId) } returns null

        // When
        viewModel.loadDefinition(definitionId)
        
        advanceUntilIdle()

        // Then
        viewModel.definition.test {
            val item = awaitItem()
            assertEquals(null, item)
        }
    }
}
