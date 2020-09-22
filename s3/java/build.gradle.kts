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
