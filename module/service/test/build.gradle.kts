plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesAnnotationProcessor(libs.dagger.compiler)

    testFixturesApi(project(":core:service:types"))
    testFixturesApi(libs.dagger.dagger)
    testFixturesApi(libs.javaxInject.inject)

    testFixturesImplementation(project(":crypto:data"))
    testFixturesImplementation(project(":core:data"))
    testFixturesImplementation(project(":core:api:types"))
    testFixturesImplementation(project(":module:store:inmemory"))
    testFixturesImplementation(libs.drift.testlib)

    // test
    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:api:types"))
    testImplementation(project(":core:service:types"))
    testImplementation(project(":module:store:inmemory")) // Dagger component
    testImplementation(libs.dagger.dagger)
    testImplementation(libs.drift.drift)
    testImplementation(libs.drift.testlib)
    testImplementation(libs.javaxInject.inject)
    testImplementation(libs.okhttp3.mockwebserver)
}
