package com.db.williamchartdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.db.williamchart.data.Line
import com.db.williamchart.data.Point
import com.db.williamchart.view.LineChartView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val set = Line()
        set.add(Point("label1", 938F))
        set.add(Point("label2", 940F))
        set.add(Point("label3", 939F))

        val view : LineChartView = findViewById(R.id.chart)
        view.add(set)
        view.show()
    }
}
