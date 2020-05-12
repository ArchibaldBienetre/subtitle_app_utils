plugins {
    id("org.jetbrains.kotlin.multiplatform") version "1.3.72"
}

repositories {
    mavenCentral()

    maven {
        url = project.uri("https://dl.bintray.com/mockito/maven")
    }
}

group = "com.example"
version = "1.0.1-SNAPSHOT"

apply(plugin = "maven-publish")
apply(plugin = "jacoco")

apply(from = "other.gradle")

