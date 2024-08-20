import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("io.github.goooler.shadow") version "8.1.7"
}

group = "de.kamiql"
version = "1.0-Beta"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
    bukkitLibrary("org.mongodb:mongodb-driver-legacy:4.5.1")
}

tasks {

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