// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
//    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
    kotlin("kapt") version "1.9.0"
}

buildscript {
    dependencies {
        classpath("com.yanzhenjie.andserver:plugin:2.1.12")
    }
}
