package com.db.williamchartdemo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.db.williamchart.data.AxisType
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

        val lineSet = linkedMapOf(
            "label1" to 5f,
            "label2" to 4.5f,
            "label3" to 4.7f,
            "label4" to 3.5f,
            "label5" to 3.6f,
            "label6" to 7.5f,
            "label7" to 7.5f,
            "label8" to 10f,
            "label9" to 5f,
            "label10" to 6.5f,
            "label11" to 3f,
            "label12" to 4f
        )

        lineChart.smooth = true
        lineChart.gradientFillColors =
            intArrayOf(
                Color.parseColor("#FFD1F2"),
                Color.TRANSPARENT
            )
        lineChart.strokeWidth = 11F
        lineChart.lineColor = Color.parseColor("#F971AC")
        lineChart.animation.duration = 10000
        lineChart.axis = AxisType.NONE
        lineChart.show(lineSet)

        val barSet = linkedMapOf(
            "JAN" to 400F,
            "FEB" to 940F,
            "MAR" to 200F,
            "MAY" to 200F,
            "APR" to 200F,
            "JUN" to 200F,
            "JUL" to 200F,
            "AUG" to 200F,
            "SEP" to 200F,
            "OCT" to 200F,
            "NOV" to 200F,
            "DEC" to 200F
        )

        barChart.animation.duration = 10000
        barChart.axis = AxisType.X
        barChart.show(barSet)

        val horizontalBarSet = linkedMapOf(
            "label1" to 400F,
            "label2" to 940F,
            "label3" to 200F
        )

        horizontalBarChart.animation.duration = 10000
        horizontalBarChart.axis = AxisType.Y
        horizontalBarChart.show(horizontalBarSet)
    }
}