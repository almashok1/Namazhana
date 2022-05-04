plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = AppConfig.jdk
    targetCompatibility = AppConfig.jdk
}

dependencies {
    // Kotlin
    api(Libs.Kotlin.STD_LIB)
    api(Libs.Kotlin.REFLECT)
    api(Libs.Kotlin.Coroutines.CORE)

    // Logging
    api(Libs.Logging.TIMBER)

    // Serializer's Annotation dependency on Retrofit
    api(Libs.Network.Serializer.MOSHI_CONVERTER)
}