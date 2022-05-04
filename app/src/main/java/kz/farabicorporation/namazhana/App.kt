package kz.farabicorporation.namazhana

import android.app.Application
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import kz.farabicorporation.namazhana.di.appModule
import kz.farabicorporation.namazhana.di.dataModule
import kz.farabicorporation.namazhana.di.domainModule
import kz.farabicorporation.namazhana.di.networkModule
import kz.farabicorporation.namazhana.di.serializerModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@App)
            modules(appModule, dataModule, domainModule, networkModule, serializerModule)
        }
    }

}

@GlideModule
class AppGlideModule : AppGlideModule()
