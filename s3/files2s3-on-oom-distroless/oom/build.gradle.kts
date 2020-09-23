plugins {
    java
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "tools.OOMGenerator"
    }
}
