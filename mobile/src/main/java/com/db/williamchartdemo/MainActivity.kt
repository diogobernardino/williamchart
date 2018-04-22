package com.db.williamchartdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.db.williamchart.data.Line
import com.db.williamchart.data.Point

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val entry = Point("label", 938)
        val set = Line()

        set.add(entry)
        set.color = 2
    }
}
