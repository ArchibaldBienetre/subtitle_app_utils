plugins {
    id("org.jetbrains.kotlin.multiplatform") version "1.3.72"
    jacoco
}

repositories {
    mavenCentral()
}

group = "com.example"
version = "1.0.1-SNAPSHOT"

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

kotlin.sourceSets.named("jvmMain") {
    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("com.google.guava:guava:29.0-android")

        // CLI:
        implementation("commons-cli:commons-cli:1.4")
        implementation("args4j:args4j:2.33")
    }

}

kotlin.sourceSets.named("jvmTest") {
    dependencies {
        implementation(kotlin("test"))
        implementation(kotlin("test-junit"))
        implementation("org.quartz-scheduler:quartz:2.3.2")
        implementation("ch.qos.logback:logback-classic:1.0.13")
    }
}

kotlin.sourceSets.named("jsMain") {
    dependencies {
        implementation(kotlin("stdlib-js"))
    }
}

kotlin.sourceSets.named("jsTest") {
    dependencies {
        implementation(kotlin("test-js"))
    }
}

kotlin.sourceSets.named("linuxMain") {
}

kotlin.sourceSets.named("linuxTest") {
}

// RG: I learned how to do this here:
// https://medium.com/@preslavrachev/kotlin-basics-create-executable-kotlin-jars-using-gradle-d17e9a8384b9
// and https://docs.gradle.org/6.4/userguide/working_with_files.html#sec:creating_uber_jar_example
tasks.named<Jar>("jvmJar") {
    manifest {
        attributes(
            // https://docs.oracle.com/javase/6/docs/technotes/guides/jar/jar.html#JAR%20Manifest
            // https://docs.oracle.com/javase/6/docs/technotes/guides/extensions/versioning.html
            mapOf(
                Pair("Main-Class", "org.example.subtitles.cli.SimpleCliKt"),
                Pair("Implementation-Version", project.version)
            )
        )
    }

    from({
        configurations.named("jvmCompileClasspath").get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })
}

// to still run LearningTest classes, add "-PdoRunLearningTests" to your gradle command
if (!project.hasProperty("doRunLearningTests")) {
    tasks.named<Test>("jvmTest") {
        filter {
            excludeTestsMatching("*LearningTest")
        }
    }
}

// ### Test Coverage + Coverage Verification ###
// inspired by https://stackoverflow.com/q/45464138/1143126
// and https://stackoverflow.com/a/49161924/1143126
val jacocoTestReport =
    tasks.register<JacocoReport>("jacocoTestReport") {
        reports {
            xml.isEnabled = true
            html.isEnabled = true
        }
        sourceDirectories.setFrom(
            files(
                "$projectDir/src/commonMain/kotlin",
                "$projectDir/src/jvmMain/kotlin"
            )
        )
        classDirectories.setFrom(
            fileTree("$buildDir/classes").matching {
                exclude("**/test/**")
            }
        )
        executionData.setFrom(
            fileTree("$buildDir").matching {
                include("jacoco/*Test.exec")
            }
        )
    }

val jacocoTestCoverageVerification =
    tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
        violationRules {
            rule {
                element = "CLASS"
                // whitelist - not recognized properly by JaCoCo
                excludes = listOf(
                    "*.DefaultImpls", "*.Companion", "*.Factory", "*.1",
                    "org.example.subtitles.cli.BasicCommandLineParams",
                    "org.example.subtitles.cli.CommandLineArgsParser",
                    "org.example.subtitles.cli.CommandLineArgsParserApacheCommonsImpl",
                    "org.example.subtitles.cli.ModificationCommandLineParams",
                    "org.example.subtitles.cli.SimpleCliKt",
                    "org.example.subtitles.cli.TimedSubtitlePrinter",
                    "org.example.subtitles.modification.SubtitlesTransformer"
                )

                includes = listOf("org.example.*")

                limit {
                    minimum = "0.95".toBigDecimal()
                }
            }

            // ## the following have been covered by tests, but test coverage is not properly recognized ## //
            rule {
                element = "CLASS"
                includes = listOf("org.example.subtitles.modification.SubtitlesTransformer", "*.Factory")

                limit {
                    minimum = "0.83".toBigDecimal()
                }
            }
            rule {
                element = "CLASS"
                includes = listOf(
                    "org.example.subtitles.serialization.SubtitleReader.DefaultImpls"
                )

                limit {
                    minimum = "0.72".toBigDecimal()
                }
            }
            rule {
                element = "CLASS"
                includes = listOf(
                    "org.example.subtitles.cli.SimpleCliKt"
                )

                limit {
                    minimum = "0.77".toBigDecimal()
                }
            }
            rule {
                element = "CLASS"
                includes = listOf(
                    "org.example.subtitles.cli.TimedSubtitlePrinter"
                )

                limit {
                    minimum = "0.69".toBigDecimal()
                }
            }
        }
        sourceDirectories.setFrom(
            files(
                "$projectDir/src/commonMain/kotlin",
                "$projectDir/src/jvmMain/kotlin"
            )
        )
        classDirectories.setFrom(
            fileTree("$buildDir/classes").matching {
                exclude("**/test/**")
            }
        )
        executionData.setFrom(
            fileTree("$buildDir").matching {
                include("jacoco/*Test.exec")
            }
        )
    }

tasks.withType(Test::class).forEach {
    it.finalizedBy(jacocoTestReport)
    it.finalizedBy(jacocoTestCoverageVerification)
}
jacocoTestCoverageVerification {
    dependsOn(jacocoTestReport)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}