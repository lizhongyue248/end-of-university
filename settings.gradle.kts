pluginManagement {
	repositories {
		maven { url = uri("https://repo.spring.io/milestone") }
		maven { url = uri("https://repo.spring.io/snapshot") }
    maven { url = uri("https://repo.spring.io/release") }
		gradlePluginPortal()
	}
}
rootProject.name = "EOU"
