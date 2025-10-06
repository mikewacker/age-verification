plugins {
    application
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    implementation(project(":site:api"))
    implementation(project(":service"))
    implementation(project(":common:provider:account-demo"))
    implementation(project(":site:client:avs"))
    implementation(project(":site:provider:accountstore-dynamodb"))
    implementation(project(":common:provider:pendingstore-redis"))
    implementation(project(":module:crypto-demo"))
    implementation(project(":common:app"))
    implementation(platform(libs.dropwizard.bom))
    implementation(libs.dropwizard.core)
    implementation(libs.jakartaValidation.api)

    // Dagger component
    implementation(platform(libs.aws.bom))
    implementation(project(":avs:api"))
    implementation(project(":site:spi"))
    implementation(project(":common:env"))
    implementation(project(":common:client:api"))
    implementation(libs.aws.dynamoDb)
    implementation(libs.darc.darc)
    implementation(libs.jedis.jedis)
}

application {
    mainClass = "org.example.age.site.app.SiteApp"
}
