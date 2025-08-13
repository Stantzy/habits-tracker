package local.project;

import local.project.Database.DatabaseManager;
import local.project.Habit.HabitManager;
import local.project.UI.ConsoleUI;

public class App {
    public static void main(String[] args) {
        final String DEFAULT_PATH =
                System.getProperty("user.home") +
                "/.habits_tracker/" +
                System.getProperty("user.name") + ".db";
        String dbPathString = args.length > 0 ? args[0] : DEFAULT_PATH;
        HabitManager habitManager = new HabitManager();
        DatabaseManager databaseManager = new DatabaseManager(dbPathString);
        ConsoleUI consoleUI = new ConsoleUI(habitManager, databaseManager);

        databaseManager.init();
        consoleUI.start();
        databaseManager.closeConnection();
    }
}
