plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    annotationProcessor("org.immutables:value")

    api(project(":api-types"))
    api(project(":core:common:api-types"))
    api(project(":core:data"))
    api("com.fasterxml.jackson.core:jackson-databind")
    api("org.immutables:value-annotations")
    api("org.jboss.xnio:xnio-api")
}
