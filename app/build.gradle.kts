@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.hilt.get().pluginId)
    id(libs.plugins.realm.get().pluginId)
    alias(libs.plugins.appsweep)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.baselineprofile)
    id(libs.plugins.kotlin.kapt.get().pluginId)
}

android {
    namespace = libs.versions.namespace.get()

    compileSdk = libs.versions.compileSdk.get().toInt()

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
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
                )
            )
        }

        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.4"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "DebugProbesKt.bin"
        }
        jniLibs {
            excludes += "lib/arm64-v8a/librealm-jni.so"
        }
    }

    appsweep {
        apiKey = "gs_appsweep_83ofXxg_qCVtI5igS4M0wofGmACUaMSdESX1c5m6"
    }

    hilt {
        enableAggregatingTask = true
    }
}

dependencies {

    implementation(libs.core.ktx)

    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.material)
    implementation(libs.material.icons)
    implementation(libs.ui.tooling.preview)

    implementation(libs.activity.compose)

    //Startup & Splash screen
    implementation(libs.startup)
    implementation(libs.splashscreen)

    implementation(libs.runtime.livedata)

    // ViewModel
    implementation(libs.viewmodel.ktx)
    implementation(libs.viewmodel.compose)
    implementation(libs.runtime.compose)
    implementation(libs.runtime.ktx)
    // Saved state module for ViewModel
    implementation(libs.viewmodel.savedstate)
    // Annotation processor
    implementation(libs.common.java8)

    // Compose dependencies
    implementation(libs.navigation.compose)

    // Kotlin + coroutines
    implementation(libs.work.runtime.ktx)
    androidTestImplementation(libs.work.testing)


    //Accompanist
    implementation(libs.flowlayout)
    implementation(libs.systemuicontroller)
    implementation(libs.permissions)
    implementation(libs.swiperefresh)
    implementation(libs.placeholder.material)
    implementation(libs.pager)
    implementation(libs.pager.indicators)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // For testing
    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.coroutines.test)

    //Hilt Work
    implementation(libs.hilt.work)
    kapt(libs.hilt.compiler)

    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.android)


    // Dagger & Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.dagger.compiler)

    // For testing
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.dagger.compiler)

    testImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.dagger.compiler)

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
    implementation(libs.profileinstaller)

    // Local unit tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    testImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.test.runner)

    testImplementation(libs.androidx.arch.core.testing)
    androidTestImplementation(libs.androidx.arch.core.testing)

    androidTestImplementation(libs.ui.test.junit)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    // Truth
    testImplementation(libs.truth)
    androidTestImplementation(libs.truth)

    //Mockk
    androidTestImplementation(libs.mockk.android)
    testImplementation(libs.mockk)

    //ACRA Logger
    implementation(libs.acra.mail)
    implementation(libs.acra.toast)
    implementation(libs.acra.notification)
    implementation(libs.acra.limiter)
    implementation(libs.acra.advanced.scheduler)

    //Sentry
    implementation(libs.sentry.android)
    implementation(libs.sentry.compose.android)

    //Baseline Profile
    "baselineProfile"(project(mapOf("path" to ":benchmark")))

    //Google Play Play Integrity API
    implementation(libs.integrity)
}

kapt {
    correctErrorTypes = true
}