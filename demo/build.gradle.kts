plugins {
    id("org.example.age.java-conventions")
    application
}

dependencies {
    implementation(project(":data"))
    implementation(project(":verification-poc"))
    implementation("com.google.guava:guava")
    implementation("org.bouncycastle:bcpkix-jdk18on")
}

application {
    mainClass.set("org.example.age.demo.Main")
}
