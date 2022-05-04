package kz.farabicorporation.namazhana.di

import kz.farabicorporation.namazhana.domain.PlaceInteractor
import kz.farabicorporation.namazhana.domain.StartInteractor
import org.koin.dsl.module

val domainModule = module {

    factory { StartInteractor(get()) }
    factory { PlaceInteractor(get(), get()) }

}