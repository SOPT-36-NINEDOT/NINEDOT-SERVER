#!/bin/bash
# shellcheck disable=SC2164
cd /home/ubuntu/ninedot-be-app

# Docker & Docker Compose가 설치되어 있는지 확인
if ! [ -x "$(command -v docker)" ]; then
  echo "Docker가 설치되어 있지 않습니다. 설치 중..."
  sudo apt update
  sudo apt install -y docker.io
  sudo systemctl start docker
  sudo systemctl enable docker
  echo "Docker 설치 완료"
fi

if ! [ -x "$(command -v docker-compose)" ]; then
  echo "Docker Compose가 설치되어 있지 않습니다. 설치 중..."
  sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
  sudo chmod +x /usr/local/bin/docker-compose
  echo "Docker Compose 설치 완료"
fi

# 기존 컨테이너 중지 및 삭제
docker-compose down

# 사용되지 않는 컨테이너, 이미지, 네트워크, 볼륨 정리
docker system prune -af

# 불필요한 Docker 볼륨도 정리 (옵션)
docker volume prune -f

# 최신 이미지 가져오기
# shellcheck disable=SC2046
export $(grep -v '^#' .env | xargs)
# shellcheck disable=SC2086
docker pull ${DOCKER_HUB_USERNAME}/ninedot-be-app:latest

# 컨테이너 실행
docker-compose --env-file .env up -d