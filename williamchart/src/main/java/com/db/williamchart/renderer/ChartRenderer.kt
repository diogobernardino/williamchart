package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.animation.VerticalAnimation
import com.db.williamchart.data.ChartEntry
import com.db.williamchart.data.ChartLabel
import com.db.williamchart.data.ChartSet
import java.lang.IllegalArgumentException


class ChartRenderer(private val view: ChartContract.View,
                    private val painter: Painter,
                    private var animation: ChartAnimation) : ChartContract.Renderer{

    private var data : ChartSet? = null

    private var xLabels : List<ChartLabel> = arrayListOf()

    private var innerFrameLeft : Float = 0F

    private var innerFrameTop : Float = 0F

    private var innerFrameRight : Float = 0F

    private var innerFrameBottom : Float = 0F

    private var isProcessed : Boolean = false

    val labelSize : Float = 60F

    var hasLabels : Boolean = true

    override fun preDraw(width: Int,
                height: Int,
                paddingLeft: Int,
                paddingTop: Int,
                paddingRight: Int,
                paddingBottom: Int) : Boolean{

        if (isProcessed) return true  // Data already processed, proceed with drawing

        if (data == null) return false  // No data, cancel drawing
        if (data!!.entries.size <= 1) throw IllegalArgumentException("A chart needs more than one entry.")

        val frameLeft = paddingLeft.toFloat()
        val frameTop = paddingTop.toFloat()
        val frameRight = width - paddingRight.toFloat()
        val frameBottom = height - paddingBottom.toFloat()

        innerFrameLeft = frameLeft
        innerFrameTop = frameTop
        innerFrameRight = frameRight
        innerFrameBottom = if (hasLabels) frameBottom - painter.measureLabelHeight(labelSize) else frameBottom

        processLabels(frameLeft, frameTop, frameRight, frameBottom)

        processEntries(frameTop, innerFrameBottom)

        isProcessed = true

        animation.animateFrom(innerFrameBottom, data!!.entries) { view.postInvalidate() }

        return false
    }

    override fun draw() {

        if (data == null) return

        if (hasLabels) view.drawLabels(xLabels)

        view.drawData(innerFrameLeft, innerFrameTop, innerFrameRight, innerFrameBottom, data!!)
    }

    override fun show() {
        view.postInvalidate()
    }

    override fun animate() {
        animation = VerticalAnimation()
        view.postInvalidate()
    }

    override fun add(set: ChartSet) {
        data = set
    }

    private fun processLabels(
            frameLeft: Float,
            frameTop: Float,
            frameRight: Float,
            frameBottom: Float) {

        val firstLabelCenter = painter.measureLabelWidth(data!!.entries.first().label, labelSize)
        val lastLabelCenter = painter.measureLabelWidth(data!!.entries.last().label, labelSize)
        val stepX = (frameRight - frameLeft - firstLabelCenter - lastLabelCenter)/
                (data!!.entries.size - 1)

        xLabels = data!!.entries.mapIndexed{index, entry ->
            ChartLabel(entry.label,
                    frameLeft + firstLabelCenter + stepX * index,
                    frameBottom) }
    }

    private fun processEntries(
            frameTop: Float,
            frameBottom: Float) {

        val (min, max) = findBorderValues(data!!.entries)

        data!!.entries.forEachIndexed { index, entry ->
            entry.x = xLabels[index].x
            entry.y = frameBottom - ((frameBottom - frameTop) * (entry.value - min) / (max - min))
        }
    }

    private fun findBorderValues(entries: MutableList<ChartEntry>): FloatArray {

        var max = Integer.MIN_VALUE.toFloat()
        var min = Integer.MAX_VALUE.toFloat()

        entries.forEach {
            if (it.value >= max) max = it.value
            if (it.value <= min) min = it.value
        }

        if (min == max) max += 1f  // All given values are equal

        return floatArrayOf(min, max)
    }

}