plugins {
    java
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "io.hanbings.nikukyu"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://repository.hanbings.com/snapshots")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-web-services")
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // Security
    implementation("cn.dev33:sa-token-spring-boot3-starter:1.43.0")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")

    // Jetbrains
    implementation("org.jetbrains:annotations:22.0.0")

    // Swagger
    implementation("com.github.xiaoymin:knife4j-openapi3-ui:4.5.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.7.0")

    // OAuth
    implementation("io.hanbings.flows:flows-common:3170ba3-SNAPSHOT")
    implementation("io.hanbings.flows:flows-github:3170ba3-SNAPSHOT")
    implementation("io.hanbings.flows:flows-discord:3170ba3-SNAPSHOT")
    implementation("io.hanbings.flows:flows-microsoft:3170ba3-SNAPSHOT")
    implementation("io.hanbings.flows:flows-google:3170ba3-SNAPSHOT")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
