plugins {
    id("org.example.age.java-conventions")
    application
}

dependencies {
    implementation(project(":data"))
    implementation(project(":verification-poc"))
    implementation("com.google.guava:guava:32.1.1-jre")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.75")

    testImplementation(project(":testing"))
}

application {
    mainClass.set("org.example.age.demo.Main")
}
