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
val apiSpecsRoot = "$projectDir/src/api-specs"

openApiGenerate {
  generatorName.set("kotlin-spring") //kotlin-spring 기반 코드 생성
  inputSpec.set("$apiSpecsRoot/specs/internal.yaml") //OpenApi 3.0문서의 위치
  outputDir.set("$buildDir/generated") //문서를 기반으로 생성될 코드의 위치
  configFile.set("$apiSpecsRoot/specs/config.json")
}

// external codegen
tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerateExternal") {
  dependsOn("openApiGenerate")
  generatorName.set("kotlin-spring")
  inputSpec.set("$apiSpecsRoot/specs/external.yaml")
  outputDir.set("${buildDir}/generated")
  configFile.set("$apiSpecsRoot/specs/config.json")
}

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiJsonGeneratorInternal") {
  dependsOn("openApiGenerate")
  generatorName.set("openapi")
  inputSpec.set("$apiSpecsRoot/specs/internal.yaml")
  outputDir.set("$buildDir/resources/main/docs/openapi/internal")
  doLast {
    copy {
      from("$apiSpecsRoot/docs")
      into("${buildDir}/resources/main/docs")
    }
  }
}

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiJsonGeneratorExternal") {
  dependsOn("openApiGenerateExternal")
  generatorName.set("openapi")
  inputSpec.set("$apiSpecsRoot/specs/external.yaml")
  outputDir.set("${buildDir}/resources/main/docs/openapi/external")
  doLast {
    copy {
      from("$apiSpecsRoot/docs")
      into("${buildDir}/resources/main/docs")
    }
  }
}

sourceSets {
  main {
    java.srcDirs(
      "$buildDir/generated/src/main/kotlin",
      "$projectDir/src/api-specs/specs"
    )
    resources.srcDir("${openApiGenerate.outputDir.get()}/src/main/resources")
  }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  dependsOn("openApiGenerate", "openApiGenerateExternal", "openApiJsonGeneratorInternal", "openApiJsonGeneratorExternal")
}

tasks.withType<org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask> {
  dependsOn("openApiGenerate", "openApiGenerateExternal", "openApiJsonGeneratorInternal", "openApiJsonGeneratorExternal")
}
