plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = Versions.compileSdk
    buildToolsVersion = Versions.buildTools

    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
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
}

dependencies {

    implementation(projects.core.verseref)

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")

    implementation("com.google.code.gson:gson:2.8.6")
    implementation("androidx.preference:preference-ktx:1.1.1")

    testImplementation("androidx.test:core-ktx:1.4.0")
    testImplementation("org.robolectric:robolectric:4.3")

    testImplementation("androidx.test.ext:junit:1.1.3")
    testImplementation("androidx.test:runner:1.4.0")
    testImplementation("androidx.test:rules:1.4.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.7.0")
}