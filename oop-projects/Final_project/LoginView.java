import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

// Handles sign-in and self-registration for student accounts
public class LoginView {

    private static final Color SKY_BLUE     = new Color(0x0A, 0x8E, 0xFF);
    private static final Color SIDEBAR_BLUE = new Color(0x08, 0x77, 0xD6);
    private static final Color CLOUDY_WHITE = new Color(0xF0, 0xF0, 0xF0);
    private static final Color AMBER_RED    = new Color(0xFF, 0x3A, 0x12);
    private static final Color CARD_WHITE   = new Color(0xFF, 0xFF, 0xFF);
    private static final Color TEXT_INK     = new Color(0x1B, 0x1B, 0x1B);
    private static final Color BORDER_GRAY  = new Color(0xD6, 0xD6, 0xD6);

    private final JFrame frame;
    private final DatabaseConfig dbConfig;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginView(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
        this.frame = new JFrame("Study Space System - Login");
        this.frame.setSize(460, 380);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(CLOUDY_WHITE);
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        // Top banner
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setBackground(SKY_BLUE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SIDEBAR_BLUE, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel title = new JLabel("Study Space Reservation System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(CARD_WHITE);

        JLabel subtitle = new JLabel("Sign in as Student or Admin", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(CLOUDY_WHITE);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        // Login form card
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(TEXT_INK);

        txtUsername = new JTextField(18);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setForeground(TEXT_INK);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(TEXT_INK);

        txtPassword = new JPasswordField(18);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setForeground(TEXT_INK);
        // Allow pressing Enter in the password box to submit
        txtPassword.addActionListener(e -> login());

        JButton btnLogin = new JButton("Log In");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogin.setBackground(AMBER_RED);
        btnLogin.setForeground(CARD_WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> login());

        JButton btnRegister = new JButton("Register Student");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegister.setBackground(SIDEBAR_BLUE);
        btnRegister.setForeground(CARD_WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.addActionListener(e -> openStudentRegistrationDialog());

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        card.add(lblUser, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
        card.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        card.add(lblPass, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1;
        card.add(txtPassword, gbc);

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnLogin);
        btnRow.add(btnRegister);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 6, 4, 6);
        card.add(btnRow, gbc);

        root.add(header, BorderLayout.NORTH);
        root.add(card, BorderLayout.CENTER);
        frame.setContentPane(root);
    }

    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Please enter both username and password.",
                    "Missing Credentials",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String sql = "SELECT id, full_name, username, role FROM public.users WHERE username = ? AND password = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserSession session = new UserSession(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("username"),
                            rs.getString("role")
                    );
                    // Close login window and launch main workspace with the active session
                    frame.dispose();
                    new MainWorkspaceView(dbConfig, session).show();
                } else {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Invalid username or password.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Database error during login:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void openStudentRegistrationDialog() {
        JTextField regFullName = new JTextField();
        JTextField regUsername = new JTextField();
        JPasswordField regPassword = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        panel.add(new JLabel("Full Name:"));
        panel.add(regFullName);
        panel.add(new JLabel("Username:"));
        panel.add(regUsername);
        panel.add(new JLabel("Password:"));
        panel.add(regPassword);

        int option = JOptionPane.showConfirmDialog(
                frame,
                panel,
                "Register Student Account",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        String fullName = regFullName.getText().trim();
        String username = regUsername.getText().trim();
        String password = new String(regPassword.getPassword());

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "All registration fields are required.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Self-registered accounts always default to the STUDENT role
        String sql = "INSERT INTO public.users (full_name, username, password, role) VALUES (?, ?, ?, 'STUDENT')";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fullName);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.executeUpdate();

            // Pre-fill username so the student only needs to type their password
            txtUsername.setText(username);
            txtPassword.setText("");
            JOptionPane.showMessageDialog(
                    frame,
                    "Student account created! You can now log in.",
                    "Registration Complete",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Could not register student:\n" + ex.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void show() {
        frame.setVisible(true);
    }
}