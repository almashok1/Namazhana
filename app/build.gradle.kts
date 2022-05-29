plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("android")
    kotlin("kapt")
}


android {
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        applicationId = AppConfig.appId
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = AppConfig.androidTestInstrumentation
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )

        }
        debug {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
        this.forEach {
            it.buildConfigField("String", "YANDEX_MAP_API", "\"YOUR YANDEX MAP KEY HERE\"")
            it.buildConfigField("String", "APP_API", "\"YOUR API KEY HERE\"")
        }
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

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
        )
    }
}

dependencies {
    implementation(project(Modules.Base.COMMON))
    implementation(project(Modules.Base.DATA))
    implementation(project(Modules.Libs.RETROFIT_API))
    implementation(project(Modules.Base.COMMON))

    implementation(Libs.AndroidX.Room.ROOM)
    kapt(Libs.AndroidX.Room.ROOM_COMPILER)

    // Logging
    debugImplementation(Libs.Logging.CHUCKER_DEBUG)
    releaseImplementation(Libs.Logging.CHUCKER_RELEASE)

    // Navigation
    implementation(Libs.Navigation.UI)
    implementation(Libs.Navigation.FRAGMENT)
    implementation(Libs.Custom.YANDEX_MAP)

    implementation(Libs.Network.Serializer.MOSHI)
    implementation(Libs.Network.Serializer.MOSHI_CONVERTER)
    implementation(Libs.Network.Serializer.MOSHI_KOTLIN)
    implementation(Libs.Network.Serializer.MOSHI_ADAPTERS)
    implementation(Libs.Network.Serializer.MOSHI_CODEGEN)

    // Custom things
    implementation(Libs.Custom.DOTS_INDICATOR)
    implementation(Libs.Custom.CHIP_NAVIGATION)
}