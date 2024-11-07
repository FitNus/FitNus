# 1단계: Gradle 빌드 이미지 설정
FROM gradle:7.6.0-jdk17 AS build
WORKDIR /app
COPY .. .
RUN ./gradlew :module-batch:bootJar --no-daemon


# 2단계: 최종 실행 이미지 생성
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/module-batch/build/libs/module-batch-*.jar batch.jar

# 복사된 .jar 파일이 있는지 확인
RUN echo "Listing files in /app:" && ls -l /app && echo "Checking if batch.jar exists:" && [ -f batch.jar ]

# 실행 명령어
ENTRYPOINT ["java", "-jar", "batch.jar"]
