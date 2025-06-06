rootProject.name = "Alan"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("boostedyaml", "dev.dejvokep:boosted-yaml:1.3.7")
            library("jda", "net.dv8tion:JDA:5.4.0")
            library("github", "org.kohsuke:github-api:2.0-rc.3")

            plugin("shadow", "com.gradleup.shadow").version("8.3.5")
        }
    }
}