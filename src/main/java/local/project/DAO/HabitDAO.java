package local.project.DAO;

import local.project.Database.DatabaseManager;
import local.project.Habit.Habit;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class HabitDAO {
    private final DatabaseManager databaseManager;

    public HabitDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void saveHabit(Habit habit) throws SQLException {
        String insertHabit = "INSERT INTO Habits (title) VALUES (?)";
        Connection conn = databaseManager.getConnection();
        try(PreparedStatement stmt = conn.prepareStatement(insertHabit)) {
            stmt.setString(1, habit.getTitle());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs != null && rs.next()) {
                int id = rs.getInt(1);
                saveCompletedDates(id, habit.getCompletedDates());
            }
        }
    }

    private void saveCompletedDates(int habitId, Set<LocalDate> dates)
    throws SQLException {
        String insertDate = "INSERT INTO Marks (habit_id, date) VALUES (?, ?)";
        Connection conn = databaseManager.getConnection();
        try(PreparedStatement stmt = conn.prepareStatement(insertDate)) {
            for(LocalDate date : dates) {
                stmt.setInt(1, habitId);
                stmt.setDate(2, Date.valueOf(date));
                stmt.executeUpdate();
            }
        }
    }

    public void saveDate(Habit habit, LocalDate localDate) throws SQLException {
        String insertDate = "INSERT INTO Marks (habit_id, date) VALUES (?, ?)";
        Connection conn = databaseManager.getConnection();
        int habitId = getIdByTitle(habit.getTitle());
        try(PreparedStatement stmt = conn.prepareStatement(insertDate)) {
            stmt.setInt(1, habitId);
            stmt.setDate(2, Date.valueOf(localDate));
            stmt.executeUpdate();
        }
    }

    public void deleteHabitByTitle(String title) throws SQLException {
        String deleteHabit = "DELETE FROM Habits WHERE title = ?";
        Connection conn = databaseManager.getConnection();
        try(PreparedStatement stmt = conn.prepareStatement(deleteHabit)) {
            stmt.setString(1, title);
            stmt.executeUpdate();
        }
    }

    public Set<Habit> getAllHabits() throws SQLException {
        String queryAllHabitTitle = "SELECT title FROM Habits";
        String queryHabitDates = "SELECT date FROM Marks WHERE habit_id = ?";
        Connection conn = databaseManager.getConnection();

        Set<Habit> habits = new HashSet<>();

        try(Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(queryAllHabitTitle);
            while(rs.next()) {
                Habit habit = new Habit(rs.getString("title"));
                habits.add(habit);
            }
        }

        if(!habits.isEmpty()) {
            for(Habit habit : habits)
                fillDatesInHabit(habit);
        }

        return habits;
    }

    public Habit getHabitById(int id) throws SQLException {
        String queryHabitTitle = "SELECT title FROM Habits WHERE id = ?";
        String queryHabitDates = "SELECT date FROM Marks WHERE habit_id = ?";
        Connection conn = databaseManager.getConnection();
        Habit habit = null;

        try(PreparedStatement stmt = conn.prepareStatement(queryHabitTitle)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                habit = new Habit(rs.getString("title"));
            }
        }

        if(habit != null)
            fillDatesInHabit(habit);

        return habit;
    }

    private int getIdByTitle(String title) throws SQLException {
        String queryHabitId = "SELECT id FROM Habits WHERE title = ?";
        Connection conn = databaseManager.getConnection();
        int id = -1;

        try(PreparedStatement stmt = conn.prepareStatement(queryHabitId)) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                id = rs.getInt(1);
            }
        }

        return id;
    }

    private void fillDatesInHabit(Habit habit) throws SQLException {
        String queryHabitDates = "SELECT date FROM Marks WHERE habit_id = ?";
        Connection conn = databaseManager.getConnection();
        if(habit != null) {
            int id = getIdByTitle(habit.getTitle());
            try(PreparedStatement stmt = conn.prepareStatement(queryHabitDates)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                while(rs.next()) {
                    LocalDate localDate = rs.getDate(1).toLocalDate();
                    habit.markCompleted(localDate);
                }
            }
        }
    }
}
