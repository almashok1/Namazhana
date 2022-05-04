package kz.farabicorporation.namazhana.api

import retrofit2.Converter

interface RetrofitConverterFactory {

    fun getConverterFactory(): Converter.Factory

}