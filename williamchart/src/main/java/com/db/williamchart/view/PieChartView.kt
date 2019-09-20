package com.db.williamchart.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.graphics.toRectF
import com.db.williamchart.ChartContract
import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Label
import com.db.williamchart.data.toRect

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ChartView(context, attrs, defStyleAttr), ChartContract.View {

    @Suppress("MemberVisibilityCanBePrivate")
    var innerCircleRadius = 0f

    override fun drawLabels(xLabels: List<Label>) {
    }

    override fun drawData(innerFrame: Frame, entries: List<DataPoint>) {
        canvas.drawCircle(CIRCLE_CENTER, CIRCLE_CENTER, CIRCLE_RADIUS, painter.paint)
        canvas.drawArc(innerFrame.toRect().toRectF(), START_ANGLE, CURRENT_ANGLE, false, painter.paint)
    }

    override fun drawDebugFrame(outerFrame: Frame, innerFrame: Frame, labelsFrame: List<Frame>) {
    }

    companion object {
        private const val CIRCLE_CENTER = 50f
        private const val CIRCLE_RADIUS = 20f
        private const val START_ANGLE = 90f
        private const val CURRENT_ANGLE = 40f
    }
}
