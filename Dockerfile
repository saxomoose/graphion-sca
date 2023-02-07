FROM amazoncorretto:19-alpine-jdk

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY gradlew /eid/gradlew
COPY web/build/ /eid/web/build/

ENTRYPOINT ["java","-jar","/eid/web/web-0.0.1.jar"]