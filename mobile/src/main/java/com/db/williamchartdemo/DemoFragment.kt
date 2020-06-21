package com.db.williamchartdemo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.db.williamchart.ExperimentalFeature
import com.db.williamchart.pointtooltip.PointTooltip
import kotlinx.android.synthetic.main.demo_fragment.*

class DemoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.demo_fragment, container, false)

    @OptIn(ExperimentalFeature::class)
    override fun onViewCreated(view: View, saveInstanceState: Bundle?) {

        /**
         * Line Chart
         */
        lineChart.gradientFillColors =
            intArrayOf(
                Color.parseColor("#81FFFFFF"),
                Color.TRANSPARENT
            )
        lineChart.animation.duration = animationDuration
        lineChart.tooltip = PointTooltip()
        lineChart.onDataPointTouchListener = { index, _, _ ->
            lineChartValue.text =
                lineSet.toList()[index]
                    .second
                    .toString()
        }
        lineChart.animate(lineSet)

        /**
         * Bar Chart
         */
        barChart.animation.duration = animationDuration
        barChart.animate(barSet)

        /**
         * Donut Chart
         */
        donutChart.donutColors = intArrayOf(
            Color.parseColor("#8DFFFFFF"),
            Color.parseColor("#9EFFFFFF"),
            Color.parseColor("#FFFFFF")
        )
        donutChart.animation.duration = animationDuration
        donutChart.animate(donutSet)

        /**
         * Horizontal Bar Chart
         */
        horizontalBarChart.animation.duration = animationDuration
        horizontalBarChart.animate(horizontalBarSet)
    }

    companion object {
        private val lineSet = linkedMapOf(
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

        private val barSet = linkedMapOf(
            "JAN" to 4F,
            "FEB" to 7F,
            "MAR" to 2F,
            "MAY" to 2.3F,
            "APR" to 5F,
            "JUN" to 4F
        )

        private val horizontalBarSet = linkedMapOf(
            "PORRO" to 5F,
            "FUSCE" to 6.4F,
            "EGET" to 3F
        )

        private val donutSet = listOf(
            20f,
            80f,
            100f
        )

        private const val animationDuration = 1000L
    }
}
