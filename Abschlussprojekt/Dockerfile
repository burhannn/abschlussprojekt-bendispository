FROM gradle as build
COPY --chown=gradle:gradle . /home/gradle
WORKDIR /home/gradle
RUN gradle build

FROM openjdk:8-jdk-alpine
ARG JAR=/home/gradle/build/libs/Abschlussprojekt-0.0.1-SNAPSHOT.jar
COPY --from=build ${JAR} /app.jar
CMD ["java", "-jar", "/app.jar"]