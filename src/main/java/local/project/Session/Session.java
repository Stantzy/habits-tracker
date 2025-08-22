package local.project.Session;

import local.project.Entity.Habit;
import local.project.Entity.User;
import local.project.Manager.DatabaseManager;
import local.project.Manager.HabitManager;
import local.project.Utils.Utils;

import java.io.Console;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Session {
    private User sessionOwner;
    private final HabitManager habitManager;
    private final DatabaseManager databaseManager;

    public Session(String dbPath) {
        habitManager = new HabitManager();
        databaseManager = new DatabaseManager(dbPath);
    }

    public void start() throws SQLException {
        boolean ok = false;
        databaseManager.init();

        try {
            final int MAX_ATTEMPTS = 3;
            int loginAttempt = 1;

            while(!ok && loginAttempt <= MAX_ATTEMPTS) {
                ok = login(loginAttempt, MAX_ATTEMPTS);
                loginAttempt++;
            }
            if (!ok) {
                System.out.println("Login failed. Aborting.");
                return;
            }
        } catch (SQLException e) {
            throw new SQLException("Session.java: start()", e);
        }

        Scanner userInput = new Scanner(System.in);
        CLI.printWelcomeMessage();
        CLI.printUserMenu();

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
                    habitManager.printStatusForToday(sessionOwner.getId());
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

    public void close() {
        databaseManager.closeConnection();
    }

    private boolean login(int attempt, int maxAttempts) throws SQLException {
        boolean success = false;
        // below: 2 = username + password, [0] is username, [1] is password
        final int ARRAY_CAPACITY = 2;
        ArrayList<String> data = new ArrayList<>(ARRAY_CAPACITY);
        byte[] passwordBytes;
        boolean registerFlag = false;

        registerFlag = readUsernameAndPassword(data);
        if(registerFlag) {
            User newUser = registerNewUser();
            sessionOwner = databaseManager.getUser(newUser.getUsername());
            success = true;
        } else {
            sessionOwner = new User(data.get(0), data.get(1));
            try {
                success = verifyUser(sessionOwner);
            } catch (SQLException e) {
                throw new SQLException("Session.java: login()", e);
            }
            if (success) {
                System.out.println("Login successful!");
            } else {
                System.out.println(
                        "Incorrect login/password! " + attempt + "/" + maxAttempts
                );
                sessionOwner = null;
            }
        }

        return success;
    }

    private boolean readUsernameAndPassword(ArrayList<String> data) {
        Console console = System.console();
        if(console == null)
            throw new RuntimeException("Session.class: Console init error");

        System.out.println(
                "To log in, enter your username. Type \"register\" to create a new user."
        );
        System.out.print("Login: ");
        String username = console.readLine();
        String password = null;
        if(username.equalsIgnoreCase("register")) {
            return true;
        } else {
            System.out.print("Password: ");
            password = new String(console.readPassword());
        }

        data.add(username);
        data.add(password);
        return false;
    }

    private User registerNewUser() throws SQLException {
        Console console = System.console();
        System.out.print("Enter new username: ");
        String newUsername = console.readLine();
        System.out.print("Enter new password: ");
        String password = new String(console.readPassword());
        User newUser = new User(newUsername, password);

        try {
            databaseManager.addUser(newUser);
            return databaseManager.getUser(newUsername);
        } catch (SQLException e) {
            throw new SQLException("Error creating new user", e);
        }
    }

    private boolean verifyUser(User user) throws SQLException {
        byte[] userHashFromDatabase;
        byte[] userCurrentHash;
        User userStoredInDatabase = null;

        try {
            userCurrentHash = user.getPasswordHash();
            if (!databaseManager.isUserExists(user))
                return false;

            userStoredInDatabase = databaseManager.getUser(user.getUsername());
            userHashFromDatabase = userStoredInDatabase.getPasswordHash();
            if(Arrays.equals(userCurrentHash, userHashFromDatabase)) {
                user.setId(userStoredInDatabase.getId());
                return true;
            }
            return false;
        } catch(SQLException e) {
            throw new SQLException("Session.java: verifyUser()", e);
        }
    }

    private void printAllHabits() throws SQLException {
        Set<Habit> habits = databaseManager.getHabitsByUser(sessionOwner.getId());
        for(Habit h : habits) {
            Set<LocalDate> dates = h.getCompletedDates();
            System.out.println(h.getTitle() + " : " +
                    Arrays.toString(dates.toArray()));
        }
    }

    private void updateHabits() throws SQLException {
        Set<Habit> habits = databaseManager.getAllHabitsWithUsersAndDates();
        for(Habit h : habits) {
            int id = h.getId();
            String title = h.getTitle();
            habitManager.addHabit(id, title, h.getUser());
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
        databaseManager.saveDateInHabit(habit, localDate);
    }

    private void handleAddHabit(Scanner userInput) throws SQLException {
        String habitTitleToAdd = userInput.nextLine();
        Habit habit;
        habit = habitManager.addHabit(0, habitTitleToAdd, sessionOwner);  // id=0?
        databaseManager.saveHabit(habit);
    }

    private void handleDeleteHabit(Scanner userInput) throws SQLException {
        String habitTitleToDelete = userInput.nextLine();
        databaseManager.deleteHabitByTitle(habitTitleToDelete);
        habitManager.deleteHabit(habitTitleToDelete);
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
