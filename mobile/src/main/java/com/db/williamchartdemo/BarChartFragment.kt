package com.db.williamchartdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.db.williamchart.data.BarSet
import com.db.williamchart.data.Point
import kotlinx.android.synthetic.main.barchart_frag.*

class BarChartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.barchart_frag, container, false)
    }

    override fun onViewCreated(view: View, saveInstanceState: Bundle?) {

        val set = BarSet()
        set.add(Point("label1", 400F))
        set.add(Point("label2", 940F))
        set.add(Point("label3", 200F))

        chart.add(set)
        chart.animation.duration = 10000
        chart.show()
    }

    companion object {

        fun newInstance() = BarChartFragment()
    }
}