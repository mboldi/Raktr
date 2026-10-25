import org.gradle.api.Project

fun Project.resolveVersion(): String {
    val overridden = findProperty("version")?.toString()
    if (!overridden.isNullOrBlank() && overridden != "unspecified") {
        return overridden
    }

    // Mirrors the "Resolve version" step of .github/workflows/docker-publish.yml, which resolves image builds.
    val describe = git(
        "describe", "--tags", "--first-parent", "--long", "--abbrev=7",
        "--match=v[0-9]*.[0-9]*.[0-9]*", "--exclude=*-*",
    )
    val (tag, distance, hash) = Regex("""v(\d+\.\d+\.\d+)-(\d+)-g([0-9a-f]+)""").matchEntire(describe)?.destructured
        ?: error("Cannot resolve a version from git describe output '$describe'")

    if (distance == "0" && git("status", "--porcelain").isEmpty()) {
        return tag
    }

    // Later commits must sort higher in Sentry, which compares the revision numerically and prereleases as text.
    return when (val branch = git("branch", "--show-current").replace(Regex("[^0-9A-Za-z-]"), "-")) {
        "", "main" -> "$tag.$distance+$hash"
        else -> "$tag.$distance+$branch.$hash"
    }

}

private fun Project.git(vararg args: String): String =
    providers.exec { commandLine("git", *args) }.standardOutput.asText.get().trim()
