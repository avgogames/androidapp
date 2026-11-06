import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

dependencies { implementation(project(":core")) }

application { mainClass.set("ExportKt") }

tasks.named<JavaExec>("run") {
    args(rootDir.resolve("../../src/core/catalog/catalog.json").canonicalPath)
}
