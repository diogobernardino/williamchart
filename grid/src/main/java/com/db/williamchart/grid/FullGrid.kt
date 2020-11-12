package com.db.williamchart.grid

import android.view.LayoutInflater
import android.view.ViewGroup
import com.db.williamchart.Grid

class FullGrid : Grid {
    override fun onCreateTooltip(parentView: ViewGroup) {
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.grid_layout, parentView, true)
    }

    override fun draw() {}
}
