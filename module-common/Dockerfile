#!/bin/bash

# base 이미지 설정
FROM openjdk:17-jdk-slim

# jar 파일을 컨테이너 내부에 복사
COPY /Users/ihyeonjung/Desktop/Sparta/FitNus/FitNus/module-common/build/libs/*.jar common.jar


# 실행 명령어
CMD ["java", "-jar", "common.jar"]