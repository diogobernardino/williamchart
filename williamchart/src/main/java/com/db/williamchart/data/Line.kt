package com.db.williamchart.data

data class Line(
        override val entries: MutableList<ChartEntry> = mutableListOf(),
        override var color: Int = -0x1000000, // Black as default
        var smooth: Boolean = false,
        val strokeWidth: Float = 4F) : ChartSet {

    override fun add(entry: ChartEntry) {
        entries.add(entry)
    }
}