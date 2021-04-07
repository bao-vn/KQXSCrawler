FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/gradle-getting-started-1.0.jar
COPY ${JAR_FILE} gradle-getting-started-1.0.jar
ENTRYPOINT ["java","-jar","/gradle-getting-started-1.0.jar"]