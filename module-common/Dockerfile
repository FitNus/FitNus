#!/bin/bash
FROM openjdk:17-jdk-slim

# 컨테이너 내의 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일을 컨테이너 내부의 작업 디렉토리로 복사
COPY build/libs/*.jar common.jar

# JAR 파일 실행
CMD ["java", "-jar", "common.jar"]


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
