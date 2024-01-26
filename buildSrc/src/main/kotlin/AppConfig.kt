import org.gradle.api.JavaVersion

object AppConfig {

    const val minSdk = 21
    const val compileSdk = 34
    const val targetSdk = compileSdk
    const val buildTools = "34.0.0"

    const val jvmTarget = "17"
    val javaCompatibility = JavaVersion.VERSION_17

}