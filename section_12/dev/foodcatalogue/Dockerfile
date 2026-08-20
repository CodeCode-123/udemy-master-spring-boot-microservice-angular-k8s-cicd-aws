FROM amazoncorretto:25
WORKDIR /opt
COPY target/*.jar /opt/app.jar
#ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar