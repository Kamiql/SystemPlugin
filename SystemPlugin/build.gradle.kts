import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("maven-publish")
}

group = "de.kamiql"
version = "1.0.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
    bukkitLibrary("org.mongodb:mongodb-driver-legacy:4.5.1")
    bukkitLibrary("net.dv8tion:JDA:5.0.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    compileTestJava {
        options.encoding = "UTF-8"
    }

    javadoc {
        options.encoding = "UTF-8"
    }

    shadowJar {
        // Set the base name for the shaded jar
        archiveBaseName.set("SystemPlugin")
        // Set the version for the shaded jar
        archiveVersion.set("1.0-Beta")
        // Set the classifier for the shaded jar
        archiveClassifier.set("")
        // Merge services in the jar file
        mergeServiceFiles()
        // Set the main class for the jar
        manifest {
            attributes["Main-Class"] = "de.kamiql.Main"
        }
    }

    bukkit {
        foliaSupported = true

        apiVersion = "1.13"

        load = BukkitPluginDescription.PluginLoadOrder.STARTUP
        authors = listOf("kamiql")
        contributors = listOf("PommesKiwi", "la0an")
        prefix = "SystemPlugin"

        name = "SystemPlugin"
        main = "de.kamiql.Main"

        commands {
            register("warp") {
                description = "Set Admin warps!"
                usage = "/warp <add/remove/update/tp <> tp: name> <name>"
                permission = "system.commands.warp"
            }
            register("hat") {
                description = "Set an item on your head!"
                usage = "/hat"
                permission = "system.commands.hat"
            }
            register("vanish") {
                description = "Toggle Vanish!"
                usage = "/vanish"
                permission = "system.commands.vanish"
            }
            register("feed") {
                description = "Feed yourself!"
                usage = "/feed"
                permission = "system.commands.feed"
            }
            register("heal") {
                description = "Heal yourself!"
                usage = "/heal"
                permission = "system.commands.heal"
            }
            register("invsee") {
                description = "See someones inventory!"
                usage = "/invsee <player>"
                permission = "system.commands.invsee"
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])

            groupId = "de.kamiql"
            artifactId = "SystemPlugin"
            version = "1.0.1"
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/DeinGitHubBenutzername/DeinRepository")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: "kamiql"
                password = project.findProperty("gpr.token") as String? ?: ""
            }
        }
    }
}