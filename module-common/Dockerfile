# 1단계: Gradle 빌드 이미지 설정
FROM gradle:7.6.0-jdk17 AS build
WORKDIR /app
COPY .. .
RUN ./gradlew :module-common:bootJar --no-daemon

# 2단계: 최종 실행 이미지 생성
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/module-common/build/libs/module-common-*.jar common.jar

# 복사된 .jar 파일이 있는지 확인
RUN echo "Listing files in /app:" && ls -l /app && echo "Checking if common.jar exists:" && [ -f common.jar ]

# 실행 명령어
ENTRYPOINT ["java", "-jar", "common.jar"]
