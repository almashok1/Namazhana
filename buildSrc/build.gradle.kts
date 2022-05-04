import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    mavenCentral()
    google()
}

buildscript {

    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:7.1.1")
    implementation("com.android.tools.build:gradle-api:7.1.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
    implementation(kotlin("gradle-plugin", version = "1.5.21"))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.6.10")
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
}
