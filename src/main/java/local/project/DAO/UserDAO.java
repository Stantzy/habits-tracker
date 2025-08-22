package local.project.DAO;

import local.project.Entity.Habit;
import local.project.Entity.User;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class UserDAO {
    private final Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    public void saveUser(User user) throws SQLException {
        String insertUser =
            "INSERT OR IGNORE INTO Users (username, password_hash) VALUES (?, ?)";
        try(PreparedStatement stmt = conn.prepareStatement(insertUser)) {
            stmt.setString(1, user.getUsername());
            stmt.setBytes(2, user.getPasswordHash());
            stmt.executeUpdate();
        }
    }

    public User getUserById(int id) throws SQLException {
        String queryUser = "SELECT * FROM Users WHERE id = ?";
        try(PreparedStatement stmt = conn.prepareStatement(queryUser)) {
            User user = null;
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                int userId = rs.getInt("id");
                String name = rs.getString("username");
                byte[] hash = rs.getBytes("password_hash");
                user = new User(userId, name, hash);
            }

            return user;
        }
    }

    public User getUserByName(String username) throws SQLException {
        String queryUser = "SELECT * FROM Users WHERE username = ?";
        try(PreparedStatement stmt = conn.prepareStatement(queryUser)) {
            User user = null;
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("username");
                byte[] hash = rs.getBytes("password_hash");
                if(id != 0 && name != null && hash != null)
                    user = new User(id, name, hash);
            }

            return user;
        } catch (SQLException e) {
            throw new SQLException("UserDAO.java: getUserByName() error, ", e);
        }
    }

    public Set<Habit> getUserHabits(int id) throws SQLException {
        String queryHabits = "SELECT * FROM Habits WHERE user_id = ?";

        try(PreparedStatement stmt = conn.prepareStatement(queryHabits)) {
            Set<Habit> userHabits = new HashSet<>();
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                int habitId = rs.getInt("id");
                String title = rs.getString("title");
                Habit habit = new Habit(id, title, id);
                userHabits.add(habit);
            }

            return userHabits;
        }
    }

    public byte[] getUserHash(int id) throws SQLException {
        String queryHash = "SELECT password_hash FROM Users WHERE id = ?";

        try(PreparedStatement stmt = conn.prepareStatement(queryHash)) {
            byte[] hash = null;
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next())
                hash = rs.getBytes("password_hash");

            return hash;
        }
    }
}
