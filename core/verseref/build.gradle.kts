plugins {
    id("com.android.library")
    id("kotlin-android")
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

//    testOptions {
//        unitTests.all {
//            useJUnitPlatform()
//        }
//        unitTests.includeAndroidResources = true
//    }
}

dependencies {
    testImplementation(libs.jUnit5.api)
    testImplementation(libs.jUnit5.params)
    testRuntimeOnly(libs.jUnit5.engine)
    testRuntimeOnly(libs.jUnit5.vintageEngine)
}