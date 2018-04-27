package com.db.williamchart

import android.graphics.Paint

class Painter(val paint: Paint = Paint()) {

    init {
        paint.textAlign = Paint.Align.CENTER
    }

    fun measureLabel(text: String, textSize: Float): Float{
        paint.textSize = textSize
        return paint.measureText(text)
    }

    fun prepare(textSize: Float = 15F,
                color: Int = -0x1000000,
                style: Paint.Style = Paint.Style.FILL,
                strokeWidth: Float = 4F): Paint {
        paint.textSize = textSize
        paint.color = color
        paint.style = style
        paint.strokeWidth = strokeWidth
        return paint
    }
}