plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.chaquo.python")
}

android {
    namespace = "coco.cheese.plugin.python"
    compileSdk = 34

    defaultConfig {
        applicationId = "coco.cheese.plugin.python"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            abiFilters.clear()
//            abiFilters.addAll(listOf("x86_64"))
//            abiFilters.addAll(listOf("x86"))
//            abiFilters.addAll(listOf("arm64-v8a"))
//            abiFilters.addAll(listOf( "armeabi-v7a"))
            abiFilters.addAll(listOf("x86_64", "x86","arm64-v8a", "armeabi-v7a"))
        }
    }
    chaquopy {
        defaultConfig {
            buildPython("C://Users//35600//anaconda3//envs//test/python.exe")
            pip {}
        }
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}