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
