plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    api(libs.dropwizard.core)
    implementation(project(":service"))

    testImplementation(libs.dropwizard.testing)
}
