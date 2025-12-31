import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    java
    id("com.avast.gradle.docker-compose")
}

val libs = the<LibrariesForLibs>() // version catalog workaround for buildSrc

dockerCompose {
    isRequiredBy(tasks.test)
    useComposeFiles = listOf("docker-compose.test.yml")
    environment.put("ALPINE_TAG", libs.versions.dockerImages.alpine)
    environment.put("DYNAMODB_TAG", libs.versions.dockerImages.dynamoDb)
    environment.put("GRADLE_TAG", libs.versions.dockerImages.gradle)
    environment.put("REDIS_TAG", libs.versions.dockerImages.redis)
    environment.put("TEMURIN_JRE_TAG", libs.versions.dockerImages.temurinJre)
}
