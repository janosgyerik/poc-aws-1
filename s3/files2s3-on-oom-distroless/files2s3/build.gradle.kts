import org.gradle.jvm.tasks.Jar

plugins {
    java

    // needed for easy running with: ./gradlew run
    application

    idea
}

repositories {
    jcenter()
}

dependencies {
    implementation(platform("software.amazon.awssdk:bom:2.5.29"))
    implementation("software.amazon.awssdk:s3")
    // required to resolve credentials using correct IAM role for the AWS_PROFILE env var
    runtime("software.amazon.awssdk:sts")
}

application {
    mainClassName = "s3.upload.Files2S3"
}

val fatJar = task("fatJar", type = Jar::class) {
    baseName = "${project.name}-with-dependencies"
    manifest {
        attributes["Main-Class"] = "s3.upload.Files2S3"
    }
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}
