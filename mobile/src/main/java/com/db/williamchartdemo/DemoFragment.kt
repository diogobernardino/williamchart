package com.db.williamchartdemo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.db.williamchart.data.ChartEntry
import com.db.williamchart.view.ChartView
import kotlinx.android.synthetic.main.demo_fragment.*

class DemoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.demo_fragment, container, false)
    }

    override fun onViewCreated(view: View, saveInstanceState: Bundle?) {

        val lineSet = mutableListOf<ChartEntry>()
        lineSet.add(ChartEntry("label1", 5f))
        lineSet.add(ChartEntry("label2", 4.5f))
        lineSet.add(ChartEntry("label3", 4.7f))
        lineSet.add(ChartEntry("label4", 3.5f))
        lineSet.add(ChartEntry("label5", 3.6f))
        lineSet.add(ChartEntry("label6", 7.5f))
        lineSet.add(ChartEntry("label7", 7.5f))
        lineSet.add(ChartEntry("label8", 10f))
        lineSet.add(ChartEntry("label9", 5f))
        lineSet.add(ChartEntry("label10", 6.5f))
        lineSet.add(ChartEntry("label11", 3f))
        lineSet.add(ChartEntry("label12", 4f))

        lineChart.add(lineSet)
        lineChart.smooth = true
        lineChart.gradientFillColors =
            intArrayOf(
                Color.parseColor("#FFD1F2"),
                Color.TRANSPARENT
            )
        lineChart.strokeWidth = 11F
        lineChart.lineColor = Color.parseColor("#F971AC")
        lineChart.animation.duration = 10000
        lineChart.axis = ChartView.Axis.NONE
        lineChart.show()

        val barSet = mutableListOf<ChartEntry>()
        barSet.add(ChartEntry("label1", 400F))
        barSet.add(ChartEntry("label2", 940F))
        barSet.add(ChartEntry("label3", 200F))

        barChart.add(barSet)
        barChart.animation.duration = 10000
        barChart.axis = ChartView.Axis.X
        barChart.show()
    }
}