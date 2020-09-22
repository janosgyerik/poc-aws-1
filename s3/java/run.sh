#!/usr/bin/env bash

set -euo pipefail

cd "$(dirname "$0")"

./gradlew build

java -jar build/libs/files2s3-with-dependencies.jar "$@"
