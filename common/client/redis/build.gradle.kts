plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    api(libs.jedis.jedis)
}

dockerCompose {
    isRequiredBy(tasks.test)
    useComposeFiles = listOf("docker-compose-test.yml")
    environment.put("REDIS_TAG", libs.versions.dockerImages.redis)
}
