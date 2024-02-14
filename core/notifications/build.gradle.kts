plugins {
    alias(libs.plugins.popos.android.library)
    alias(libs.plugins.popos.android.hilt)
}

android {
    namespace = "com.niyaj.core.notifications"

}

dependencies {
    api(project(":core:model"))

    implementation(project(":core:common"))

    compileOnly(platform(libs.androidx.compose.bom))
    compileOnly(libs.androidx.compose.runtime)
}