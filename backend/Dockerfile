FROM ghcr.io/graalvm/jdk-community:24
WORKDIR /app
COPY ./build/libs/backend-1.0.0.jar /app/backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "backend.jar"]
