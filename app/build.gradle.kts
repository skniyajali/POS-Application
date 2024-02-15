import com.niyaj.samples.apps.popos.PoposBuildType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.popos.android.application)
    alias(libs.plugins.popos.android.application.compose)
    alias(libs.plugins.popos.android.application.flavors)
    alias(libs.plugins.popos.android.application.jacoco)
    alias(libs.plugins.popos.android.hilt)
    alias(libs.plugins.popos.android.realm)
    id("jacoco")
    alias(libs.plugins.popos.android.application.firebase)
    id("com.google.android.gms.oss-licenses-plugin")
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sentry)
    alias(libs.plugins.appsweep)
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
        debug {
            applicationIdSuffix = PoposBuildType.DEBUG.applicationIdSuffix
        }
        val release = getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            applicationIdSuffix = PoposBuildType.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            // To publish on the Play store a private signing key is required, but to allow anyone
            // who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            signingConfig = signingConfigs.getByName("debug")
            // Ensure Baseline Profile is fresh for release builds.
            baselineProfile.automaticGenerationDuringBuild = true
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
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
        apiKey = "gs_appsweep_2sU1w2D_VgxRVNNmlBhWdMawvtcd6wZHHZkcsUSz"
    }

    allprojects {
        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
                if (project.findProperty("composeCompilerReports") == "true") {
                    freeCompilerArgs += listOf(
                        "-P",
                        "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.buildDir.absolutePath}/compose_compiler"
                    )
                }
                if (project.findProperty("composeCompilerMetrics") == "true") {
                    freeCompilerArgs += listOf(
                        "-P",
                        "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.buildDir.absolutePath}/compose_compiler"
                    )
                }
            }
        }
    }
}

dependencies {

    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:worker"))
    implementation(project(":core:analytics"))

    implementation(project(":feature:account"))
    implementation(project(":feature:addonitem"))
    implementation(project(":feature:address"))
    implementation(project(":feature:cart"))
    implementation(project(":feature:cart_selected"))
    implementation(project(":feature:cart_order"))
    implementation(project(":feature:category"))
    implementation(project(":feature:charges"))
    implementation(project(":feature:customer"))
    implementation(project(":feature:data_deletion"))
    implementation(project(":feature:employee"))
    implementation(project(":feature:employee_payment"))
    implementation(project(":feature:employee_attendance"))
    implementation(project(":feature:expenses"))
    implementation(project(":feature:expenses_category"))
    implementation(project(":feature:home"))
    implementation(project(":feature:order"))
    implementation(project(":feature:print"))
    implementation(project(":feature:printer"))
    implementation(project(":feature:product"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:reminder"))
    implementation(project(":feature:app_settings"))
//    implementation(project(":feature:printer_info"))
    implementation(project(":feature:reports"))

    androidTestImplementation(project(":core:testing"))
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.accompanist.testharness)

    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.kotlinx.coroutines.guava)

    androidTestImplementation(kotlin("test"))
    debugImplementation(libs.androidx.compose.ui.testManifest)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.flowlayout)
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.accompanist.placeholder.material)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.window.manager)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.kotlinx.coroutines.guava)

    // Sentry
    implementation(libs.sentry.android)
    implementation(libs.sentry.compose.android)

    //RaamCosta Library
    implementation(libs.raamcosta.animation.core)
    ksp(libs.raamcosta.ksp)

    // Timber
    implementation(libs.timber)

    //Acra
    implementation(libs.acra.toast)
    implementation(libs.acra.mail)

    // Vanpara Dialogs
    implementation(libs.dialog.core)
    implementation(libs.dialog.datetime)

    //Truth
    implementation(libs.truth)

    //Turbine
    implementation(libs.turbine)

    //RevealSwipe
    implementation(libs.revealswipe)

    //Pos.printer
    implementation(libs.pos.printer)

    //Google Play Play Integrity API
    implementation(libs.play.integrity)

    // Google Play In-App Updates API
    implementation(libs.play.app.update)
    implementation(libs.play.app.update.ktx)

    // zxing QR code library
    implementation(libs.zxing.core)

    // Play GMS Scanner library
    implementation(libs.play.gms.scanner)

    // Play Service Base
    implementation(libs.play.service)

    baselineProfile(project(":benchmark"))
}

baselineProfile {
    // Don't build on every iteration of a full assemble.
    // Instead enable generation directly for the release build variant.
    automaticGenerationDuringBuild = false
}