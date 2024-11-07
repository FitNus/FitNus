# 1단계: Gradle 빌드 이미지 설정
FROM gradle:7.3.3-jdk17 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew build -x test

# 2단계: 최종 실행 이미지 생성
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar batch.jar

# 실행 명령어
CMD ["java", "-jar", "batch.jar"]
