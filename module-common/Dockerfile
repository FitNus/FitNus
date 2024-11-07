# 1단계: Gradle 빌드 이미지 설정
FROM gradle:7.3.3-jdk17 AS builder
WORKDIR /app
COPY .. .
RUN gradle :module-common:bootJar --no-daemon


# 2단계: 최종 실행 이미지 생성
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=builder /app/common/build/libs/common-*.jar common.jar

# 실행 명령어
CMD ["java", "-jar", "common.jar"]
