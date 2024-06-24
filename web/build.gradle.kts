import org.flywaydb.gradle.task.FlywayCleanTask
import org.flywaydb.gradle.task.FlywayMigrateTask

extra["graphion.db.dev.url"] = providers.gradleProperty("graphion.db.dev.url").get()
extra["graphion.db.test.url"] = providers.gradleProperty("graphion.db.test.url").get()

plugins {
    id("java")
    alias(libs.plugins.flyway)
    alias(libs.plugins.springboot)
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
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.session:spring-session-data-redis")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    implementation(libs.dss.crl.parser)
    implementation(libs.dss.pades.pdfbox)
    implementation(libs.dss.pades)
    // implementation(libs.dss.pdfa)
    implementation(libs.dss.service)
    implementation(libs.dss.token)
    implementation(libs.dss.tsl.validation)
    implementation(libs.dss.utils.apache.commons)
    implementation(libs.mailgun)
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
            // tasks.findByName("resetDatabase"),
            tasks.findByName("redisFlushall"),
            // tasks.findByName("resetDefaultBucket")
    )
    jvmArgs (
            "-Dspring.output.ansi.enabled=ALWAYS",
            "-Dcom.sun.management.jmxremote=true",
            "-Djava.rmi.server.hostname=10.0.0.2",
            "-Dcom.sun.management.jmxremote.port=1099",
            "-Dcom.sun.management.jmxremote.rmi.port=1099",
            "-Dcom.sun.management.jmxremote.authenticate=false",
            "-Dcom.sun.management.jmxremote.ssl=false"
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
    commandLine("docker", "exec", "graphion-minio-client-1", "mc", "rm", "--recursive", "--force", "minio-server/graphion-test/closing-documents")
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