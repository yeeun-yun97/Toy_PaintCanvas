package com.github.yeeun_yun97.toy.paintcanvas

import android.graphics.Color
import android.util.Log
import java.lang.Math.sqrt
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

data class Point(
    val x: Int,
    val y: Int
)

data class Line(
    val firstPoint: Point,
    val secondPoint: Point
)

data class Rect(val x: Int, val y: Int, val width: Int, val height: Int, val scale: Float)

class PaintCanvasTool(
    private val sw: Int, private val sh: Int,
    canvasX: Int, canvasY: Int,
    private val canvasWidth: Int, private val canvasHeight: Int
) {
    // onCanvas X => canvasX ~ (canvasX+canvasWidth)
    val canvasStart: Int = canvasX
    private val canvasEnd: Int = canvasX + canvasWidth

    // onCanvas Y => canvasY ~ (canvasY+canvasHeight)
    val canvasTop: Int = canvasY
    private val canvasBottom: Int = canvasY + canvasHeight

    private val random = Random()
    var line = createRandomLine()

    private fun createRandomLine(): Line =
        Line(
            Point(random.nextInt(canvasWidth), random.nextInt(canvasHeight)),
            Point(random.nextInt(canvasWidth), random.nextInt(canvasHeight))
        )
    fun setRandomLine() {
        line = createRandomLine()
    }

    fun screenToCanvas(point: Point): Point {
        if (point.x < 0 || point.x > sw
            || point.y < 0 || point.y > sh
        ) {
            //Error: point is out of canvas and screen
        } else if (point.x < canvasStart || point.x > canvasEnd
            || point.y < canvasTop || point.y > canvasBottom
        ) {
            //Error point is out of canvas
        } else {
            return Point(point.x - canvasStart, point.y - canvasTop)
        }
        return Point(-1, -1)
    }

    fun canErase(
        point: Point,
        r: Float,
        drawPoint: (Float, Float, Int) -> Unit
    ): Boolean {
        val canvasPoint = screenToCanvas(point)

        val firstPoint = line.firstPoint
        val secondPoint = line.secondPoint

        // (a * x) + (-1 * y) + c = 0
        val a: Double = (firstPoint.y - secondPoint.y) / (firstPoint.x - secondPoint.x).toDouble()
        val c = firstPoint.y - ((firstPoint.x) * a)

        // ?????? ?????? ?????? ?????? ????????? (?????? ??????)
        var d = ((a * canvasPoint.x) - canvasPoint.y + c) / kotlin.math.sqrt((a * a) + 1.0)
        val distance =
            if (d < 0) d * -1
            else d

        // ?????? ??????????????? ????????? ????????? ?????? ??????
        if (distance > r) {
            return false
        } else { // ?????? ???????????????, ?????? ?????? ?????? ?????? ????????? ????????????
            val crossA: Float = -(1 / a).toFloat() // ????????? ?????????

            val hdx: Float = (d / sqrt(((crossA * crossA).toDouble() + 1))).toFloat()
            val hdy: Float = hdx * crossA

            var tempX = canvasPoint.x + hdx
            var tempY = canvasPoint.y + hdy
            if (((tempX * a) + c).toInt() != tempY.toInt()) {
                tempY = canvasPoint.y - hdy
                if (((tempX * a) + c).toInt() != tempY.toInt()) {
                    tempX = canvasPoint.x - hdx
                    if (((tempX * a) + c).toInt() != tempY.toInt())
                        tempY = canvasPoint.y + hdx
                }
            }
            val hx = tempX
            val hy = tempY
            drawPoint(hx, hy, Color.MAGENTA)// ??? H (?????? ?????? ????????? ??????)

            //?????? ???????????? ??? H??? ????????? ??? ?????? ??????.
            if (inRange(firstPoint.x, secondPoint.x, hx.toDouble()) == 0
                && inRange(firstPoint.y, secondPoint.y, hy.toDouble()) == 0
            ) return true

            val l = sqrt(r.pow(2) - distance.pow(2))
            val ldx = (l / sqrt(a.pow(2) + 1))

            val lxPlus = hx + ldx
            val lyPlus = a * lxPlus + c
            drawPoint(lxPlus.toFloat(), lyPlus.toFloat(), Color.BLUE)

            val lxMinus = hx - ldx
            val lyMinus = a * lxMinus + c
            drawPoint(lxMinus.toFloat(), lyMinus.toFloat(), Color.RED)

            val xRangeOverlap = isRangeOverlap(firstPoint.x, secondPoint.x, lxPlus, lxMinus)
            if (!xRangeOverlap) return false

            val yRangeOverlap = isRangeOverlap(firstPoint.y, secondPoint.y, lyPlus, lyMinus)
            if (!yRangeOverlap) return false

            return true
        }
    }

    private fun isRangeOverlap(first: Int, second: Int, check: Double, check2: Double): Boolean {
        val inRange = inRange(first, second, check)
        val inRange2 = inRange(first, second, check2)
        return !(inRange != 0 && inRange == inRange2) //(-1,-1)?????????, (1,1)??????????????? ????????? ?????? ????????? ?????? ?????????.
    }

    private fun inRange(firstPoint: Int, secondPoint: Int, check: Double): Int {
        return if (check < Math.min(firstPoint, secondPoint)) -1
        else if (check > Math.max(firstPoint, secondPoint)) 1
        else 0
    }

    fun transform(image: Rect, pivot: Point): Rect? {

        return null
    }

    fun move(image: Rect, dx: Int, dy: Int): Rect? {
        return image.copy(x = image.x + dx, y = image.y + dy)
    }

    fun resize(image: Rect, pivot: Point, scale: Float): Rect? {
        val start: Float = image.x.toFloat()
        val end: Float = start + (image.width * image.scale)
        val top: Float = image.y.toFloat()
        val bottom: Float = top + (image.height * image.scale)

        // ???????????? ???????????? ???????????? ?????? ??????
        if (pivot.x.toFloat() == start) {
            if (pivot.y.toFloat() == top) {
                // ?????? ???
                return image.copy(
                    scale = scale
                )
            } else if (pivot.y.toFloat() == bottom) {
                // ?????? ??????
                return image.copy(
                    y = (bottom - (image.height * scale)).roundToInt(),
                    scale = scale
                )
            }
        } else if (pivot.x.toFloat() == end) {
            if (pivot.y.toFloat() == top) {
                // ????????? ???
                return image.copy(
                    x = (end - (image.width * scale)).roundToInt(),
                    scale = scale
                )
            } else if (pivot.y.toFloat() == bottom) {
                // ????????? ??????
                return image.copy(
                    x = (end - (image.width * scale)).roundToInt(),
                    y = (bottom - (image.height * scale)).roundToInt(),
                    scale = scale
                )
            }
        }

        // ???????????? ???????????? ?????? ?????? (????????? ??????)
        if (pivot.x.toFloat() in start..end && pivot.y.toFloat() in top..bottom) {
            Log.d("TAG", "????????? ?????????.")
            val miniRectWidth = pivot.x - start
            val miniRectHeight = pivot.y - top
            val miniRectStart = (pivot.x - ((miniRectWidth / image.scale) * scale)).roundToInt()
            val miniRectEnd = (pivot.y - ((miniRectHeight / image.scale) * scale)).roundToInt()
            return image.copy(x = miniRectStart, y = miniRectEnd, scale = scale)
        }
        return null
    }

    fun isOnRect(image: Rect, x: Float, y: Float): Boolean {
        val start = image.x.toFloat()
        val end = start + (image.scale * image.width)
        val top = image.y.toFloat()
        val bottom = top + (image.scale * image.height)

        return (x in start..end && y in top..bottom)
    }


}