plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    java
    kotlin("jvm")
    `maven-publish`
}

group = "com.koql"
version = rootProject.version

repositories {
    mavenCentral()
}
val vertxVersion: String by project
val kotlinVersion: String by project
val kotlinCoroutineVersion: String by project
val kotlinLoggingVersion: String by project
val jacksonVersion: String by project
dependencies {
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    api(kotlin("stdlib", kotlinVersion))
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = kotlinCoroutineVersion)
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-jdk8", version = kotlinCoroutineVersion)
    api(kotlin("reflect", kotlinVersion))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
tasks {

    compileJava {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
    }
    compileTestJava {
        sourceCompatibility = JavaVersion.VERSION_17.toString()

    }
    compileKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    }

}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.ktdata-team.koql"
            artifactId = "koql-base"

            from(components["java"])
        }
    }
}
