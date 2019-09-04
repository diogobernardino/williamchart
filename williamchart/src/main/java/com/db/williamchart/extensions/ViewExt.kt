package com.db.williamchart.extensions

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat

fun View.getDrawable(drawableRes: Int): Drawable? =
    ContextCompat.getDrawable(this.context, drawableRes)