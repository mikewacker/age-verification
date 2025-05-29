plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    api(project(":api"))
    api(libs.awsSdk.dynamoDb)
    api(libs.jedis.jedis)
    api(libs.junitJupiter.api)
    implementation(testFixtures(project(":common")))
    implementation(project(":testing"))
}
