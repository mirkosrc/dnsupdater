import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.springframework.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.ben.manes.versions)
    alias(libs.plugins.littlerobots.version.catalog.update)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    `jvm-test-suite`
}

group = "net.flyingelectrons"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.microutils.kotlin.logging)
    
    testImplementation(libs.spring.boot.starter.test)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        register<JvmTestSuite>("componentTest") {
            dependencies {
                implementation(project())
                implementation(libs.spring.boot.starter.test)
                implementation(libs.spring.boot.starter.web)
                implementation(libs.io.mockk)
                implementation(libs.com.ninja.squad.springmockk)
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    rejectVersionIf {
        listOf("alpha", "beta", "m", "b", "rc").any {
            candidate.version.lowercase().contains(it)
        }
    }
}

