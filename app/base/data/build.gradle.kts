plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
    }

    compileOptions {
        sourceCompatibility = AppConfig.jdk
        targetCompatibility = AppConfig.jdk
    }
    kotlinOptions {
        jvmTarget = AppConfig.jdk.toString()
    }
}

dependencies {
    api(project(Modules.Base.ARCH))
    api(project(Modules.Base.DOMAIN))
    api(project(Modules.Libs.RETROFIT_API))

    // Dependency Injection
    implementation(Libs.DI.KOIN.KOIN)
    implementation(Libs.DI.KOIN.KOIN_ANDROID)
    implementation(Libs.DI.KOIN.KOIN_ANDROID_NAVIGATION)

    // Lifecycle
    implementation(Libs.Lifecycle.VIEWMODEL)
    implementation(Libs.Lifecycle.LIVEDATA)
    implementation(Libs.Lifecycle.RUNTIME)
}