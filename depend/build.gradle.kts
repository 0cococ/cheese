import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)

}
object Config {
    object Projects {
        // https://mvnrepository.com/artifact/net.bytebuddy/byte-buddy
        const val BYTE_BUDDY = "net.bytebuddy:byte-buddy:${Versions.BYTE_BUDDY}"
        const val JAVENODE = "com.caoccao.javet:javenode:${Versions.JAVENODE}"
        const val JAVET__GROUP = "com.caoccao.javet"
        const val JAVET__MODULE = "javet"
        const val JAVET_ANDROID = "com.caoccao.javet:javet-android-v8:${Versions.JAVET}"
        const val JAVET_NODE_ANDROID = "com.caoccao.javet:javet-android-node:${Versions.JAVET_NODE}"
        const val OAID = "com.github.gzu-liyujiang:Android_CN_OAID:${Versions.OAID}"
        const val HMS = "com.huawei.hms:ads-identifier:${Versions.HMS}"
        // https://mvnrepository.com/artifact/io.vertx/vertx-core
        const val VERTX = "io.vertx:vertx-core:${Versions.VERTX}"
    }
    object Versions {
        const val BYTE_BUDDY = "1.14.10"
        const val JAVENODE = "0.5.0"
        const val JAVET = "3.1.0"
        const val JAVET_NODE = "3.1.0"
        const val JETTY_WEBSOCKET = "9.4.53.v20231009"
        const val VERTX = "4.5.0"
        const val OAID = "4.2.7"
        const val HMS = "3.4.62.300"
    }
}
android {
    namespace = "coco.cheese.depend"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    api(fileTree(file("libs")) {
        include("*.jar")
        include("*.aar")
    })
//    api(Config.Projects.JAVENODE) {
//        exclude(group = Config.Projects.JAVET__GROUP, module = Config.Projects.JAVET__MODULE)
//    }
    api("net.lingala.zip4j:zip4j:1.3.1")
    api ("com.github.tiann:FreeReflection:3.2.0")
    api ("androidx.webkit:webkit:1.10.0")
    api (project(":opencv"))
    api (project(":mirror"))
    api (project(":yolo"))
    api ("io.insert-koin:koin-android:3.5.3")
    api ("com.elvishew:xlog:1.11.0")
    api ("com.github.getActivity:XXPermissions:18.5")
    api ("com.github.getActivity:Toaster:12.5")
    api ("com.github.getActivity:EasyWindow:10.3")
    api  ("com.tencent.bugly:crashreport:latest.release")
    api ("com.yanzhenjie.andserver:api:2.1.12")
    api ("com.github.CodingGay.BlackReflection:core:1.1.4")
    api ("com.linkedin.dexmaker:dexmaker:2.28.3")
    api ("com.linkedin.dexmaker:dexmaker-mockito:2.28.3")
    api("com.squareup.okhttp3:okhttp:4.12.0")
    api("com.google.code.gson:gson:2.10")
    api("com.github.Vove7.Android-Accessibility-Api:accessibility-api:4.1.2")
    api("androidx.compose.runtime:runtime:1.6.6")

    api(Config.Projects.BYTE_BUDDY)
    api(Config.Projects.JAVET_NODE_ANDROID)
    api(Config.Projects.VERTX)
    api(Config.Projects.OAID)
    api(Config.Projects.HMS)

//    ksp ("com.yanzhenjie.andserver:processor:2.1.12")
//    ksp ("com.github.CodingGay.BlackReflection:compiler:1.1.4")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}