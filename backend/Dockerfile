FROM openjdk:11

# Create working directory
RUN mkdir /app
WORKDIR /app

# Copy the latest build into the image
COPY ./target/doppelgaenger-1.0-SNAPSHOT-jar-with-dependencies.jar backend.jar

# Boot up the server
EXPOSE 8888
CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseG1GC", "-XX:+UseStringDeduplication", "-jar", "backend.jar"]
