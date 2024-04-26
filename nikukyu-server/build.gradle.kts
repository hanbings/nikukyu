plugins {
    java
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "io.hanbings"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach { options.compilerArgs.add("--enable-preview") }
tasks.withType<Test>().configureEach { jvmArgs("--enable-preview") }
tasks.withType<JavaExec>().configureEach { jvmArgs("--enable-preview") }

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://repository.hanbings.io/snapshots")
    }
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // Jetbrains
    implementation("org.jetbrains:annotations:22.0.0")

    // OAuth
    implementation("io.hanbings.flows:flows-common:3170ba3-SNAPSHOT")
    implementation("io.hanbings.flows:flows-github:3170ba3-SNAPSHOT")
    implementation("io.hanbings.flows:flows-discord:3170ba3-SNAPSHOT")
    implementation("io.hanbings.flows:flows-microsoft:3170ba3-SNAPSHOT")
    implementation("io.hanbings.flows:flows-google:3170ba3-SNAPSHOT")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
}
