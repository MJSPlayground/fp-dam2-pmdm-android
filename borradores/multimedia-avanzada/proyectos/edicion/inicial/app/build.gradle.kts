plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}
android {
    namespace = "es.mjsplayground.pmdm.multimedia.avanzada.edicion"
    compileSdk = 36
    defaultConfig {
        applicationId = "es.mjsplayground.pmdm.multimedia.avanzada.edicion"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    lint { abortOnError = true }
}
kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
dependencies {
    implementation(libs.activity.compose)
    implementation(libs.core)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.media.transformer)
    implementation(libs.media.effect)
    implementation(libs.media.exoplayer)
    implementation(libs.media.ui)
    implementation(libs.media.hls)
    implementation(libs.media.rtsp)
    implementation(libs.cropper)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.test)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.runner)
    debugImplementation(libs.compose.test.manifest)
}
