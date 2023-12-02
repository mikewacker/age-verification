plugins {
    id("org.example.age.java-conventions")
    application
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:verification-poc"))
    implementation("com.google.guava:guava")
    implementation("org.bouncycastle:bcpkix-jdk18on")
}

application {
    mainClass.set("org.example.age.demo.Main")
}
