plugins {
    alias(libs.plugins.popos.android.feature)
    alias(libs.plugins.popos.android.library.compose)
    alias(libs.plugins.popos.android.library.jacoco)
}

android {
    namespace = "com.niyaj.feature.product"

    ksp {
        arg("compose-destinations.moduleName", "product")
        arg("compose-destinations.mode", "navgraphs")
        arg("compose-destinations.useComposableVisibility", "true")
    }

}

dependencies {
    implementation(libs.dialog.core)
    implementation(libs.accompanist.permissions)
}