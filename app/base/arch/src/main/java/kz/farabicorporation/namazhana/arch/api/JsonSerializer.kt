package kz.farabicorporation.namazhana.arch.api

interface JsonSerializer {

    fun<T> jsonToObject(type: Class<T>, string: String?): T?

    fun<T> objectToJson(type: Class<T>, item: T?): String?

}