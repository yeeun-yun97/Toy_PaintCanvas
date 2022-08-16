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

        this.slider.addOnChangeListener(Slider.OnChangeListener { slider, value, fromUser -> setRadius(value) })
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
                drawEraser(point.x.toFloat(), point.y.toFloat(), eraserRadius)
            }
        }
        return super.onTouchEvent(event)
    }

    // 그린것 위에 다시 선분을 그려주고, 다시 그릴 것을 알리는 함수
    private fun refresh() {
        drawBaseLine()
        imageView.invalidate()
    }

    // 지우개를 그리고 지울지 확인하고 표시하는 함수
    private fun drawEraser(x: Float, y: Float, r: Float) {
        paintBackground()
        paintCircle(x, y, Color.CYAN, r)
        refresh()
        val canErase = paintTool.canErase(
            Point(x.toInt() + paintTool.canvasStart, y.toInt() + paintTool.canvasTop),
            r,
            ::paintCircle
        )
        textView.setText(if (canErase) "지움" else "지우지 않음")
    }

    // 주어진 선분을 그리는 함수
    private fun drawBaseLine() {
        val line = paintTool.line
        paintLine(
            line.firstPoint.x.toFloat(),
            line.firstPoint.y.toFloat(),
            line.secondPoint.x.toFloat(),
            line.secondPoint.y.toFloat(),
        )
    }

    // 그림 그리는 함수
    /** 배경을 그린다.*/
    private fun paintBackground() {
        canvas.drawColor(backgroundColor)
    }

    /** 원을 그린다.*/
    private fun paintCircle(x: Float, y: Float, color: Int, r: Float = 5F) {
        val paint = Paint()
        paint.setColor(color)
        val rect = RectF()
        rect.set(x - r, y - r, x + r, y + r)
        canvas.drawArc(rect, 0F, 360F, true, paint)
    }

    /** 선분을 그린다.*/
    private fun paintLine(
        x1: Float, y1: Float,
        x2: Float, y2: Float,
        color: Int = foregroundColor,
    ) {
        val paint = Paint()
        paint.setColor(color)
        canvas.drawLine(x1, y1, x2, y2, paint)
    }

    // 이벤트 핸들러 함수
    /** 주어진 선분을 새로 만들고, 초기화한다.*/
    private fun initRandomLine() {
        paintTool.setRandomLine()
        textView.setText("")
        paintBackground()
        refresh()
    }

    /** 다음 액티비티로 넘어간다.*/
    private fun intentNext() {
        val intent = Intent(this, ResizeActivity::class.java)
        startActivity(intent)
    }

    /** 지우개 반지름의 크기를 변경한다.*/
    private fun setRadius(value: Float) {
        this.eraserRadius = value
        paintBackground()
        refresh()
    }


}