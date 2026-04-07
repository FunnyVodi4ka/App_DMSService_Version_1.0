FROM eclipse-temurin:21-jdk-alpine-3.22

WORKDIR /app

EXPOSE 8084

COPY dms-impl/target/*.jar dms-impl.jar

ENTRYPOINT ["java", "-jar", "dms-impl.jar"]
