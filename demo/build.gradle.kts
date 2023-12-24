plugins {
    id("org.example.age.java-conventions")
    application
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    implementation(project(":crypto:data"))
    implementation(project(":core:data"))
    implementation(project(":core:verification-poc"))
    implementation(project(":core:service:types"))
    implementation(project(":core:service:endpoint"))
    implementation(project(":module:extractor:demo"))
    implementation(project(":module:extractor:builtin"))
    implementation(project(":module:store:inmemory"))
    implementation(project(":module:service:resource"))
    implementation(project(":core:api:endpoint")) // Dagger component
    implementation(project(":infra:service")) // Dagger component
    implementation(project(":core:service:crypto")) // Dagger component
    implementation("com.google.errorprone:error_prone_annotations")
    implementation("com.google.dagger:dagger")
    implementation("com.google.guava:guava")
    implementation("io.undertow:undertow-core")
    implementation("javax.inject:javax.inject")
    implementation("org.bouncycastle:bcpkix-jdk18on")
    implementation("org.bouncycastle:bcprov-jdk18on")

    // test
    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:data"))
    testImplementation(project(":core:verification-poc"))
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
