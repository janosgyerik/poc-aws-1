POC: trigger file upload on OOM in distroless
=============================================


Setup
-----

Build the jars, in the project root (one level up) run: 

    ./gradlew build

Copy the jars to `app` directory:

    cp ../oom/build/libs/oom.jar ../files2s3/build/libs/files2s3-with-dependencies.jar app

Build and run the docker image:

    docker build -t oom . && docker run oom

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
