package com.db.williamchartdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.db.williamchart.Tooltip

class PointTooltip : Tooltip {

    private lateinit var tooltipView: View

    override fun onCreateTooltip(parentView: ViewGroup) {
        tooltipView =
            LayoutInflater.from(parentView.context)
                .inflate(R.layout.point_tooltip_layout, parentView, false)
        tooltipView.visibility = View.INVISIBLE
        parentView.addView(tooltipView)
    }

    override fun onDataPointTouch(x: Float, y: Float) {
        tooltipView.visibility = View.VISIBLE
        tooltipView.x = x - tooltipView.width / 2
        tooltipView.y = y - tooltipView.height / 2
    }

    override fun onDataPointClick(x: Float, y: Float) {}
}
