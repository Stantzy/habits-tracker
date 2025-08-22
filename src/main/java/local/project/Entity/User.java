package local.project.Entity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Set;

public class User {
    private int id;
    private String username;
    private byte[] passwordHash = null;
    private Set<Habit> habits;

    public User(int id, String username, byte[] hash) {
        this.id = id;
        this.username = username;
        this.passwordHash = hash;
    }

    public User(String username, String password) {
        this.id = -1;
        this.username = username;
        this.passwordHash = hashPassword(password);
    }

    public static byte[] hashPassword(byte[] password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static byte[] hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] byteArray = password.getBytes();
            md.update(byteArray);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void setHabits(Set<Habit> habitSet) {
        this.habits = habitSet;
    }

    public void setId(int id) { this.id = id; }

    public int getId() { return id; }

    public String getUsername() { return username; }

    public byte[] getPasswordHash() { return passwordHash; }
}
