plugins {
	id("fabric-loom") version "1.6-SNAPSHOT"
	id("legacy-looming") version "1.6-SNAPSHOT" // Version must be the same as fabric-loom's
}

base {
	archivesName = project.property("archives_base_name").toString()
	version = project.property("mod_version").toString()
	group = project.property("maven_group").toString()
}

repositories {
	mavenCentral()
}

dependencies {
	minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
	mappings(legacy.yarn(project.property("minecraft_version").toString(), project.property("yarn_build").toString()))
	modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

	implementation("com.esotericsoftware.yamlbeans:yamlbeans:${project.property("yaml_version")}") {
		include(this)
	}
}

tasks {
	processResources {
		val projectProperties = mapOf(
			"version" to project.version
		)

		inputs.properties(projectProperties)

		filesMatching("fabric.mod.json") {
			expand(projectProperties) {
				escapeBackslash = true
			}
		}
	}

	withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.release = 8
	}

	java {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8

		// If you remove this line, sources will not be generated.
		withSourcesJar()
	}

	jar {
		from("LICENSE") {
			rename { "${it}_${project.base.archivesName.get()}" }
		}
	}
}
