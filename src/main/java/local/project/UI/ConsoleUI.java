package local.project.UI;

import local.project.DAO.HabitDAO;
import local.project.Database.DatabaseManager;
import local.project.Habit.Habit;
import local.project.Habit.HabitManager;
import local.project.Utils.Utils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ConsoleUI {
    private static final String WELCOME_MSG = "Welcome to The Habits Tracker";
    private static final String USER_MENU =
            """
            1. Show habits
            2. Mark habit
            3. Print status for today
            4. Add habit
            5. Delete habit
            6. Exit
            """;
    private final HabitManager habitManager;
    private final HabitDAO habitDAO;

    public ConsoleUI(HabitManager habitManager, DatabaseManager databaseManager) {
        this.habitManager = habitManager;
        habitDAO = new HabitDAO(databaseManager);
    }

    private void printWelcomeMessage() {
        System.out.println(WELCOME_MSG);
    }

    private void printUserMenu() {
        System.out.println(USER_MENU);
    }

    public void start() throws SQLException {
        Scanner userInput = new Scanner(System.in);
        printWelcomeMessage();
        printUserMenu();

        while(userInput.hasNext()) {
            updateHabits();
            String s = userInput.nextLine();
            switch (s) {
                case "1":   // 1. Show habits
                    printAllHabits();
                    break;
                case "2":   // 2. Mark habit
                    handleMarkHabit(userInput);
                    break;
                case "3":   // 3. Print status for today
                    habitManager.printStatusForToday();
                    break;
                case "4":   // 4. Add habit
                    handleAddHabit(userInput);
                    break;
                case "5":   // 5. Delete habit
                    handleDeleteHabit(userInput);
                    break;
                case "6":   // 6. Exit
                    userInput.close();
                    return;
            }
        }
    }

    private void updateHabits() throws SQLException {
        Set<Habit> habits = habitDAO.getAllHabits();
        for(Habit h : habits) {
            String title = h.getTitle();
            habitManager.addHabit(title);
            Set<LocalDate> dates = h.getCompletedDates();
            for(LocalDate date : dates)
                habitManager.markCompleted(title, date);
        }
    }

    private void handleMarkHabit(Scanner userInput) throws SQLException {
        System.out.print("Enter the habit to mark: ");
        String habitTitleToMark = userInput.nextLine();
        System.out.print("Enter date in following format <dd-mm-yyyy> or <today>: ");
        String dateToMark = userInput.nextLine();

        markHabit(habitTitleToMark, dateToMark);
        Habit habit = habitManager
                .getHabits()
                .get(habitTitleToMark.toLowerCase(Locale.ROOT));
        LocalDate localDate = Utils.getLocalDateFromString(dateToMark);
        habitDAO.saveDate(habit, localDate);
    }

    private void handleAddHabit(Scanner userInput) throws SQLException {
        String habitTitleToAdd = userInput.nextLine();
        Habit habit;
        habit = habitManager.addHabit(habitTitleToAdd);
        habitDAO.saveHabit(habit);
    }

    private void handleDeleteHabit(Scanner userInput) throws SQLException {
        String habitTitleToDelete = userInput.nextLine();
        habitDAO.deleteHabitByTitle(habitTitleToDelete);
        habitManager.deleteHabit(habitTitleToDelete);
    }

    private void printAllHabits() {
        HashMap<String, Habit> habits = habitManager.getHabits();
        for(String key : habitManager.getHabits().keySet()) {
            Habit h = habits.get(key);
            Set<LocalDate> dates = h.getCompletedDates();
            System.out.println(h.getTitle() + " : " +
                    Arrays.toString(dates.toArray()));
        }
    }

    private void markHabit(String title, String dateString) {
        if(dateString.toLowerCase(Locale.ROOT).equals("today")) {
            habitManager.markCompleted(title, LocalDate.now());
        } else {
            try {
                LocalDate localDate = Utils.getLocalDateFromString(dateString);
                habitManager.markCompleted(title, localDate);
            } catch (DateTimeParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
