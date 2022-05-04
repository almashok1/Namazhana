import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

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

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(project(Modules.Base.COMMON))

    // Navigation
    implementation(Libs.Navigation.UI)
    implementation(Libs.Navigation.FRAGMENT)
}
