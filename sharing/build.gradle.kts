// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
}

buildscript {
    dependencies {
        classpath(libs.gradle.license.plugin)
    }
}

val minSdkVersion by extra(23)
val targetSdkVersion by extra(36)
val compileSdkVersion by extra(36)
val buildToolsVersion by extra("36.1.0")
val ndkVersion by extra("29.0.14206865")