# ---- Build Stage ----
# Use Maven base image from the Docker Hub
FROM eclipse-temurin:17-jdk AS build

# common for all images
ENV MAVEN_HOME /usr/share/maven

COPY --from=maven:3.9.6-eclipse-temurin-11 ${MAVEN_HOME} ${MAVEN_HOME}
COPY --from=maven:3.9.6-eclipse-temurin-11 /usr/local/bin/mvn-entrypoint.sh /usr/local/bin/mvn-entrypoint.sh
COPY --from=maven:3.9.6-eclipse-temurin-11 /usr/share/maven/ref/settings-docker.xml /usr/share/maven/ref/settings-docker.xml

# Set the current working directory inside the image
WORKDIR /app

# Copy the source code to the container
COPY src /app/src
COPY pom.xml /app

# Package the application
RUN ${MAVEN_HOME}/bin/mvn clean install

# ---- Deploy Stage ----
FROM eclipse-temurin:17-jdk

# Copy the built JAR from the build stage
COPY --from=build /app/target/thymeleaf-0.0.1-SNAPSHOT.jar /app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]