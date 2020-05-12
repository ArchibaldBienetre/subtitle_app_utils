import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

kotlin {
    jvm()
    js {
        browser {
        }
        nodejs {
        }
    }
    // For ARM, should be changed to iosArm32 or iosArm64
    // For Linux, should be changed to e.g. linuxX64
    // For MacOS, should be changed to e.g. macosX64
    // For Windows, should be changed to e.g. mingwX64
    linuxX64("linux")
    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}

kotlin.sourceSets.getByName("jvmMain") {
    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("com.google.guava:guava:29.0-android")

        // CLI:
        implementation("commons-cli:commons-cli:1.4")
    }

}

kotlin.sourceSets.getByName("jvmTest") {
    dependencies {
        implementation(kotlin("test"))
        implementation(kotlin("test-junit"))
        implementation("org.quartz-scheduler:quartz:2.3.2")
        implementation("ch.qos.logback:logback-classic:1.0.13")
    }
}

kotlin.sourceSets.getByName("jsMain") {
    dependencies {
        implementation(kotlin("stdlib-js"))
    }
}

kotlin.sourceSets.getByName("jsTest") {
    dependencies {
        implementation(kotlin("test-js"))
    }
}

kotlin.sourceSets.getByName("linuxMain") {
}

kotlin.sourceSets.getByName("linuxTest") {
}

apply(from = "other.gradle")

