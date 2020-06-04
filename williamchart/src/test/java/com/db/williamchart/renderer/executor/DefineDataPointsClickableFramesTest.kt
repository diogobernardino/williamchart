package com.db.williamchart.renderer.executor

import org.junit.Assert.assertEquals
import org.junit.Test

class DefineDataPointsClickableFramesTest {

    private val defineDataPointsClickableFrames by lazy {
        DefineDataPointsClickableFrames()
    }

    @Test
    fun `clickable areas have the exact radius`() {
        // Act
        val firstClickableFrame = defineDataPointsClickableFrames(
            xCoordinates = listOf(0f),
            yCoordinates = listOf(0f),
            clickableRadius = 1
        ).first()

        // Assert
        assertEquals(-1f, firstClickableFrame.left)
        assertEquals(-1f, firstClickableFrame.top)
        assertEquals(1f, firstClickableFrame.right)
        assertEquals(1f, firstClickableFrame.bottom)
    }
}
