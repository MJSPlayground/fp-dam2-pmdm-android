plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}
android {
    namespace = "es.mjsplayground.pmdm.cartografia"
    compileSdk = 36
    defaultConfig {
        applicationId = "es.mjsplayground.pmdm.cartografia"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures { compose = true; buildConfig = true }
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
    implementation(libs.lifecycle.runtime)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    debugImplementation(libs.compose.tooling)
    implementation(libs.maplibre)
    implementation(libs.coroutines)
    implementation(libs.serialization.json)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.test)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.runner)
    debugImplementation(libs.compose.test.manifest)
}

tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
    systemProperty("aprendizaje.geocerca", providers.gradleProperty("aprendizaje.geocerca").orNull ?: "false")
}
