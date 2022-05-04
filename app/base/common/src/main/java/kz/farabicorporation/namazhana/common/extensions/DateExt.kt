package kz.farabicorporation.namazhana.common.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFormatFull by lazy {
    SimpleDateFormat("d MM. yyyy", Locale.getDefault())
}

fun Date.toReadableFullFormat(): String {
    return dateFormatFull.format(this)
}

fun Date.toFormat(pattern: String) : String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}