plugins {
    java

    // needed for easy running with: ./gradlew run
    application

    idea
}

application {
    mainClassName = "tools.OOMGenerator"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "tools.OOMGenerator"
    }
}
