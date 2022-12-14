package com.github.yeeun_yun97.toy.paintcanvas

import android.content.Intent
import android.content.res.Resources
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.slider.Slider

class ResizeActivity : AppCompatActivity() {
    private lateinit var paintTool: PaintCanvasTool
    private lateinit var canvas: Canvas

    // colors
    private val backgroundColor = Color.WHITE

    // views
    private lateinit var imageView: ImageView
    private lateinit var refreshButton: Button
    private lateinit var nextButton: Button
    private lateinit var slider: Slider

    // attrs
    private val originImage = Rect(100, 100, 100, 100, 1F)
    private var image: Rect = originImage.copy()
    private lateinit var bitmap: Bitmap
    private var pivot: Point = Point(150, 150)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resize)

        this.imageView = findViewById(R.id.canvas)

        val bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
        this.canvas = Canvas(bitmap)

        this.paintTool = PaintCanvasTool(
            Resources.getSystem().displayMetrics.widthPixels,
            Resources.getSystem().displayMetrics.heightPixels,
            0, 80, 300, 300
        )
        this.bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image)

        this.imageView.setImageBitmap(bitmap)
        this.nextButton = findViewById(R.id.nextButton)
        this.refreshButton = findViewById(R.id.refreshButton)
        this.slider = findViewById(R.id.slider)

        slider.addOnChangeListener(Slider.OnChangeListener { slider, value, fromUser ->
            resizeImage(value)
        })
        refreshButton.setOnClickListener { cancelResizing() }
        nextButton.setOnClickListener { intentNext() }

        refresh()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event is MotionEvent) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                val point = paintTool.screenToCanvas(Point(event.x.toInt(), event.y.toInt()))
                if (point.x != -1 && point.y != -1) {
                    this.pivot = point
                    refresh()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    // ??? ????????? ???????????? ????????? ????????? ????????? ????????? ??????
    private fun refresh() {
        paintBackground()
        drawImage()
        drawPivot()
        imageView.invalidate()
    }

    // ????????? ????????? ????????? ??????
    private fun drawImage() {
        paintImage(
            bitmap,
            image.x.toFloat(),
            image.y.toFloat(),
            image.x + (image.scale * image.width),
            image.y + (image.scale * image.width)
        )
    }

    // ?????? ????????? ????????? ??????
    private fun drawPivot() {
        paintCircle(pivot.x.toFloat(), pivot.y.toFloat(), Color.BLACK, 5F)
    }

    // ?????? ????????? ??????
    /** ????????? ?????????.*/
    private fun paintBackground() {
        canvas.drawColor(backgroundColor)
    }

    /** ????????? ???????????? ?????????.*/
    private fun paintImage(bitmap: Bitmap, x1: Float, y1: Float, x2: Float, y2: Float) {
        val rectF = RectF(x1, y1, x2, y2)
        canvas.drawBitmap(bitmap, null, rectF, Paint())
    }

    /** ?????? ?????????.*/
    private fun paintCircle(x: Float, y: Float, color: Int, r: Float = 5F) {
        val paint = Paint()
        paint.setColor(color)
        val rect = RectF()
        rect.set(x - r, y - r, x + r, y + r)
        canvas.drawArc(rect, 0F, 360F, true, paint)
    }

    // ????????? ????????? ??????
    /** ?????? ??????????????? ????????????.*/
    private fun intentNext() {
        val intent = Intent(this, MoveActivity::class.java)
        startActivity(intent)
    }

    /** ????????? ????????? ?????? ?????? ?????? ?????? ????????? ????????????.*/
    private fun cancelResizing() {
        this.image = originImage.copy()
        slider.setValue(1.0F)
        refresh()

    }

    /** ????????? ???????????? ????????????.*/
    private fun resizeImage(value: Float) {
        val resized = paintTool.resize(image, pivot, value)
        if (resized is Rect) {
            this.image = resized
            refresh()
        } else {
            Toast.makeText(this, "???????????? ?????? ?????? ???????????????", Toast.LENGTH_SHORT).show()
        }
    }


}