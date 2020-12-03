From openjdk:12-alpine

COPY java -jar build/libs/outbrain-api-*.jar /outbrain.jar

CMD ["java" , "-jar" , "/outbrain.jar"]