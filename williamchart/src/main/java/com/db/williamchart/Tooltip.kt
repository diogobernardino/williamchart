package com.db.williamchart

import android.view.ViewGroup

interface Tooltip {
    fun onCreateTooltip(parentView: ViewGroup)
    fun onDataPointTouch(x: Float, y: Float)
    fun onDataPointClick(index: Int, x: Float, y: Float)
}
