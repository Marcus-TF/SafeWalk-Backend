FROM ubuntu:22.04

LABEL authors="Marcus Tulio"

RUN apt update && \
    apt install -y openjdk-17-jdk && \
    apt clean && rm -rf /var/lib/apt/lists/*

CMD java -version

ENTRYPOINT ["java", "-version"]