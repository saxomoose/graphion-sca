import com.github.gradle.node.npm.task.NpmTask
import org.flywaydb.gradle.task.FlywayCleanTask
import org.flywaydb.gradle.task.FlywayMigrateTask

extra["graphion.db.dev.url"] = providers.gradleProperty("graphion.db.dev.url").get()
extra["graphion.db.test.url"] = providers.gradleProperty("graphion.db.test.url").get()

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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven("https://gitlab.com/api/v4/projects/19948337/packages/maven")
}

dependencies {
    compileOnly(libs.flywaycore)
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.session:spring-session-data-redis")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    implementation(libs.dss.pades.pdfbox)
    implementation(libs.dss.pades)
    // implementation(libs.dss.pdfa)
    implementation(libs.dss.service)
    implementation(libs.dss.token)
    implementation(libs.dss.tsl.validation)
    implementation(libs.dss.utils.apache.commons)
    // implementation(libs.jaxb.core)
    // Runtime only?
    implementation(libs.minio)
    implementation(libs.webeid)
    implementation(libs.xmlbind)
    implementation(project(":lib"))
    runtimeOnly(libs.jaxb.runtime)
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

// jvm
tasks.bootRun {
    dependsOn(
            tasks.findByName("resetDatabase"),
            tasks.findByName("redisFlushall"),
            tasks.findByName("resetDefaultBucket")
    )
    jvmArgs (
            "-Dspring.output.ansi.enabled=ALWAYS"
    )
    systemProperty("spring.profiles.active", "dev")
}


// test
tasks.test {
    enabled = false
}

tasks.register<Test>("targetTest") {
//    dependsOn(tasks.findByName("flywayCleanMigrateTest"))
    outputs.upToDateWhen { false }
    dependsOn(
            tasks.findByName("resetDatabaseTest"),
            tasks.findByName("redisFlushallTest"),
            tasks.findByName("resetDefaultBucketTest")
    )
    jvmArgs(
            // Colored output in console.
            "-Dspring.output.ansi.enabled=ALWAYS",
            "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=0.0.0.0:5005"
    )
    systemProperty("spring.profiles.active", "test")
    useJUnitPlatform {
        includeTags("target")
    }
}

// docker
tasks.register<Exec>("resetDatabase") {
    commandLine("docker", "exec", "graphion-postgres-1", "./scripts/reset.sh")
}

tasks.register<Exec>("redisFlushall") {
    commandLine("docker", "exec", "graphion-redis-1", "redis-cli", "flushall")
}

tasks.register<Exec>("resetDefaultBucket") {
    commandLine("docker", "exec", "graphion-minio-client-1", "mc", "rm", "--recursive", "--force", "minio-server/graphion")
}

tasks.register<Exec>("resetDatabaseTest") {
    commandLine("docker", "exec", "graphion-test-postgres-1", "./scripts/reset.sh")
}

tasks.register<Exec>("redisFlushallTest") {
    commandLine("docker", "exec", "graphion-test-redis-1", "redis-cli", "flushall")
}

tasks.register<Exec>("resetDefaultBucketTest") {
    commandLine("docker", "exec", "graphion-test-minio-client-1", "mc", "rm", "--recursive", "--force", "minio-server/graphion")
}



// flyway
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
    url = "${project.extra["graphion.db.dev.url"]}"
}

tasks.flywayClean {
    url = "${project.extra["graphion.db.dev.url"]}"
}

tasks.register<Task>("flywayCleanMigrate") {
    dependsOn(tasks.flywayClean)
    dependsOn(tasks.flywayMigrate)
}

tasks.register<FlywayMigrateTask>("flywayMigrateTest") {
    url = "${project.extra["graphion.db.test.url"]}"
}

tasks.register<FlywayCleanTask>("flywayCleanTest") {
    url = "${project.extra["graphion.db.test.url"]}"
}

tasks.register<Task>("flywayCleanMigrateTest") {
    dependsOn(tasks.findByName("flywayCleanTest"))
    dependsOn(tasks.findByName("flywayMigrateTest"))
}

// node
node {
    download.set(true)
    version.set(providers.gradleProperty("node.version").get())
    workDir.set(file("${project.rootDir}/.gradle/node"))
    nodeProjectDir.set(file("${project.projectDir}"))
}

tasks.register<NpmTask>("setNPMRegistry") {
    val args: ListProperty<String> = objects.listProperty(String::class.java)
    args.addAll("config", "set", "registry", "https://gitlab.com/api/v4/packages/npm")
    this.args.set(args)
}

tasks.npmInstall {
    dependsOn(tasks.findByName("setNPMRegistry"))
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
//    dependsOn(tasks.findByName("npmRunBuild"))
//    dependsOn(tasks.findByName("npmRunBuildProd"))
}

