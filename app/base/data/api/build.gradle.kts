plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = AppConfig.jdk
    targetCompatibility = AppConfig.jdk
}

dependencies {
    implementation(project(Modules.Base.ARCH))

    // Network Connection
    implementation(Libs.Network.RETROFIT)
    implementation(Libs.Network.LOGGING_INTERCEPTOR)
}