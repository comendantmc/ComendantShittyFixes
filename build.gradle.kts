import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    val kotlinVersion: String by System.getProperties()
    kotlin("jvm").version(kotlinVersion)
    val shadowVersion: String by System.getProperties()
    id("com.github.johnrengelman.shadow").version(shadowVersion)
    id("xyz.jpenilla.run-paper") version "1.1.0"
}

val pluginGroup: String by project
group = pluginGroup
val pluginVersion: String by project
version = pluginVersion

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    val kotlinVersion: String by System.getProperties()
    implementation(kotlin("stdlib", kotlinVersion))
    implementation("org.bstats", "bstats-bukkit", "3.0.0")
    val paperAPIVersion: String by project
    compileOnly("com.destroystokyo.paper", "paper-api", paperAPIVersion)
    compileOnly("com.comphenix.protocol", "ProtocolLib", "4.8.0")
}

// This can be slower than manually relocating, if that's going to be an issue manually do so instead.
val autoRelocate by tasks.register<ConfigureShadowRelocation>("configureShadowRelocation", ConfigureShadowRelocation::class) {
    target = tasks.getByName("shadowJar") as ShadowJar?
    val packageName = "${project.group}.${project.name.toLowerCase()}"
    prefix = "$packageName.shaded"
}

tasks {
    val javaVersion = JavaVersion.VERSION_16
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions { jvmTarget = javaVersion.toString() }
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
    }
    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    shadowJar {
        archiveClassifier.set("")
        project.configurations.implementation.get().isCanBeResolved = true
        configurations = listOf(project.configurations.implementation.get())
        dependsOn(autoRelocate)
        minimize()
    }
    build { dependsOn(shadowJar) }
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        val minecraftVersion: String by project
        minecraftVersion(minecraftVersion)
    }
}