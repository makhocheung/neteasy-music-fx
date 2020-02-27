plugins {
    java
    idea
    id("org.openjfx.javafxplugin") version "0.0.8"
}

group = "pub.cellebi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}

javafx {
    version = "11.0.2"
    modules = mutableListOf("javafx.controls")
}

idea {
    module.outputDir = file("out/production/classes")
}