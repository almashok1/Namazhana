package kz.farabicorporation.namazhana.common.extensions

import android.content.res.Resources
import android.util.TypedValue

fun dpToPx(value: Float) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    value,
    Resources.getSystem().displayMetrics
)

fun pxToDp(value: Float) = value / Resources.getSystem().displayMetrics.density

inline val displayHeightPx get() = Resources.getSystem().displayMetrics.heightPixels

inline val displayWidthPx get() = Resources.getSystem().displayMetrics.widthPixels

inline val Int.dpToPx get() = dpToPx(this.toFloat())

inline val Int.pxToDp get() = pxToDp(this.toFloat())

inline val Float.dpToPx get() = dpToPx(this)

inline val Float.pxToDp get() = pxToDp(this)