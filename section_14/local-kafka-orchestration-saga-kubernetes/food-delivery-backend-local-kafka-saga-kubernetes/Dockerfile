# Build stage
FROM maven:3.9.12-amazoncorretto-25 AS build
WORKDIR /app

# Copy the root build files
COPY pom.xml .

# Copy the parent core module and child microservices
COPY core/pom.xml ./core/
COPY foodcatalogue/pom.xml ./foodcatalogue/
COPY message/pom.xml ./message/
COPY order/pom.xml ./order/
COPY payment/pom.xml ./payment/
COPY restaurantlisting/pom.xml ./restaurantlisting/
COPY userinfo/pom.xml ./userinfo/


# Mount the local host .m2 directory to the container's .m2 directory
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests

# Build the core dependency first, then the microservice
RUN mvn clean install -pl core
RUN mvn clean package -pl foodcatalogue -DskipTests
RUN mvn clean package -pl message -DskipTests
RUN mvn clean package -pl order -DskipTests
RUN mvn clean package -pl payment -DskipTests
RUN mvn clean package -pl restaurantlisting -DskipTests
RUN mvn clean package -pl userinfo -DskipTests

# Runtime Image for foodcatalogue ---
FROM maven:3.9.12-amazoncorretto-25 AS foodcatalogue
WORKDIR /app
# Copy only the compiled jar for this specific microservice
COPY --from=build /app/foodcatalogue/target/foodcatalogue-1.0.0.jar app.jar
EXPOSE 9082
ENTRYPOINT ["java", "-jar", "app.jar"]

# Runtime Image for message ---
FROM maven:3.9.12-amazoncorretto-25 AS message
WORKDIR /app
# Copy only the compiled jar for this specific microservice
COPY --from=build /app/message/target/message-1.0.0.jar app.jar
EXPOSE 9085
ENTRYPOINT ["java", "-jar", "app.jar"]

# Runtime Image for order ---
FROM maven:3.9.12-amazoncorretto-25 AS order
WORKDIR /app
# Copy only the compiled jar for this specific microservice
COPY --from=build /app/order/target/order-1.0.0.jar app.jar
EXPOSE 9084
ENTRYPOINT ["java", "-jar", "app.jar"]

# Runtime Image for payment ---
FROM maven:3.9.12-amazoncorretto-25 AS payment
WORKDIR /app
# Copy only the compiled jar for this specific microservice
COPY --from=build /app/payment/target/payment-1.0.0.jar app.jar
EXPOSE 9086
ENTRYPOINT ["java", "-jar", "app.jar"]

# Runtime Image for restaurantlisting ---
FROM maven:3.9.12-amazoncorretto-25 AS restaurantlisting
WORKDIR /app
# Copy only the compiled jar for this specific microservice
COPY --from=build /app/restaurantlisting/target/restaurantlisting-1.0.0.jar app.jar
EXPOSE 9081
ENTRYPOINT ["java", "-jar", "app.jar"]

# Runtime Image for userinfo ---
FROM maven:3.9.12-amazoncorretto-25 AS userinfo
WORKDIR /app
# Copy only the compiled jar for this specific microservice
COPY --from=build /app/userinfo/target/userinfo-1.0.0.jar app.jar
EXPOSE 9083
ENTRYPOINT ["java", "-jar", "app.jar"]








## Copy root parent pom and all module poms/source codes for dependency resolution
#COPY pom.xml .
#COPY core/pom.xml ./core/
#COPY foodcatalogue/pom.xml ./foodcatalogue/
#COPY message/pom.xml ./message/
#COPY order/pom.xml ./order/
#COPY payment/pom.xml ./payment/
#COPY restaurantlisting/pom.xml ./restaurantlisting/
#COPY userinfo/pom.xml ./userinfo/
#
#
##ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
#ENTRYPOINT exec java $JAVA_OPTS -jar app.jar