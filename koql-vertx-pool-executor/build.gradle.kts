plugins {
    id("java")
    kotlin("jvm")
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
    api(project(":koql-base"))
    api(group = "io.vertx", name = "vertx-sql-client-templates", version = vertxVersion)
    api(group = "io.vertx", name = "vertx-lang-kotlin", version = vertxVersion)
    api(group = "io.vertx", name = "vertx-lang-kotlin-coroutines", version = vertxVersion)
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    api(kotlin("stdlib", kotlinVersion))
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = kotlinCoroutineVersion)
    api(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-jdk8", version = kotlinCoroutineVersion)
    api(kotlin("reflect", kotlinVersion))
    testImplementation(project(":koql-dialect-pgsql"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testImplementation(group = "io.vertx", name = "vertx-pg-client", version = vertxVersion)
    testImplementation("com.ongres.scram", "client", "2.1")
    testImplementation("com.github.freva:ascii-table:1.8.0")
    testImplementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
    testImplementation("ch.qos.logback:logback-classic:1.4.6")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
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
