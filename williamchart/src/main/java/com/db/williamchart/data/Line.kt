package com.db.williamchart.data

data class Line(
        override val entries: List<ChartEntry> = arrayListOf(),
        override var color: Int = 0,
        val smooth: Boolean = false) : ChartSet {

    override fun add(entry: ChartEntry) {
        // TODO("not implemented")
    }
}