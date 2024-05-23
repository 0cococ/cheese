plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "coco.cheese"
    compileSdk = 34

    defaultConfig {
        applicationId = "coco.cheese"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
//        packagingOptions {
//            exclude("lib/x86/**")
//        }
        ndk {
            abiFilters.clear()
//            abiFilters.addAll(listOf("x86_64"))
//            abiFilters.addAll(listOf("x86"))
//            abiFilters.addAll(listOf("arm64-v8a"))
//            abiFilters.addAll(listOf( "armeabi-v7a"))
            abiFilters.addAll(listOf("x86_64", "x86","arm64-v8a", "armeabi-v7a"))
        }
//        applicationVariants.all {
//            val flavorName = this.flavorName
//            this.outputs
//                .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
//                .forEach { output ->
//                    val versionName = "0.0.4"
//                    val outputFileName = "${flavorName}-${versionName}.apk"
//                    println("OutputFileName: $outputFileName")
//                    output.outputFileName = outputFileName
//                }
//        }
//        flavorDimensions("abi")
//        productFlavors {
//            create("all") {
//                dimension = "abi"
//                ndk{
//                    abiFilters.clear()
//                    abiFilters.addAll(listOf("x86_64", "x86","arm64-v8a", "armeabi-v7a"))
//                }
//            }
//            create("x86_64") {
//                dimension = "abi"
//                ndk{
//                    abiFilters.clear()
//                    abiFilters.add("x86_64")
//                }
//            }
//            create("x86") {
//                dimension = "abi"
//                ndk.abiFilters.clear()
//                ndk.abiFilters.add("x86")
//            }
//            create("arm64-v8a") {
//                dimension = "abi"
//                ndk.abiFilters.clear()
//                ndk.abiFilters.add("arm64-v8a")
//            }
//            create("armeabi-v7a") {
//                dimension = "abi"
//                ndk.abiFilters.clear()
//                ndk.abiFilters.add("armeabi-v7a")
//            }
//        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/io.netty.versions.properties"
        }
    }
}

dependencies {

    implementation (project(":core"))
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation (libs.androidx.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}