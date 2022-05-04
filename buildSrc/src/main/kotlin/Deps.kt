object Modules {

    object Base {
        const val ARCH = ":app:base:arch"
        const val DATA = ":app:base:data"
        const val DOMAIN = ":app:base:domain"
        const val COMMON = ":app:base:common"
    }

    object Features {

    }

    object Libs {
        const val RETROFIT_API = ":app:base:data:api"
    }
}

object Libs {

    object AndroidX {
        const val CORE_KTX = "androidx.core:core-ktx:1.7.0"
        const val APPCOMPAT = "androidx.appcompat:appcompat:1.3.1"
        const val MATERIAL = "com.google.android.material:material:1.4.0"
        const val VIEW_PAGER2 = "androidx.viewpager2:viewpager2:1.0.0"
        const val SWIPE_REFRESH = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
    }

    object Kotlin {
        private const val VERSION = "1.6.10"
        const val STD_LIB = "org.jetbrains.kotlin:kotlin-stdlib:$VERSION"
        const val REFLECT = "org.jetbrains.kotlin:kotlin-reflect:$VERSION"

        object Coroutines {
            private const val VERSION = "1.6.0"
            const val CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$VERSION"
            const val ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$VERSION"
        }
    }

    object DI {
        object KOIN {
            private const val VERSION = "3.1.4"
            const val KOIN = "io.insert-koin:koin-core:$VERSION"
            const val KOIN_ANDROID = "io.insert-koin:koin-android:$VERSION"
            const val KOIN_ANDROID_NAVIGATION = "io.insert-koin:koin-androidx-navigation:$VERSION"
            const val KOIN_TEST = "io.insert-koin:koin-test:$VERSION"
            const val KOIN_KSP_COMPILER = "io.insert-koin:koin-ksp-compiler:${Versions.KOIN_KSP}"
            const val KOIN_KSP_ANNOTATION = "io.insert-koin:koin-annotations:${Versions.KOIN_KSP}"
        }
    }

    object Network {
        const val RETROFIT = "com.squareup.retrofit2:retrofit:2.9.0"
        const val LOGGING_INTERCEPTOR = "com.squareup.okhttp3:logging-interceptor:4.9.3"

        object Serializer {
            const val MOSHI_CONVERTER = "com.squareup.retrofit2:converter-moshi:2.9.0"
            const val MOSHI = "com.squareup.moshi:moshi:1.12.0"
            const val MOSHI_KOTLIN = "com.squareup.moshi:moshi-kotlin:1.12.0"
            const val MOSHI_CODEGEN = "com.squareup.moshi:moshi-kotlin-codegen:1.12.0"
            const val MOSHI_ADAPTERS = "com.squareup.moshi:moshi-adapters:1.8.0"
        }
    }

    object Logging {
        const val TIMBER = "com.jakewharton.timber:timber:4.7.1"
        const val CHUCKER_DEBUG = "com.github.chuckerteam.chucker:library:3.5.2"
        const val CHUCKER_RELEASE = "com.github.chuckerteam.chucker:library-no-op:3.5.2"
    }

    object Navigation {
        private const val VERSION = "2.4.1"
        const val FRAGMENT = "androidx.navigation:navigation-fragment-ktx:$VERSION"
        const val UI =  "androidx.navigation:navigation-ui-ktx:$VERSION"
    }

    object Lifecycle {
        private const val VERSION = "2.4.1"
        const val VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:$VERSION"
        const val LIVEDATA = "androidx.lifecycle:lifecycle-livedata-ktx:$VERSION"
        const val RUNTIME = "androidx.lifecycle:lifecycle-runtime-ktx:$VERSION"
        const val RUNTIME_COMPOSE = "androidx.compose.runtime:runtime-livedata:1.0.5"
        const val SAVED_STATE = "androidx.lifecycle:lifecycle-viewmodel-savedstate:$VERSION"
        const val JAVA_8 = "androidx.lifecycle:lifecycle-common-java8:$VERSION"
        const val COMPILER = "androidx.lifecycle:lifecycle-compiler:$VERSION" /* kapt */
    }


    object Animations {
        const val SHIMMER = "com.facebook.shimmer:shimmer:0.5.0"
    }

    object Images {
        const val GLIDE = "com.github.bumptech.glide:glide:4.13.0"
        const val GLIDE_COMPILER = "com.github.bumptech.glide:compiler:4.13.0"
        const val GLIDE_TRANSFORMATIONS = "jp.wasabeef:glide-transformations:4.0.0"
        const val BLURRY = "jp.wasabeef:blurry:4.0.1"
    }

    object Custom {
        const val YANDEX_MAP = "com.yandex.android:maps.mobile:4.0.0-lite"
        const val DOTS_INDICATOR = "com.tbuonomo:dotsindicator:4.2"
        const val CHIP_NAVIGATION = "com.github.ismaeldivita:chip-navigation-bar:1.4.0"
    }
}