import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    val kotlin_version = "2.0.20"
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    id("java")
    id("com.google.cloud.tools.jib") version "3.1.2"
    id("org.jetbrains.kotlin.jvm") version kotlin_version
    id("org.jetbrains.kotlin.kapt") version kotlin_version
    id("org.jetbrains.kotlin.plugin.jpa") version kotlin_version
    kotlin("plugin.spring") version kotlin_version
    kotlin("plugin.allopen") version kotlin_version
    idea
}

group = "org.starter.api"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.3")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.15")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // #. 로그 수집기
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    // JPA & QueryDSL
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    implementation("com.querydsl:querydsl-apt:5.0.0:jakarta")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("com.querydsl:querydsl-sql:${dependencyManagement.importedProperties["querydsl.version"]}")
    implementation("com.querydsl:querydsl-sql-spring:${dependencyManagement.importedProperties["querydsl.version"]}")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("org.hibernate.javax.persistence:hibernate-jpa-2.1-api:1.0.2.Final")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("com.blazebit:blaze-persistence-integration-querydsl-expressions-jakarta:1.6.11")

    implementation("com.amazonaws:aws-java-sdk-bom:1.12.220")
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.638")

    // File Upload & Commons IO
    implementation("commons-fileupload:commons-fileupload:1.4")
    implementation("commons-io:commons-io:2.7")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

val querydslDir = "build/generated/sources/annotationProcessor"
idea {
    module {
        val kaptMain = file(querydslDir)
        sourceDirs.add(kaptMain)
        generatedSourceDirs.add(kaptMain)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

