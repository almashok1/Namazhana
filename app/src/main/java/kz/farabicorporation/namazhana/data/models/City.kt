package kz.farabicorporation.namazhana.data.models

class City(
    val name: String,
    val countryName: String? = null,
    val coordinate: Coordinate,
    val id: Int,
    val distance: Double? = null
)