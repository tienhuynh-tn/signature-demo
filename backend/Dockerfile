# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
# Optional: warm the cache
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests clean package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# add dockerize (small wait tool)
RUN apt-get update && apt-get install -y curl ca-certificates && rm -rf /var/lib/apt/lists/* \
 && curl -L -o /tmp/dockerize.tar.gz https://github.com/jwilder/dockerize/releases/download/v0.7.0/dockerize-linux-amd64-v0.7.0.tar.gz \
 && tar -C /usr/local/bin -xzvf /tmp/dockerize.tar.gz && rm /tmp/dockerize.tar.gz

COPY --from=build /app/target/signature-demo-be.jar /app/signature-demo-be.jar

EXPOSE 8081
# command overridden by compose to add waiting, but keep a sane default:
CMD ["sh", "-c", "dockerize -wait tcp://oracle-db:1521 -timeout 6m && java -jar /app/signature-demo-be.jar"]

## Copy the fat jar (finalName from the pom)
#COPY --from=build /app/target/signature-demo-be.jar /app/app.jar
#
#EXPOSE 8081
#ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"
#ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
