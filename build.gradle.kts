import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "xyz.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	// rest template
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.apache.httpcomponents.client5:httpclient5")
	// webclient
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("io.projectreactor.netty:reactor-netty:1.2.3")
	// apache-commons
	implementation(group = "org.apache.commons", name = "commons-lang3", version = "3.15.0")
	// gson
	implementation("com.google.code.gson:gson:2.11.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
		jvmTarget.set(JvmTarget.JVM_17)
	}
}

tasks.apply {
	withType<BootJar> {
		archiveFileName.set("httpclient-lab-${version}.jar")
	}
	withType<Test> {
		useJUnitPlatform()
	}
	bootBuildImage {
		builder = "paketobuildpacks/builder-jammy-base:latest"
	}
}

