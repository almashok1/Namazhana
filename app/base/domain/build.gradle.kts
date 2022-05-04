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
}