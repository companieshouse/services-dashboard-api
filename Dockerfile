# Build:  docker build -t services-dashboard-api .
# Run:
    # ./set-docker-vars.sh
    # docker run --env-file ./env.list -v ${HOME}/.aws/credentials:/root/.aws/credentials -t -i -p 8080:8080 services-dashboard-api

FROM openjdk:21-slim

RUN apt-get -y update && apt-get -y upgrade

ENV APP_FILE=services-dashboard-api.jar

# not necessary (already set) but to document that it's the supposed home with
# -v ${HOME}/.aws/credentials:/root/.aws/credentials
#                             ^^^^^
ENV HOME=/root

# ENV LC_ALL C
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

# Open the port
EXPOSE 8080

# Copy JAR
COPY $APP_FILE /app.jar

# Launch the Spring Boot application
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar" ]
