plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    api(project(":api"))
    api(libs.dagger.dagger)
    api(libs.dropwizard.core)
    api(libs.jackson.databind)
    api(libs.jedis.jedis)
    api(libs.junitJupiter.api)
    api(libs.okhttp.okhttp)
    api(libs.redisEmbedded.redis)
    api(libs.retrofit.retrofit)
    implementation(libs.assertj.core)
    implementation(libs.jakartaInject.api)
    implementation(libs.jaxRs.api)
    implementation(libs.retrofit.mock)
}
