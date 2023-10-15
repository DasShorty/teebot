plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version ("8.1.1")
    application
}

group = "de.dasshorty"
version = "1.0-SNAPSHOT"
val javaMainClass = "de.dasshorty.teebot.Bot"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.13")

    // 3rd party api's
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.github.twitch4j:twitch4j:1.17.0")

    // mongo
    implementation("org.mongodb:mongodb-driver-reactivestreams:1.13.1")
}

application {
    mainClass = javaMainClass
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}

tasks.withType(Jar::class.java) {
    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Main-Class"] = javaMainClass
    }
}