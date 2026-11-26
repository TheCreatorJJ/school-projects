import javax.swing.SwingUtilities;

// Entry point for the Study Space Reservation System
public class AppMain {

    public static void main(String[] args) {
        // Launch Swing UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            DatabaseConfig config = new DatabaseConfig();
            new LoginView(config).show();
        });
    }
}