plugins {
    alias(libs.plugins.popos.android.library)
    alias(libs.plugins.popos.android.library.jacoco)
    alias(libs.plugins.popos.android.hilt)
    alias(libs.plugins.popos.android.realm)
}

android {
    namespace = "com.niyaj.database"
}

dependencies {

    implementation(project(":core:model"))
    implementation(project(":core:common"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.collections.immutable)

    androidTestImplementation(project(":core:testing"))

    implementation(libs.timber)

    //Moshi
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.moshi)
}