package kz.farabicorporation.namazhana.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kz.farabicorporation.namazhana.api.DateAdapter
import kz.farabicorporation.namazhana.api.MoshiSerializer
import kz.farabicorporation.namazhana.api.RetrofitConverterFactory
import kz.farabicorporation.namazhana.arch.api.JsonSerializer
import org.koin.dsl.module

val serializerModule = module {

    single<Moshi> { Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(DateAdapter())
        .build()
    }

    single<JsonSerializer> { MoshiSerializer(get())  }

    single<RetrofitConverterFactory> { MoshiSerializer(get())  }

}