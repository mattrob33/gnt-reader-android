import com.google.protobuf.gradle.*

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.google.protobuf") version "0.8.12"
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
    
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.3"
    }
}

dependencies {

    // Core
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")

    // Compose
    api("androidx.compose.ui:ui:1.0.3")
    api("androidx.compose.material:material:1.0.3")
    api("androidx.compose.material:material-icons-extended:1.0.3")
    api("androidx.compose.ui:ui-tooling:1.0.3")
    api("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    api("androidx.activity:activity-compose:1.3.1")
    api("androidx.navigation:navigation-compose:2.4.0-alpha06")

    // Datastore
    api( "com.google.protobuf:protobuf-javalite:3.10.0")
    api("androidx.datastore:datastore:1.0.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
}

protobuf {
    protoc {
        // latest version number: https://mvnrepository.com/artifact/com.google.protobuf/protoc
        artifact = "com.google.protobuf:protoc:3.10.0"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins{
                create("java") {
                    option("lite")
                }
            }
        }
    }
}