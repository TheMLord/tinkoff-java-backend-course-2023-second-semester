FROM openjdk:17
COPY target/bot.jar /app/bot.jar
WORKDIR /app
CMD ["java", "-jar", "bot.jar"]

