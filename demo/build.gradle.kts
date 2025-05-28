plugins {
    application
    id("org.example.age.java-conventions")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":app"))
    implementation(testFixtures(project(":common")))
    implementation(project(":testing"))
    implementation(project(":test-containers"))
    implementation(libs.dropwizard.core)
    implementation(libs.jackson.annotations)
    implementation(libs.jackson.databind)
    implementation(libs.okhttp.okhttp)
    implementation(libs.retrofit.retrofit)
}

application {
    mainClass = "org.example.age.demo.Demo"
}
