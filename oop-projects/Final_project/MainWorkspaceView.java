//imports layout managers, colors, JDBC classess and Swing controls
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class MainWorkspaceView {

    private static final int FRAME_WIDTH = 940;
    private static final int FRAME_HEIGHT = 560;

    //Palette Constants
    private static final Color SKY_BLUE     = new Color(0x0A, 0x8E, 0xFF);
    private static final Color SIDEBAR_BLUE = new Color(0x08, 0x77, 0xD6);
    private static final Color CLOUDY_WHITE = new Color(0xF0, 0xF0, 0xF0);
    private static final Color AMBER_RED    = new Color(0xFF, 0x3A, 0x12);
    private static final Color SELECTED_RED = new Color(0xFF, 0xE3, 0xDC);
    private static final Color CARD_WHITE   = new Color(0xFF, 0xFF, 0xFF);
    private static final Color TEXT_INK     = new Color(0x1B, 0x1B, 0x1B);
    private static final Color BORDER_GRAY  = new Color(0xD6, 0xD6, 0xD6);

    //Instance Variables
    private final JFrame frame;
    private final DatabaseConfig dbConfig;
    private final UserSession session;

    //UI Components
    private JPanel adminSectionPanel;
    private JTabbedPane workspaceTabs;
    private JTextField txtStudentName;
    private JTextField txtSpaceName;
    private JTextField txtSearchKeyword;

    //Table Models and Tables
    private DefaultTableModel mainTableModel;
    private JTable reservationsTable;
    private DefaultTableModel searchTableModel;
    private JTable searchResultsTable;

    //Constructor
    public MainWorkspaceView(DatabaseConfig dbConfig, UserSession session) {
        this.dbConfig = dbConfig; // Store the database configuration for later use
        this.session = session; // Store the user session for later use
        this.frame = new JFrame("Study Space System"); // Create the main application window
        this.frame.setSize(FRAME_WIDTH, FRAME_HEIGHT); // Set the size of the window
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure the application exits when the window is closed
        this.frame.setLocationRelativeTo(null); // Center the window on the screen

        buildUI(); // Build the user interface
        applySessionRules(); // Apply session-based rules (e.g., admin controls visibility)
        loadMainReservations(); // Load the main reservations into the table
    }

    //Builds the user interface by creating and arranging panels and components
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(CLOUDY_WHITE); // Set the background color of the root panel
        root.setBorder(new EmptyBorder(12, 12, 12, 12)); // Add padding around the root panel

        root.add(createHeaderPanel(), BorderLayout.NORTH); // Add the header panel to the top of the root panel
        root.add(createSidebarPanel(), BorderLayout.WEST); // Add the sidebar panel to the left side of the root panel
        root.add(createTabbedWorkspace(), BorderLayout.CENTER); // Add the tabbed workspace to the center of the root panel

        frame.setContentPane(root); // Set the root panel as the content pane of the main frame
    }

    //Creates the header panel containing the application title, subtitle, and user role badge
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(12, 4));
        header.setBackground(SKY_BLUE); // Set the background color of the header panel
        header.setBorder(BorderFactory.createCompoundBorder( // Create a compound border for the header panel
                BorderFactory.createLineBorder(SIDEBAR_BLUE, 1),
                new EmptyBorder(12, 18, 12, 18) // Add padding inside the border
        ));

        // Create a sub-panel for the title and subtitle
        JPanel leftText = new JPanel(new BorderLayout(0, 4));
        leftText.setOpaque(false);  // Make the sub-panel transparent so the header background shows through

        // Create and configure the title label
        JLabel title = new JLabel("Study Space Reservation System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Set the font of the title label
        title.setForeground(CARD_WHITE); // Set the text color of the title label

        // Create and configure the subtitle label
        JLabel subtitle = new JLabel("Student & Admin Workspace and Study Space Booking");
        // Set the font and color of the subtitle label
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        // Set the text color of the subtitle label
        subtitle.setForeground(CLOUDY_WHITE);

        // Add the title and subtitle labels to the leftText panel
        leftText.add(title, BorderLayout.NORTH);
        // Add the subtitle label to the bottom of the leftText panel
        leftText.add(subtitle, BorderLayout.SOUTH);

        // Create and configure the user role badge
        JLabel badge = new JLabel(session.getRole().toUpperCase() + ": " + session.getFullName());
        // Set the font, color, and background of the badge based on the user's role
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(session.isAdmin() ? CARD_WHITE : TEXT_INK); // Set the text color of the badge based on the user's role
        badge.setOpaque(true); 
        badge.setBackground(session.isAdmin() ? AMBER_RED : CARD_WHITE); // Set the background color of the badge based on the user's role
        badge.setBorder(new EmptyBorder(6, 12, 6, 12)); // Add padding inside the badge

        header.add(leftText, BorderLayout.CENTER); // Add the leftText panel to the center of the header panel
        header.add(badge, BorderLayout.EAST); // Add the badge to the right side of the header panel
        return header; // Return the fully constructed header panel
    }

    //Creates the sidebar panel containing navigation buttons and admin controls
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS)); // Set the layout of the sidebar to a vertical box layout
        sidebar.setBackground(SKY_BLUE);
        sidebar.setPreferredSize(new Dimension(190, 0)); // Set the preferred width of the sidebar and allow it to expand vertically
        sidebar.setBorder(BorderFactory.createCompoundBorder( 
                BorderFactory.createLineBorder(SIDEBAR_BLUE, 1),
                new EmptyBorder(14, 12, 14, 12)
        )); // Add a border and padding to the sidebar

        // Create and configure the navigation title label
        JLabel navTitle = new JLabel("NAVIGATION");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        navTitle.setForeground(CARD_WHITE);
        navTitle.setAlignmentX(JLabel.LEFT_ALIGNMENT); // Align the navigation title to the left


        // Create and configure the "Reservations" button
        JButton btnReservationsTab = createSidebarButton("Reservations");
        btnReservationsTab.addActionListener(e -> {
            workspaceTabs.setSelectedIndex(0);
            loadMainReservations();
        }); // Add an action listener to switch to the "Reservations" tab and load the main reservations when clicked

        // Create and configure the "Search" button
        JButton btnSearchTab = createSidebarButton("Search");
        btnSearchTab.addActionListener(e -> { // Add an action listener to switch to the "Search" tab and focus on the search input field when clicked
            workspaceTabs.setSelectedIndex(1); // Switch to the "Search" tab when clicked
            txtSearchKeyword.requestFocusInWindow(); // Focus on the search input field when the "Search" button is clicked
        });

        // Create the admin section panel and configure its layout and appearance
        adminSectionPanel = new JPanel();
        // Set the layout of the admin section panel to a vertical box layout
        adminSectionPanel.setLayout(new BoxLayout(adminSectionPanel, BoxLayout.Y_AXIS));
        // Set the background of the admin section panel to be transparent so that the sidebar background shows through
        adminSectionPanel.setOpaque(false);
        // Set the alignment of the admin section panel to the left
        adminSectionPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        

        // Create and configure the admin title label
        JLabel adminTitle = new JLabel("ADMIN CONTROLS");
        // Set the font, color, and alignment of the admin title label
        adminTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        // Set the text color of the admin title label to white
        adminTitle.setForeground(CARD_WHITE);
        // Align the admin title label to the left
        adminTitle.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        JButton btnEdit = createSidebarButton("Edit Selected"); // Create and configure the "Edit Selected" button
        btnEdit.addActionListener(e -> editSelectedReservation()); // Add an action listener to edit the selected reservation when clicked

        JButton btnCancel = createSidebarButton("Cancel Selected"); // Create and configure the "Cancel Selected" button
        btnCancel.addActionListener(e -> cancelSelectedReservation()); // Add an action listener to cancel the selected reservation when clicked

        adminSectionPanel.add(Box.createRigidArea(new Dimension(0, 18))); // Add vertical spacing before the admin title
        adminSectionPanel.add(adminTitle); // Add the admin title label to the admin section panel
        adminSectionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        adminSectionPanel.add(btnEdit); // Add the "Edit Selected" button to the admin section panel
        adminSectionPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        adminSectionPanel.add(btnCancel); // Add the "Cancel Selected" button to the admin section panel

        JButton btnLogout = createSidebarButton("Log Out"); // Create and configure the "Log Out" button
        btnLogout.addActionListener(e -> logout()); // Add an action listener to log out the user when clicked

        // Add components to the sidebar panel in the desired order
        sidebar.add(navTitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnReservationsTab);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnSearchTab);
        sidebar.add(adminSectionPanel);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);

        return sidebar;
    }

    //Creates a styled sidebar button with the specified text
    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text); // Create a new JButton with the specified text
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(SIDEBAR_BLUE);
        btn.setForeground(CARD_WHITE);
        btn.setFocusPainted(false); // Disable the focus painting to remove the default focus border when the button is clicked
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Set the cursor to a hand cursor when hovering over the button
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36)); 
        btn.setAlignmentX(JButton.LEFT_ALIGNMENT); // Align the button to the left within its container
        btn.setBorder(BorderFactory.createCompoundBorder( 
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return btn;
    }

    //Applies session-based rules to the UI, such as showing/hiding admin controls and setting the student name field
    private void applySessionRules() {
        adminSectionPanel.setVisible(session.isAdmin());
        if (!session.isAdmin()) {
            txtStudentName.setText(session.getFullName());
            txtStudentName.setEditable(false);
        }
    }

    //Handles user logout by disposing the current frame and showing the login view
    private void logout() {
        frame.dispose(); // Close the current main workspace window
        new LoginView(dbConfig).show(); // Create and display the login view, passing the database configuration for future use
    }


    //Creates the tabbed workspace containing the "Reservations" and "Search Reservations" tabs
    private JTabbedPane createTabbedWorkspace() {
        workspaceTabs = new JTabbedPane();
        workspaceTabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        workspaceTabs.setBackground(SKY_BLUE);
        workspaceTabs.setForeground(TEXT_INK);

        workspaceTabs.addTab("Reservations", createReservationsTab());
        workspaceTabs.addTab("Search Reservations", createSearchTab());

        return workspaceTabs;
    }

    //Creates the "Reservations" tab containing the reservation form and the current reservations table
    private JPanel createReservationsTab() {
        JPanel tab = new JPanel(new BorderLayout(0, 12));
        tab.setBackground(CLOUDY_WHITE);
        tab.setBorder(new EmptyBorder(10, 10, 10, 10));

        tab.add(createReserveFormCard(), BorderLayout.NORTH);
        tab.add(createTableCard("Current Reservations", true), BorderLayout.CENTER);

        return tab;
    }

    //Creates the "Search Reservations" tab containing the search form and the search results table
    private JPanel createSearchTab() {
        JPanel tab = new JPanel(new BorderLayout(0, 12));
        tab.setBackground(CLOUDY_WHITE);
        tab.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel searchCard = new JPanel(new GridBagLayout());
        searchCard.setBackground(CARD_WHITE);
        searchCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints(); // Set up constraints for the layout of components within the search card
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make components fill horizontally within their grid cells

        // Create and configure the search label and input field
        JLabel lblKeyword = new JLabel("Student Name or Space:"); 
        lblKeyword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblKeyword.setForeground(TEXT_INK);

        // Create and configure the search input field
        txtSearchKeyword = new JTextField(20);
        txtSearchKeyword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearchKeyword.setForeground(TEXT_INK);
        txtSearchKeyword.setBackground(CARD_WHITE);
        txtSearchKeyword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));
        txtSearchKeyword.addActionListener(e -> runSearch());

        // Create and configure the search button
        JButton btnRunSearch = new JButton("Search");
        btnRunSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRunSearch.setBackground(SKY_BLUE);
        btnRunSearch.setForeground(CARD_WHITE);
        btnRunSearch.setFocusPainted(false);
        btnRunSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRunSearch.addActionListener(e -> runSearch());

        // Create and configure the "Show All" button to clear the search input and display all reservations
        JButton btnClearSearch = new JButton("Show All");
        btnClearSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClearSearch.setBackground(SIDEBAR_BLUE);
        btnClearSearch.setForeground(CARD_WHITE);
        btnClearSearch.setFocusPainted(false);
        btnClearSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClearSearch.addActionListener(e -> {
            txtSearchKeyword.setText("");
            runSearch();
        });

        // Add the search label, input field, and buttons to the search card using the defined grid bag constraints
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        searchCard.add(lblKeyword, gbc);

        // Add the search input field to the search card
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
        searchCard.add(txtSearchKeyword, gbc);

        // Add the search button to the search card
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        searchCard.add(btnRunSearch, gbc);

        // Add the "Show All" button to the search card
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0;
        searchCard.add(btnClearSearch, gbc);

        // Add the search card to the top of the tab and the search results table below it
        tab.add(searchCard, BorderLayout.NORTH);
        tab.add(createTableCard("Search Results", false), BorderLayout.CENTER);

        return tab;
    }

    //Creates the reservation form card containing input fields for student name and study space, along with a "Reserve Space" button
    private JPanel createReserveFormCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        // Set up constraints for the layout of components within the reservation form card
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        // Create and configure the student name label and input field
        JLabel lblStudent = new JLabel("Student Name:");
        lblStudent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStudent.setForeground(TEXT_INK);

        // Create and configure the student name input field
        txtStudentName = new JTextField(16);
        txtStudentName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtStudentName.setForeground(TEXT_INK);
        txtStudentName.setBackground(CARD_WHITE);
        txtStudentName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));

        // Create and configure the study space label and input field
        JLabel lblSpace = new JLabel("Study Space:");
        lblSpace.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSpace.setForeground(TEXT_INK);


        // Create and configure the study space input field
        txtSpaceName = new JTextField(16);
        txtSpaceName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSpaceName.setForeground(TEXT_INK);
        txtSpaceName.setBackground(CARD_WHITE);
        txtSpaceName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));

        // Create and configure the "Reserve Space" button
        JButton btnReserve = new JButton("Reserve Space");
        btnReserve.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReserve.setBackground(AMBER_RED);
        btnReserve.setForeground(CARD_WHITE);
        btnReserve.setFocusPainted(false);
        btnReserve.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReserve.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                new EmptyBorder(7, 14, 7, 14)
        ));
        btnReserve.addActionListener(e -> reserveSpace());

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        card.add(lblStudent, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
        card.add(txtStudentName, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        card.add(lblSpace, gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1;
        card.add(txtSpaceName, gbc);

        gbc.gridx = 4; gbc.gridy = 0; gbc.weightx = 0;
        card.add(btnReserve, gbc);

        return card;
    }

    //Creates a card panel containing a table with the specified heading text. The table can be either the main reservations table or the search results table based on the isMainTable flag.
    private JPanel createTableCard(String headingText, boolean isMainTable) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel tableHeading = new JLabel(headingText);
        tableHeading.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeading.setForeground(TEXT_INK);

        String[] columns = {"ID", "Student Name", "Study Space", "Created At"};
        DefaultTableModel model = new ReadOnlyTableModel(columns, 0);

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT_INK);
        table.setBackground(CARD_WHITE);
        table.setRowHeight(24);
        table.setGridColor(BORDER_GRAY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(SELECTED_RED);
        table.setSelectionForeground(TEXT_INK);
        table.setDefaultRenderer(Object.class, new ReservationRowRenderer());

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new ReservationHeaderRenderer());
        header.setBackground(SKY_BLUE);
        header.setForeground(CARD_WHITE);

        if (isMainTable) {
            mainTableModel = model;
            reservationsTable = table;
        } else {
            searchTableModel = model;
            searchResultsTable = table;
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(CARD_WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_GRAY, 1));

        card.add(tableHeading, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    //Handles the reservation process by validating input, saving the reservation to the database, and updating the UI accordingly
    private void reserveSpace() {
        String studentName = txtStudentName.getText().trim();
        String spaceName = txtSpaceName.getText().trim();

        if (studentName.isEmpty() || spaceName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Please enter both Student Name and Study Space.",
                    "Missing Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Reservation reservation = new Reservation(studentName, spaceName); // Create a new Reservation object with the provided student name and study space
        String sql = "INSERT INTO public.reservations (student_name, space_name) VALUES (?, ?)"; // Prepare the SQL statement to insert a new reservation into the database

        try (Connection conn = dbConfig.getConnection(); // Establish a connection to the database using the provided DatabaseConfig
             PreparedStatement stmt = conn.prepareStatement(sql)) { // Prepare the SQL statement for execution, allowing for parameter substitution to prevent SQL injection

            stmt.setString(1, reservation.getStudentName());// Set the first parameter (student_name) in the SQL statement to the provided student name
            stmt.setString(2, reservation.getSpaceName()); // Set the second parameter (space_name) in the SQL statement to the provided study space
            stmt.executeUpdate(); // Execute the SQL statement to insert the new reservation into the database

            if (session.isAdmin()) { // If the current user is an admin, clear the student name input field after saving the reservation
                txtStudentName.setText(""); // Clear the student name input field to allow for a new entry
            }
            txtSpaceName.setText(""); // Clear the study space input field to allow for a new entry
            txtSpaceName.requestFocusInWindow(); // Set the focus back to the study space input field for convenience

        // After successfully saving the reservation, reload the main reservations table to reflect the new entry
            loadMainReservations();
            // If the user is currently on the "Search Reservations" tab, re-run the search to update the search results with the new reservation
            JOptionPane.showMessageDialog(
                    frame,
                    "Reserved \"" + reservation.getSpaceName() + "\" for " + reservation.getStudentName() + ".",
                    "Reservation Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException ex) {
            showError("Failed to save reservation", ex); // Display an error message if there was an issue saving the reservation to the database
        }
    }

    //Populates the specified table model with reservation data from the database, optionally filtering by a keyword. Returns the number of rows populated.
    private int populateTable(DefaultTableModel targetModel, String keyword) throws SQLException {
        boolean hasFilter = keyword != null && !keyword.trim().isEmpty();
        String sql = hasFilter
                ? "SELECT id, student_name, space_name, created_at FROM public.reservations "
                  + "WHERE student_name ILIKE ? OR space_name ILIKE ? ORDER BY id ASC"
                : "SELECT id, student_name, space_name, created_at FROM public.reservations ORDER BY id ASC";

        targetModel.setRowCount(0); // Clear existing rows in the target table model before populating it with new data

        // Use try-with-resources to ensure that the database connection and prepared statement are closed automatically after use
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (hasFilter) {
                String pattern = "%" + keyword.trim() + "%"; // Create a search pattern for the SQL query to match any student name or study space containing the keyword, case-insensitively
                stmt.setString(1, pattern); // Set the first parameter in the SQL statement to the search pattern for student names
                stmt.setString(2, pattern); // Set the second parameter in the SQL statement to the search pattern for study spaces
            }

            // Execute the SQL query and process the result set to populate the target table model with reservation data
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    Reservation r = new Reservation(
                            rs.getInt("id"),
                            rs.getString("student_name"),
                            rs.getString("space_name"),
                            rs.getTimestamp("created_at")
                    );
                    targetModel.addRow(new Object[]{
                            r.getId(),
                            r.getStudentName(),
                            r.getSpaceName(),
                            r.getCreatedAt()
                    });
                    count++;
                }
                return count;
            }
        }
    }

    //Loads the main reservations into the main table by calling populateTable with no filter. Displays an error message if there is a database error.
    private void loadMainReservations() {
        try {
            populateTable(mainTableModel, null);
        } catch (SQLException ex) {
            showError("Failed to load reservations", ex);
        }
    }

    //Runs a search for reservations based on the keyword entered in the search input field. Populates the search results table and displays a message if no results are found or if there is a database error.
    private void runSearch() {
        String keyword = txtSearchKeyword.getText().trim();
        try {
            int count = populateTable(searchTableModel, keyword);
            if (count == 0) {
                JOptionPane.showMessageDialog(
                        frame,
                        keyword.isEmpty()
                                ? "No reservations found."
                                : "No reservations matched \"" + keyword + "\".",
                        "Search Results",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (SQLException ex) {
            showError("Failed to search reservations", ex);
        }
    }

    //Retrieves the data of the currently selected row in the active table (either the main reservations table or the search results table) and returns it as an array of objects. Returns null if no row is selected.
    private Object[] getSelectedRowData() {
        boolean onSearchTab = workspaceTabs.getSelectedIndex() == 1;
        JTable activeTable = onSearchTab ? searchResultsTable : reservationsTable;
        DefaultTableModel activeModel = onSearchTab ? searchTableModel : mainTableModel;

        int row = activeTable.getSelectedRow();
        if (row == -1) {
            return null;
        }

        return new Object[]{
                (int) activeModel.getValueAt(row, 0),
                String.valueOf(activeModel.getValueAt(row, 1)),
                String.valueOf(activeModel.getValueAt(row, 2))
        };
    }

    //Allows an admin user to edit the selected reservation by displaying a dialog with editable fields for student name and study space. Updates the reservation in the database and refreshes the table upon confirmation.
    private void editSelectedReservation() {
        if (!session.isAdmin()) {
            return;
        }

        Object[] selected = getSelectedRowData();
        if (selected == null) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Select a reservation row in the active tab first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int id = (int) selected[0];
        String currentStudent = (String) selected[1];
        String currentSpace = (String) selected[2];

        JTextField editStudentField = new JTextField(currentStudent);
        JTextField editSpaceField = new JTextField(currentSpace);

        JPanel dialogPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        dialogPanel.add(new JLabel("Student Name:"));
        dialogPanel.add(editStudentField);
        dialogPanel.add(new JLabel("Study Space:"));
        dialogPanel.add(editSpaceField);

        int result = JOptionPane.showConfirmDialog(
                frame,
                dialogPanel,
                "Edit Reservation #" + id,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String updatedStudent = editStudentField.getText().trim();
        String updatedSpace = editSpaceField.getText().trim();

        if (updatedStudent.isEmpty() || updatedSpace.isEmpty()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Student Name and Study Space cannot be empty.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String sql = "UPDATE public.reservations SET student_name = ?, space_name = ? WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, updatedStudent);
            stmt.setString(2, updatedSpace);
            stmt.setInt(3, id);
            stmt.executeUpdate();

            loadMainReservations();
            if (workspaceTabs.getSelectedIndex() == 1) {
                runSearch();
            }
            JOptionPane.showMessageDialog(
                    frame,
                    "Updated reservation #" + id + ".",
                    "Updated",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException ex) {
            showError("Failed to update reservation", ex);
        }
    }

    private void cancelSelectedReservation() {
        if (!session.isAdmin()) {
            return;
        }

        Object[] selected = getSelectedRowData();
        if (selected == null) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Select a reservation row in the active tab first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int id = (int) selected[0];
        String studentName = (String) selected[1];
        String spaceName = (String) selected[2];

        int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Cancel reservation #" + id + " (" + studentName + " - " + spaceName + ")?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM public.reservations WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

            loadMainReservations();
            if (workspaceTabs.getSelectedIndex() == 1) {
                runSearch();
            }
            JOptionPane.showMessageDialog(
                    frame,
                    "Cancelled reservation #" + id + ".",
                    "Cancelled",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException ex) {
            showError("Failed to cancel reservation", ex);
        }
    }
    //Displays an error message dialog with the specified message and details from the SQLException.
    private void showError(String message, SQLException ex) {
        JOptionPane.showMessageDialog(
                frame,
                message + ":\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
    //Shows the main workspace window by setting its visibility to true.
    public void show() {
        frame.setVisible(true);
    }
    // A custom table model that makes all cells non-editable, used for displaying reservation data in the tables.
    private static class ReadOnlyTableModel extends DefaultTableModel {
        ReadOnlyTableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount); // Call the superclass constructor to initialize the table model with the specified column names and row count
        }

        @Override // Override the isCellEditable method to make all cells non-editable
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    // A custom table cell renderer that changes the background color of selected rows to a specific red color and sets the text color to a predefined ink color.
    private static class ReservationRowRenderer extends DefaultTableCellRenderer {
        @Override // Override the getTableCellRendererComponent method to customize the rendering of table cells based on selection state
        public Component getTableCellRendererComponent(
                JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
            c.setBackground(isSelected ? SELECTED_RED : CARD_WHITE);
            c.setForeground(TEXT_INK);
            return c;
        }
    }

    // A custom table cell renderer for the table header that sets a specific background color, text color, font, and border for the header cells.
    private static class ReservationHeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
            lbl.setBackground(SKY_BLUE);
            lbl.setForeground(CARD_WHITE);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setOpaque(true);
            lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 1, BORDER_GRAY),
                    new EmptyBorder(6, 8, 6, 8)
            ));
            return lbl;
        }
    }
}