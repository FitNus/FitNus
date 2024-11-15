#!/bin/bash

# Define service directories
services=(
"module-common"
"module-batch"
)

for service in "${services[@]}"; do
echo "Building Gradle project in $service"
./gradlew $service:bootJar -x test
done

docker-compose build --no-cache
docker-compose up -d