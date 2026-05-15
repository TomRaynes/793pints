import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.buildKonfig)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "Shared"
            isStatic = true
            // Bundle ID needed for the iOS linker; matches the Xcode app's PRODUCT_BUNDLE_IDENTIFIER.
            binaryOption("bundleId", "com.pints793.mobile.shared")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.json)

            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.coroutines)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.jetbrains.navigation.compose)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.androidx.security.crypto)
            implementation(libs.androidx.activity.compose)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.pints793.mobile.shared"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

buildkonfig {
    packageName = "com.pints793.mobile.config"
    defaultConfigs {
        // Default = dev. iOS simulator and Android emulator each map to a host loopback.
        // Override at build time:  ./gradlew … -PIOS_BASE_URL=https://abcd.ngrok-free.app/api/v1
        // …or uncomment IOS_BASE_URL in mobile/gradle.properties (or ~/.gradle/gradle.properties).
        val androidBaseUrl = (findProperty("ANDROID_BASE_URL") as String?) ?: "http://10.0.2.2:8080/api/v1"
        val iosBaseUrl     = (findProperty("IOS_BASE_URL")     as String?) ?: "http://localhost:8080/api/v1"
        logger.lifecycle("📡  ANDROID_BASE_URL = $androidBaseUrl")
        logger.lifecycle("📡  IOS_BASE_URL     = $iosBaseUrl")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "ANDROID_BASE_URL", androidBaseUrl)
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "IOS_BASE_URL",     iosBaseUrl)
    }
}

