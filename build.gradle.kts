import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "2.6.3"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  id("org.asciidoctor.jvm.convert") version "3.3.2"
  kotlin("jvm") version "1.6.10"
  kotlin("plugin.spring") version "1.6.10"
//  id("org.springframework.experimental.aot") version "0.11.1"
}

group = "wiki.zyue"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
  maven { url = uri("https://repo.spring.io/release") }
  maven { url = uri("https://repo.spring.io/milestone") }
  maven { url = uri("https://repo.spring.io/snapshot") }
}

val asciidoctorExtensions: Configuration by configurations.creating
val snippetsDir by extra { file("build/generated-snippets") }
extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
//	implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
  implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
//  implementation("org.springframework.boot:spring-boot-starter-graphql")
//	implementation("org.springframework.boot:spring-boot-starter-mail")
//	implementation("org.springframework.boot:spring-boot-starter-rsocket")
//	implementation("org.springframework.boot:spring-boot-starter-websocket")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.security:spring-security-oauth2-jose")
  implementation("org.springframework.security:spring-security-oauth2-resource-server")
  implementation("org.springframework.security:spring-security-oauth2-client")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  implementation("javax.validation:validation-api")
//	implementation("org.springframework.security:spring-security-messaging")
//	implementation("org.springframework.security:spring-security-rsocket")
  implementation("com.querydsl:querydsl-jpa")
  implementation("com.nimbusds:nimbus-jose-jwt:9.15.2")
  implementation("com.querydsl:querydsl-mongodb") {
    exclude("org.mongodb", "mongo-java-driver")
  }
  asciidoctorExtensions("org.springframework.restdocs:spring-restdocs-asciidoctor")
  developmentOnly("org.springframework.boot:spring-boot-devtools")
//	runtimeOnly("io.r2dbc:r2dbc-postgresql")
//	runtimeOnly("org.postgresql:postgresql")
  annotationProcessor("javax.persistence:javax.persistence-api")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jpa")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
  testImplementation("it.ozimov:embedded-redis:0.7.3") {
    exclude("org.slf4j", "slf4j-simple")
  }
  testImplementation("io.projectreactor:reactor-test")
//  testImplementation("org.springframework.graphql:spring-graphql-test")
  testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")
  testImplementation("org.springframework.security:spring-security-test")
}


tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "17"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.test {
  outputs.dir(snippetsDir)
}

// https://github.com/spring-io/start.spring.io/issues/676#issuecomment-859641317
tasks.asciidoctor {
  dependsOn(tasks.withType<Test>())
  inputs.dir(snippetsDir)
  configurations(asciidoctorExtensions.name)
}

tasks.register<Copy>("apiDocument") {
  group = "documentation"
  from(tasks.asciidoctor.get().outputDir)
  into("src/main/resources/static/docs")
}

tasks.bootJar {
  dependsOn(tasks.withType<AsciidoctorTask>())
  dependsOn(tasks.withType<Copy>())
}

//tasks.withType<BootBuildImage> {
//  builder = "paketobuildpacks/builder:tiny"
//  environment = mapOf("BP_NATIVE_IMAGE" to "true")
//}
