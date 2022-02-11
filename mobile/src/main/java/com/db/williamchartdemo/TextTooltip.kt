package com.db.williamchartdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.db.williamchart.Tooltip

internal class TextTooltip(
    private val data: List<Pair<String, Float>>
) : Tooltip {

    private lateinit var tooltipView: View

    override fun onCreateTooltip(parentView: ViewGroup) {
        tooltipView =
            LayoutInflater.from(parentView.context)
                .inflate(R.layout.text_tooltip_layout, parentView, false)
        tooltipView.visibility = View.INVISIBLE
        parentView.addView(tooltipView)
    }

    override fun onDataPointTouch(x: Float, y: Float) {}

    override fun onDataPointClick(index: Int, x: Float, y: Float) {
        val textView: TextView = tooltipView.findViewById(R.id.tooltipText)
        textView.text = data[index].second.toString()
        tooltipView.visibility = View.VISIBLE
        tooltipView.x = x - tooltipView.width / 2
        tooltipView.y = y - tooltipView.height
    }
}
