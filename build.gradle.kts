plugins {
    java
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
    val jfxOptions = object {
        val group = "org.openjfx"
        val version = "15"
        val fxModules = arrayListOf("javafx-base","javafx-controls","javafx-graphics","javafx-media","javafx-swing")
    }
    jfxOptions.run {
        val osName = System.getProperty("os.name")
        val platform = when {
            osName.startsWith("Mac", ignoreCase = true) -> "mac"
            osName.startsWith("Windows", ignoreCase = true) -> "win"
            osName.startsWith("Linux", ignoreCase = true) -> "linux"
            else -> "mac"
        }
        fxModules.forEach {
            implementation("$group:$it:$version:$platform")
        }
    }
}

application{
    mainModule.set("neteasy.music.fx.main")
    mainClass.set("pub.cellebi.neteasyfx.MusicApp")
    applicationDefaultJvmArgs = arrayListOf("-Dhttps.protocols=TLSv1.1",
            "-Xms50m", "-Xmx256m", "-Dsun.java2d.opengl=true", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseZGC")
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