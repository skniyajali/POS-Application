pluginManagement {
    includeBuild("build-logic")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "Popos"
include(":app")
include(":benchmark")
include(":core")
include(":feature")
include(":core:database")
include(":core:common")
include(":core:ui")
include(":core:designsystem")
include(":core:testing")
include(":core:analytics")
include(":core:data")
include(":core:notifications")
include(":core:model")
include(":core:domain")
include(":core:worker")
include(":feature:account")
include(":feature:addonitem")
include(":feature:address")
include(":feature:app_settings")
include(":feature:cart")
include(":feature:cart_order")
include(":feature:category")
include(":feature:charges")
include(":feature:customer")
include(":feature:data_deletion")
include(":feature:employee")
include(":feature:employee_attendance")
include(":feature:employee_payment")
include(":feature:expenses")
include(":feature:expenses_category")
include(":feature:home")
include(":feature:order")
include(":feature:printer")
include(":feature:product")
include(":feature:profile")
include(":feature:reminder")
include(":feature:reports")
include(":feature:cart_selected")
include(":feature:print")
