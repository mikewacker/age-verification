plugins {
    java
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<Jar> {
    archiveBaseName = "${rootProject.name}-${project.path.substring(1).replace(':', '-')}"
}
