plugins {
    alias(libs.plugins.popos.android.feature)
    alias(libs.plugins.popos.android.library.compose)
    alias(libs.plugins.popos.android.library.jacoco)
}

android {
    namespace = "com.niyaj.feature.printer"

    ksp {
        arg("compose-destinations.moduleName", "printer")
        arg("compose-destinations.mode", "navgraphs")
        arg("compose-destinations.useComposableVisibility", "true")
    }
}

dependencies {
    api(project(":feature:print"))

    implementation(libs.accompanist.permissions)
    implementation(libs.pos.printer)
}