plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesImplementation(libs.assertj.core)

    // test
    testImplementation(libs.guava.testlib)
}
