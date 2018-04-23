package com.db.williamchart.data

interface ChartSet {

    val entries: MutableList<ChartEntry>
    var color: Int

    fun add(entry: ChartEntry)
}