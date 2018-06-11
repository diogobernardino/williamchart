package com.db.williamchartdemo

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_linechart -> {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.contentFrame, LineChartFragment.newInstance())
                    commit()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_barchart -> {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.contentFrame, BarChartFragment.newInstance())
                    commit()
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_linechart
    }
}
