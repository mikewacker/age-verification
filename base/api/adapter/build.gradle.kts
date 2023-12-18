plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api(project(":base:api:base"))

    // test
    testImplementation(project(":base:api:base"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation("org.mockito:mockito-core")
}
