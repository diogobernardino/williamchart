package com.db.williamchart.data

import android.support.annotation.ColorInt

data class BarSet(
    override val entries: MutableList<ChartEntry> = mutableListOf(),
    @ColorInt override var color: Int = -0x1000000,
    var radius: Float = 0F
) : ChartSet {

    override fun add(entry: ChartEntry) {
        entries.add(entry)
    }
}