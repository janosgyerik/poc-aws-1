POC: trigger file upload on OOM in distroless
=============================================


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
