FROM gradle:8.7.0-jdk17-alpine AS build
WORKDIR /app
COPY . .
ARG BOOT_MODULE=ninedot-api
RUN chmod +x ./gradlew && ./gradlew :${BOOT_MODULE}:bootJar -x test
RUN mkdir -p /out && \
    cp ${BOOT_MODULE}/build/libs/*-SNAPSHOT.jar /out/app.jar || \
    cp ${BOOT_MODULE}/build/libs/*.jar /out/app.jar

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
