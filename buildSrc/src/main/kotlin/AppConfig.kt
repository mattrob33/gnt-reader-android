import org.gradle.api.JavaVersion

object AppConfig {

    const val minSdk = 21
    const val compileSdk = 32
    const val targetSdk = compileSdk
    const val buildTools = "30.0.3"

    const val jvmTarget = "1.8"
    val javaCompatibility = JavaVersion.VERSION_1_8

}