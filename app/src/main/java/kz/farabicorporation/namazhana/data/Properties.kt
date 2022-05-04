package kz.farabicorporation.namazhana.data

const val FEEDBACK_MAIL = "namazhanaApp@gmail.com"
const val FEEDBACK_MAIL_TITLE = "Отправить отзыв"

fun getMailSubject(name: String) = "Отзыв о месте \"$name\""