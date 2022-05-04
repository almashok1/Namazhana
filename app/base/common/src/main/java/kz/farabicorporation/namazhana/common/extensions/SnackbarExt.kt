package kz.farabicorporation.namazhana.common.extensions

import android.app.Activity
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.view.isVisible
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kz.farabicorporation.namazhana.common.R
import kz.farabicorporation.namazhana.common.databinding.LayoutSnackbarViewBinding
import kotlin.math.ceil


fun Activity.getRootView(): FrameLayout {
    return findViewById(android.R.id.content)
}

enum class SnackbarStyleType {
    NORMAL, DANGER, SUCCESS
}

sealed class SnackbarLeadingType {
    object NoLeading: SnackbarLeadingType()

    class Image(
        @DrawableRes val drawableRes: Int
    ): SnackbarLeadingType()

    class Progress(
        val duration: Int = 5000,
    ): SnackbarLeadingType() {

        private var onTicked: ((Long) -> Unit)? = null

        private val countDownTimer: CountDownTimer by lazy {
            object : CountDownTimer(duration.toLong(), 10L) {
                override fun onTick(millisUntilFinished: Long) {
                    onTicked?.invoke(millisUntilFinished)
                }

                override fun onFinish() {}
            }
        }

        fun setOnTicked(onTicked: (Long) -> Unit) {
            this.onTicked = onTicked
        }

        fun startCountdown() {
            countDownTimer.start()
        }
    }
}

fun Activity.makeSnackBar(
    message: String,
    duration: Int = 5000,
    action: (() -> Unit)? = null,
    onFinished: (() -> Unit)? = null,
    actionText: String? = null,
    root: View? = null,
    @IdRes anchorViewRes: Int? = null,
    styleType: SnackbarStyleType = SnackbarStyleType.NORMAL,
    leadingType: SnackbarLeadingType = SnackbarLeadingType.NoLeading,
    marginBottom: Int = 0
): Snackbar {
    val parent: View = root ?: getRootView()
    val binding = LayoutSnackbarViewBinding.inflate(LayoutInflater.from(parent.context))
    return Snackbar.make(parent, "", duration).apply {
        (view as ViewGroup).addView(binding.root, 0)
        view.setPadding(0, 0, 0, 0)
        view.setBackgroundResource(styleType.getBackground())
        view.elevation = 0f
        anchorViewRes?.let {
            setAnchorView(anchorViewRes)
        }
        view.translationY = -marginBottom.dpToPx
        if (leadingType is SnackbarLeadingType.Progress) leadingType.setOnTicked  {
            val progress = ceil(it / 1000f).toInt()
            binding.tvProgress.text = progress.toString()
            binding.progressIndicator.progress = it.toInt()
        }
        addCallback(object: BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onShown(transientBottomBar: Snackbar?) {
                super.onShown(transientBottomBar)
                if (leadingType is SnackbarLeadingType.Progress) {
                    leadingType.startCountdown()
                }
            }

            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT || event == Snackbar.Callback.DISMISS_EVENT_SWIPE) {
                    onFinished?.invoke()
                }
            }
        })
        binding.run {
            tvTitle.text = message
            tvAction.text = actionText ?: "OK"
            tvAction.setOnClickListener {
                action?.invoke()
                this@apply.dismiss()
            }
            setLeading(leadingType)
        }
    }
}

@DrawableRes
fun SnackbarStyleType.getBackground() = when(this) {
    SnackbarStyleType.NORMAL -> R.drawable.bg_snackbar_filled_normal
    SnackbarStyleType.DANGER -> R.drawable.bg_snackbar_filled_danger
    SnackbarStyleType.SUCCESS -> R.drawable.bg_snackbar_filled_success
}

fun LayoutSnackbarViewBinding.setLeading(type: SnackbarLeadingType) {
    when(type) {
        is SnackbarLeadingType.Image -> {
            tvProgress.isVisible = false
            progressIndicator.isVisible = false
            ivIcon.isVisible = true
            ivIcon.setImageResource(type.drawableRes)
        }
        SnackbarLeadingType.NoLeading -> {
            vgStartIcon.isVisible = false
        }
        is SnackbarLeadingType.Progress -> {
            tvProgress.isVisible = true
            progressIndicator.isVisible = true
            ivIcon.isVisible = false
            progressIndicator.max = type.duration
        }
    }
}
