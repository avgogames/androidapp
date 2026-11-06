// Standalone build that runs the Android app's :core catalog on the JVM and writes it as JSON
// for the React Native app. Kept out of the root build so Android CI is unaffected.
// Run from the repo root:
//   ./gradlew -p pixel-paint-rn/scripts/export-catalog :exporter:run
pluginManagement { repositories { mavenCentral(); gradlePluginPortal() } }

dependencyResolutionManagement {
    repositories { mavenCentral() }
    versionCatalogs { create("libs") { from(files("../../../gradle/libs.versions.toml")) } }
}

rootProject.name = "export-catalog"

include(":core")
project(":core").projectDir = file("../../../core")
include(":exporter")
