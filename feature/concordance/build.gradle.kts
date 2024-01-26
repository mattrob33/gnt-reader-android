plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace="com.mattrobertson.greek.reader.concordance"

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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    implementation(projects.core.verseref)
    implementation(projects.core.dbApi)
    implementation(projects.core.settings)
    implementation(projects.core.ui)

    implementation(libs.androidCoreKtx)
    implementation(libs.appcompat)
    implementation(libs.material)

    testImplementation("junit:junit:4.+")
}