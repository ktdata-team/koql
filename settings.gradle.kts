pluginManagement {
    repositories {

        mavenLocal()
        maven("http://maven.aliyun.com/nexus/content/groups/public/")
        maven("https://dl.bintray.com/kotlin/exposed")
        maven("https://jitpack.io")
        jcenter()
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven("https://clojars.org/repo/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        mavenCentral()
        maven { setUrl("https://plugins.gradle.org/m2/") }
    }

    plugins {
        val kotlinVersion:String by settings
        id( "org.jetbrains.kotlin.jvm")  version kotlinVersion
        id ("org.jetbrains.kotlin.plugin.noarg")  version kotlinVersion
        id ("org.jetbrains.kotlin.plugin.allopen")  version kotlinVersion
        kotlin("kapt") version kotlinVersion

        id("com.google.cloud.tools.jib") version "1.7.0"
    }


}


rootProject.name = "koql"
include ("koql-dsl")
include("koql-core")
