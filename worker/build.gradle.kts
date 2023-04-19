plugins {
    id("java")
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
    implementation(project(":lib"))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-json")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

// jvm
tasks.bootRun {
    systemProperty("spring.profiles.active", "dev")
    jvmArgs(
            "--enable-preview",
            // Colored output in console.
            "-Dspring.output.ansi.enabled=ALWAYS",
            // Startup time optimization
    )
}

// test
tasks.test {
    jvmArgs(
            "--enable-preview",
            // Colored output in console.
            "-Dspring.output.ansi.enabled=ALWAYS",
            // Startup time optimization
    )
    systemProperty("spring.profiles.active", "test")
    useJUnitPlatform()
//    dependsOn(tasks.findByName("flywayCleanMigrateTest"))
}