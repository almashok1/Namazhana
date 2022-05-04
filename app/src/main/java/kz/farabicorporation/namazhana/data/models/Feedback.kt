package kz.farabicorporation.namazhana.data.models

import java.util.Date

class Feedback(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val createdAt: Date,
    val message: String?
)