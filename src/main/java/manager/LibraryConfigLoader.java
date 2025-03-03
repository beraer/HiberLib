package manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LibraryConfigLoader {
    private static final Properties properties = new Properties();
    private static boolean initialized = false;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        if (!initialized) {
            try (InputStream input = LibraryConfigLoader.class.getClassLoader()
                    .getResourceAsStream("library-config.properties")) {
                if (input == null) {
                    throw new RuntimeException("Unable to find library-config.properties");
                }
                properties.load(input);
                initialized = true;
            } catch (IOException e) {
                throw new RuntimeException("Error loading library configuration", e);
            }
        }
    }

    public static int getMaxBorrowingsPerUser() {
        return Integer.parseInt(properties.getProperty("library.borrowing.max-per-user"));
    }

    public static int getStandardLoanDays() {
        return Integer.parseInt(properties.getProperty("library.borrowing.standard-loan-days"));
    }

    public static String getLibrarianRole() {
        return properties.getProperty("library.roles.librarian");
    }

    public static String getSeniorLibrarianRole() {
        return properties.getProperty("library.roles.senior-librarian");
    }

    public static String getUserRole() {
        return properties.getProperty("library.roles.user");
    }
}