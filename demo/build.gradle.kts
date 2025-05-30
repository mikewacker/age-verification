plugins {
    application
    id("org.example.age.java-conventions")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":app"))
    implementation(testFixtures(project(":common")))
    implementation(testFixtures(project(":module:store-redis")))
    implementation(libs.bundles.json)
    implementation(libs.bundles.retrofit)
}

application {
    mainClass = "org.example.age.demo.Demo"
}

tasks.matching { it.group == "distribution" }.configureEach { enabled = false }
