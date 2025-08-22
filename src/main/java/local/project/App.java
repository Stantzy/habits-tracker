package local.project;

import local.project.Session.Session;
import local.project.Utils.Utils;

import java.sql.SQLException;

public class App {
    private static final String DEFAULT_DIRECTORY =
            System.getProperty("user.home") + "/.habits_tracker/";

    public static void main(String[] args) {
        String dbDirectory = args.length > 0 ? args[0] : DEFAULT_DIRECTORY;

        try {
            Utils.prepareDatabaseDirectory(dbDirectory);
            Session session = new Session(dbDirectory);
            session.start();
            session.close();
        } catch(SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
