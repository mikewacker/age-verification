plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
    id("buildlogic.docker-compose-test")
}

dependencies {
    api(libs.jedis.jedis)
}
