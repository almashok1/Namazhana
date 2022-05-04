package kz.farabicorporation.namazhana.common.extensions

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kz.farabicorporation.namazhana.common.R

const val DRIVE_BASE_URL = "https://docs.google.com/uc?id="

fun ImageView.loadWithDriveUrl(id: String?, @DrawableRes placeholder: Int = R.drawable.placeholder_image) {
    loadUrl(DRIVE_BASE_URL+id, placeholder)
}

fun ImageView.loadUrl(
    url: String?,
    @DrawableRes placeholder: Int = R.drawable.placeholder_image,
    @DrawableRes fallback: Int = R.drawable.placeholder_image,
    timeOut: Int = 20000
) {
    if (url.isNullOrEmpty()) {
        loadDrawable(fallback)
    } else {
        Glide.with(this)
            .load(url)
            .fallback(fallback)
            .error(fallback)
            .placeholder(placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .timeout(timeOut)
            .into(this)
    }
}

fun ImageView.loadUrl(
    url: String?, placeholder: Drawable,
    timeOut: Int = 20000
) {
    if (url.isNullOrEmpty()) {
        loadDrawable(placeholder)
    } else {
        Glide.with(this)
            .load(url)
            .placeholder(placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .timeout(timeOut)
            .into(this)
    }
}

fun ImageView.loadDrawable(@DrawableRes resId: Int) {
    Glide.with(this)
        .load(resId)
        .into(this)
}

fun ImageView.loadDrawable(drawable: Drawable) {
    Glide.with(this)
        .load(drawable)
        .into(this)
}