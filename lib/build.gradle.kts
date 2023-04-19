plugins {
    id("java")
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.springboot).apply(false)
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.springdependencymanagement)
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
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
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-json")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
