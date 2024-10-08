plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version ("8.1.1")
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "de.dasshorty"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.1.2")

    // 3rd party api's
    implementation("com.google.code.gson:gson:2.11.0")

    implementation("com.github.twitch4j:twitch4j:1.22.0")

    // mongodb
    implementation("org.mongodb:mongodb-driver-sync:5.2.0")

    // OkHttp Client
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // https://square.github.io/okhttp/

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
}
