package kz.farabicorporation.namazhana.domain

interface Mapper<IN, OUT> {
    fun map(input: IN): OUT
}