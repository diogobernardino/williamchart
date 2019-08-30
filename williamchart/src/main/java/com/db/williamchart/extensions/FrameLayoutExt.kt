package com.db.williamchart.extensions

import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.FrameLayout

fun FrameLayout.obtainStyledAttributes(attrsSet: AttributeSet?, attrsId: IntArray): TypedArray {
    return context.theme.obtainStyledAttributes(attrsSet, attrsId, 0, 0)
}