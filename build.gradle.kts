plugins {
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  id("org.springframework.boot") version "3.5.0"
  id("io.spring.dependency-management") version "1.1.7"
  id("org.openapi.generator") version "7.2.0"
}

group = "io.github.dhslrl321"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

val buildDir = layout.buildDirectory.get().asFile

/**
 * build 및 compile 을 위한 source set 설정 + generated code 를 src 에서 사용할 수 있도록 처리
 */
sourceSets {
  main {
    java.srcDirs("$buildDir/generated/src/main/kotlin", "$projectDir/src/api-specs/specs")
    resources.srcDir("${openApiGenerate.outputDir.get()}/src/main/resources")
  }
}

/**
 * kotlin compile 시 oas codegen dependency 추가
 */
tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    dependsOn("openApiGenerate", "openApiJsonGeneratorInternal", "openApiJsonGeneratorExternal")
  }
  withType<org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask> {
    dependsOn("openApiGenerate")
  }
}

val apiSpecsRoot = "$projectDir/src/api-specs"

/**
 * oas codegen
 */
openApiGenerate {
  generatorName.set("kotlin-spring") //kotlin-spring 기반 코드 생성 .
  inputSpec.set("$apiSpecsRoot/specs/internal.yaml") //OpenApi 3.0문서의 위치
  outputDir.set("${buildDir}/generated") //문서를 기반으로 생성될 코드의 위치
  configFile.set("$apiSpecsRoot/specs/config.json")
}

/**
 * internal 용 swagger 문서 생성을 위한 task
 */
tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiJsonGeneratorInternal") {
  dependsOn("openApiGenerate")
  generatorName.set("openapi")
  inputSpec.set("$apiSpecsRoot/specs/internal.yaml") //OpenApi 3.0문서의 위치
  outputDir.set("${buildDir}/resources/main/docs/openapi/internal") //문서를 기반으로 생성될 코드의 위치
  doLast {
    copy {
      from("$apiSpecsRoot/docs")
      into("${buildDir}/resources/main/docs")
    }
  }
}

/**
 * external 용 swagger 문서 생성을 위한 task
 */
tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiJsonGeneratorExternal") {
  dependsOn("openApiGenerate")
  generatorName.set("openapi")
  inputSpec.set("$apiSpecsRoot/specs/external.yaml") //OpenApi 3.0문서의 위치
  outputDir.set("${buildDir}/resources/main/docs/openapi/external") //문서를 기반으로 생성될 코드의 위치
  doLast {
    copy {
      from("$apiSpecsRoot/docs")
      into("${buildDir}/resources/main/docs")
    }
  }
}
