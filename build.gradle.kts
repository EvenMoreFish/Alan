plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

group = "uk.firedev"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_25

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.boostedyaml)
    implementation(libs.jda) {
        exclude(module="opus-java")
    }
    implementation(libs.github)
}

tasks {
    jar {
        enabled = false
    }
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        manifest {
            attributes["Main-Class"] = "uk.firedev.alan.Main"
        }
        archiveBaseName.set("alan")
        archiveVersion.set("")
        archiveClassifier.set("")

        minimize()

        // Libs Package
        relocate("dev.dejvokep.boostedyaml", "uk.firedev.alan.libs.boostedyaml")
    }
}