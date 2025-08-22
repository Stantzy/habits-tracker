package local.project.Session;

public class CLI {
    public static final String WELCOME_MSG = "Welcome to The Habits Tracker";
    public static final String USER_MENU =
            """
            1. Show habits
            2. Mark habit
            3. Print status for today
            4. Add habit
            5. Delete habit
            6. Exit
            """;

    public static void printWelcomeMessage() {
        System.out.println(WELCOME_MSG);
    }

    public static void printUserMenu() {
        System.out.println(USER_MENU);
    }
}
