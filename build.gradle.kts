import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	// [Note 2.1-1] 코틀린 버전 변수에 담아 일괄적용
	val kotlinVersion = "1.9.22"

	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
//	kotlin("jvm") version "1.9.22"
//	kotlin("plugin.spring") version "1.9.22"
//	kotlin("plugin.jpa") version "1.9.22"
	kotlin("jvm") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
	kotlin("plugin.jpa") version kotlinVersion
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	// [Note 2.3-1] Validatiop 체크 Annotation Dependency
	// Maven Repository에서 Spring Boot Starter Validation 검색해서 Gradle Kotlin꺼추가
	implementation("org.springframework.boot:spring-boot-starter-validation")
	// [Note 3.2-1] SpringSecurity & JWT Dependency
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
}

// [Note 2.1-2] JPA
// : plugin.spring에서 오픈해준 것 외에 추가로 오픈
allOpen {
	annotation("jakarta.persistence.Entity")
}
// : 매개변수가 없는 생성자 자동으로 추가
noArg {
	annotation("jakarta.persistence.Entity")
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
