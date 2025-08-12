# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests clean package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/signature-demo-be.jar /app/signature-demo-be.jar

EXPOSE 7070
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/signature-demo-be.jar"]