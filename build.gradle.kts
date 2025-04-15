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
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")
    implementation("org.json:json:20210307")    //JSON parsing
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("run", JavaExec::class){
    mainClass.set("booksystem.Main")
    classpath = sourceSets["main"].runtimeClasspath
}