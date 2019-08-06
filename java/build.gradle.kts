import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    java

    // needed for easy running with: ./gradlew run
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
    implementation("com.google.protobuf:protobuf-java:${extra["protobuf.version"]}")
    implementation("com.google.protobuf:protobuf-java-util:${extra["protobuf.version"]}")

    implementation(platform("com.amazonaws:aws-java-sdk-bom:1.11.604"))
    implementation("com.amazonaws:aws-java-sdk-sqs")
    // required to resolve credentials using correct IAM role for the AWS_PROFILE env var
    runtime("com.amazonaws:aws-java-sdk-sts")
}

// Specify the protoc executable to use to generate files
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${extra["protobuf.version"]}"
    }
}

// IntelliJ needs this, otherwise generated protobuf sources are not recognized (pity!)
idea {
    module {
        sourceDirs.add(file("${protobuf.protobuf.generatedFilesBaseDir}/main/java"))
        generatedSourceDirs.add(file("${protobuf.protobuf.generatedFilesBaseDir}/main/java"))
    }
}

application {
    mainClassName = "poll.q.poc.Receive"
}
