plugins {
    application
    id("buildlogic.java-conventions")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    implementation(platform(libs.dropwizard.bom))
    implementation(project(":site:api"))
    implementation(project(":avs:api"))
    implementation(project(":site:app"))
    implementation(project(":avs:app"))
    implementation(project(":testing"))
    implementation(testFixtures(project(":module:store-dynamodb")))
    implementation(libs.dropwizard.core)
}

application {
    mainClass = "org.example.age.demo.Demo"
}

dockerCompose {
    isRequiredBy(tasks.run)
}
