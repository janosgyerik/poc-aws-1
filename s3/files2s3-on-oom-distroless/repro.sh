#!/usr/bin/env bash

set -euo pipefail

cd "$(dirname "$0")"

mkdir -pv poc/app

./gradlew build

cp -v oom/build/libs/oom.jar files2s3/build/libs/files2s3-with-dependencies.jar hello/build/libs/hello.jar poc/app

docker build -t oom -f poc/Dockerfile poc
docker run oom
