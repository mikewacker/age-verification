plugins {
    application
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    implementation(project(":avs:endpoint"))
    implementation(project(":common:provider:account-demo"))
    implementation(project(":avs:client:site"))
    implementation(project(":avs:provider:accountstore-dynamodb"))
    implementation(project(":common:provider:pendingstore-redis"))
    implementation(project(":avs:provider:certificatesigner-demo"))
    implementation(project(":avs:provider:userlocalizer-demo"))
    implementation(project(":common:app"))

    // Dagger component
    implementation(project(":common:client:api"))
    implementation(libs.darc.darc)
}

application {
    mainClass = "org.example.age.avs.app.AvsApp"
}
