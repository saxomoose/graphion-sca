import com.github.gradle.node.npm.task.NpmTask
import org.flywaydb.gradle.task.FlywayCleanTask
import org.flywaydb.gradle.task.FlywayMigrateTask

extra["db.dev.url"] = providers.gradleProperty("db.dev.url").get()
extra["db.test.url"] = providers.gradleProperty("db.test.url").get()

plugins {
    id("java")
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.flyway)
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.node)
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.springboot)
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.springdependencymanagement)
}

group = "io.github.saxomoose"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_19
    targetCompatibility = JavaVersion.VERSION_19
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.flyway.core)
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation(libs.javafaker)
    implementation(libs.gson)
    implementation(libs.guava)
    implementation(libs.minio)
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
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

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<Test>().configureEach {
    jvmArgs("--enable-preview")
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs("--enable-preview")
}

tasks.bootRun {
    systemProperty("spring.profiles.active", "dev")
}

tasks.test {
    systemProperty("spring.profiles.active", "test")
}

flyway {
    schemas = arrayOf(providers.gradleProperty("flyway.schemas").get())
    user = providers.gradleProperty("flyway.user").get()
    password = providers.gradleProperty("flyway.password").get()
    locations = arrayOf("classpath:db/migration/v001")
    cleanDisabled = false
    outOfOrder = true
}

tasks.flywayMigrate {
    dependsOn(tasks.classes)
    url = "${project.extra["db.dev.url"]}"
}

tasks.flywayClean {
    url = "${project.extra["db.dev.url"]}"
}

tasks.register<Task>("flywayCleanMigrate") {
    dependsOn(tasks.flywayClean)
    dependsOn(tasks.flywayMigrate)
}

tasks.register<FlywayMigrateTask>("flywayMigrateTest") {
    dependsOn(tasks.classes)
    url = "${project.extra["db.test.url"]}"
}

tasks.register<FlywayCleanTask>("flywayCleanTest") {
    url = "${project.extra["db.test.url"]}"
}

tasks.register<Task>("flywayCleanMigrateTest") {
    dependsOn(tasks.findByName("flywayMigrateTest"))
    dependsOn(tasks.findByName("flywayCleanTest"))
}

node {
    download.set(true)
    version.set(providers.gradleProperty("node.version").get())
    workDir.set(file("${project.rootDir}/.gradle/node"))
    nodeProjectDir.set(file("${project.projectDir}"))
}

tasks.register<NpmTask>("npmRunBuild") {
    dependsOn(tasks.npmInstall)
    npmCommand.set(listOf("run", "build"))
    inputs.dir("node_modules")
    inputs.dir("src/main/resources/static/js")
    inputs.dir("src/main/resources/static/scss")
    inputs.file("package.json")
    inputs.file("webpack.config.js")
    outputs.dir("$buildDir/resources/main/static")
}

tasks.register<NpmTask>("npmRunBuildProd") {
    dependsOn(tasks.npmInstall)
    npmCommand.set(listOf("run", "build-prod"))
    inputs.dir("node_modules")
    inputs.dir("src/main/resources/static/js")
    inputs.dir("src/main/resources/static/scss")
    inputs.file("package.json")
    inputs.file("webpack.config.js")
    outputs.dir("$buildDir/resources/main/static")
}

tasks.processResources {
    dependsOn(tasks.findByName("npmRunBuild"))
//    dependsOn(tasks.findByName("npmRunBuildProd"))
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

