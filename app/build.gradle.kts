import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

data class Version(
    val code: Int?,
    val name: String?
)

// Load optional CLI params
val cliProps = Version(
    code = (project.findProperty("versionCode") as? String)?.toInt(),
    name = project.findProperty("versionName") as? String
)

// Read from local.properties
val localProps = Version(
    code = gradleLocalProperties(rootDir).getProperty("versionCode")?.toInt(),
    name = gradleLocalProperties(rootDir).getProperty("versionName")
)

/*
 * These properties are set via command line properties by our CI and so must be set by
 * local.properties when running local builds.
 */

val appVersionCode: Int =
    cliProps.code ?:
    localProps.code ?:
    throw InvalidUserDataException("No versionCode provided. You must provide a version code via CLI properties or local.properties.")

val appVersionName: String =
    cliProps.name ?:
    localProps.name ?:
    throw InvalidUserDataException("No versionName provided. You must provide a version name via CLI properties or local.properties.")

android {
    compileSdk = AppConfig.compileSdk
    buildToolsVersion = AppConfig.buildTools

    defaultConfig {
        applicationId = "com.mattrobertson.greek.reader"

        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk

        versionCode = appVersionCode
        versionName = appVersionName
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
    implementation(projects.feature.tutorial)
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