plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = Versions.compileSdk
    buildToolsVersion = Versions.buildTools

    defaultConfig {
        applicationId = "com.mattrobertson.greek.reader"

        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk

        versionCode = Versions.appVersionCode
        versionName = Versions.appVersionName
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
        sourceCompatibility = Versions.javaCompatibility
        targetCompatibility = Versions.javaCompatibility
    }

    kotlinOptions {
        jvmTarget = Versions.jvmTarget
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }
}

dependencies {
    implementation(projects.core.verseref)
    implementation(projects.core.dbInternal)
    implementation(projects.core.dbApi)
    implementation(projects.core.ui)

    implementation(projects.feature.audio)
    implementation(projects.feature.concordance)
    implementation(projects.feature.gloss)
    implementation(projects.feature.reading)
    implementation(projects.feature.settings)
    implementation(projects.feature.vocab)

    // Core
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.activity:activity-ktx:1.3.1")

    // Dependency Injection
    implementation("com.google.dagger:hilt-android:${Versions.hilt}")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.hilt}")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:${Versions.hiltViewModels}")
    kapt("androidx.hilt:hilt-compiler:${Versions.hiltViewModels}")

    // UI
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")

    // Analytics
    implementation("com.google.firebase:firebase-analytics:19.0.0")
    implementation("com.google.firebase:firebase-crashlytics:18.2.1")
}