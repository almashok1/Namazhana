package kz.farabicorporation.namazhana.common.extensions

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Point
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kz.farabicorporation.namazhana.common.R
import kotlin.math.floor

fun Fragment.setStatusBarColor(@ColorRes color: Int) {
    requireActivity().run {
        setStatusBarColor(color)
    }
}

fun Activity.setStatusBarColor(@ColorRes color: Int) {
    window.statusBarColor = getColorCompat(color)
}

fun Activity.makeStatusBarTransparent() {
    window.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        statusBarColor = Color.TRANSPARENT
    }
}

fun Context.showDialog(
    message: String,
    title: String? = null,
    positiveButtonText: String? = null,
    negativeButtonText: String? = null,
    @StyleRes style: Int = 0,
    onClick: (d: DialogInterface, which: Int) -> Unit = { _, _ -> }
) {
    MaterialAlertDialogBuilder(this, style)
        .setMessage(message)
        .setPositiveButton(positiveButtonText ?: "OK", onClick)
        .apply {
            title?.let { setTitle(it) }
            negativeButtonText?.let {
                setNegativeButton(it, null)
            }
        }
        .show()
}

fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)
fun Context.getDrawableCompat(@DrawableRes drawableRes: Int) =
    ContextCompat.getDrawable(this, drawableRes)

fun Context.screenDimension(): Point {
    val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val size = Point()
    display.getSize(size)
    return size
}


fun Context.getNavigationBarsHeight(): Int {
    val deviceDensity = resources.displayMetrics.density
    val idNavigationBarHeight: Int = applicationContext.resources.getIdentifier("navigation_bar_height", "dimen", "android")
    val navigationHeight = if (idNavigationBarHeight > 0) resources.getDimensionPixelSize(idNavigationBarHeight) / deviceDensity else 0f
    return navigationHeight.toInt()
}

fun Context.getActionBarHeight(): Int {
    val typedValue = TypedValue()
    val attributeResourceId = android.R.attr.actionBarSize
    return if (theme.resolveAttribute(attributeResourceId, typedValue, true)) {
        TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
    } else 56.dpToPx.toInt()
}

