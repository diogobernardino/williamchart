package com.db.williamchartdemo

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_my_portfolio.*

class MyPortfolioFragment : Fragment(R.layout.fragment_my_portfolio) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            this.requireActivity().window.statusBarColor = Color.parseColor("#331A39")
        }

        myPortfolioDonutChart.donutColors = intArrayOf(Color.parseColor("#FD9A70"))
        myPortfolioDonutChart.show(donutChartData)

        myPortfolioLineChart.tooltip = PointTooltip()
        myPortfolioLineChart.show(lineChartData)

        myPortfolioBarChart.show(barChartData)

        myPortfolioDaysButton.isActivated = true
    }

    companion object {
        private val donutChartData = listOf(40f)

        private val lineChartData = linkedMapOf(
            "1" to 4F,
            "2" to 8F,
            "3" to 9F,
            "4" to 10F,
            "5" to 11F,
            "6" to 15F
        )

        private val barChartData = linkedMapOf(
            "label1" to 5f,
            "label2" to 4.5f,
            "label3" to 4.7f,
            "label4" to 3.5f,
            "label5" to 3.6f,
            "label6" to 7.5f,
            "label7" to 7.5f,
            "label8" to 8f,
            "label9" to 5f,
            "label10" to 6.5f,
            "label11" to 3f,
            "label12" to 4f
        )
    }
}
