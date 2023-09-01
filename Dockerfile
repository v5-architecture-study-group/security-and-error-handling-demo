FROM maven:3-eclipse-temurin-20-alpine AS build
RUN apk --no-cache add nodejs npm

# It appears Vite does not like to run builds from /
RUN mkdir -p /build
ENV HOME=/build
WORKDIR $HOME

ADD . $HOME
RUN --mount=type=cache,target=/root/.m2 mvn clean package -Pproduction

FROM eclipse-temurin:20-jre-alpine
COPY --from=build /build/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

EXPOSE 8080
