package local.project;

import local.project.Database.DatabaseManager;
import local.project.Habit.HabitManager;
import local.project.UI.ConsoleUI;
import local.project.Utils.Utils;

import java.sql.SQLException;

public class App {
    private static final String DEFAULT_DIRECTORY =
            System.getProperty("user.home") + "/.habits_tracker/";

    public static void main(String[] args) {
        String dbDirectory = args.length > 0 ? args[0] : DEFAULT_DIRECTORY;

        try {
            Utils.prepareDatabaseDirectory(dbDirectory);

            HabitManager habitManager = new HabitManager();
            DatabaseManager databaseManager = new DatabaseManager(dbDirectory);
            ConsoleUI consoleUI = new ConsoleUI(habitManager, databaseManager);

            databaseManager.init();
            consoleUI.start();
            databaseManager.closeConnection();
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Error preparing the database: " + e.getMessage()
            );
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Database operation error: " + e.getMessage()
            );
        }
    }


}
