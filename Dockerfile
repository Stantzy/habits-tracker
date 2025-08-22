FROM openjdk:17-jdk-slim

RUN apt-get update && \
	apt-get install -y sqlite3 libsqlite3-dev && \
	rm -rf /var/lib/apt/lists/*

RUN apt-get update && \
	apt-get install -y maven && \
	rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY . .

RUN mvn clean package

CMD ["java", "-jar", "target/habits-tracker-1.0-SNAPSHOT-jar-with-dependencies.jar"]
