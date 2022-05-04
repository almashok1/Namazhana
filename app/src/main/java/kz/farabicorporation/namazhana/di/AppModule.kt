package kz.farabicorporation.namazhana.di

import android.content.Context
import android.content.SharedPreferences
import kz.farabicorporation.namazhana.MainViewModel
import kz.farabicorporation.namazhana.ui.MainMapViewModel
import kz.farabicorporation.namazhana.ui.OnboardingViewModel
import kz.farabicorporation.namazhana.ui.StartViewModel
import kz.farabicorporation.namazhana.ui.details.PlaceDetailsViewModel
import kz.farabicorporation.namazhana.ui.filter.FilterViewModel
import kz.farabicorporation.namazhana.ui.list.PlaceListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val SHARED_PREF_NAME = "namazhana_shared_prefs"

val appModule = module {

    single<SharedPreferences> { androidContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE) }

    viewModel { StartViewModel(get()) }
    viewModel { MainViewModel() }
    viewModel { OnboardingViewModel(get()) }
    viewModel { MainMapViewModel(get()) }
    viewModel { PlaceDetailsViewModel(get()) }
    viewModel { parameters -> FilterViewModel(parameters.get(), get()) }
    viewModel { parameters -> PlaceListViewModel(parameters.get(), get()) }
}