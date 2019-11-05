package com.db.williamchartdemo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.db.williamchart.data.AxisType
import com.db.williamchart.data.Scale
import com.db.williamchart.view.ImplementsAlphaChart
import kotlinx.android.synthetic.main.demo_fragment.*
import kotlin.math.roundToInt

@ImplementsAlphaChart
class DemoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.demo_fragment, container, false)

    override fun onViewCreated(view: View, saveInstanceState: Bundle?) {

        lineChart.gradientFillColors =
            intArrayOf(
                Color.parseColor("#81FFFFFF"),
                Color.TRANSPARENT
            )
        lineChart.axis = AxisType.Y
        lineChart.scale = Scale(1f, 12f)
        lineChart.yScaleLabel = { "${it.roundToInt()}" }
        lineChart.animation.duration = 1000
        lineChart.animate(lineSet)

        barChart.animation.duration = 1000
        barChart.animate(barSet)

        donutChart.animation.duration = 1000
        donutChart.animate(130f)

        horizontalBarChart.animation.duration = 1000
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
    }
}
