From openjdk:12-alpine

COPY build/libs/outbrain-*.jar /outbrain.jar

CMD ["java" , "-jar" , "/outbrain.jar"]