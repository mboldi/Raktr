import org.gradle.api.Project

fun Project.envOrProperty(envName: String, propertyName: String): String? =
    System.getenv(envName)?.ifBlank { null }
        ?: findProperty(propertyName)?.toString()?.ifBlank { null }
