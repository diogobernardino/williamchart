package com.db.williamchartdemo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.db.williamchart.data.Line
import com.db.williamchart.data.Point
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

        val set = Line()
        set.add(Point("label1", 5f))
        set.add(Point("label2", 4.5f))
        set.add(Point("label3", 4.7f))
        set.add(Point("label4", 3.5f))
        set.add(Point("label5", 3.6f))
        set.add(Point("label6", 7.5f))
        set.add(Point("label7", 7.5f))
        set.add(Point("label8", 10f))
        set.add(Point("label9", 5f))
        set.add(Point("label10", 6.5f))
        set.add(Point("label11", 3f))
        set.add(Point("label12", 4f))
        set.smooth = true
        set.gradientFillColors = intArrayOf(Color.WHITE, Color.BLACK)

        chart.add(set)
        chart.animation.duration = 10000
        chart.show()
    }

    companion object {

        fun newInstance() = LineChartFragment()
    }
}