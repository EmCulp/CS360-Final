plugins {
    kotlin("jvm") version "1.8.22" // Updated Kotlin version
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.sparkjava:spark-core:2.9.4")
    implementation("com.sparkjava:spark-template-mustache:2.7.1")
    implementation ("mysql:mysql-connector-java:8.0.28")
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.json:json:20210307")    //JSON parsing
    implementation("de.svenkubiak:jBCrypt:0.4.1")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()

    // Correctly configuring logging for test events
    testLogging {
        events("passed", "skipped", "failed") // Specify which events to log
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL // More detailed error information
        showStandardStreams = true // Display standard output and error streams
    }
}

tasks.register("run", JavaExec::class){
    mainClass.set("booksystem.Main")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("/src/main/resources/book.csv", "/src/main/resources/books.csv")
}


tasks.register<JavaExec>("runBookFetcher") {
    dependsOn("clean", "build")
    mainClass.set("booksystem.BookFetcher")
    classpath = sourceSets["main"].runtimeClasspath

    doFirst {
        println("Classpath: ${classpath.asPath}")
    }
}


// Set the logging level for Gradle builds
gradle.startParameter.logLevel = org.gradle.api.logging.LogLevel.LIFECYCLE
