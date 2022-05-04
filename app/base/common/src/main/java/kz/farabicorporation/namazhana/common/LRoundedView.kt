package kz.farabicorporation.namazhana.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import kz.farabicorporation.namazhana.common.extensions.dpToPx


class LRoundedView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val cornerRadius = 20.dpToPx
    private val startClipX = 104.dpToPx
    private val endClipY = 35.dpToPx
    private val width = 231.dpToPx
    private val height = 144.dpToPx

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        pathEffect = CornerPathEffect(cornerRadius)
        color = Color.WHITE
    }
    private val path = Path().apply {
        moveTo(0f, 0f)
        lineTo(startClipX, 0f)
        lineTo(startClipX, endClipY)
        lineTo(width, endClipY)
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(path, paint)
        super.onDraw(canvas)
    }

//    private class CustomOutline(
//        val path: Path,
//        val start: Int,
//        val width: Int,
//        val height: Int,
//        val radius: Float
//    ) : ViewOutlineProvider() {
//        override fun getOutline(view: View, outline: Outline) {
//            outline.offset(0, 0)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                outline.setPath(path)
//            } else {
//                outline.setRoundRect(Rect(0, start, width, height), radius)
//            }
//        }
//    }

}