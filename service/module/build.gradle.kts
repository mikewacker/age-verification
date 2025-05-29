plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    api(project(":api"))
    implementation(libs.jaxRs.api)
}
