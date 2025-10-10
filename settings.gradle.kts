rootProject.name = "Alan"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("boostedyaml", "dev.dejvokep:boosted-yaml:1.3.7")
            library("github", "org.kohsuke:github-api:2.0-rc.3")
            library("jda", "net.dv8tion:JDA:6.0.0")

            plugin("shadow", "com.gradleup.shadow").version("9.2.2")
        }
    }
}