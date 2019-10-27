package com.db.williamchart.data

import com.db.williamchart.renderer.RendererConstants.Companion.notInitialized

data class Scale(val min: Float, val max: Float)

fun Scale.notInitialized() = max == min && min == notInitialized