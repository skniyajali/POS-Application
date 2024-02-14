plugins {
    alias(libs.plugins.popos.android.library)
    alias(libs.plugins.popos.android.library.compose)
    alias(libs.plugins.popos.android.hilt)
}

android {
    namespace = "com.niyaj.core.testing"

}

dependencies {
    api(kotlin("test"))
    api(libs.androidx.compose.ui.test)
    api(libs.roborazzi)
    api(project(":core:analytics"))
    api(project(":core:data"))
    api(project(":core:model"))
    api(project(":core:notifications"))

    debugApi(libs.androidx.compose.ui.testManifest)

    implementation(libs.accompanist.testharness)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.test.rules)
    implementation(libs.hilt.android.testing)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.kotlinx.datetime)
    implementation(libs.robolectric.shadows)
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
}