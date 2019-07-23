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
        lineSet.add(Point("label1", 5f))
        lineSet.add(Point("label2", 4.5f))
        lineSet.add(Point("label3", 4.7f))
        lineSet.add(Point("label4", 3.5f))
        lineSet.add(Point("label5", 3.6f))
        lineSet.add(Point("label6", 7.5f))
        lineSet.add(Point("label7", 7.5f))
        lineSet.add(Point("label8", 10f))
        lineSet.add(Point("label9", 5f))
        lineSet.add(Point("label10", 6.5f))
        lineSet.add(Point("label11", 3f))
        lineSet.add(Point("label12", 4f))
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