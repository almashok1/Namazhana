package kz.farabicorporation.namazhana.common.extensions

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kz.farabicorporation.namazhana.common.R

fun BottomSheetDialog.setDimBehind() {
    val dialog = this
    dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
}

fun BottomSheetDialog.setFullScreen(context: Context, marginTop: Int = 0) {
    val behavior = getBottomSheetBehavior()
    val targetHeight = context.screenDimension().y - marginTop
    behavior.peekHeight = targetHeight

    getBottomSheetView().run {
        layoutParams.height = targetHeight
        layoutParams = layoutParams
    }
    behavior.state = BottomSheetBehavior.STATE_EXPANDED
}

fun BottomSheetDialog.setUpRoundedCorners(onShowListener: (DialogInterface.() -> Unit)? = null) {
    setOnShowListener {
        getBottomSheetView().setBackgroundResource(R.drawable.bg_rounded_top_21)
        onShowListener?.invoke(it)
    }
}

fun BottomSheetDialogFragment.setUpRoundedCorners(onShowListener: (DialogInterface.() -> Unit)? = null) {
    (dialog as BottomSheetDialog).setUpRoundedCorners(onShowListener)
}

fun BottomSheetDialogFragment.setFullScreen(context: Context, marginTop: Int = 0) {
    (dialog as BottomSheetDialog).setFullScreen(context, marginTop)
}

fun BottomSheetDialog.disableCollapsing() {
    val behavior = getBottomSheetBehavior()
    behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    })
}

private fun BottomSheetDialog.getBottomSheetView() : FrameLayout {
    return findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
}

fun BottomSheetDialog.getBottomSheetBehavior() : BottomSheetBehavior<View> {
    val bottomSheet = getBottomSheetView()
    return BottomSheetBehavior.from(bottomSheet)
}
