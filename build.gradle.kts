plugins {
    kotlin("jvm") version Versions.KOTLIN
    kotlin("kapt") version Versions.KOTLIN
    kotlin("plugin.spring") version Versions.KOTLIN
    kotlin("plugin.jpa") version Versions.KOTLIN

    id("org.springframework.boot") version Versions.SPRING_BOOT
    id("io.spring.dependency-management") version Versions.DEPENDENCY_MANAGEMENT
    id("io.gitlab.arturbosch.detekt") version Versions.DETEKT
    idea
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")

        plugin("kotlin")
        plugin("kotlin-kapt")
        plugin("kotlin-spring")
        plugin("kotlin-jpa")
        plugin("java-test-fixtures")
        plugin("idea")
    }

    dependencies {
        kapt("org.springframework.boot:spring-boot-configuration-processor")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.springframework.boot:spring-boot-starter")

        testImplementation("io.mockk:mockk:${Versions.MOCKK}")
        testImplementation("io.kotest:kotest-runner-junit5:${Versions.KOTEST}")
        testImplementation("io.kotest:kotest-assertions-core:${Versions.KOTEST}")
        testImplementation("io.kotest:kotest-property:${Versions.KOTEST}")
        testImplementation("io.kotest.extensions:kotest-extensions-spring:${Versions.KOTEST_EXTENSION_SPRING}")
        testImplementation("org.springframework.boot:spring-boot-starter-test") { exclude(group = "org.mockito") }
    }

    val integrationTestSourceSet = "integrationTest"
    val testFixturesSourceSet = "testFixtures"

    tasks {
        jar { enabled = true }
        bootJar { enabled = false }
        compileKotlin {
            kotlinOptions.jvmTarget = "17"
            kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = "17"
        }
        test {
            useJUnitPlatform()
        }
    }

    sourceSets {
        create(integrationTestSourceSet) {
            compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
            runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        }
    }
    configurations["${integrationTestSourceSet}RuntimeOnly"].extendsFrom(configurations.testRuntimeOnly.get())
    configurations["${integrationTestSourceSet}CompileOnly"].extendsFrom(configurations.testCompileOnly.get())
    configurations["${integrationTestSourceSet}Implementation"].extendsFrom(configurations.testImplementation.get())

    task<Test>(integrationTestSourceSet) {
        testClassesDirs = sourceSets[integrationTestSourceSet].output.classesDirs
        classpath = sourceSets[integrationTestSourceSet].runtimeClasspath
        useJUnitPlatform()
    }

    idea {
        module {
            testSources.from(sourceSets[integrationTestSourceSet].allSource.sourceDirectories.files)
            testSources.from(sourceSets[testFixturesSourceSet].allSource.sourceDirectories.files)
        }
    }
}
