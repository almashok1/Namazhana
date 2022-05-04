package kz.farabicorporation.namazhana.common.extensions

import android.content.Context
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.widget.Toast
import androidx.viewbinding.ViewBinding

fun ViewBinding.showToast(stringResId: Int, duration: Int = Toast.LENGTH_SHORT){
    root.context.showToast(stringResId, duration)
}

fun ViewBinding.showToast(message: String, duration: Int = Toast.LENGTH_SHORT){
    root.context.showToast(message, duration)
}

fun Context.showToast(stringResId: Int, duration: Int = Toast.LENGTH_SHORT){
    showToast(getString(stringResId), duration)
}

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT){
    val centeredText: Spannable = SpannableString(message)
    centeredText.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, message.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    Toast.makeText(this, centeredText, duration).show()
}