plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = AppConfig.compileSdk
    buildToolsVersion = AppConfig.buildTools

    defaultConfig {
        applicationId = "com.mattrobertson.greek.reader"

        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk

        versionCode = AppConfig.appVersionCode
        versionName = AppConfig.appVersionName
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }

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

    implementation(projects.core.dbApi)
    implementation(projects.core.dbInternal)
    implementation(projects.core.settings)
    implementation(projects.core.ui)
    implementation(projects.core.verseref)

    implementation(projects.feature.audio)
    implementation(projects.feature.concordance)
    implementation(projects.feature.gloss)
    implementation(projects.feature.reading)
    implementation(projects.feature.settingsUi)
    implementation(projects.feature.vocab)

    // Core
    implementation(libs.kotlin)
    implementation(libs.androidCoreKtx)
    implementation(libs.appcompat)
    implementation(libs.activity)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.hilt.viewmodels.compiler)

    // UI
    implementation(libs.material)
    implementation(libs.lifecycleRuntime)

    // Analytics
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
}