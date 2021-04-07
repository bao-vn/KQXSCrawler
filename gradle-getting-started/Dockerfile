#FROM openjdk:8-jdk-alpine
#ARG JAR_FILE=target/gradle-getting-started-1.0.jar
#COPY ${JAR_FILE} gradle-getting-started-1.0.jar
#ENTRYPOINT ["java","-jar","/gradle-getting-started-1.0.jar
FROM openjdk:8 AS TEMP_BUILD_IMAGE
ENV APP_HOME=/usr/app
WORKDIR $APP_HOME
COPY build.gradle settings.gradle gradlew $APP_HOME
COPY gradle $APP_HOME/gradle
RUN ./gradlew build || return 0 
COPY . .
RUN ./gradlew build

FROM openjdk:8
ENV ARTIFACT_NAME=your-application.jar
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY --from=TEMP_BUILD_IMAGE $APP_HOME/build/libs/$ARTIFACT_NAME .
EXPOSE 8080
CMD ["java","-jar",$ARTIFACT_NAME]