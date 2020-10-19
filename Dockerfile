FROM anapsix/alpine-java:latest

COPY sem4lab1newsPart.jar /usr/app/

WORKDIR /usr/app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "sem4lab1newsPart.jar"]
