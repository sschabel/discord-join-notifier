FROM amazoncorretto:17.0.9-al2023-headless
LABEL Sam Schabel
COPY target/join-notifier-0.0.1.jar join-notifier-0.0.1.jar
ENTRYPOINT ["java","-jar","/join-notifier-0.0.1.jar"]