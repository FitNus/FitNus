# 1단계: Gradle 빌드 이미지 설정
FROM openjdk:17-jdk

# 2단계: 최종 실행 이미지 생성
COPY build/libs/*.jar common.jar

# 실행 명령어
ENTRYPOINT ["java", "-jar", "common.jar"]
