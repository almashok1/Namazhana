import org.gradle.api.JavaVersion

object AppConfig {
    const val minSdk = 23
    const val targetSdk = 31
    const val compileSdk = 32
    const val appId = "kz.adamant.cooking_recipes"
    const val androidTestInstrumentation = "androidx.test.runner.AndroidJUnitRunner"
    val jdk = JavaVersion.VERSION_1_8
}

object Versions {
    const val KSP = "1.6.10-1.0.2"
    const val KOIN_KSP = "1.0.0-beta-1"
}