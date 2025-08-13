package local.project.Database;

import local.project.Utils.Utils;

import java.sql.*;

public class DatabaseManager {
    private final String dbPath;
    private final String url;
    private Connection connection;
    private boolean existFlag = false;

    public DatabaseManager(String dbPath) {
        this.dbPath = dbPath;
        url = "jdbc:sqlite:" + dbPath;
        existFlag = Utils.checkFile(dbPath);
    }

    public void init() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.execute("PRAGMA foreign_keys = ON;");
            statement.close();
            if(!existFlag) {
                System.out.println("Database created.");
                initializeSchema();
            } else {
                System.out.println("Database connected.");
            }
            existFlag = Utils.checkFile(dbPath);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error connecting to the database");
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeSchema() {
        try {
            String sql_create_users =
                """
                CREATE TABLE Users (
                    id INTEGER,
                    username TEXT UNIQUE,
                    PRIMARY KEY(id)
                );
                """;
            String sql_create_habits =
                """
                CREATE TABLE Habits (
                    id INTEGER,
                    title TEXT UNIQUE,
                    user_id INTEGER,
                    PRIMARY KEY(id),
                    FOREIGN KEY (user_id) REFERENCES Users (id)
                    ON DELETE CASCADE
                );
                """;
            String sql_create_marks =
                """
                CREATE TABLE Marks (
                    date DATE,
                    habit_id INTEGER,
                    PRIMARY KEY(date, habit_id),
                    FOREIGN KEY (habit_id) REFERENCES Habits (id)
                    ON DELETE CASCADE
                );
                """;
            Statement statement = connection.createStatement();
            statement.execute(sql_create_users);
            statement.execute(sql_create_habits);
            statement.execute(sql_create_marks);
            statement.close();

            System.out.println("Schema initialized.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
