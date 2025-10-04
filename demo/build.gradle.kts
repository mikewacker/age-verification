plugins {
    application
    id("buildlogic.java-conventions")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    implementation(project(":site:api"))
    implementation(project(":avs:api"))
    implementation(project(":site:app"))
    implementation(project(":avs:app"))
    implementation(project(":testing"))
    implementation(testFixtures(project(":module:store-redis")))
    implementation(testFixtures(project(":module:store-dynamodb")))
}

application {
    mainClass = "org.example.age.demo.Demo"
}

dockerCompose {
    isRequiredBy(tasks.run)
}

tasks.matching { it.group == "distribution" }.configureEach { enabled = false }
