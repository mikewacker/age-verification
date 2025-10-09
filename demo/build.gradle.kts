plugins {
    application
    id("buildlogic.java-conventions")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    implementation(project(":site:api"))
    implementation(project(":avs:api"))
    implementation(project(":testing"))
}

application {
    mainClass = "org.example.age.demo.Demo"
}

dockerCompose {
    isRequiredBy(tasks.run)
    environment.put("ALPINE_TAG", libs.versions.dockerImages.alpine)
    environment.put("DYNAMODB_TAG", libs.versions.dockerImages.dynamoDb)
    environment.put("GRADLE_TAG", libs.versions.dockerImages.gradle)
    environment.put("REDIS_TAG", libs.versions.dockerImages.redis)
    environment.put("TEMURIN_JRE_TAG", libs.versions.dockerImages.temurinJre)
}
