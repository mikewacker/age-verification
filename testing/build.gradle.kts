plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    api(project(":common:api"))
    api(project(":common:env"))
    api(libs.bundles.json)
    api(libs.bundles.retrofit)
    implementation(libs.bundles.dagger)
    implementation(libs.bundles.dropwizard) // also provides RuntimeDelegate for JAX-RS response
    implementation(libs.bundles.jaxRs)
    implementation(libs.assertj.core)
    implementation(libs.retrofit.mock)

    testImplementation(libs.dropwizard.testing)
}
