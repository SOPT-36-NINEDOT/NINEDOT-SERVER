FROM gradle:8.7.0-jdk17-alpine AS build
WORKDIR /app
COPY . .
ARG BOOT_MODULE=ninedot-api
RUN chmod +x ./gradlew && \
    ./gradlew :${BOOT_MODULE}:bootJar -x test
RUN mkdir -p /out && \
    cp ${BOOT_MODULE}/build/libs/*-SNAPSHOT.jar /out/app.jar || \
    cp ${BOOT_MODULE}/build/libs/*.jar /out/app.jar

FROM eclipse-temurin:17.0.12_7-jre-alpine
WORKDIR /app
ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY --from=build /out/app.jar app.jar
ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-jar","app.jar"]
