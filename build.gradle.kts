plugins {
    java
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("org.beryx.jlink") version "2.21.0"
    application
}

group = "pub.cellebi"
version = "1.0"

java {
    modularity.inferModulePath.set(true)
    sourceCompatibility = org.gradle.api.JavaVersion.VERSION_14
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core", "jackson-databind", "2.10.3")
    testImplementation("junit", "junit", "4.13")
}

application {
    mainClassName = "pub.cellebi.neteasyfx.MusicApp"
    applicationDefaultJvmArgs = arrayListOf("-Dhttps.protocols=TLSv1.1",
            "-Xms50m", "-Xmx256m", "-Dsun.java2d.opengl=true", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseZGC")
}

javafx {
    version = "14.0.2.1"
    modules = mutableListOf("javafx.controls", "javafx.media", "javafx.swing")
}

jlink {

    jpackage {
        jvmArgs = listOf("-Dhttps.protocols=TLSv1.1", "-Xms50m", "-Xmx128m",
                "-Dsun.java2d.opengl=true", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseZGC")
        outputDir = "Apps"
        imageName = "Cellebi Music"

    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}