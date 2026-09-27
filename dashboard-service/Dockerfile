FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos lo necesario para este servicio (shared-events y los 4 pom)
COPY pom.xml .
COPY shared-events/pom.xml shared-events/pom.xml
COPY ingestion-service/pom.xml ingestion-service/pom.xml
COPY ai-classifier-service/pom.xml ai-classifier-service/pom.xml
COPY dashboard-service/pom.xml dashboard-service/pom.xml

#Instalación de shared-events
COPY shared-events/src shared-events/src
RUN mvn -pl shared-events install -DskipTests

# Compilación del servicio
COPY dashboard-service/src dashboard-service/src
RUN mvn -pl dashboard-service -am package -DskipTests -Dfile.encoding=UTF-8 -Dproject.build.sourceEncoding=UTF-8

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/dashboard-service/target/dashboard-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]