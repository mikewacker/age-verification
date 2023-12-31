plugins {
    id("org.example.age.java-conventions")
    application
}

dependencies {
    // main
    annotationProcessor(libs.dagger.compiler)

    implementation(project(":crypto:data"))
    implementation(project(":core:data"))
    implementation(project(":core:api:types"))
    implementation(project(":core:service:types"))
    implementation(project(":core:service:endpoint"))
    implementation(project(":module:extractor:demo"))
    implementation(project(":module:extractor:builtin"))
    implementation(project(":module:store:inmemory"))
    implementation(project(":module:service:resource"))
    implementation(project(":infra:client"))
    implementation(project(":core:api:endpoint")) // Dagger component
    implementation(project(":infra:service")) // Dagger component
    implementation(project(":core:service:crypto")) // Dagger component
    implementation(libs.dagger.dagger)
    implementation(libs.drift.api)
    implementation(libs.errorprone.annotations)
    implementation(libs.jackson.core)
    implementation(libs.jackson.databind)
    implementation(libs.javaxInject.inject)
    implementation(libs.undertow.core)

    // test
    testImplementation(libs.undertow.core)
}

application {
    mainClass.set("org.example.age.demo.Main")
}

// Avoids a duplicate JAR error; we don't need to run these tasks anyway.
tasks {
    distTar {
        enabled = false
    }
    distZip {
        enabled = false
    }
}
