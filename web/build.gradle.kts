import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("java")
    id("org.springframework.boot") version "3.0.1"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.github.node-gradle.node") version "3.5.1"
}

group = "com.saxomoose"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

node {
    download.set(true)
    version.set("18.13.0")
    workDir.set(file("${project.rootDir}/.gradle/node"))
    nodeProjectDir.set(file("${project.projectDir}"))
}

tasks.register<NpmTask>("npmRunBuild") {
    dependsOn(tasks.npmInstall)
    npmCommand.set(listOf("run", "build"))
    inputs.dir("node_modules")
    inputs.dir("src/main/resources")
    inputs.file("package.json")
    inputs.file("webpack.config.js")
    outputs.dir("$buildDir/resources/main/static")
}

tasks.register<NpmTask>("npmRunWatch") {
    dependsOn(tasks.npmInstall)
    npmCommand.set(listOf("run", "watch"))
    inputs.dir("node_modules")
    inputs.dir("src/main/resources")
    inputs.file("package.json")
    inputs.file("webpack.config.js")
    outputs.dir("$buildDir/resources/main/static")
}

tasks.processResources {
//    dependsOn(tasks.findByName("npmRunWatch"))
    dependsOn(tasks.findByName("npmRunBuild"))
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

