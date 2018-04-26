package com.db.williamchart

import android.graphics.Paint

class Painter(val paint: Paint = Paint()) {

    init {
        paint.textAlign = Paint.Align.CENTER
    }

    fun measureTextCenter(text: String, textSize: Float): Float{
        paint.textSize = textSize
        return paint.measureText(text) / 2
    }

    fun prepare(textSize: Float): Paint {
        paint.textSize = textSize
        return paint
    }
}