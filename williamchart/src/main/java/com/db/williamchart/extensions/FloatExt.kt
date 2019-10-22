package com.db.williamchart.extensions

import com.db.williamchart.data.DonutDataPoint

fun Float.toDonutDataPoint(): DonutDataPoint = DonutDataPoint(this)