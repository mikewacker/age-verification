plugins {
    application
    id("buildlogic.java-conventions")
}

dependencies {
    implementation(project(":site:api"))
    implementation(project(":avs:api"))
    implementation(project(":app"))
    implementation(project(":testing"))
    implementation(testFixtures(project(":module:store-redis")))
    implementation(testFixtures(project(":module:store-dynamodb")))
    implementation(libs.bundles.json)
    implementation(libs.bundles.retrofit)
}

application {
    mainClass = "org.example.age.demo.Demo"
}

tasks.matching { it.group == "distribution" }.configureEach { enabled = false }
