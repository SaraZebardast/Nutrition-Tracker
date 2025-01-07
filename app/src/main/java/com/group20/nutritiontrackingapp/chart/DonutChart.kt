package com.group20.nutritiontrackingapp.chart

import android.view.View

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.group20.nutritiontrackingapp.R

class DonutChart(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    private var colors = listOf(
        ContextCompat.getColor(context, R.color.Pastel_Purple),
        ContextCompat.getColor(context, R.color.Pastel_Red),
        ContextCompat.getColor(context, R.color.Pastel_Blue)
    )

    companion object {
        private var data = listOf(33f, 33f, 33f)

        fun updateData(carb: Float, pro: Float, fat: Float) {
            data = listOf(carb, pro, fat)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val total = data.sum()
        var startAngle = 0f

        // Çemberin boyutlarını ayarla
        val rect = RectF(100f, 100f, width - 100f, height - 100f)
        val strokeWidth = 100f
        paint.strokeWidth = strokeWidth

        for (i in data.indices) {
            paint.color = colors[i]
            val sweepAngle = (data[i] / total) * 360
            canvas.drawArc(rect, startAngle, sweepAngle, false, paint)
            startAngle += sweepAngle
        }
    }
}
