plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
    explicitApi()
    jvmToolchain(17)
}

android {
    namespace = "com.lastweek.sharing.mjpeg"
    compileSdk = rootProject.extra["compileSdkVersion"] as Int
    buildToolsVersion = rootProject.extra["buildToolsVersion"] as String

    defaultConfig {
        minSdk = rootProject.extra["minSdkVersion"] as Int
    }

    buildFeatures {
        compose = true
    }

    androidResources {
        ignoreAssetsPattern = "!dev"
    }
}

dependencies {
    api(projects.common)
    api(libs.ktor.server.cio)
    api(libs.ktor.server.compression)
    api(libs.ktor.server.caching.headers)
    api(libs.ktor.server.default.headers)
    api(libs.ktor.server.forwarded.header)
    api(libs.ktor.server.cors)
    api(libs.ktor.server.websockets)
    api(libs.ktor.server.status.pages)


    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

configurations.all {
    exclude("org.fusesource.jansi", "jansi")
}