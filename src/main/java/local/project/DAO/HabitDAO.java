package local.project.DAO;

import local.project.Entity.Habit;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class HabitDAO {
    private final Connection conn;

    public HabitDAO(Connection conn) {
        this.conn = conn;
    }

    public int saveHabit(Habit habit) throws SQLException {
        String insertHabit = "INSERT OR IGNORE INTO Habits (title, user_id) VALUES (?, ?)";

        try(PreparedStatement stmt = conn.prepareStatement(insertHabit)) {
            int id = -1;
            stmt.setString(1, habit.getTitle());
            stmt.setInt(2, habit.getUserId());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();

            if(rs != null && rs.next()) {
                id = rs.getInt(1);
                saveCompletedDates(id, habit.getCompletedDates());
            }

            return id;
        }
    }

    private void saveCompletedDates(int habitId, Set<LocalDate> dates)
    throws SQLException {
        String insertDate = "INSERT OR IGNORE INTO Marks (habit_id, date) VALUES (?, ?)";
        try(PreparedStatement stmt = conn.prepareStatement(insertDate)) {
            for(LocalDate date : dates) {
                stmt.setInt(1, habitId);
                stmt.setDate(2, Date.valueOf(date));
                stmt.executeUpdate();
            }
        }
    }

    public void saveDate(Habit habit, LocalDate localDate) throws SQLException {
        if(habit == null)
            return;
        String insertDate = "INSERT OR IGNORE INTO Marks (habit_id, date) VALUES (?, ?)";
        int habitId = getIdByTitle(habit.getTitle());
        try(PreparedStatement stmt = conn.prepareStatement(insertDate)) {
            stmt.setInt(1, habitId);
            stmt.setDate(2, Date.valueOf(localDate));
            stmt.executeUpdate();
        }
    }

    public void deleteHabitByTitle(String title) throws SQLException {
        String deleteHabit = "DELETE FROM Habits WHERE title = ?";
        try(PreparedStatement stmt = conn.prepareStatement(deleteHabit)) {
            stmt.setString(1, title);
            stmt.executeUpdate();
        }
    }

    public Set<Habit> getAllHabits() throws SQLException {
        String queryAllHabitTitle = "SELECT * FROM Habits";

        try(Statement stmt = conn.createStatement()) {
            Set<Habit> habits = new HashSet<>();
            ResultSet rs = stmt.executeQuery(queryAllHabitTitle);

            while(rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                int userId = rs.getInt("user_id");
                Habit habit = new Habit(id, title, userId);
                habits.add(habit);
            }

            return habits;
        }

    }

    public Habit getHabitById(int id) throws SQLException {
        String queryHabitTitle = "SELECT title FROM Habits WHERE id = ?";

        try(PreparedStatement stmt = conn.prepareStatement(queryHabitTitle)) {
            Habit habit = null;
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                String title = rs.getString("title");
                int userId = rs.getInt("user_id");
                habit = new Habit(id, title, userId);
            }

            return habit;
        }
    }

    public Set<Habit> getHabitsByUser(int userId) throws SQLException {
        String queryHabits = "SELECT * FROM Habits WHERE user_id = ?";

        try(PreparedStatement stmt = conn.prepareStatement(queryHabits)) {
            Set<Habit> habits = new HashSet<>();
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                int habitId = rs.getInt("id");
                String title = rs.getString("title");
                Habit habit = new Habit(habitId, title, userId);
                habits.add(habit);
            }
            return habits;
        }
    }

    public Set<LocalDate> getHabitDates(int id) throws SQLException {
        String queryDates = "SELECT * FROM Marks WHERE habit_id = ?";

        try(PreparedStatement stmt = conn.prepareStatement(queryDates)) {
            Set<LocalDate> localDates = new HashSet<>();
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                LocalDate localDate = rs.getDate("date").toLocalDate();
                localDates.add(localDate);
            }

            return localDates;
        }
    }

    private int getIdByTitle(String title) throws SQLException {
        String queryHabitId = "SELECT id FROM Habits WHERE title = ?";

        try(PreparedStatement stmt = conn.prepareStatement(queryHabitId)) {
            int id = -1;
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                id = rs.getInt(1);
            }

            return id;
        }
    }
}
