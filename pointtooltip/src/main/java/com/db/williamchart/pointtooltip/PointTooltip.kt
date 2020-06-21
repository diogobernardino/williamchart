package com.db.williamchart.pointtooltip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.db.williamchart.Tooltip

class PointTooltip : Tooltip {

    var drawableRes = R.drawable.circle_point

    override fun onCreateTooltip(parentView: ViewGroup) {
        val tooltipView: View =
            LayoutInflater.from(parentView.context)
                .inflate(R.layout.point_tooltip_layout, parentView, false)
        val imageView = tooltipView.findViewById<ImageView>(R.id.tooltipImage)
        imageView.setImageResource(drawableRes)
    }

    override fun onDataPointTouch(x: Float, y: Float) {
        TODO("Not yet implemented")
    }
}
