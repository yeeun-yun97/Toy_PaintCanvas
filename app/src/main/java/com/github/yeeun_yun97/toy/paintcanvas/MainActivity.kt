package com.github.yeeun_yun97.toy.paintcanvas

import android.content.Intent
import android.content.res.Resources
import android.graphics.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider

class MainActivity : AppCompatActivity() {
    private lateinit var paintTool: PaintCanvasTool
    private lateinit var canvas: Canvas

    // colors
    private val backgroundColor = Color.WHITE
    private val foregroundColor = Color.BLACK

    // views
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var refreshButton: Button
    private lateinit var nextButton: Button
    private lateinit var slider: Slider

    // attrs
    private var eraserRadius: Float = 20F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.imageView = findViewById(R.id.imageView)
        this.nextButton = findViewById(R.id.nextButton)
        this.refreshButton = findViewById(R.id.refreshButton)
        this.textView = findViewById(R.id.resultTextView)
        this.slider = findViewById(R.id.slider)

        val bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
        this.canvas = Canvas(bitmap)

        this.paintTool = PaintCanvasTool(
            Resources.getSystem().displayMetrics.widthPixels,
            Resources.getSystem().displayMetrics.heightPixels,
            0, 80, 300, 300
        )

        this.slider.addOnChangeListener(Slider.OnChangeListener { slider, value, fromUser ->
            setRadius(
                value
            )
        })
        this.imageView.setImageBitmap(bitmap)
        this.nextButton.setOnClickListener { intentNext() }
        this.refreshButton.setOnClickListener { initRandomLine() }

        paintBackground()
        refresh()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event is MotionEvent) {
            if (event.action == ACTION_DOWN) {
                val point = paintTool.screenToCanvas(Point(event.x.toInt(), event.y.toInt()))
                if (point.x != -1 && point.y != -1)
                    drawEraser(point.x.toFloat(), point.y.toFloat(), eraserRadius)
            }
        }
        return super.onTouchEvent(event)
    }

    // ????????? ?????? ?????? ????????? ????????????, ?????? ?????? ?????? ????????? ??????
    private fun refresh() {
        drawBaseLine()
        imageView.invalidate()
    }

    // ???????????? ????????? ????????? ???????????? ???????????? ??????
    private fun drawEraser(x: Float, y: Float, r: Float) {
        paintBackground()
        paintCircle(x, y, Color.CYAN, r)
        refresh()
        val canErase = paintTool.canErase(
            Point(x.toInt() + paintTool.canvasStart, y.toInt() + paintTool.canvasTop),
            r,
            ::paintCircle
        )
        textView.setText(if (canErase) "??????" else "????????? ??????")
    }

    // ????????? ????????? ????????? ??????
    private fun drawBaseLine() {
        val line = paintTool.line
        paintLine(
            line.firstPoint.x.toFloat(),
            line.firstPoint.y.toFloat(),
            line.secondPoint.x.toFloat(),
            line.secondPoint.y.toFloat(),
        )
    }

    // ?????? ????????? ??????
    /** ????????? ?????????.*/
    private fun paintBackground() {
        canvas.drawColor(backgroundColor)
    }

    /** ?????? ?????????.*/
    private fun paintCircle(x: Float, y: Float, color: Int, r: Float = 5F) {
        val paint = Paint()
        paint.setColor(color)
        val rect = RectF()
        rect.set(x - r, y - r, x + r, y + r)
        canvas.drawArc(rect, 0F, 360F, true, paint)
    }

    /** ????????? ?????????.*/
    private fun paintLine(
        x1: Float, y1: Float,
        x2: Float, y2: Float,
        color: Int = foregroundColor,
    ) {
        val paint = Paint()
        paint.setColor(color)
        canvas.drawLine(x1, y1, x2, y2, paint)
    }

    // ????????? ????????? ??????
    /** ????????? ????????? ?????? ?????????, ???????????????.*/
    private fun initRandomLine() {
        paintTool.setRandomLine()
        textView.setText("")
        paintBackground()
        refresh()
    }

    /** ?????? ??????????????? ????????????.*/
    private fun intentNext() {
        val intent = Intent(this, ResizeActivity::class.java)
        startActivity(intent)
    }

    /** ????????? ???????????? ????????? ????????????.*/
    private fun setRadius(value: Float) {
        this.eraserRadius = value
        paintBackground()
        refresh()
    }


}