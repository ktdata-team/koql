pluginManagement {
    repositories {
        mavenLocal()
//        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://clojars.org/repo/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven { setUrl("https://plugins.gradle.org/m2/") }
    }

    plugins {
        val kotlinVersion:String by settings
        id( "org.jetbrains.kotlin.jvm")  version kotlinVersion
        id ("org.jetbrains.kotlin.plugin.noarg")  version kotlinVersion
        id ("org.jetbrains.kotlin.plugin.allopen")  version kotlinVersion
        kotlin("kapt") version kotlinVersion

    }


}


rootProject.name = "koql"
include("koql-base")
include("koql-dialect-pgsql")
include("koql-vertx-pool-executor")
