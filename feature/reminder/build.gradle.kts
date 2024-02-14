plugins {
    alias(libs.plugins.popos.android.feature)
    alias(libs.plugins.popos.android.library.compose)
    alias(libs.plugins.popos.android.library.jacoco)
}

android {
    namespace = "com.niyaj.feature.reminder"

    ksp {
        arg("compose-destinations.moduleName", "reminder")
        arg("compose-destinations.mode", "navgraphs")
        arg("compose-destinations.useComposableVisibility", "true")
    }
}

dependencies {
    api(project(":core:notifications"))

    implementation(libs.dialog.core)
    implementation(libs.dialog.datetime)
    implementation(libs.revealswipe)
}