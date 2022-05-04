package kz.farabicorporation.namazhana.common.extensions

import android.graphics.Color
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.color

fun TextView.markWithRequiredAsterisk() {
    text = text.toString().markWithRequiredAsterisk()
}

fun String.markWithRequiredAsterisk() = buildSpannedString {
    color(Color.RED) { append("* ") }
    append(this@markWithRequiredAsterisk)
}

