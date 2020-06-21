package com.db.williamchart.pointtooltip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.db.williamchart.ExperimentalFeature
import com.db.williamchart.Tooltip

@ExperimentalFeature
class PointTooltip : Tooltip {

    private lateinit var tooltipView: View

    @Suppress("MemberVisibilityCanBePrivate")
    var drawableRes = R.drawable.circle_point

    override fun onCreateTooltip(parentView: ViewGroup) {
        tooltipView =
            LayoutInflater.from(parentView.context)
                .inflate(R.layout.point_tooltip_layout, parentView, false)
        tooltipView.visibility = View.INVISIBLE

        val imageView: ImageView = tooltipView.findViewById(R.id.tooltipImage)
        imageView.setImageResource(drawableRes)

        parentView.addView(tooltipView)
    }

    override fun onDataPointTouch(x: Float, y: Float) {}

    override fun onDataPointClick(x: Float, y: Float) {
        tooltipView.visibility = View.VISIBLE
        tooltipView.x = x - tooltipView.width / 2
        tooltipView.y = y - tooltipView.height / 2
    }
}
