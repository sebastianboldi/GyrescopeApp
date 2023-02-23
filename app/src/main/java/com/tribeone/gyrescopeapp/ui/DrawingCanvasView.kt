package com.tribeone.gyrescopeapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.PathShape
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.tribeone.gyrescopeapp.R
import kotlin.math.abs
import kotlin.math.roundToInt


class DrawingCanvasView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private var showBlueLine: Boolean = false
    private var oldRoll: Float = 0f
    private var mTrapezoid: ShapeDrawable? = null
    private var mCurve: ShapeDrawable? = null
    private var mRedLine: ShapeDrawable? = null
    private var mYellowLine: ShapeDrawable? = null
    private var deciding = 0f

    private val topY = 110f
    private val bottomY = 250f

    private val topStartX = 90f
    private val topEndX = 150f
    private val bottomStartX = 50f
    private val bottomEndX = 200f

    private val path = Path()
    private val redPath = Path()
    private val yellowPath = Path()
    private val curvePath = Path()
    private var add1: Float = 0f
    private var add2: Float = 0f

    private var width = 3.0f
    private var shaderRainbow: Shader? = null
    private var shapeHeight: Float = 240f

    init {

        invalidate()

        val rainbow: IntArray = intArrayOf(Color.GREEN, Color.YELLOW, Color.RED)
        shaderRainbow = LinearGradient(
            50f, 50f, 50f, shapeHeight, rainbow,
            null, Shader.TileMode.REPEAT
        )
        val matrix = Matrix()
        shaderRainbow?.setLocalMatrix(matrix)

        path.moveTo(topStartX, topY)
        path.lineTo(topEndX, topY)
        path.lineTo(bottomEndX, bottomY)
        path.lineTo(bottomStartX, bottomY)
        path.lineTo(topStartX, topY)

        redPath.moveTo(60f, 210f)
        redPath.lineTo(80f, 210f)
        redPath.moveTo(165f, 210f)
        redPath.lineTo(185f, 210f)

        yellowPath.moveTo(80f, 150f)
        yellowPath.lineTo(95f, 150f)
        yellowPath.moveTo(150f, 150f)
        yellowPath.lineTo(165f, 150f)

        mTrapezoid = ShapeDrawable(PathShape(path, 250.0f, shapeHeight))
        mTrapezoid?.paint?.apply {
            this.style = Paint.Style.STROKE
            this.strokeWidth = width
            this.shader = shaderRainbow
        }

        mCurve = ShapeDrawable(PathShape(curvePath, 250.0f, shapeHeight))
        mCurve?.paint?.apply {
            this.style = Paint.Style.STROKE
            this.strokeWidth = 2f
            this.color = resources.getColor(R.color.darkblue)
        }

        mRedLine = ShapeDrawable(PathShape(redPath, 250.0f, shapeHeight))
        mRedLine?.paint?.apply {
            this.style = Paint.Style.STROKE
            this.strokeWidth = width
            this.color = Color.RED
        }

        mYellowLine = ShapeDrawable(PathShape(yellowPath, 250.0f, shapeHeight))
        mYellowLine?.paint?.apply {
            this.style = Paint.Style.STROKE
            this.strokeWidth = width
            this.color = Color.YELLOW
        }


    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas.apply {
            mTrapezoid?.draw(canvas!!)
            mRedLine?.draw(canvas!!)
            mYellowLine?.draw(canvas!!)
            mCurve?.draw(canvas!!)
        }

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mTrapezoid?.setBounds(0, 0, w, h)
        mYellowLine?.setBounds(0, 0, w, h)
        mRedLine?.setBounds(0, 0, w, h)
        mCurve?.setBounds(0, 0, w, h)
    }

    fun calculate(roll: Float?, pitch: Float?){

        if (abs(roll ?: (0f - oldRoll)) >= 1) {
            oldRoll = roll ?: 0f

            val roll2digits: Int = roll?.roundToInt()?:0 ///slowDownByDividing
            deciding += roll2digits.toFloat()
            var subtractingValue = 5

            showBlueLine = true
            if (deciding == 0f) {   //if deciding is zero
                subtractingValue = 0
                showBlueLine = false
            } else if (deciding >= -5 && deciding <= 5) {     //if deciding is between -5 and 5
                subtractingValue = if (deciding > 0) {
                    (deciding - 1f).roundToInt()
                } else {
                    (deciding + 1f).roundToInt()
                }
                showBlueLine = false
            } else if (deciding < 0) {  //if deciding is negative
                subtractingValue = -subtractingValue
                showBlueLine = true
            }
            add1 = deciding
            add2 = deciding - subtractingValue
            Log.e("TAG", "deciding $deciding ")
            Log.e("TAG", "add1 - add2 values: $add1 $add2 \n $subtractingValue: ")

            curvePath.reset()
            if(showBlueLine){
                curvePath.moveTo(bottomStartX, bottomY)
                curvePath.quadTo(topStartX - 6f, bottomY / 2, topStartX + add1, topY)
                curvePath.lineTo(topEndX + add2, topY)
                curvePath.quadTo(topEndX + 6f, bottomY / 2, bottomEndX, bottomY)
            }

            invalidate()
        }
    }

    fun changePathColor(objectNear: Boolean){
        if(objectNear){
            mTrapezoid?.paint?.apply {
                this.style = Paint.Style.STROKE
                this.strokeWidth = width
                this.color = Color.RED
            }
            //--
            mCurve?.paint?.apply {
                this.style = Paint.Style.STROKE
                this.strokeWidth = width
                this.color = Color.RED
            }
        }else{
            mTrapezoid?.paint?.apply {
                this.style = Paint.Style.STROKE
                this.strokeWidth = width
                this.shader = shaderRainbow
            }
            //--
            mCurve?.paint?.apply {
                this.style = Paint.Style.STROKE
                this.strokeWidth = width
                this.color = resources.getColor(R.color.darkblue)
            }
        }
        invalidate()
    }

}