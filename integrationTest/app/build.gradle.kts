plugins {
    `java-library`
    id("buildlogic.java-conventions")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    testImplementation(platform(libs.dropwizard.bom))
    testImplementation(project(":site:api"))
    testImplementation(project(":avs:api"))
    testImplementation(project(":site:app"))
    testImplementation(project(":avs:app"))
    testImplementation(testFixtures(project(":module:store-redis")))
    testImplementation(testFixtures(project(":module:store-dynamodb")))
    testImplementation(libs.dropwizard.testing)
}

dockerCompose {
    isRequiredBy(tasks.test)
}
