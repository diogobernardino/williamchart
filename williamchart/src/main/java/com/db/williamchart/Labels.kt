package com.db.williamchart

import android.graphics.Canvas
import android.graphics.Paint
import com.db.williamchart.data.Label

interface Labels {
    fun draw(canvas: Canvas, paint: Paint, xLabels: List<Label>)
}
