import org.gradle.api.JavaVersion

object Versions {

    // Config
    const val minSdk = 21
    const val compileSdk = 30
    const val targetSdk = compileSdk
    const val buildTools = "30.0.3"

    const val appVersionCode = 22
    const val appVersionName = "7.1.2"

    const val jvmTarget = "1.8"
    val javaCompatibility = JavaVersion.VERSION_1_8

    // Project Core
    const val kotlin = "1.5.21"
    const val agp = "7.0.0"

    // Libraries
    const val compose = "1.0.1"
    const val hilt = "2.37"
    const val hiltViewModels = "1.0.0-alpha03"
    const val junit = "4.13.2"
    const val room = "2.3.0"
}