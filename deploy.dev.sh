#!/bin/bash
# shellcheck disable=SC2164
cd /home/ubuntu/ninedot-be-app-dev

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

COMPOSE_FILE="docker-compose.dev.yml"
APP_BASE_NAME="ninedot-be-app-dev"
ACTIVE_COLOR_FILE="/home/ubuntu/ninedot-be-app-dev/.active_color"
NGINX_SERVICE="nginx"
HEALTH_TIMEOUT_SEC=120
HEALTH_INTERVAL_SEC=5

if [ ! -f "${ACTIVE_COLOR_FILE}" ]; then
  echo "blue" > "${ACTIVE_COLOR_FILE}"
fi

ACTIVE_COLOR=$(cat "${ACTIVE_COLOR_FILE}")
if [ "${ACTIVE_COLOR}" = "blue" ]; then
  NEW_COLOR="green"
else
  NEW_COLOR="blue"
fi

NEW_CONTAINER="${APP_BASE_NAME}-${NEW_COLOR}"
OLD_CONTAINER="${APP_BASE_NAME}-${ACTIVE_COLOR}"
NEW_SERVICE="app_${NEW_COLOR}"
OLD_SERVICE="app_${ACTIVE_COLOR}"

if [ ! -f .env ]; then
  echo ".env 파일이 없습니다. 서버에 /home/ubuntu/ninedot-be-app-dev/.env 를 미리 주입해 주세요."
  exit 1
fi

# 최신 이미지 가져오기
# shellcheck disable=SC2046
export $(grep -v '^#' .env | xargs)
# shellcheck disable=SC2086
docker pull ${DOCKER_HUB_USERNAME}/ninedot-be-app-dev:latest

# 컨테이너 실행
docker-compose -f "${COMPOSE_FILE}" --env-file .env up -d "${NGINX_SERVICE}" "${NEW_SERVICE}"

echo "애플리케이션 헬스 체크 대기 중..."
elapsed=0
while [ "${elapsed}" -lt "${HEALTH_TIMEOUT_SEC}" ]; do
  status=$(docker inspect -f '{{.State.Health.Status}}' "${NEW_CONTAINER}" 2>/dev/null || true)
  if [ "${status}" = "healthy" ]; then
    echo "헬스 체크 성공"
    break
  fi
  if [ "${status}" = "unhealthy" ]; then
    echo "헬스 체크 실패"
    exit 1
  fi
  sleep "${HEALTH_INTERVAL_SEC}"
  elapsed=$((elapsed + HEALTH_INTERVAL_SEC))
done

if [ "${elapsed}" -ge "${HEALTH_TIMEOUT_SEC}" ]; then
  echo "헬스 체크 타임아웃"
  exit 1
fi

mkdir -p nginx/conf.d
sed "s|__UPSTREAM__|${NEW_CONTAINER}:8080|" nginx/app.conf.template > nginx/conf.d/app.conf
docker-compose -f "${COMPOSE_FILE}" exec -T "${NGINX_SERVICE}" nginx -s reload
echo "${NEW_COLOR}" > "${ACTIVE_COLOR_FILE}"

if docker ps --format '{{.Names}}' | grep -q "^${OLD_CONTAINER}$"; then
  docker-compose -f "${COMPOSE_FILE}" stop "${OLD_SERVICE}" || true
  docker-compose -f "${COMPOSE_FILE}" rm -f "${OLD_SERVICE}" || true
fi

docker image prune -f
