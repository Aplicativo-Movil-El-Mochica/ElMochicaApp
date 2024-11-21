pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()

        // Añadir flatDir aquí
        flatDir {
            dirs("app/libs") // Ruta relativa a la raíz del proyecto
        }
    }
}

rootProject.name = "ElMochicaApp"
include(":app")

