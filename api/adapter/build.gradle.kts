plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api(project(":api:base"))

    // test
    testImplementation(testFixtures(project(":api:base")))
}
