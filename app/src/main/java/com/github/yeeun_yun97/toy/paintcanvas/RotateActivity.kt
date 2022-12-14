package com.github.yeeun_yun97.toy.paintcanvas

import android.content.res.Resources
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.slider.Slider

class RotateActivity : AppCompatActivity() {
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
    private val originPivot = Point(150, 150)
    private var pivot: Point = originPivot
    private var rotateDegree: Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rotate)

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
            rotateImage(value)
        })
        refreshButton.setOnClickListener { cancelRotating() }
        nextButton.isEnabled = false

        refresh()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event is MotionEvent) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                val point = paintTool.screenToCanvas(Point(event.x.toInt(), event.y.toInt()))
                if (point.x != -1 && point.y != -1) {
                    this.pivot = point
                    this.rotateDegree = 0F
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
        paintRotatedImage(
            bitmap,
            image,
            rotateDegree
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

    private fun paintRotatedImage(bitmap: Bitmap, image: Rect, degree: Float) {
        val matrix = Matrix()
        matrix.preTranslate(100F, 100F)
        matrix.setRotate(degree, pivot.x.toFloat(), pivot.y.toFloat())

        matrix.preScale(1.0F, 1.0F)

        val scaledBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, image.width, image.height, null, true
        )
        canvas.drawBitmap(scaledBitmap, matrix, Paint())
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
    /** ???????????? ???????????????.*/
    private fun rotateImage(value: Float) {
        this.rotateDegree = value
        refresh()
    }

    /** ????????? ?????? ?????? ?????? ?????? ????????? ????????????.*/
    private fun cancelRotating() {
        this.image = originImage.copy()
        this.rotateDegree = 0F
        slider.value = 0F
        this.pivot = originPivot
        refresh()
    }


}