package com.db.williamchart

import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface

/**
 * The Paint class holds the paint object used to draw charts
 * and provides methods to speed up its customization before drawing.
 */
class Painter(val paint: Paint = Paint()) {

    init {
        paint.textAlign = Paint.Align.CENTER
    }

    /**
     * Measure text width given a certain font size.
     *
     * @param text text to be measured.
     * @param textSize size used in font.
     * @return width of the text.
     */
    fun measureLabelWidth(text: String, textSize: Float): Float {
        paint.textSize = textSize
        return paint.measureText(text)
    }

    /**
     * Measure the maximum text height for a given font size.
     *
     * @param textSize size used in font.
     * @return height of the text.
     */
    fun measureLabelHeight(textSize: Float): Float {
        paint.textSize = textSize
        return paint.descent() - paint.ascent()
    }

    fun measureLabelAscent(textSize: Float): Float {
        paint.textSize = textSize
        return paint.ascent()
    }

    fun measureLabelDescent(textSize: Float): Float {
        paint.textSize = textSize
        return paint.descent()
    }

    /**
     * Prepares a Paint object already configured to be used.
     *
     * @param textSize new font size to set in the paint.
     * @param color new color (including alpha) to set in the paint.
     * @param style new style to set in the paint.
     * @param strokeWidth new stroke width, used when style is Stroke or StrokeAndFill.
     * @param shader May be null. New shader to be set in the paint.
     * @param typeface May be null. New Typeface to be set in the paint.
     * @return Configured Paint object.
     */
    fun prepare(
        textSize: Float = 15F,
        color: Int = -0x1000000,
        style: Paint.Style = Paint.Style.FILL,
        strokeWidth: Float = 4F,
        shader: Shader? = null,
        font: Typeface? = null
    ): Paint {
        paint.textSize = textSize
        paint.color = color
        paint.style = style
        paint.strokeWidth = strokeWidth
        paint.shader = shader
        paint.typeface = font

        return paint
    }
}