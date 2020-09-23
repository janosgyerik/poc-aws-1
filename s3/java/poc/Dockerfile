FROM gcr.io/distroless/java:11
# https://github.com/GoogleContainerTools/distroless/issues/253
ENV APP_HOME="/app"
# note: -Xmx2m would be enough to run Java, but not enough for the uploader program,
# ending up in a recursive OOM death loop; -Xmx10m is probably enough
ENV JAVA_TOOL_OPTIONS="-Xmx10m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${APP_HOME}/dump.hprof \
    -XX:OnOutOfMemoryError='java -jar ${APP_HOME}/files2s3-with-dependencies.jar ${APP_HOME}/dump.hprof bucket' \
    -Dfile.encoding=UTF-8 -XX:+ExitOnOutOfMemoryError -verbose:gc -Xlog:gc::time,level -Djava.net.preferIPv4Stack=true"
COPY --chown=nonroot:nonroot app "${APP_HOME}"
USER nonroot
WORKDIR "${APP_HOME}"
CMD ["oom.jar"]
