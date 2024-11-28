plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") // Para habilitar KAPT
    id("dagger.hilt.android.plugin") // Plugin de Hilt
}

android {
    namespace = "com.upao.elmochicaapp"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.upao.elmochicaapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 5
        versionName = "5.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Carga los archivos .aar desde la carpeta libs
    implementation(files("libs/izipay-sdk-2.0.0.aar"))
    implementation(files("libs/sonic-sdk-release-1.4.0.aar"))
    implementation(files("libs/TMXDeviceSecurityHealth-RL-6.3-77.aar"))
    implementation(files("libs/TMXProfiling-RL-6.3-77.aar"))
    implementation(files("libs/visa-sensory-branding-2.1.aar"))
    implementation(files("libs/TMXAuthentication-RL-6.3-77.aar"))
    implementation(files("libs/TMXBehavioralBiometrics-RL-6.3-77.aar"))
    implementation(files("libs/TMXProfilingConnections-RL-6.3-77.aar"))
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")
    implementation("com.github.skydoves:powerspinner:1.2.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("com.github.skydoves:balloon:1.3.6")
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // Material Components para TabLayout
    implementation("com.google.android.material:material:1.9.0")
    // Retrofit y Coroutines
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-compiler:2.44")

    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kapt {
    correctErrorTypes = true
}
