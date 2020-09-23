POC: trigger file upload on OOM in distroless
=============================================

The code here is about a POC, to build a Docker image based on
`gcr.io/distroless/java:11`, executing a Java program which,
on OutOfMemoryError, triggers another Java program to upload
the generated heap dump file to an accessible location.

This is needed because the image runs on AWS Fargate,
and it's not accessible from outside by design.

The problem
-----------

The JVM option `-XX:OnOutOfMemoryError='java -jar ...'` allows the execution
of a command on OOM. On execution of the command, we observe
this log output:

    #   Executing /bin/sh -c "java -jar ..."

Which doesn't look good, because `/bin/sh` doesn't exist on Distroless.
There is no further output, and indeed the program wasn't executed.

Conclusion
----------

Looking at the JVM's source code, in [vmError.cpp][vmError] we can see that the
program is executed using `os::fork_and_exec`. Looking at its implementation
in [os_linux.cpp][os_linux], it's clear the program is executed with `/bin/sh`.

As a workaround, we used a copy of `/busybox/sh` from the debug flavor
of the image, with the following modifications to `Dockerfile`:

    FROM gcr.io/distroless/java:11-debug AS build
    ...
    COPY --from=build /busybox/sh /bin/sh

Then the execution of the Java program on OOM works well.

Another idea (not implemented) is to implement the heap dump uploader
program in Go, and use that native binary as `/bin/sh`.

A remaining challenge is to make the filename of the dump file unique,
and identify the Fargate node it runs on. An [HeapDumpPath][idea] (not implemented)
is to set `HeapDumpPath` during runtime using JMX.

[vmError]: https://hg.openjdk.java.net/jdk/jdk11/file/308410473abe/src/hotspot/share/utilities/vmError.cpp#l1577
[os_linux]: https://hg.openjdk.java.net/jdk/jdk11/file/308410473abe/src/hotspot/os/linux/os_linux.cpp#l5723
[HeapDumpPath]: https://stackoverflow.com/a/34077587

Setup
-----

Clone this repo and POC branch:

    git clone git@github.com:janos-ss/poc-aws --branch feature/janos/files2s3

Run `./repro.sh`, it will build the jars and the docker image. 

Running the same command outside of docker
------------------------------------------

As a sanity check, this may be helpful sometimes.

In your shell:

    APP_HOME=/tmp
    cp app/* /tmp
    export JAVA_TOOL_OPTIONS

And copy paste the `JAVA_TOOL_OPTIONS` line from `Dockerfile`, and run:

    java -jar /tmp/oom.jar

Using a shell in `distroless`
-----------------------------

The standard `distroless` doesn't have a shell. To have a shell,
change the base image to `gcr.io/distroless/java:11-debug`.

With that base image, you can enter the shell with:

    docker run --entrypoint=sh -it oom
