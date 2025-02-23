plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)
    annotationProcessor(libs.immutables.value)

    api(project(":service:module"))
    api(libs.dagger.dagger)
    api(libs.immutables.annotations)
    api(libs.jackson.databind)
    implementation(libs.jakartaInject.api)
    implementation(libs.jedis.jedis)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":testing"))
    testImplementation(libs.redisEmbedded.redis)
}
