#!/bin/bash

# base 이미지 설정
FROM openjdk:17-jdk-slim

# jar 파일을 컨테이너 내부에 복사
COPY build/libs/*.jar common.jar

# 외부 호스트 8080 포트로 노출
EXPOSE 8080

# 실행 명령어
CMD ["java", "-jar", "common.jar"]