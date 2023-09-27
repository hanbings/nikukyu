plugins {
    java
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
}

group = "io.hanbings.server"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // spring web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // spring annotation
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // jetbrains annotations
    implementation("org.jetbrains:annotations:23.0.0")

    // gson
    implementation("com.google.code.gson:gson:2.10")

    // spring data
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    // flows
    @Suppress("SpellCheckingInspection") implementation("io.hanbings.flows:flows-common:3170ba3-SNAPSHOT")
    @Suppress("SpellCheckingInspection") implementation("io.hanbings.flows:flows-github:3170ba3-SNAPSHOT")
    @Suppress("SpellCheckingInspection") implementation("io.hanbings.flows:flows-discord:3170ba3-SNAPSHOT")
    @Suppress("SpellCheckingInspection") implementation("io.hanbings.flows:flows-microsoft:3170ba3-SNAPSHOT")
    @Suppress("SpellCheckingInspection") implementation("io.hanbings.flows:flows-google:3170ba3-SNAPSHOT")

    // lombok
    @Suppress("SpellCheckingInspection") compileOnly("org.projectlombok:lombok")
    @Suppress("SpellCheckingInspection") annotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
