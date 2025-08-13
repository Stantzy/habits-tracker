package local.project.Utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class Utils {
    public static boolean checkFile(String filePath) {
        Path path = Paths.get(filePath);
        return Files.exists(path) && Files.isRegularFile(path);
    }

    public static LocalDate getLocalDateFromString(String dateString)
    throws DateTimeParseException {
        LocalDate localDate;
        if(dateString.toLowerCase(Locale.ROOT).equals("today")) {
            localDate = LocalDate.now();
        } else {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d-M-yyyy");
            localDate = LocalDate.parse(dateString, dateTimeFormatter);
        }
        return localDate;
    }
}
