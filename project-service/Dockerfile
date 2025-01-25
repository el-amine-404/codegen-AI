FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/project-service-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]