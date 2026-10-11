plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.mattrobertson.greek.reader.plans"
    compileSdk = AppConfig.compileSdk
    buildToolsVersion = AppConfig.buildTools

    defaultConfig {
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = AppConfig.javaCompatibility
        targetCompatibility = AppConfig.javaCompatibility
    }

    kotlinOptions { jvmTarget = AppConfig.jvmTarget }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get() }
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.verseref)
    implementation(libs.androidCoreKtx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.bundles.compose)
    testImplementation("junit:junit:4.13.2")
}
