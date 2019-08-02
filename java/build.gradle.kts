import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    java
    application

    id("com.google.protobuf") version "0.8.10"
    idea
}

repositories {
    jcenter()
}

ext {
    set("protobuf.version", "3.9.0")
}

dependencies {
    implementation(platform("software.amazon.awssdk:bom:2.5.29"))
    implementation("software.amazon.awssdk:sqs")
    runtime("software.amazon.awssdk:sts")
    implementation("com.google.protobuf:protobuf-java:${extra["protobuf.version"]}")
    implementation("com.google.protobuf:protobuf-java-util:${extra["protobuf.version"]}")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${extra["protobuf.version"]}"
    }
}

idea {
    module {
        sourceDirs.add(file("${protobuf.protobuf.generatedFilesBaseDir}/main/java"))
        generatedSourceDirs.add(file("${protobuf.protobuf.generatedFilesBaseDir}/main/java"))
    }
}

application {
    mainClassName = "poll.q.poc.Receive"
}
