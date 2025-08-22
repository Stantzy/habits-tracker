package local.project.Manager;

import local.project.DAO.HabitDAO;
import local.project.DAO.UserDAO;
import local.project.Entity.Habit;
import local.project.Entity.User;
import local.project.Utils.Utils;

import java.sql.*;
import java.time.LocalDate;
import java.util.Set;

public class DatabaseManager {
    private final String dbPath;
    private final String url;
    private Connection connection;
    private boolean existFlag = false;
    private HabitDAO habitDAO;
    private UserDAO userDAO;

    public DatabaseManager(String directoryPath) {
        this.dbPath = directoryPath + System.getProperty("user.name") + ".db";
        url = "jdbc:sqlite:" + dbPath;
        existFlag = Utils.checkFile(dbPath);
    }

    public void init() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
            habitDAO = new HabitDAO(connection);
            userDAO = new UserDAO(connection);
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
            throw new RuntimeException(e);
        }
    }

    public Set<Habit> getHabitsByUser(int userId) throws SQLException {
        Set<Habit> userHabits = habitDAO.getHabitsByUser(userId);

        for(Habit h : userHabits) {
            int id = h.getId();
            Set<LocalDate> dates = habitDAO.getHabitDates(id);
            h.setCompletedDates(dates);
        }

        return userHabits;
    }

    public Set<Habit> getAllHabitsWithUsersAndDates() throws SQLException {
        Set<Habit> allHabits = habitDAO.getAllHabits();

        for(Habit h : allHabits) {
            int userId = h.getUserId();
            Set<LocalDate> dates = habitDAO.getHabitDates(h.getId());
            User user = userDAO.getUserById(userId);
            Set<Habit> userHabits = habitDAO.getHabitsByUser(userId);

            for(Habit userHabit : userHabits)
                userHabit.setUser(user);

            user.setHabits(userHabits);
            h.setUser(user);
            h.setCompletedDates(dates);
        }

        return allHabits;
    }

    public void addUser(User user) throws SQLException {
        userDAO.saveUser(user);
    }

    public User getUser(String username) throws SQLException {
        return userDAO.getUserByName(username);
    }

    public boolean isUserExists(User user) throws SQLException {
            try {
                User userInDatabase = userDAO.getUserByName(user.getUsername());
                return userInDatabase != null;
            } catch (SQLException e) {
                throw new SQLException("Session.java: isUserExists()", e);
            }
        }

    public byte[] getUserHash(User user) throws SQLException {
        return userDAO.getUserHash(user.getId());
    }

    public void saveHabit(Habit habit) throws SQLException {
        habitDAO.saveHabit(habit);
    }

    public void deleteHabitByTitle(String habitTitle) throws SQLException {
        habitDAO.deleteHabitByTitle(habitTitle);
    }

    public void saveDateInHabit(Habit habit, LocalDate localDate) throws SQLException {
        habitDAO.saveDate(habit, localDate);
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private void initializeSchema() {
        try {
            String sql_create_users =
                """
                CREATE TABLE Users (
                    id INTEGER,
                    username TEXT UNIQUE,
                    password_hash BLOB,
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
}
