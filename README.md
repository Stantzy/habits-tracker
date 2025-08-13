# Habits Tracker
An application to track your habits in a database and mark them as completed.

## Technologies
- Java 17+
- Maven
- SQLite
- JDBC

## Build and Run
1. Clone repository:
```bash
git clone https://github.com/Stantzy/habits-tracker.git
cd habits-tracker
```
2. Compile:
```bash
mvn clean package
```
3. Run:
```bash
java --enable-native-access=ALL-UNNAMED -jar target/habits-tracker-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Simple CLI
```
Database connected.
Welcome to The Habits Tracker
1. Show habits
2. Mark habit
3. Print status for today
4. Add habit
5. Delete habit
6. Exit
```
## Project Structure
- '/src' - source code
- 'README.md' - project description
