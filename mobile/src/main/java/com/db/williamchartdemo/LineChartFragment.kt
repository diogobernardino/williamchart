package com.db.williamchartdemo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.db.williamchart.data.BarSet
import com.db.williamchart.data.Line
import com.db.williamchart.data.Point
import com.db.williamchart.view.ChartView
import kotlinx.android.synthetic.main.linechart_frag.*

class LineChartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.linechart_frag, container, false)
    }

    override fun onViewCreated(view: View, saveInstanceState: Bundle?) {

        val lineSet = Line()
        lineSet.add(Point("label1", 938F))
        lineSet.add(Point("label2", 940F))
        lineSet.add(Point("label3", 939F))
        lineSet.smooth = true
        lineSet.gradientFillColors =
            intArrayOf(
                Color.parseColor("#FFD1F2"),
                Color.TRANSPARENT
            )
        lineSet.strokeWidth = 11F
        lineSet.color = Color.parseColor("#F971AC")

        lineChart.add(lineSet)
        lineChart.animation.duration = 10000
        lineChart.axis = ChartView.Axis.NONE

        lineChart.show()

        val barSet = BarSet()
        barSet.add(Point("label1", 400F))
        barSet.add(Point("label2", 940F))
        barSet.add(Point("label3", 200F))

        barChart.add(barSet)
        barChart.animation.duration = 10000
        barChart.axis = ChartView.Axis.X
        barChart.show()
    }
}