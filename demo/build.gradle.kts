plugins {
    id("org.example.age.java-conventions")
    application
}

dependencies {
    // main
    implementation(project(":crypto:data"))
    implementation(project(":core:data"))
    implementation(project(":core:verification-poc"))
    implementation("com.google.errorprone:error_prone_annotations")
    implementation("com.google.guava:guava")
    implementation("org.bouncycastle:bcpkix-jdk18on")
    implementation("org.bouncycastle:bcprov-jdk18on")

    // test
    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:data"))
    testImplementation(project(":core:verification-poc"))
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
