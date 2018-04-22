package com.db.williamchart.data

interface ChartSet {

    val entries: List<ChartEntry>
    var color: Int

    fun add(entry: ChartEntry)
}