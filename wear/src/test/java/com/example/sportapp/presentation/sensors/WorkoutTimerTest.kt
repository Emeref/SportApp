package com.example.sportapp.presentation.sensors

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class WorkoutTimerTest {

    private fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return if (hours > 0) {
            String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format(Locale.US, "%02d:%02d", minutes, secs)
        }
    }

    @Test
    fun `formatTime returns correct string for seconds only`() {
        assertEquals("00:45", formatTime(45))
    }

    @Test
    fun `formatTime returns correct string for minutes and seconds`() {
        assertEquals("12:34", formatTime(754))
    }

    @Test
    fun `formatTime returns correct string for hours minutes and seconds`() {
        assertEquals("01:02:03", formatTime(3723))
    }

    @Test
    fun `formatTime returns correct string for exactly one hour`() {
        assertEquals("01:00:00", formatTime(3600))
    }
}
