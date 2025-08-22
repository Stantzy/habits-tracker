package local.project.Entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Habit {
    private int id;
    private String title;
    private Set<LocalDate> completedDates;
    private int userId;
    private User user;

    public Habit(int id, String title, int userId) {
        this.id = id;
        this.title = title;
        this.userId = userId;
        completedDates = new HashSet<>();
    }

    public Habit(int id, String title, User user) {
        this.title = title;
        this.user = user;
        userId = user.getId();
        completedDates = new HashSet<>();
    }

    public void markCompleted(LocalDate date) {
        completedDates.add(date);
    }

    public int getId() { return id; }

    public void setUserId(int id) { this.userId = userId; }

    public int getUserId() { return userId; }

    public void setUser(User user) { this.user = user; }

    public User getUser() { return user; }

    public String getTitle() {
        return title;
    }

    public void setCompletedDates(Set<LocalDate> completedDates) {
        this.completedDates = completedDates;
    }

    public Set<LocalDate> getCompletedDates() {
        return completedDates;
    }
}
