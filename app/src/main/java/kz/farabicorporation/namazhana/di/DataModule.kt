package kz.farabicorporation.namazhana.di

import kz.farabicorporation.namazhana.data.ApiHelper
import kz.farabicorporation.namazhana.data.CitiesLocalDataSource
import org.koin.dsl.module

val dataModule = module {
    factory { ApiHelper(get(), get(), get()) }
    single { CitiesLocalDataSource(get()) }
}