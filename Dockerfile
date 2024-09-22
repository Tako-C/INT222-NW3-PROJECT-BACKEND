FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /usr/src/app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk
WORKDIR /app
COPY --from=builder /usr/src/app/target/*.jar /app/app.jar
CMD ["java", "-jar", "app.jar"]
