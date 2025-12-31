plugins {
    application
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    implementation(project(":site:endpoint"))
    implementation(project(":common:provider:account-demo"))
    implementation(project(":site:client:avs"))
    implementation(project(":site:provider:accountstore-dynamodb"))
    implementation(project(":common:provider:pendingstore-redis"))
    implementation(project(":site:provider:certificateverifier-demo"))
    implementation(project(":site:provider:userlocalizer-demo"))
    implementation(project(":common:app"))

    // Dagger component
    implementation(project(":common:client:api"))
    implementation(libs.darc.darc)
}

application {
    mainClass = "org.example.age.site.app.SiteApp"
}
