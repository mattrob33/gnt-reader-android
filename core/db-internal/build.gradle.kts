plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = AppConfig.compileSdk
    buildToolsVersion = AppConfig.buildTools

    defaultConfig {
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = AppConfig.javaCompatibility
        targetCompatibility = AppConfig.javaCompatibility
    }

    kotlinOptions {
        jvmTarget = AppConfig.jvmTarget
    }
}

dependencies {
    implementation(projects.core.verseref)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // Room
    api(libs.room.runtime)
    api(libs.room.ktx)
    kapt(libs.room.compiler)

    // Testing
//    testImplementation("androidx.room:room-testing:${Versions.room}")
//    testImplementation("junit:junit:${Versions.junit}")
}