package kz.farabicorporation.namazhana.common.extensions

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kz.farabicorporation.namazhana.common.CooldownAction
import kz.farabicorporation.namazhana.common.R
import kz.farabicorporation.namazhana.common.databinding.LayoutDialogAlertTextBinding

internal const val MIN_CLICK_REFRESH_INTERVAL = 300L

/* View */
fun View.onSafeClick(refreshInterval: Long = MIN_CLICK_REFRESH_INTERVAL, action: () -> Unit) {
    val cooldown = CooldownAction(refreshInterval, action)
    setOnClickListener {
        cooldown.tryAction()
    }
}

abstract class DoubleClickListener : View.OnClickListener {
    var lastClickTime: Long = 0
    override fun onClick(v: View?) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < MIN_CLICK_REFRESH_INTERVAL * 2) {
            onDoubleClick()
        } else {
            onJustClick()
        }
        lastClickTime = clickTime
    }

    abstract fun onDoubleClick()
    abstract fun onJustClick()
}

/* ViewBinding */

inline var ViewBinding.isVisible: Boolean
    get() = root.isVisible
    set(value) {
        root.isVisible = value
    }


fun Context.showInfoDialog(
    message: String,
    positiveButtonText: String = "OK",
    onPositiveClick: (dialog: DialogInterface, i: Int) -> Unit = { dialog, _ -> dialog.dismiss() }
) {
    val view = LayoutDialogAlertTextBinding.inflate(LayoutInflater.from(this))
    view.root.text = message
    MaterialAlertDialogBuilder(this)
        .setCustomTitle(view.root)
        .setPositiveButton(positiveButtonText, onPositiveClick)
        .create()
        .show()
}

fun Fragment.showInfoDialog(
    message: String,
    positiveButtonText: String = "OK",
    onPositiveClick: (dialog: DialogInterface, i: Int) -> Unit = { dialog, _ -> dialog.dismiss() }
) = requireContext().showInfoDialog(message, positiveButtonText, onPositiveClick)

fun ViewBinding.beginChangeBoundsTransition() {
    if (root is ViewGroup) {
        (root as ViewGroup).beginChangeBoundsTransition()
    }
}

fun ViewGroup.beginChangeBoundsTransition(){
    TransitionManager.endTransitions(this)
    TransitionManager.beginDelayedTransition(this, ChangeBounds())
}


fun MaterialButton.setShowProgress(
    showProgress: Boolean = false,
    textSource: String?,
    @DrawableRes iconSource: Int? = null,
    @ColorRes progressColor: Int = R.color.white
) {
    iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
    icon = if (showProgress) {
        CircularProgressDrawable(context).apply {
            setStyle(CircularProgressDrawable.DEFAULT)
            setColorSchemeColors(context.getColorCompat(progressColor))
            start()
        }
    } else iconSource?.let { context.getDrawableCompat(it) }
    text = if (showProgress) "" else textSource
    if (icon != null) {
        icon.callback = object : Drawable.Callback {
            override fun unscheduleDrawable(who: Drawable, what: Runnable) {}
            override fun invalidateDrawable(who: Drawable) {
                this@setShowProgress.invalidate()
            }

            override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {}
        }
    }
}