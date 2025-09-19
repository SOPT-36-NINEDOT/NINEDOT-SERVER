FROM gradle:8.7.0-jdk17-alpine AS build
WORKDIR /app
ARG BOOT_MODULE=ninedot-api

COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY settings.gradle* build.gradle* ./
COPY ${BOOT_MODULE}/build.gradle* ${BOOT_MODULE}/

RUN chmod +x ./gradlew
RUN --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew --no-daemon :${BOOT_MODULE}:dependencies -x test || true

COPY . .

RUN --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew --no-daemon :${BOOT_MODULE}:bootJar -x test

RUN set -e; mkdir -p /out; \
    JAR="$(ls -1 ${BOOT_MODULE}/build/libs/*.jar 2>/dev/null | grep -v -- '-plain\.jar' | head -n1)"; \
    if [ -z "$JAR" ]; then echo "Boot JAR not found"; exit 1; fi; \
    cp "$JAR" /out/app.jar

FROM eclipse-temurin:17.0.12_7-jre-alpine

ENV TZ=Asia/Seoul
ENV JAVA_TOOL_OPTIONS="-XX:+ExitOnOutOfMemoryError -XX:MaxRAMPercentage=75.0"

RUN apk add --no-cache tzdata ca-certificates && update-ca-certificates

RUN addgroup -S -g 10001 app && adduser -S -u 10001 -G app app

WORKDIR /app
RUN mkdir -p /tmp /app/logs && chown -R app:app /app /tmp

COPY --from=build --chown=app:app /out/app.jar /app/app.jar

USER app
ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-Djava.io.tmpdir=/tmp","-jar","/app/app.jar"]
