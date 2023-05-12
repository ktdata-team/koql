plugins {
    id("java")
    kotlin("jvm")
    `maven-publish`
}

group = "com.koql"
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    api(project(":koql-base"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
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
            artifactId = "koql-dialect-pgsql"

            from(components["java"])
        }
    }
}
