FROM gradle:8.7.0-jdk17 AS build
WORKDIR /app/ninedot36

COPY . .
RUN ls -al
RUN chmod +x gradlew
RUN ./gradlew build -x test

RUN cp $(ls /app/ninedot36/build/libs/*.jar | head -n 1) /app/ninedot36/build/libs/app.jar

FROM openjdk:17-jdk-slim
WORKDIR /app

ENV TZ=Asia/Seoul
RUN apt-get update && apt-get install -y tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY --from=build /app/ninedot36/build/libs/app.jar app.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]