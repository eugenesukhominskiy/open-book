FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/open-book-0.0.1-SNAPSHOT.jar open-book.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "open-book.jar"]