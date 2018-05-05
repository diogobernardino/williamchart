package com.db.williamchart

import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface

class Painter(val paint: Paint = Paint()) {

    init {
        paint.textAlign = Paint.Align.CENTER
    }

    fun measureLabelWidth(text: String, textSize: Float): Float{
        paint.textSize = textSize
        return paint.measureText(text)
    }

    fun measureLabelHeight(textSize: Float) : Float{
        paint.textSize = textSize
        return paint.descent() - paint.ascent()
    }

    fun prepare(textSize: Float = 15F,
                color: Int = -0x1000000,
                style: Paint.Style = Paint.Style.FILL,
                strokeWidth: Float = 4F,
                shader: Shader? = null): Paint {
        paint.textSize = textSize
        paint.color = color
        paint.style = style
        paint.strokeWidth = strokeWidth
        paint.shader = shader

        return paint
    }
}