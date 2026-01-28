buildscript {
    repositories {
        maven { url = uri("https://repo.spongepowered.org/repository/maven-public/") }
        mavenCentral()
    }
    dependencies {
        classpath("org.spongepowered:mixingradle:0.7-SNAPSHOT")
    }
}

plugins {
    eclipse
    idea
    id("net.minecraftforge.gradle")
    id("me.modmuss50.mod-publish-plugin")
}

apply(plugin = "org.spongepowered.mixin")

group = project.property("mod.group") as String
version = project.property("mod.version") as String

base {
    archivesName.set(project.property("mod.id") as String)
}

java {
    toolchain.languageVersion.set(
        JavaLanguageVersion.of(
            when {
                stonecutter.eval(stonecutter.current.version, ">=1.18") -> 17
                stonecutter.eval(stonecutter.current.version, ">=1.17") -> 16
                else -> 8
            }
        )
    )
}

minecraft {
    mappings("official", project.property("deps.minecraft") as String)

    enableIdeaPrepareRuns = true
    copyIdeResources = true
    generateRunFolders = true

    runs {
        // applies to all the run configs below
        configureEach {
            workingDirectory = "run"
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")

            mods {
                create(project.property("mod.id") as String) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("client") {
        }

        create("server") {
            args("--nogui")
        }
    }
}

configure<org.spongepowered.asm.gradle.plugins.MixinExtension> {
    val modId = project.property("mod.id")
    add(sourceSets.main.get(), "$modId.refmap.json")
    config("$modId.mixins.json")
}

repositories {
    maven { url = uri("https://cursemaven.com") }
}

dependencies {
    minecraft("net.minecraftforge:forge:${project.property("deps.minecraft") as String}-${project.property("deps.forge") as String}")

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    implementation(fg.deobf("curse.maven:citadel-331936:${project.property("deps.citadel")}"))
    implementation(fg.deobf("curse.maven:ice-and-fire-dragons-264231:${project.property("deps.iceandfire")}"))
}

tasks.named<Jar>("jar") {
    finalizedBy("reobfJar")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.named<ProcessResources>("processResources") {
    val replaceProperties = mapOf(
        "minecraft_version" to project.property("deps.minecraft") as String,
        "minecraft_version_range" to "[${project.property("deps.minecraft") as String}]",
        "forge_version" to project.property("deps.forge") as String,
        "forge_version_range" to "[0,)",
        "loader_version_range" to "[0,)",
        "mod_id" to project.property("mod.id") as String,
        "mod_name" to project.property("mod.name"),
        "mod_license" to project.property("mod.license"),
        "mod_version" to project.property("mod.version"),
        "mod_authors" to project.property("mod.authors"),
        "mod_description" to project.property("mod.description")
    )

    inputs.properties(replaceProperties)

    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
        expand(replaceProperties + mapOf("project" to project))
    }
}

publishMods {
    file = tasks.jar.map { it.archiveFile.get() }

    val modVersion = project.property("mod.version") as String
    val minecraftVersion = project.property("deps.minecraft") as String
    type = if (modVersion.contains("alpha")) ALPHA
    else if (modVersion.contains("beta")) BETA
    else STABLE

    displayName = "${project.property("mod.name")} $modVersion for ${stonecutter.current.version} Forge"
    version = "${modVersion}-${minecraftVersion}-forge"
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    modLoaders.add("forge")

    modrinth {
        projectId = project.property("publish.modrinth") as String
        accessToken = env.MODRINTH_API_KEY.orNull()
        minecraftVersions.add(minecraftVersion)
    }

    curseforge {
        projectId = project.property("publish.curseforge") as String
        accessToken = env.CURSEFORGE_API_KEY.orNull()
        minecraftVersions.add(minecraftVersion)
    }
}
