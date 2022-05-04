package kz.farabicorporation.namazhana.api

import kz.farabicorporation.namazhana.arch.api.JsonSerializer
import kz.farabicorporation.namazhana.arch.models.ServerErrorResponse
import kz.farabicorporation.namazhana.arch.utils.fromJson

fun String?.parseResponseBodyError(serializer: JsonSerializer): String? {
    return if (this.isNullOrBlank()) null
    else {
        val errorBody = serializer.fromJson<ServerErrorResponse>(this)
        errorBody?.error
    }
}