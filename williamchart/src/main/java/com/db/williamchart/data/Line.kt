package com.db.williamchart.data

import android.support.annotation.Size

data class Line(
        override val entries: MutableList<ChartEntry> = mutableListOf(),
        override var color: Int = -0x1000000, // Black as default
        var smooth: Boolean = false,
        var strokeWidth: Float = 4F,
        var fillColor: Int = 0,
        @Size(min = 2, max = 2) var gradientFillColors: IntArray = intArrayOf()) : ChartSet {

    override fun add(entry: ChartEntry) {
        entries.add(entry)
    }

    fun hasFill() : Boolean {
        return fillColor != 0
    }

    fun hasGradientFillColors() : Boolean {
        return gradientFillColors.isNotEmpty()
    }
}