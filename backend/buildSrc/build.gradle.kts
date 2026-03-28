plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("com.palantir.gradle.gitversion:gradle-git-version:3.4.0")
}
