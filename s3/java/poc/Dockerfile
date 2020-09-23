FROM gcr.io/distroless/java:11-debug
# https://github.com/GoogleContainerTools/distroless/issues/253
ENV APP_HOME="/app"
# note: -Xmx2m would be enough to run Java, but not enough for the file uploader program,
# ending up in a recursive OOM death loop; -Xmx10m is enough
ENV JAVA_TOOL_OPTIONS="-Xmx2m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${APP_HOME}/dump.hprof \
    -XX:OnOutOfMemoryError='java -jar ${APP_HOME}/hello.jar' \
    -Dfile.encoding=UTF-8 -XX:+ExitOnOutOfMemoryError -Djava.net.preferIPv4Stack=true"
COPY --chown=nonroot:nonroot app "${APP_HOME}"
USER nonroot
WORKDIR "${APP_HOME}"
CMD ["oom.jar"]
