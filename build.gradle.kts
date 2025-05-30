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
    languageVersion.set(JavaLanguageVersion.of(21))
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

  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

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
 * base codegen task 정의 (internal 을 base 로 함)
 *
 * openApiGenerate task 는 codegen plugin 에서 필수로 정의되어야 하는 부분
 */
openApiGenerate {
  generatorName.set("kotlin-spring") //kotlin-spring 기반 코드 생성
  inputSpec.set("$projectDir/src/api-specs/specs/internal.yaml") //OpenApi 3.0문서의 위치
  outputDir.set("$buildDir/generated") //문서를 기반으로 생성될 코드의 위치
  configFile.set("$projectDir/src/api-specs/specs/config.json")
}

tasks.named("openApiGenerate") {
  finalizedBy("openApiGenerateExternal")
}

/**
 * external codegen task 정의
 *
 * openApiGenerate block 은 하나만 정의할 수 있어서 external 문서를 생성하기 위해서는 새로운 task 를 추가해줘야 함.
 */
tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerateExternal") {
  dependsOn("openApiGenerate")
  generatorName.set("kotlin-spring")
  inputSpec.set("$projectDir/src/api-specs/specs/external.yaml")
  outputDir.set("${buildDir}/generated")
  configFile.set("$projectDir/src/api-specs/specs/config.json")
}

/**
 * 아래부터는 swagger 문서 생성을 위한 영역
 *
 * 해당 task 에서 생성된 openapi.json 을 기반으로 swagger.html 나 redoc.html 을 생성함
 */
tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiJsonGeneratorInternal") {
  dependsOn("openApiGenerate")
  generatorName.set("openapi")
  inputSpec.set("$projectDir/src/api-specs/specs/internal.yaml")
  outputDir.set("$buildDir/resources/main/docs/openapi/internal")
  doLast {
    copy {
      from("$projectDir/src/api-specs/docs")
      into("${buildDir}/resources/main/docs")
    }
  }
}

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiJsonGeneratorExternal") {
  dependsOn("openApiGenerate")
  generatorName.set("openapi")
  inputSpec.set("$projectDir/src/api-specs/specs/external.yaml")
  outputDir.set("${buildDir}/resources/main/docs/openapi/external")
  doLast {
    copy {
      from("$projectDir/src/api-specs/docs")
      into("${buildDir}/resources/main/docs")
    }
  }
}

/**
 * 개발시 생성된 코드를 src/main 에서 사용할 수 있도록 sourceSet 설정
 */
sourceSets {
  main {
    java.srcDirs(
      "$buildDir/generated/src/main/kotlin",
      "$projectDir/src/api-specs/specs"
    )
    resources.srcDir("${openApiGenerate.outputDir.get()}/src/main/resources")
  }
}

/**
 * kotlin compile 혹은 build 전에 codegen 을 항상 수행하도록 dependency 추가
 */
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  dependsOn("openApiGenerate", "openApiGenerateExternal", "openApiJsonGeneratorInternal", "openApiJsonGeneratorExternal")
}

tasks.withType<org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask> {
  dependsOn("openApiGenerate", "openApiGenerateExternal", "openApiJsonGeneratorInternal", "openApiJsonGeneratorExternal")
}

// ✅ 빌드 타이밍 안정성 보장
tasks.named<ProcessResources>("processResources") {
  dependsOn("openApiGenerate", "openApiGenerateExternal")
}
