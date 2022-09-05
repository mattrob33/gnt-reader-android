plugins {
    id("com.android.library")
    kotlin("android")
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

    implementation(libs.androidCoreKtx)
    implementation(libs.appcompat)

    implementation(libs.gson)
    implementation(libs.androidPreferenceKtx)

    testImplementation("androidx.test:core-ktx:1.4.0")
    testImplementation("org.robolectric:robolectric:4.3")

    testImplementation("androidx.test.ext:junit:1.1.3")
    testImplementation("androidx.test:runner:1.4.0")
    testImplementation("androidx.test:rules:1.4.0")

    testImplementation(libs.jUnit5.api)
    testImplementation(libs.jUnit5.params)
    testRuntimeOnly(libs.jUnit5.engine)
    testRuntimeOnly(libs.jUnit5.vintageEngine)
}