import com.niyaj.samples.apps.popos.PoposBuildType

plugins {
    id("popos.android.application")
    id("popos.android.application.compose")
    id("popos.android.application.flavors")
    id("popos.android.application.jacoco")
    id("popos.android.hilt")
    id("jacoco")
    id(libs.plugins.realm.get().pluginId)
    alias(libs.plugins.appsweep)
    alias(libs.plugins.sentry)
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    defaultConfig {
        applicationId = libs.versions.namespace.get()
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
        testInstrumentationRunner = "com.niyaj.popos.HiltTestRunner"
        manifestPlaceholders.putAll(mapOf("sentryEnvironment" to "production"))
    }

    buildTypes {
        debug {
            applicationIdSuffix = PoposBuildType.DEBUG.applicationIdSuffix
        }

        val release = getByName("release") {
            isMinifyEnabled = true
            applicationIdSuffix = PoposBuildType.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            // To publish on the Play store a private signing key is required, but to allow anyone
            // who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            signingConfig = signingConfigs.getByName("debug")
            // Ensure Baseline Profile is fresh for release builds.
//            baselineProfile.automaticGenerationDuringBuild = true
        }

        create("benchmark") {
            // Enable all the optimizations from release build through initWith(release).
            initWith(release)
            matchingFallbacks.add("release")
            // Debug key signing is available to everyone.
            signingConfig = signingConfigs.getByName("debug")
            // Only use benchmark proguard rules
            proguardFiles("benchmark-rules.pro")
            isMinifyEnabled = true
            applicationIdSuffix = PoposBuildType.BENCHMARK.applicationIdSuffix
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
            excludes.add("META-INF/LICENSE.md")
            excludes.add("META-INF/LICENSE-notice.md")
            excludes.add("DebugProbesKt.bin")
        }
    }

    appsweep {
        apiKey = "gs_appsweep_2sU1w2D_VgxRVNNmlBhWdMawvtcd6wZHHZkcsUSz"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    namespace = libs.versions.namespace.get()
}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)

    //Startup & Splash screen
    implementation(libs.androidx.core.startup)
    implementation(libs.androidx.core.splashscreen)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // Saved state module for ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    // Compose dependencies
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Kotlin + coroutines
    implementation(libs.androidx.work.ktx)
    androidTestImplementation(libs.androidx.work.testing)

    //Accompanist
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.swiperefresh)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // For testing
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    // Timber
    implementation(libs.timber)

    //RevealSwipe
    implementation(libs.revealswipe)

    //Pos.printer
    implementation(libs.pos.printer)

    //Realm
    implementation(libs.realm.library.base)

    // debugImplementation because LeakCanary should only run in debug builds.
    debugImplementation(libs.leakcanary)

    //Compose Material Dialogs
    implementation(libs.dialog.core)
    implementation(libs.dialog.datetime)

    //RaamCosta Library
    implementation(libs.raamcosta.animation.core)
    ksp(libs.raamcosta.ksp)

    //Moshi
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.moshi)

    //ProfileInstaller
    implementation(libs.androidx.profileinstaller)

    // Local unit tests
    testImplementation(libs.junit4)
    androidTestImplementation(libs.junit4)

    androidTestImplementation(libs.androidx.test.espresso.core)
    testImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.runner)

    testImplementation(libs.androidx.arch.core.testing)
    androidTestImplementation(libs.androidx.arch.core.testing)

    androidTestImplementation(libs.androidx.compose.ui.test)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.testManifest)

    // Truth
    testImplementation(libs.truth)
    androidTestImplementation(libs.truth)

    //Mockk
    androidTestImplementation(libs.mockk.android)
    testImplementation(libs.mockk)

    //ACRA Logger
    implementation(libs.acra.mail)
    implementation(libs.acra.toast)

    //Sentry
    implementation(libs.sentry.android)
    implementation(libs.sentry.compose.android)

    //Baseline Profile
    "baselineProfile"(project(mapOf("path" to ":benchmark")))

    //Google Play Play Integrity API
    implementation(libs.play.integrity)

    // Google Play In-App Updates API
    implementation(libs.play.app.update)
    implementation(libs.play.app.update.ktx)

    // Coil Library
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)

    // zxing QR code library
    implementation(libs.zxing.core)

    // Play GMS Scanner library
    implementation(libs.play.gms.scanner)

    // Play Service Base
    implementation(libs.play.service)
}

baselineProfile {
    // Don't build on every iteration of a full assemble.
    // Instead enable generation directly for the release build variant.
    automaticGenerationDuringBuild = false
}