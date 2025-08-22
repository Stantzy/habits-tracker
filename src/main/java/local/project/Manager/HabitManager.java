package local.project.Manager;

import local.project.Entity.Habit;
import local.project.Entity.User;
import local.project.Utils.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

public class HabitManager {
    private final HashMap<String, Habit> habits;

    public HabitManager() {
        habits = new HashMap<>();
    }

    public Habit addHabit(int id, String title, User user) {
        habits.put(title.toLowerCase(Locale.ROOT), new Habit(id, title, user));
        return habits.get(title.toLowerCase(Locale.ROOT));
    }

    public void deleteHabit(String title) {
        habits.remove(title.toLowerCase(Locale.ROOT));
    }

    public HashMap<String, Habit> getHabits() {
        return habits;
    }

    public void markCompleted(String title, String dateString) {
        Habit habit = habits.get(title.toLowerCase(Locale.ROOT));
        LocalDate localDate;

        if(dateString.toLowerCase(Locale.ROOT).equals("today")) {
            localDate = LocalDate.now();
        } else {
            try {
                localDate = Utils.getLocalDateFromString(dateString);
            } catch (DateTimeParseException e) {
                throw new RuntimeException(e);
            }
        }

        if(habit != null)
            habit.markCompleted(localDate);
    }

    public void markCompleted(String title, LocalDate localDate) {
        Habit habit = habits.get(title.toLowerCase(Locale.ROOT));
        if(habit != null)
            habit.markCompleted(localDate);
    }

    public boolean isCompletedToday(String title) {
        Habit habit = habits.get(title.toLowerCase(Locale.ROOT));
        return habit != null && habit.getCompletedDates().contains(LocalDate.now());
    }

    public void printStatusForToday(int userId) {
        Set<String> habitsTitles = habits.keySet();
        System.out.println("Status for today:");
        for(String title : habitsTitles) {
            Habit habit = habits.get(title);
            if(habit.getUserId() == userId)
                System.out.println(title + " : " + isCompletedToday(title));
        }
    }
}
