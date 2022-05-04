package kz.farabicorporation.namazhana.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import kz.farabicorporation.namazhana.BuildConfig
import kz.farabicorporation.namazhana.api.ApiBuilder
import kz.farabicorporation.namazhana.data.ApiKeyDelegateInterceptor
import kz.farabicorporation.namazhana.data.api.CountryApi
import kz.farabicorporation.namazhana.data.api.FeedbackApi
import kz.farabicorporation.namazhana.data.api.PlaceApi
import org.koin.dsl.module

val networkModule = module {
    single { ChuckerInterceptor.Builder(get()).build() }
    single { ApiKeyDelegateInterceptor(BuildConfig.APP_API) }

    single<CountryApi> {
        ApiBuilder(get(), get()).buildService(BASE_URL, get<ChuckerInterceptor>(), get<ApiKeyDelegateInterceptor>())
    }

    single<FeedbackApi> {
        ApiBuilder(get(), get()).buildService(BASE_URL, get<ChuckerInterceptor>(), get<ApiKeyDelegateInterceptor>())
    }

    single<PlaceApi> {
        ApiBuilder(get(), get()).buildService(BASE_URL, get<ChuckerInterceptor>(), get<ApiKeyDelegateInterceptor>())
    }

}

private const val BASE_URL = "http://62.84.115.153:8080/api/v1/"