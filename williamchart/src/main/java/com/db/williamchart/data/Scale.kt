package com.db.williamchart.data

import com.db.williamchart.renderer.RendererConstants.Companion.notInitialized

data class Scale(val min: Float, val max: Float) {
    val size = max - min
}

fun Scale.notInitialized() = max == min && min == notInitialized
