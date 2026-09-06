package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseConnection {

    private static String url = "jdbc:postgresql://localhost:5432/banking_db";
    private static String user = "postgres";
    private static String password = "postgres";

    static {
        Map<String, String> envMap = loadDotEnv();

        if (envMap.containsKey("DB_URL") && !envMap.get("DB_URL").isEmpty()) {
            url = envMap.get("DB_URL");
        } else if (System.getenv("DB_URL") != null && !System.getenv("DB_URL").isEmpty()) {
            url = System.getenv("DB_URL");
        }

        if (envMap.containsKey("DB_USER") && !envMap.get("DB_USER").isEmpty()) {
            user = envMap.get("DB_USER");
        } else if (System.getenv("DB_USER") != null && !System.getenv("DB_USER").isEmpty()) {
            user = System.getenv("DB_USER");
        }

        if (envMap.containsKey("DB_PASSWORD")) {
            password = envMap.get("DB_PASSWORD");
        } else if (System.getenv("DB_PASSWORD") != null) {
            password = System.getenv("DB_PASSWORD");
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found. Ensure the JAR file is on your classpath.");
            e.printStackTrace();
        }
    }

    private static Map<String, String> loadDotEnv() {
        Map<String, String> map = new HashMap<>();
        File envFile = new File(".env");
        if (envFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    int eqIdx = line.indexOf('=');
                    if (eqIdx > 0) {
                        String key = line.substring(0, eqIdx).trim();
                        String val = line.substring(eqIdx + 1).trim();
                        if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
                            val = val.substring(1, val.length() - 1);
                        }
                        map.put(key, val);
                    }
                }
            } catch (IOException ignored) {}
        }
        return map;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
