plugins {
    id("com.android.application")
}

android {
    namespace = "com.coolet.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.coolet.app.preview"
        minSdk = 26
        targetSdk = 35
        versionCode = 5
        versionName = "0.2.3"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
