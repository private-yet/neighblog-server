rootProject.name = "neighblog-server"

pluginManagement {
    repositories { gradlePluginPortal() }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    "neighblog-api",
    "neighblog-domain",
    "infrastructure:neighblog-rich-site-summary",
)
