plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.camp3rcraft"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.5")

    // MiniMessage для цветов
    implementation("net.kyori:adventure-text-minimessage:4.14.0")

    // Scoreboard API
    implementation("net.kyori:adventure-text-serializer-legacy:4.14.0")

    // JSON для конфигурации
    implementation("com.google.code.gson:gson:2.10.1")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        relocate("net.kyori", "com.camp3rcraft.coconutlib.libs.kyori")
        relocate("com.google.gson", "com.camp3rcraft.coconutlib.libs.gson")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
} 