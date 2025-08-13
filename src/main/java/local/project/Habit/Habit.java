package local.project.Habit;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Habit {
    private final String title;
    private final Set<LocalDate> completedDates;

    public Habit(String title) {
        this.title = title;
        completedDates = new HashSet<>();
    }

    public void markCompleted(LocalDate date) {
        completedDates.add(date);
    }

    public String getTitle() {
        return title;
    }

    public Set<LocalDate> getCompletedDates() {
        return completedDates;
    }
}
