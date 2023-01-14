# https://spring.io/guides/gs/spring-boot-docker/
FROM openjdk:17-jdk-alpine

ARG MYSQL_DB_MOVIES_URL
ENV MYSQL_DB_MOVIES_URL ${MYSQL_DB_MOVIES_URL?notset}

ARG apikey
ENV apikey ${apikey?notset}
WORKDIR /usr/src/app

COPY . .

RUN addgroup -g 1001 -S appuser && adduser -u 1001 -S appuser -G appuser
RUN chown -R 1001:1001 /usr/src/app
RUN ls
USER 1001

EXPOSE 8080
RUN cat  /usr/src/app/src/main/resources/application-mogenius.properties >  /usr/src/app/src/main/resources/application.properties
RUN cat  /usr/src/app/src/main/resources/application-mogenius.properties >  /usr/src/app/src/test/resources/application.properties
RUN chmod +x gradlew
RUN echo ${MYSQL_DB_MOVIES_URL}
RUN ./gradlew build -PMYSQL_DB_URL_PROPERTY="${MYSQL_DB_MOVIES_URL}" -Papikey="${apikey}"

ENTRYPOINT ["java","-jar","/usr/src/app/build/libs/Movies_Selector-0.0.1-SNAPSHOT.jar"]