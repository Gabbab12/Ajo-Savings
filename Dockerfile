FROM openjdk:17
LABEL maintainer="ajo-savingsdevs"
WORKDIR /app
COPY target/AjoSavings-0.0.1-SNAPSHOT.jar /app/AjoSavings.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "AjoSavings.jar", "--spring.profiles.active=prod"]