FROM openjdk:17
COPY target/scrapper.jar /app/scrapper.jar
WORKDIR /app
CMD ["java", "-jar", "scrapper.jar"]
