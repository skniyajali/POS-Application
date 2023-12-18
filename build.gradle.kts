buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {}
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.realm) apply false
    alias(libs.plugins.sentry)
    alias(libs.plugins.androidx.baselineprofile) apply false
    alias(libs.plugins.androidx.benchmark) apply false
}

