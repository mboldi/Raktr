import com.palantir.gradle.gitversion.GitVersionPlugin
import com.palantir.gradle.gitversion.VersionDetails
import groovy.lang.Closure
import org.gradle.api.Project

fun Project.resolveVersion(): String {
    val overridden = findProperty("version")?.toString()
    if (!overridden.isNullOrBlank() && overridden != "unspecified") {
        return overridden
    }

    plugins.apply(GitVersionPlugin::class.java)

    @Suppress("UNCHECKED_CAST")
    val closure = extensions.extraProperties.get("versionDetails") as Closure<VersionDetails>
    val details = closure.call()
    val tag = details.lastTag.removePrefix("v")

    if (details.isCleanTag) {
        return tag
    }

    // Sentry sorts prereleases as plain text, so the commit distance goes in its numeric revision.
    val hash = details.gitHash.take(7)
    return when (val branch = details.branchName?.replace(Regex("[^0-9A-Za-z-]"), "-")) {
        null, "main", "HEAD" -> "$tag.${details.commitDistance}+$hash"
        else -> "$tag.${details.commitDistance}+$branch.$hash"
    }

}
