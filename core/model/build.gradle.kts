plugins {
    alias(libs.plugins.popos.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.niyaj.core.model"
}

dependencies {
    implementation(project(":core:common"))

    api(libs.androidx.compose.runtime)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.collections.immutable)

    api(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)

}