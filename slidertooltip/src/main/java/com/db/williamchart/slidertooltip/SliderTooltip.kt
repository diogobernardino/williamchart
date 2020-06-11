package com.db.williamchart.slidertooltip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.db.williamchart.Tooltip

class SliderTooltip : Tooltip {

    private lateinit var tooltipView: View

    override fun onCreateTooltip(parentView: ViewGroup) {
        tooltipView =
            LayoutInflater.from(parentView.context)
                .inflate(R.layout.tooltip_layout, parentView, false)
    }

    override fun onDataPointTouch(x: Float, y: Float) {
        tooltipView.x = x
    }
}
