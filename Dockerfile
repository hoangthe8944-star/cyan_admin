FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src src

RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

ENV PORT=8081
ENV APP_UPLOAD_DIR=/app/uploads

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

CMD ["sh", "-c", "java -Dserver.port=${PORT} -jar /app/app.jar"]
