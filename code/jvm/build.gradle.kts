import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
}

group = "pt.isel.leic"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation:3.0.4")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// for JDBI
	implementation("org.jdbi:jdbi3-core:3.37.1")
	implementation("org.jdbi:jdbi3-kotlin:3.37.1")
	implementation("org.jdbi:jdbi3-postgres:3.37.1")
	implementation("org.postgresql:postgresql:42.7.2")

	// for Spring Security
	implementation("org.springframework.security:spring-security-core:6.2.3")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.jsonwebtoken:jjwt:0.9.1")

	// for testing
	testImplementation("org.mockito:mockito-core:3.12.4")
	testImplementation(kotlin("test"))
	testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
