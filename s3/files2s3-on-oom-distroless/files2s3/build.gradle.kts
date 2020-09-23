import org.gradle.jvm.tasks.Jar

plugins {
    java
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

val fatJar = task("fatJar", type = Jar::class) {
    archiveFileName.set("${project.name}-with-dependencies.jar")
    manifest {
        attributes["Main-Class"] = "s3.upload.Files2S3"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}
