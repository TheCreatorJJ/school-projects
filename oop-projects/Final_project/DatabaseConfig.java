import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Reads database credentials from db.properties and opens JDBC connections
public class DatabaseConfig {

    private static final String CONFIG_FILE = "db.properties";
    private final Properties props;

    public DatabaseConfig() {
        this.props = new Properties();
        loadProperties();
    }

    // Fail fast on startup if the properties file is missing or unreadable
    private void loadProperties() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
        } catch (IOException ex) {
            throw new RuntimeException(
                    "Could not load " + CONFIG_FILE + " from working directory.",
                    ex
            );
        }
    }

    public Connection getConnection() throws SQLException {
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        if (url == null || user == null || password == null) {
            throw new SQLException("Missing db.url, db.user, or db.password in " + CONFIG_FILE);
        }

        // Trim url and user in case db.properties has trailing spaces
        return DriverManager.getConnection(url.trim(), user.trim(), password);
    }
}