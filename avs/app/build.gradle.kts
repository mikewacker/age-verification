plugins {
    application
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    implementation(platform(libs.dropwizard.bom))
    implementation(project(":avs:api"))
    implementation(project(":service"))
    implementation(project(":common:provider:account-demo"))
    implementation(project(":avs:client:site"))
    implementation(project(":avs:provider:accountstore-dynamodb"))
    implementation(project(":common:provider:pendingstore-redis"))
    implementation(project(":avs:provider:certificatesigner-demo"))
    implementation(project(":avs:provider:userlocalizer-demo"))
    implementation(project(":common:app"))
    implementation(libs.dropwizard.core)
    implementation(libs.jakartaValidation.api)

    // Dagger component
    implementation(platform(libs.aws.bom))
    implementation(project(":site:api"))
    implementation(project(":avs:spi"))
    implementation(project(":common:client:api"))
    implementation(project(":common:env"))
    implementation(libs.aws.dynamoDb)
    implementation(libs.darc.darc)
    implementation(libs.jedis.jedis)
}

application {
    mainClass = "org.example.age.avs.app.AvsApp"
}
