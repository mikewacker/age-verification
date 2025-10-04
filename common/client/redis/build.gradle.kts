plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    implementation(libs.jedis.jedis)
}

dockerCompose {
    isRequiredBy(tasks.test)
    useComposeFiles = listOf("$projectDir/docker-compose-test.yml")
}
