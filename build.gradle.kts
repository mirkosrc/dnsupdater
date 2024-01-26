import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val mockKVersion = "1.13.8"
val kotlinLoggingVersion = "3.0.5"
val springmockkVersion = "4.0.2"

plugins {
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.github.ben-manes.versions") version "0.50.0"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
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
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
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
                implementation("org.springframework.boot:spring-boot-starter-test")
                implementation("org.springframework.boot:spring-boot-starter-web")
                implementation("io.mockk:mockk:$mockKVersion")
                implementation("com.ninja-squad:springmockk:$springmockkVersion")
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

