Markdown
# Study Space Reservation System — Technical Guide & Line-by-Line Walkthrough

## 1. Purpose of the System
The **Study Space Reservation System** is a desktop Java Swing and PostgreSQL application designed to manage study space bookings with role-based access for **Students** and **Admins**.

* **Students** can register an account, log in, reserve study spaces, view current reservations, and search reservations by student name or study space.
* **Admins** can log in, reserve spaces for any student, search reservations, edit existing reservations, and cancel/delete reservations.
* **Purpose of the design**: Demonstrate clean object-oriented **composition** (rather than overusing inheritance), externalized database configuration (`db.properties`), parameterized SQL via JDBC (`PreparedStatement`), and clear separation between student and administrative workflows.

---

## 2. System Design & Architecture
The project uses **composition**—classes *contain* the components or dependencies they need (such as `JFrame`, `DatabaseConfig`, and `UserSession`) instead of extending `JFrame`.

### Project File Layout
```text
Final_project/
 ├── lib/
 │    └── postgresql-42.7.14.jar   # PostgreSQL JDBC driver
 ├── db.properties                 # Externalized DB URL, username, and password
 ├── DatabaseConfig.java           # Loads db.properties and creates JDBC Connections
 └── finalsspacesystem.java        # Models (Reservation, UserSession), LoginView, and main UI
How Data & Control Flow
Startup (finalsspacesystem.main): Creates DatabaseConfig (reading db.properties) and launches LoginView on the Swing Event Dispatch Thread (SwingUtilities.invokeLater).

Authentication & Registration (LoginView):

Users can register a new STUDENT account (INSERT INTO public.users ...).

Users log in with username and password (SELECT ... FROM public.users WHERE username = ? AND password = ?).

On success, a UserSession object is created, LoginView disposes its frame, and finalsspacesystem opens with that session.

Role-Based Dashboard (finalsspacesystem):

Header: Shows system title, subtitle, and a role badge (STUDENT: <Name> or ADMIN: <Name>).

Sidebar: Provides tab navigation (Reservations, Search), role-gated ADMIN CONTROLS (Edit Selected, Cancel Selected—visible only when session.isAdmin() is true), and Log Out (returns to LoginView).

Workspace (JTabbedPane):

Tab 1 (Reservations): Form to reserve a space + Current Reservations table (ID, Student Name, Study Space, Created At).

Tab 2 (Search Reservations): Keyword search input + Search and Show All buttons + Search Results table.

Status Bar: Displays live feedback on loads, searches, inserts, updates, deletes, and session state.

3. Database Design (PostgreSQL + pgAdmin 4)
Database name: study_space_db (typically on localhost:5432).

Table 1: public.users
Stores user credentials, display names, and roles (STUDENT or ADMIN).

SQL
CREATE TABLE IF NOT EXISTS public.users (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    username VARCHAR(60) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT'
);

INSERT INTO public.users (full_name, username, password, role)
VALUES ('System Admin', 'admin', 'admin123', 'ADMIN')
ON CONFLICT (username) DO NOTHING;
id: Auto-incrementing primary key (SERIAL).

full_name: Display name shown in the header badge and pre-filled for student reservations.

username: Unique login identifier (UNIQUE prevents duplicate usernames).

password: Credential checked during login (in production systems, this should be hashed rather than plain text).

role: Determines access level ('STUDENT' hides admin controls; 'ADMIN' enables edit and cancel).

Table 2: public.reservations
Stores study space reservation records.

SQL
CREATE TABLE IF NOT EXISTS public.reservations (
    id SERIAL PRIMARY KEY,
    student_name VARCHAR(120) NOT NULL,
    space_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
id: Unique reservation identifier (SERIAL PRIMARY KEY).

student_name: Name of the student holding the reservation.

space_name: Name/label of the reserved study space.

created_at: Timestamp automatically recorded when the row is inserted (DEFAULT CURRENT_TIMESTAMP).

4. Line-by-Line Walkthrough of Every Class & Method
A. DatabaseConfig.java
Externalizes database connection settings so credentials are not hardcoded inside Java source files.

private static final String CONFIG_FILE = "db.properties"; — Constant filename for properties in the working directory.

private final Properties props; — Holds key-value pairs (db.url, db.user, db.password).

public DatabaseConfig():

Initializes this.props = new Properties();.

Calls loadProperties(); so configuration is ready as soon as DatabaseConfig is instantiated.

private void loadProperties():

Opens db.properties inside try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) (try-with-resources auto-closes the stream).

Calls props.load(fis); to parse the .properties file.

Catches IOException and wraps it in a RuntimeException with a clear message if the file cannot be read.

public Connection getConnection() throws SQLException:

Reads db.url, db.user, and db.password from props.

Checks if any of the three is null; if so, throws an SQLException explaining what is missing.

Trims url and user and calls DriverManager.getConnection(...) to open and return a JDBC Connection to PostgreSQL.

B. Reservation (Model / Entity Class)
Encapsulates a single reservation row from public.reservations.

Fields: private int id;, private String studentName;, private String spaceName;, private Timestamp createdAt; — Private fields enforce encapsulation.

public Reservation(int id, String studentName, String spaceName, Timestamp createdAt): Full constructor used when reading an existing record from ResultSet.

public Reservation(String studentName, String spaceName): Convenience constructor used when creating a new reservation before id and created_at are generated by PostgreSQL.

getId(), setId(int), getStudentName(), setStudentName(String), getSpaceName(), setSpaceName(String), getCreatedAt(), setCreatedAt(Timestamp): Standard getters and setters for controlled field access.

C. UserSession (Session / Role Class)
Holds the authenticated user's identity and role after login.

Fields: private final int id;, private final String fullName;, private final String username;, private final String role; — Immutable (final) once logged in.

public UserSession(int id, String fullName, String username, String role): Assigns all four session fields.

getId(), getFullName(), getUsername(), getRole(): Read-only accessors for session info.

public boolean isAdmin(): Returns true if role equals "ADMIN" (case-insensitive), allowing role checks to read cleanly (session.isAdmin()).

D. LoginView (Authentication & Student Registration Window)
Composes a JFrame (private final JFrame frame;) and DatabaseConfig (private final DatabaseConfig dbConfig;) to handle sign-in and student registration.

Color Constants: Defines the palette (SKY_BLUE, SIDEBAR_BLUE, CLOUDY_WHITE, AMBER_RED, CARD_WHITE, TEXT_INK, BORDER_GRAY).

public LoginView(DatabaseConfig dbConfig):

Stores dbConfig.

Creates new JFrame("Study Space System - Login"), sets size (460x380), sets EXIT_ON_CLOSE, centers on screen (setLocationRelativeTo(null)), and calls buildUI().

private void buildUI():

Creates the outer root JPanel (BorderLayout, CLOUDY_WHITE background, padding).

Builds the top Sky Blue header panel with title and subtitle.

Builds the white card panel using GridBagLayout with txtUsername, txtPassword, Log In button (Amber Red), and Register Student button (Darker Blue).

Attaches txtPassword.addActionListener(e -> login()) so pressing Enter in the password field submits login, and sets frame.setContentPane(root).

private void login():

Trims username and reads password from JPasswordField.

Validates that neither is empty; shows a warning dialog if blank.

Runs SELECT id, full_name, username, role FROM public.users WHERE username = ? AND password = ? using a PreparedStatement.

If a row matches: builds a UserSession, disposes the login frame (frame.dispose()), and opens new finalsspacesystem(dbConfig, session).show().

If no row matches: shows "Invalid username or password.".

If SQL fails: catches SQLException and displays a database error dialog.

private void openStudentRegistrationDialog():

Creates a modal dialog with Full Name, Username, and Password fields.

If the user clicks OK, trims inputs and checks that all three are non-empty.

Executes INSERT INTO public.users (full_name, username, password, role) VALUES (?, ?, ?, 'STUDENT') via PreparedStatement.

Pre-fills txtUsername with the newly registered username, clears txtPassword, and confirms registration.

Catches SQLException (such as a duplicate username) and displays an error dialog.

public void show(): Makes the login JFrame visible (frame.setVisible(true)).

E. finalsspacesystem (Main Student & Admin Workspace)
Composes JFrame, DatabaseConfig, and UserSession to run the reservation and search workspace.

public finalsspacesystem(DatabaseConfig dbConfig, UserSession session):

Saves dbConfig and session, configures the 940x560 centered JFrame, builds the UI (buildUI()), applies role-based rules (applySessionRules()), and loads initial table rows (loadMainReservations()).

private void buildUI():

Assembles the root panel (BorderLayout) with header (NORTH), sidebar (WEST), tabbed workspace (CENTER), and status bar (SOUTH).

private JPanel createHeaderPanel():

Builds the Sky Blue header with title, subtitle, and right-aligned role badge (STUDENT: ... in white/ink or ADMIN: ... in Amber Red/white).

private JPanel createSidebarPanel():

Builds the vertical Sky Blue sidebar (BoxLayout along Y_AXIS) containing:

NAVIGATION: Reservations button (switches to tab 0 and reloads table) and Search button (switches to tab 1 and focuses txtSearchKeyword).

adminSectionPanel (ADMIN CONTROLS): Edit Selected and Cancel Selected buttons.

Box.createVerticalGlue() + Log Out button at the bottom.

private JButton createSidebarButton(String text):

Reusable helper that styles sidebar buttons consistently (SIDEBAR_BLUE, white bold text, hand cursor, full width, border, and padding).

private void applySessionRules():

Sets adminSectionPanel.setVisible(session.isAdmin()) so ADMIN CONTROLS is completely hidden for students and visible for admins.

If !session.isAdmin(), sets txtStudentName.setText(session.getFullName()) and txtStudentName.setEditable(false) so students book under their own logged-in name.

private void logout():

Disposes the main window (frame.dispose()) and opens a fresh LoginView(dbConfig).

private JTabbedPane createTabbedWorkspace():

Creates the JTabbedPane with two tabs: "Reservations" and "Search Reservations".

private JPanel createReservationsTab():

Combines createReserveFormCard() (NORTH) and createTableCard("Current Reservations", true) (CENTER).

private JPanel createSearchTab():

Creates the search input card (txtSearchKeyword, Search button, Show All button) at NORTH and createTableCard("Search Results", false) at CENTER.

Pressing Enter in txtSearchKeyword or clicking Search calls runSearch(); Show All clears the box and calls runSearch().

private JPanel createReserveFormCard():

Lays out Student Name: (txtStudentName), Study Space: (txtSpaceName), and Reserve Space button (AMBER_RED, calls reserveSpace()) using GridBagLayout.

private JPanel createTableCard(String headingText, boolean isMainTable):

Creates a white card with heading, DefaultTableModel (@Override public boolean isCellEditable returns false so cells aren't edited inline), and a styled JTable.

Configures single-row selection (ListSelectionModel.SINGLE_SELECTION), pale red selected row highlight (SELECTED_RED = #FFE3DC), custom row renderer, and Sky Blue header renderer (SKY_BLUE = #0A8EFF).

Assigns either (mainTableModel, reservationsTable) or (searchTableModel, searchResultsTable) depending on isMainTable.

private JPanel createStatusBar():

Creates the bottom status bar panel and initializes statusLabel with the logged-in user's name and role.

private void reserveSpace():

Reads and trims txtStudentName and txtSpaceName; warns if either is empty.

Creates a Reservation object and runs INSERT INTO public.reservations (student_name, space_name) VALUES (?, ?) via PreparedStatement.

Clears txtSpaceName (and clears txtStudentName only if session.isAdmin(), keeping a student's name locked in), refocuses txtSpaceName, refreshes the main table, and updates statusLabel.

private int populateTable(DefaultTableModel targetModel, String keyword) throws SQLException:

Shared query helper used by both loading and searching.

Clears targetModel (setRowCount(0)).

If keyword is non-empty, queries with WHERE student_name ILIKE ? OR space_name ILIKE ? ORDER BY id ASC using "%" + keyword.trim() + "%" (case-insensitive partial match in PostgreSQL). Otherwise selects all rows ordered by id ASC.

Instantiates Reservation objects from each ResultSet row, adds rows to targetModel, and returns the row count.

private void loadMainReservations():

Calls populateTable(mainTableModel, null) and updates statusLabel with the loaded count.

private void runSearch():

Reads txtSearchKeyword, calls populateTable(searchTableModel, keyword), and updates statusLabel with either the match count or total rows shown.

private Object[] getSelectedRowData():

Checks which tab is currently selected (workspaceTabs.getSelectedIndex() == 1 for Search, otherwise Reservations) to pick the active JTable and DefaultTableModel.

Returns {id, studentName, spaceName} for the selected row, or null if no row is selected.

private void editSelectedReservation():

Guards with if (!session.isAdmin()) return;.

Gets the selected row via getSelectedRowData(); warns if null.

Opens a confirmation dialog with Student Name and Study Space fields pre-filled with current values.

Validates non-empty inputs, then runs UPDATE public.reservations SET student_name = ?, space_name = ? WHERE id = ?.

Refreshes the main table (and search table if on the Search tab) and updates statusLabel.

private void cancelSelectedReservation():

Guards with if (!session.isAdmin()) return;.

Gets the selected row via getSelectedRowData(); warns if null.

Asks for YES_NO_OPTION confirmation showing the reservation ID, student name, and space name.

Runs DELETE FROM public.reservations WHERE id = ?, refreshes the active table(s), and updates statusLabel.

private void showError(String message, SQLException ex):

Displays an error JOptionPane combining the context message and ex.getMessage().

public void show(): Makes the main JFrame visible.

public static void main(String[] args):

Calls SwingUtilities.invokeLater(...) so Swing components start on the Event Dispatch Thread, instantiates DatabaseConfig, and displays LoginView.

---

### Quick Tip for Your Defense
If your professor points at any line and asks *"What does this do and why did you write it this way?"*, focus on:
1. **Why composition?** Keeps UI and dependencies modular without inheriting unnecessary `JFrame` methods across every screen.
2. **Why `PreparedStatement` + `?`?** Prevents SQL injection and safely handles text values.
3. **Why `try (...)`?** Automatically closes JDBC resources (`Connection`, `PreparedStatement`, `ResultSet`) to prevent leaks.
4. **Why `@Override`?** Tells the compiler you're intentionally overriding a superclass method so typos don't slip past unnoticed.




