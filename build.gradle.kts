plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version ("8.1.1")
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    application
}

group = "de.dasshorty"
version = "1.0-SNAPSHOT"
val javaMainClass = "de.dasshorty.teebot.Bot"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.24")

    // 3rd party api's
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.github.twitch4j:twitch4j:1.17.0")

    // mongodb
    implementation("org.mongodb:mongodb-driver-sync:4.10.2")

    // OkHttp Client
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // https://square.github.io/okhttp/

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass = javaMainClass
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
}

tasks.withType(Jar::class.java) {
    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Main-Class"] = javaMainClass
    }
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
}
