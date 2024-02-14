plugins {
    alias(libs.plugins.popos.android.library)
    alias(libs.plugins.popos.android.library.jacoco)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.niyaj.core.domain"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.pos.printer)

    testImplementation(project(":core:testing"))
}