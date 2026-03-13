# Using official Java 17 image
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
# Coping the built jar file
COPY target/ecommerceBackend-0.0.1-SNAPSHOT.jar app.jar
# Running the application
ENTRYPOINT ["java","-jar","/app.jar"]