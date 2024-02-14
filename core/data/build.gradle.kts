plugins {
    alias(libs.plugins.popos.android.library)
    alias(libs.plugins.popos.android.library.jacoco)
    alias(libs.plugins.popos.android.hilt)
    alias(libs.plugins.popos.android.realm)
//    id("kotlinx-serialization")
}

android {
    namespace = "com.niyaj.core.data"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    api(project(":core:common"))
    api(project(":core:database"))

    implementation(project(":core:model"))
    implementation(project(":core:analytics"))
    implementation(project(":core:notifications"))

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.timber)

    implementation(libs.play.gms.scanner)
    implementation(libs.play.service)
    implementation(libs.play.app.update)

    testImplementation(project(":core:testing"))
}