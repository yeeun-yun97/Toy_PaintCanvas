package com.github.yeeun_yun97.toy.paintcanvas

import android.content.Intent
import android.content.res.Resources
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import kotlin.math.roundToInt

class MoveActivity : AppCompatActivity() {
    private lateinit var paintTool: PaintCanvasTool
    private lateinit var canvas: Canvas

    // colors
    private val backgroundColor = Color.WHITE

    // views
    private lateinit var imageView: ImageView
    private lateinit var refreshButton: Button
    private lateinit var nextButton: Button

    // attrs
    private var originImage: Rect = Rect(100, 100, 100, 100, 1F)
    private var image: Rect = originImage.copy()
    private lateinit var bitmap: Bitmap
    private var prevX: Float? = null
    private var prevY: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_move)

        this.imageView = findViewById(R.id.canvas)
        this.refreshButton = findViewById(R.id.refreshButton)
        this.nextButton = findViewById(R.id.nextButton)

        val bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
        this.canvas = Canvas(bitmap)

        this.paintTool = PaintCanvasTool(
            Resources.getSystem().displayMetrics.widthPixels,
            Resources.getSystem().displayMetrics.heightPixels,
            0, 80, 300, 300
        )
        this.bitmap = BitmapFactory.decodeResource(resources, R.drawable.image)

        this.imageView.setImageBitmap(bitmap)
        this.refreshButton.setOnClickListener { cancelMove() }
        this.nextButton.setOnClickListener { intentNext() }

        refresh()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event is MotionEvent) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val point =
                        paintTool.screenToCanvas(Point(event.x.roundToInt(), event.y.roundToInt()))
                    if (point.x != -1 && point.y != -1 &&
                        paintTool.isOnRect(
                            image,
                            point.x.toFloat(),
                            point.y.toFloat()
                        )
                    ) {
                        prevX = event.x
                        prevY = event.y
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (prevX is Float && prevY is Float) {
                        val dx = (event.x - prevX!!).roundToInt()
                        val dy = (event.y - prevY!!).roundToInt()
                        this.image = paintTool.move(image, dx, dy)!!
                        refresh()
                        prevX = event.x
                        prevY = event.y
                    }
                }
                MotionEvent.ACTION_UP -> {
                    prevX = null
                    prevY = null
                }
            }
        }
        return super.onTouchEvent(event)
    }

    // ??? ????????? ???????????? ?????????, ?????? ????????? ?????? ??????
    private fun refresh() {
        paintBackground()
        drawImage()
        imageView.invalidate()
    }

    // ????????? ???????????? ????????? ??????
    private fun drawImage() {
        paintImage(
            this.bitmap,
            image.x.toFloat(),
            image.y.toFloat(),
            image.x + (image.scale * image.width),
            image.y + (image.scale * image.height)
        )
    }

    // ?????? ????????? ??????
    /** ????????? ???????????? ?????????.*/
    private fun paintImage(bitmap: Bitmap, x1: Float, y1: Float, x2: Float, y2: Float) {
        val rectF = RectF(x1, y1, x2, y2)
        canvas.drawBitmap(bitmap, null, rectF, Paint())
    }

    /** ????????? ?????????.*/
    private fun paintBackground() {
        canvas.drawColor(backgroundColor)
    }

    // ????????? ????????? ??????
    /** ????????? ????????? ?????? ?????? ?????? ?????? ????????? ????????????.*/
    private fun cancelMove() {
        image = originImage.copy()
        refresh()
    }

    /** ?????? ??????????????? ????????????.*/
    private fun intentNext() {
        val intent = Intent(this, RotateActivity::class.java)
        startActivity(intent)
    }


}