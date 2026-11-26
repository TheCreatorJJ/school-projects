import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

// Main application window for booking, searching, and managing study spaces
public class MainWorkspaceView {

    private static final int FRAME_WIDTH = 940;
    private static final int FRAME_HEIGHT = 560;

    // UI color palette
    private static final Color SKY_BLUE      = new Color(0x0A, 0x8E, 0xFF);
    private static final Color SIDEBAR_BLUE  = new Color(0x08, 0x77, 0xD6);
    private static final Color CLOUDY_WHITE  = new Color(0xF0, 0xF0, 0xF0);
    private static final Color AMBER_RED     = new Color(0xFF, 0x3A, 0x12);
    private static final Color SELECTED_RED  = new Color(0xFF, 0xE3, 0xDC);
    private static final Color CARD_WHITE    = new Color(0xFF, 0xFF, 0xFF);
    private static final Color TEXT_INK      = new Color(0x1B, 0x1B, 0x1B);
    private static final Color BORDER_GRAY   = new Color(0xD6, 0xD6, 0xD6);

    private final JFrame frame;
    private final DatabaseConfig dbConfig;
    private final UserSession session;

    private JPanel adminSectionPanel;
    private JTabbedPane workspaceTabs;
    private JTextField txtStudentName;
    private JTextField txtSpaceName;
    private JTextField txtSearchKeyword;

    private DefaultTableModel mainTableModel;
    private JTable reservationsTable;
    private DefaultTableModel searchTableModel;
    private JTable searchResultsTable;
    private JLabel statusLabel;

    public MainWorkspaceView(DatabaseConfig dbConfig, UserSession session) {
        this.dbConfig = dbConfig;
        this.session = session;
        this.frame = new JFrame("Study Space System");
        this.frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setLocationRelativeTo(null);

        buildUI();
        applySessionRules();
        loadMainReservations();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(CLOUDY_WHITE);
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        root.add(createHeaderPanel(), BorderLayout.NORTH);
        root.add(createSidebarPanel(), BorderLayout.WEST);
        root.add(createTabbedWorkspace(), BorderLayout.CENTER);
        root.add(createStatusBar(), BorderLayout.SOUTH);

        frame.setContentPane(root);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(12, 4));
        header.setBackground(SKY_BLUE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SIDEBAR_BLUE, 1),
                new EmptyBorder(12, 18, 12, 18)
        ));

        JPanel leftText = new JPanel(new BorderLayout(0, 4));
        leftText.setOpaque(false);

        JLabel title = new JLabel("Study Space Reservation System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(CARD_WHITE);

        JLabel subtitle = new JLabel("Student & Admin Workspace • Study Space Booking");
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        subtitle.setForeground(CLOUDY_WHITE);

        leftText.add(title, BorderLayout.NORTH);
        leftText.add(subtitle, BorderLayout.SOUTH);

        // Highlight admin role badge in red; keep student badge neutral white
        JLabel badge = new JLabel(session.getRole().toUpperCase() + ": " + session.getFullName());
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(session.isAdmin() ? CARD_WHITE : TEXT_INK);
        badge.setOpaque(true);
        badge.setBackground(session.isAdmin() ? AMBER_RED : CARD_WHITE);
        badge.setBorder(new EmptyBorder(6, 12, 6, 12));

        header.add(leftText, BorderLayout.CENTER);
        header.add(badge, BorderLayout.EAST);
        return header;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SKY_BLUE);
        sidebar.setPreferredSize(new Dimension(190, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SIDEBAR_BLUE, 1),
                new EmptyBorder(14, 12, 14, 12)
        ));

        JLabel navTitle = new JLabel("NAVIGATION");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        navTitle.setForeground(CARD_WHITE);
        navTitle.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        JButton btnReservationsTab = createSidebarButton("Reservations");
        btnReservationsTab.addActionListener(e -> {
            workspaceTabs.setSelectedIndex(0);
            loadMainReservations();
        });

        JButton btnSearchTab = createSidebarButton("Search");
        btnSearchTab.addActionListener(e -> {
            workspaceTabs.setSelectedIndex(1);
            txtSearchKeyword.requestFocusInWindow();
        });

        // Separate container so admin actions can be toggled off for student logins
        adminSectionPanel = new JPanel();
        adminSectionPanel.setLayout(new BoxLayout(adminSectionPanel, BoxLayout.Y_AXIS));
        adminSectionPanel.setOpaque(false);
        adminSectionPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);

        JLabel adminTitle = new JLabel("ADMIN CONTROLS");
        adminTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        adminTitle.setForeground(CARD_WHITE);
        adminTitle.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        JButton btnEdit = createSidebarButton("Edit Selected");
        btnEdit.addActionListener(e -> editSelectedReservation());

        JButton btnCancel = createSidebarButton("Cancel Selected");
        btnCancel.addActionListener(e -> cancelSelectedReservation());

        adminSectionPanel.add(Box.createRigidArea(new Dimension(0, 18)));
        adminSectionPanel.add(adminTitle);
        adminSectionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        adminSectionPanel.add(btnEdit);
        adminSectionPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        adminSectionPanel.add(btnCancel);

        JButton btnLogout = createSidebarButton("Log Out");
        btnLogout.addActionListener(e -> logout());

        sidebar.add(navTitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnReservationsTab);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnSearchTab);
        sidebar.add(adminSectionPanel);
        // Push the logout button to the bottom of the sidebar
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);

        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(SIDEBAR_BLUE);
        btn.setForeground(CARD_WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setAlignmentX(JButton.LEFT_ALIGNMENT);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return btn;
    }

    // Lock down admin controls and auto-fill student name when logged in as a student
    private void applySessionRules() {
        adminSectionPanel.setVisible(session.isAdmin());
        if (!session.isAdmin()) {
            txtStudentName.setText(session.getFullName());
            txtStudentName.setEditable(false);
        }
    }

    private void logout() {
        frame.dispose();
        new LoginView(dbConfig).show();
    }

    private JTabbedPane createTabbedWorkspace() {
        workspaceTabs = new JTabbedPane();
        workspaceTabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        workspaceTabs.setBackground(SKY_BLUE);
        workspaceTabs.setForeground(TEXT_INK);

        workspaceTabs.addTab("Reservations", createReservationsTab());
        workspaceTabs.addTab("Search Reservations", createSearchTab());

        return workspaceTabs;
    }

    private JPanel createReservationsTab() {
        JPanel tab = new JPanel(new BorderLayout(0, 12));
        tab.setBackground(CLOUDY_WHITE);
        tab.setBorder(new EmptyBorder(10, 10, 10, 10));

        tab.add(createReserveFormCard(), BorderLayout.NORTH);
        tab.add(createTableCard("Current Reservations", true), BorderLayout.CENTER);

        return tab;
    }

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

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblKeyword = new JLabel("Student Name or Space:");
        lblKeyword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblKeyword.setForeground(TEXT_INK);

        txtSearchKeyword = new JTextField(20);
        txtSearchKeyword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearchKeyword.setForeground(TEXT_INK);
        txtSearchKeyword.setBackground(CARD_WHITE);
        txtSearchKeyword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));
        txtSearchKeyword.addActionListener(e -> runSearch());

        JButton btnRunSearch = new JButton("Search");
        btnRunSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRunSearch.setBackground(SKY_BLUE);
        btnRunSearch.setForeground(CARD_WHITE);
        btnRunSearch.setFocusPainted(false);
        btnRunSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRunSearch.addActionListener(e -> runSearch());

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

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        searchCard.add(lblKeyword, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
        searchCard.add(txtSearchKeyword, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        searchCard.add(btnRunSearch, gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0;
        searchCard.add(btnClearSearch, gbc);

        tab.add(searchCard, BorderLayout.NORTH);
        tab.add(createTableCard("Search Results", false), BorderLayout.CENTER);

        return tab;
    }

    private JPanel createReserveFormCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblStudent = new JLabel("Student Name:");
        lblStudent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStudent.setForeground(TEXT_INK);

        txtStudentName = new JTextField(16);
        txtStudentName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtStudentName.setForeground(TEXT_INK);
        txtStudentName.setBackground(CARD_WHITE);
        txtStudentName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));

        JLabel lblSpace = new JLabel("Study Space:");
        lblSpace.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSpace.setForeground(TEXT_INK);

        txtSpaceName = new JTextField(16);
        txtSpaceName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSpaceName.setForeground(TEXT_INK);
        txtSpaceName.setBackground(CARD_WHITE);
        txtSpaceName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));

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

    // Builds either the main table or search table based on isMainTable
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
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Prevent inline edits; admins must use the Edit Selected button
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT_INK);
        table.setBackground(CARD_WHITE);
        table.setRowHeight(24);
        table.setGridColor(BORDER_GRAY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(SELECTED_RED);
        table.setSelectionForeground(TEXT_INK);

        // Keep row text readable when highlighted
        DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                c.setBackground(isSelected ? SELECTED_RED : CARD_WHITE);
                c.setForeground(TEXT_INK);
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, rowRenderer);

        // Custom header styling so Look-and-Feel defaults don't override SKY_BLUE
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
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
        };
        table.getTableHeader().setDefaultRenderer(headerRenderer);
        table.getTableHeader().setBackground(SKY_BLUE);
        table.getTableHeader().setForeground(CARD_WHITE);

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

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        bar.setBackground(CARD_WHITE);
        bar.setBorder(BorderFactory.createLineBorder(BORDER_GRAY, 1));

        statusLabel = new JLabel("Logged in as " + session.getFullName() + " (" + session.getRole() + ").");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_INK);

        bar.add(statusLabel);
        return bar;
    }

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

        Reservation reservation = new Reservation(studentName, spaceName);
        String sql = "INSERT INTO public.reservations (student_name, space_name) VALUES (?, ?)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, reservation.getStudentName());
            stmt.setString(2, reservation.getSpaceName());
            stmt.executeUpdate();

            // Only clear student name for admins; students keep their locked full name
            if (session.isAdmin()) {
                txtStudentName.setText("");
            }
            txtSpaceName.setText("");
            txtSpaceName.requestFocusInWindow();

            loadMainReservations();
            statusLabel.setText("Reserved \"" + reservation.getSpaceName() + "\" for " + reservation.getStudentName() + ".");

        } catch (SQLException ex) {
            showError("Failed to save reservation", ex);
        }
    }

    // Shared loader for both full table refreshes and keyword searches
    private int populateTable(DefaultTableModel targetModel, String keyword) throws SQLException {
        boolean hasFilter = keyword != null && !keyword.trim().isEmpty();
        String sql = hasFilter
                ? "SELECT id, student_name, space_name, created_at FROM public.reservations "
                  + "WHERE student_name ILIKE ? OR space_name ILIKE ? ORDER BY id ASC"
                : "SELECT id, student_name, space_name, created_at FROM public.reservations ORDER BY id ASC";

        targetModel.setRowCount(0);

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (hasFilter) {
                String pattern = "%" + keyword.trim() + "%";
                stmt.setString(1, pattern);
                stmt.setString(2, pattern);
            }

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

    private void loadMainReservations() {
        try {
            int count = populateTable(mainTableModel, null);
            statusLabel.setText("Loaded " + count + " reservation(s) from public.reservations.");
        } catch (SQLException ex) {
            showError("Failed to load reservations", ex);
        }
    }

    private void runSearch() {
        String keyword = txtSearchKeyword.getText().trim();
        try {
            int count = populateTable(searchTableModel, keyword);
            if (keyword.isEmpty()) {
                statusLabel.setText("Showing all " + count + " reservation(s) in Search tab.");
            } else {
                statusLabel.setText("Found " + count + " match(es) for \"" + keyword + "\".");
            }
        } catch (SQLException ex) {
            showError("Failed to search reservations", ex);
        }
    }

    // Reads whichever tab is currently visible so Edit/Cancel works from either view
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
            statusLabel.setText("Updated reservation #" + id + ".");

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
            statusLabel.setText("Cancelled reservation #" + id + ".");

        } catch (SQLException ex) {
            showError("Failed to cancel reservation", ex);
        }
    }

    private void showError(String message, SQLException ex) {
        JOptionPane.showMessageDialog(
                frame,
                message + ":\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void show() {
        frame.setVisible(true);
    }
}