plugins {
    id("org.example.age.java-conventions")
    application
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    implementation(project(":crypto:data"))
    implementation(project(":core:data"))
    implementation(project(":base:api:base"))
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
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.google.errorprone:error_prone_annotations")
    implementation("com.google.dagger:dagger")
    implementation("io.undertow:undertow-core")
    implementation("javax.inject:javax.inject")

    // test
    testImplementation("io.undertow:undertow-core")
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
