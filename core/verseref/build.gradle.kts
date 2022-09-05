plugins {
    id("com.android.library")
    id("kotlin-android")
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

//    testOptions {
//        unitTests.all {
//            useJUnitPlatform()
//        }
//        unitTests.includeAndroidResources = true
//    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.7.0")
}