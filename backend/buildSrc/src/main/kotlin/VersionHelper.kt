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
        return "v$tag"
    }

    val hash = details.gitHash.take(7)
    return when (val branch = details.branchName.replace("/", "-")) {
        "main", "master" -> "v$tag-$hash"
        else -> "v$tag-$branch-$hash"
    }

}
