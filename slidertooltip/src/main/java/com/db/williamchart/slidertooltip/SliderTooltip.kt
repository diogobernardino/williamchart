package com.db.williamchart.slidertooltip

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.db.williamchart.ExperimentalFeature
import com.db.williamchart.Tooltip

@ExperimentalFeature
class SliderTooltip : Tooltip {

    private lateinit var tooltipView: View

    var color = Color.BLACK

    override fun onCreateTooltip(parentView: ViewGroup) {
        tooltipView =
            LayoutInflater.from(parentView.context)
                .inflate(R.layout.tooltip_layout, parentView, false)
        tooltipView.setBackgroundColor(color)
        tooltipView.visibility = View.INVISIBLE
        parentView.addView(tooltipView)
    }

    override fun onDataPointTouch(x: Float, y: Float) {
        tooltipView.visibility = View.VISIBLE
        tooltipView.x = x - tooltipView.width / 2
    }
}
