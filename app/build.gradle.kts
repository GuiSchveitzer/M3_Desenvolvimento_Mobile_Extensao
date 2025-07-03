plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.m3_desenvolvimento_mobile_extensao"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.m3_desenvolvimento_mobile_extensao"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    // Conversor JSON (de/para objetos Java)
    implementation("com.google.code.gson:gson:2.11.0")

    // --- Arquitetura Android Jetpack: ViewModel + LiveData ---
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")  // Para armazenar e gerenciar dados da UI
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")    // Para dados observáveis reativos

    // --- Room: Banco de dados local SQLite com ORM ---
    implementation("androidx.room:room-runtime:2.6.1")         // Runtime do Room
    annotationProcessor("androidx.room:room-compiler:2.6.1")   // Necessário para gerar o código do Room (somente Java)

    // --- Retrofit: Requisições HTTP e API REST ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")                // Biblioteca principal
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")         // Conversor de JSON → Java usando Gson

    // --- WorkManager: Tarefas em segundo plano (ex: notificações agendadas) ---
    implementation("androidx.work:work-runtime:2.9.0")   // Agendador de tarefas moderno e resiliente

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
