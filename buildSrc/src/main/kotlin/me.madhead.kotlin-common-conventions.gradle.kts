import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
}

repositories {
    mavenCentral()
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
            )
        }
    }
}
