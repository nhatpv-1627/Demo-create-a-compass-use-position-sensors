package com.vannhat.sensordemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class DirectionPoint @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var circlePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var pointsPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var directionViewPath = Path()
    var degree: Float = 0f

    init {
        circlePaint.apply {
            color = Color.RED
            strokeWidth = 10f
            style = Paint.Style.STROKE
        }

        textPaint.apply {
            color = Color.BLACK
            strokeWidth = 5f
            style = Paint.Style.STROKE
            textSize = 55f
        }

        pointsPaint.apply {
            color = Color.BLACK
            strokeWidth = 5f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.SQUARE
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val cx = (this.width / 2).toFloat()
        val cy = (this.height / 2).toFloat()
        val radius =
            if (width <= height) (width / 2 * 0.8).toFloat() else (height / 2 * 0.8).toFloat()
        val northRad = radius - 150

        canvas?.drawCircle(cx, cy, radius, circlePaint)
        canvas?.drawCircle(cx, cy, 2f, circlePaint)

        directionViewPath.apply {
            moveTo(cx, cy - radius + 30)
            lineTo(cx - 20, cy - radius + 80)
            lineTo(cx, cy - radius + 60)
            lineTo(cx + 20, cy - radius + 80)
            close()
        }
        canvas?.drawPath(directionViewPath, pointsPaint)

        var norX = 0f
        var norY = 0f
        when {
            degree == 0f -> {
                norX = cx
                norY = cy - northRad
            }
            degree > 0 && degree < Math.PI / 2 -> {
                norX = cx - sin(degree) * northRad
                norY = cx - cos(degree) * northRad
            }
            degree == (Math.PI / 2).toFloat() -> {
                norX = cx - northRad
                norY = cy
            }
            degree > Math.PI / 2 && degree < Math.PI -> {
                norX = cx - cos(degree - (Math.PI / 2).toFloat()) * northRad
                norY = cy + sin(degree - (Math.PI / 2).toFloat()) * northRad
            }
            degree == Math.PI.toFloat() -> {
                norX = cx
                norY = cy + northRad
            }
            degree < 0 && degree > -(Math.PI / 2).toFloat() -> {
                norX = cx + sin(abs(degree)) * northRad
                norY = cy - cos(abs(degree)) * northRad
            }
            degree == -(Math.PI / 2).toFloat() -> {
                norX = cx + northRad
                norY = cy
            }
            degree < -(Math.PI / 2).toFloat() && degree >= -179.9999 -> {
                norX = cx + cos(abs(degree) - (Math.PI / 2).toFloat()) * northRad
                norY = cy + sin(abs(degree) - (Math.PI / 2).toFloat()) * northRad
            }
        }

        canvas?.drawText("N", norX, norY, textPaint)
    }
}
