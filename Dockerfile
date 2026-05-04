FROM bellsoft/liberica-openjre-debian:25-cds
COPY /target/company-0.0.1-SNAPSHOT.jar application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]