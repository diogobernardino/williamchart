package com.db.williamchartdemo

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        set.add(Point("label1", 938F))
        set.add(Point("label2", 940F))
        set.add(Point("label3", 939F))
        set.smooth = false
        set.gradientFillColors = intArrayOf(Color.WHITE, Color.BLACK)

        chart.add(set)
        chart.animation.duration = 10000
        chart.render()
    }

    companion object {

        fun newInstance() = LineChartFragment()
    }
}