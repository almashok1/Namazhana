package kz.farabicorporation.namazhana.common.extensions

fun String.formatPhone(): String {
    if(startsWith("+")) return this // already formatted
    val s = this
    return try {
        "+7 ${s[1]}${s[2]}${s[3]} ${s[4]}${s[5]}${s[6]} ${s[7]}${s[8]} ${s[9]}${s[10]}"
    } catch (e: Throwable) {
        s
    }
}

inline fun <R : CharSequence> R?.ifNullOrEmpty(defaultValue: () -> R): R =
    if (isNullOrEmpty()) defaultValue() else this