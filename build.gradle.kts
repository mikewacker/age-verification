subprojects {
    tasks.withType<Jar> {
        archiveBaseName = "${rootProject.name}-${project.path.substring(1).replace(':', '-')}"
    }
}
