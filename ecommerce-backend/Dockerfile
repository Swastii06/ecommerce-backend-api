# Using official Java 17 image
FROM eclipse-temurin:17-jdk-alpine   ##alpine is a tiny,superfast
VOLUME /tmp    ##gives space to write temporary files that do background work (like processing Tomcat web server)
# Coping the built jar file
COPY target/ecommerceBackend-0.0.1-SNAPSHOT.jar app.jar   ##takes the jar file from computer and copies it inside the Linux container & renames it to app.jar
# Running the application
ENTRYPOINT ["java","-jar","/app.jar"]    ##tells the container what to do the second it wakes up