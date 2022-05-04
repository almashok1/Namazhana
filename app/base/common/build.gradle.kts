plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("kapt")
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

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    api(project(Modules.Base.ARCH))
    api(project(Modules.Base.DATA))

    // Kotlin
    api(Libs.Kotlin.Coroutines.ANDROID)

    // Android
    api(Libs.AndroidX.CORE_KTX)
    api(Libs.AndroidX.APPCOMPAT)
    api(Libs.AndroidX.MATERIAL)
    api(Libs.AndroidX.SWIPE_REFRESH)

    // Navigation
    implementation(Libs.Navigation.UI)
    implementation(Libs.Navigation.FRAGMENT)

    // Image loading
    api(Libs.Images.GLIDE)
    api(Libs.Images.BLURRY)
    kapt(Libs.Images.GLIDE_COMPILER)

    // Dependency Injection
    api(Libs.DI.KOIN.KOIN)
    api(Libs.DI.KOIN.KOIN_ANDROID)
    api(Libs.DI.KOIN.KOIN_ANDROID_NAVIGATION)

    // Lifecycle
    api(Libs.Lifecycle.VIEWMODEL)
    api(Libs.Lifecycle.LIVEDATA)
    api(Libs.Lifecycle.RUNTIME)
    api(Libs.Lifecycle.SAVED_STATE)
    api(Libs.Lifecycle.JAVA_8)

    // Animation
    api(Libs.Animations.SHIMMER)
}