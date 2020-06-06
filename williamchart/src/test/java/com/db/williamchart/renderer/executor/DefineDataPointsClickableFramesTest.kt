package com.db.williamchart.renderer.executor

import com.db.williamchart.data.Frame
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
            innerFrame = Frame(0f, 0f, 0f, 0f),
            datapointsCoordinates = listOf(Pair(0f, 0f)),
            clickableRadius = 1
        ).first()

        // Assert
        assertEquals(-1f, firstClickableFrame.left)
        assertEquals(-1f, firstClickableFrame.top)
        assertEquals(1f, firstClickableFrame.right)
        assertEquals(1f, firstClickableFrame.bottom)
    }
}
