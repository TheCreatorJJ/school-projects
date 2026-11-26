# Study Space Reservation System

A Java Swing desktop application backed by **PostgreSQL** (via JDBC) for booking, searching, and managing campus study spaces. The application is structured into modular Java classes and supports role-based access control for **Students** and **Administrators**.

---

## 1. Project Overview & Scenario

### Why We Built This
During peak study periods, shared study rooms and tables are often tracked manually through paper logs, chat messages, or disconnected spreadsheets—leading to double-bookings, confusion over availability, and extra administrative overhead.

This system provides a centralized desktop application where:
* **Students** can self-register, log in, and reserve study spaces under their own name.
* **Administrators** can reserve spaces on behalf of any student, search bookings by student or space name, and edit or cancel existing reservations.

### Tech Stack
* **Language & UI:** Java (JDK 11+) with `javax.swing` and `java.awt`
* **Database:** PostgreSQL (`public.users` and `public.reservations`)
* **Database Connectivity:** JDBC (`java.sql.*` + PostgreSQL JDBC driver JAR in `lib/`)

---

## 2. Project Structure

```text
Final_project/
├── db.properties              # Database connection settings (URL, username, password)
├── lib/                       # External libraries (e.g., postgresql-42.7.4.jar)
├── bin/                       # Compiled .class output directory
├── AppMain.java               # Application entry point
├── DatabaseConfig.java        # Loads db.properties and creates JDBC connections
├── UserSession.java           # Immutable session model for the logged-in user
├── Reservation.java           # Data model representing a study space booking
├── LoginView.java             # Login screen and student registration dialog
├── MainWorkspaceView.java     # Main dashboard, booking form, search tab, and admin controls
└── README.md                  # Project documentation
```

---

## 3. Database Setup & Running the Project

### Required PostgreSQL Tables
Ensure your PostgreSQL database contains the following tables in the `public` schema:

```sql
CREATE TABLE public.users (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    username VARCHAR(60) UNIQUE NOT NULL,
    password VARCHAR(120) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT'
);

CREATE TABLE public.reservations (
    id SERIAL PRIMARY KEY,
    student_name VARCHAR(120) NOT NULL,
    space_name VARCHAR(120) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Configuration (`db.properties`)
Place `db.properties` in the root of `Final_project/`:

```properties
db.url=jdbc:postgresql://localhost:5432/your_database_name
db.user=postgres
db.password=your_password
```

### Compile & Run (Windows PowerShell)
From inside `Final_project`:

```powershell
Remove-Item *.class -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path bin -Force | Out-Null
javac -d bin -cp "lib\*" *.java
java -cp "bin;lib\*" AppMain
```

*(On macOS/Linux, replace `;` with `:` in `java -cp "bin:lib/*" AppMain`.)*

---

## 4. How the Application Works (End-to-End Flow)

1. **Startup (`AppMain` -> `DatabaseConfig` -> `LoginView`):**
   * `AppMain` schedules startup on Swing's Event Dispatch Thread (`SwingUtilities.invokeLater`).
   * `DatabaseConfig` loads `db.properties` from the working directory.
   * `LoginView` opens a 460×380 login window.
2. **Authentication & Student Registration (`LoginView`):**
   * **Login:** Reads `txtUsername` and `txtPassword`, checks for blank values, and runs a parameterized `SELECT` query against `public.users`. On match, it builds a `UserSession`, disposes `LoginView`, and opens `MainWorkspaceView`.
   * **Student Registration:** Opens a modal `JOptionPane` form for Full Name, Username, and Password, inserts a new `'STUDENT'` row into `public.users`, and pre-fills the username field.
3. **Role-Based Workspace (`MainWorkspaceView`):**
   * `applySessionRules()` checks `session.isAdmin()`.
   * For **Students**, the `ADMIN CONTROLS` sidebar section is hidden (`setVisible(false)`) and `txtStudentName` is pre-filled with `session.getFullName()` and locked (`setEditable(false)`).
   * For **Admins**, `Edit Selected` and `Cancel Selected` buttons are visible, and `txtStudentName` stays editable.
4. **Booking, Searching & Admin Operations:**
   * **Reserve (`reserveSpace`):** Validates inputs, creates a `Reservation` object, executes an `INSERT` into `public.reservations`, reloads the main table, and displays a confirmation dialog.
   * **Search (`runSearch` / `populateTable`):** Uses `ILIKE` with `%keyword%` wildcards on `student_name` and `space_name` for case-insensitive matching, or loads all rows ordered by `id ASC` when blank.
   * **Edit & Cancel (`editSelectedReservation` / `cancelSelectedReservation`):** Reads the selected row from whichever tab is currently active (`getSelectedRowData`), verifies admin status, executes `UPDATE` or `DELETE` by `id`, and refreshes the table(s).

---

## 5. Object-Oriented Programming (OOP) Concepts Used

* **Encapsulation:** Instance fields across `Reservation`, `UserSession`, `DatabaseConfig`, `LoginView`, and `MainWorkspaceView` are marked `private`, exposing controlled access through getters, setters, or public UI methods (`show()`).
* **Immutability (`UserSession`):** All fields (`id`, `fullName`, `username`, `role`) are `private final` with no setters, ensuring session identity cannot change after login.
* **Constructor Overloading (`Reservation`):** Provides a 4-parameter constructor (for rows loaded from PostgreSQL with `id` and `createdAt`) and a 2-parameter constructor (for new reservations prior to database insertion).
* **Composition ("Has-A" Relationship):** `LoginView` and `MainWorkspaceView` hold references to `DatabaseConfig` and `UserSession` and compose Swing containers (`JFrame`, `JPanel`, `JTabbedPane`, `JTable`) rather than subclassing `JFrame`.
* **Inheritance & Polymorphism:** Static nested classes in `MainWorkspaceView` (`ReadOnlyTableModel`, `ReservationRowRenderer`, and `ReservationHeaderRenderer`) extend `DefaultTableModel` and `DefaultTableCellRenderer`, overriding `isCellEditable` and `getTableCellRendererComponent` to customize table behavior and styling.

---

## 6. Line-by-Line Code Breakdown

### Module 1: `AppMain.java`
| Lines | Code / Block | Explanation |
| :--- | :--- | :--- |
| `1` | `import javax.swing.SwingUtilities;` | Imports Swing's thread utility class for safe GUI initialization. |
| `3` | `public class AppMain {` | Declares the public entry-point class matching `AppMain.java`. |
| `5` | `public static void main(String[] args) {` | Standard JVM entry method executed when running `java AppMain`. |
| `6–10` | `SwingUtilities.invokeLater(() -> { ... });` | Queues GUI creation on the Event Dispatch Thread (EDT) to prevent Swing threading issues. |
| `7–8` | `DatabaseConfig config = new DatabaseConfig();` / `new LoginView(config).show();` | Loads `db.properties`, passes `config` into `LoginView`, and makes the login window visible. |

---

### Module 2: `DatabaseConfig.java`
| Lines | Code / Block | Explanation |
| :--- | :--- | :--- |
| `1–6` | `import java.io...` / `import java.sql...` / `import java.util.Properties;` | Imports file stream, exception, JDBC connection, and `.properties` parser classes. |
| `8–11` | `public class DatabaseConfig {` / `private static final String CONFIG_FILE = "db.properties";` / `private final Properties props;` | Encapsulates configuration file name and loaded key-value properties. |
| `13–16` | `public DatabaseConfig() { this.props = new Properties(); loadProperties(); }` | Constructor initializes `Properties` and immediately loads `db.properties`. |
| `18–27` | `private void loadProperties() { ... }` | Opens `db.properties` via `try-with-resources` (`FileInputStream`) and throws a `RuntimeException` if the file cannot be read. |
| `29–40` | `public Connection getConnection() throws SQLException { ... }` | Reads `db.url`, `db.user`, and `db.password`, validates none are `null`, trims whitespace on URL/user, and returns an active JDBC `Connection`. |

---

### Module 3: `UserSession.java`
| Lines | Code / Block | Explanation |
| :--- | :--- | :--- |
| `1–5` | `public class UserSession { private final int id; ... }` | Declares immutable (`private final`) fields storing authenticated user ID, full name, username, and role. |
| `7–12` | `public UserSession(int id, String fullName, String username, String role)` | Constructor assigning all four session attributes upon successful login. |
| `14–28` | `getId()`, `getFullName()`, `getUsername()`, `getRole()` | Read-only getter methods exposing session properties to UI views. |
| `30–32` | `public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(role); }` | Null-safe helper returning `true` when the user's role is `"ADMIN"`. |

---

### Module 4: `Reservation.java`
| Lines | Code / Block | Explanation |
| :--- | :--- | :--- |
| `1–7` | `import java.sql.Timestamp;` / `public class Reservation { ... }` | Declares private fields (`id`, `studentName`, `spaceName`, `createdAt`) matching `public.reservations` columns. |
| `9–14` | `public Reservation(int id, String studentName, String spaceName, Timestamp createdAt)` | Full constructor used when mapping rows returned by a `ResultSet`. |
| `16–19` | `public Reservation(String studentName, String spaceName)` | Overloaded constructor used when creating a new reservation before `id` and `createdAt` exist. |
| `21–51` | `getId`/`setId`, `getStudentName`/`setStudentName`, `getSpaceName`/`setSpaceName`, `getCreatedAt`/`setCreatedAt` | Standard getters and setters for encapsulated fields. |

---

### Module 5: `LoginView.java`
| Lines | Code / Block | Explanation |
| :--- | :--- | :--- |
| `1–21` | `import java.awt...` / `import java.sql...` / `import javax.swing...` | Imports layout managers, colors, fonts, JDBC classes, and Swing controls. |
| `23–37` | Palette constants & instance fields (`frame`, `dbConfig`, `txtUsername`, `txtPassword`) | Defines consistent UI colors and stores the login window, DB helper, and input fields. |
| `39–46` | `public LoginView(DatabaseConfig dbConfig)` | Configures a `460x380` centered `JFrame` with `EXIT_ON_CLOSE` and builds the UI. |
| `48–133` | `private void buildUI()` | Creates `root` (`BorderLayout`), `header` (title + subtitle banner), and `card` (`GridBagLayout`) with username/password fields, Enter-key submission (`txtPassword.addActionListener`), and `Log In` / `Register Student` buttons. |
| `135–182` | `private void login()` | Validates non-empty credentials, queries `public.users` with `PreparedStatement`, creates `UserSession` if `rs.next()` succeeds, disposes `LoginView`, and opens `MainWorkspaceView`. |
| `184–245` | `private void openStudentRegistrationDialog()` | Displays a 3×2 `JOptionPane` registration dialog, validates inputs, inserts a `'STUDENT'` user into `public.users`, and pre-fills `txtUsername`. |
| `247–249` | `public void show()` | Calls `frame.setVisible(true)` to display the login screen. |

---

### Module 6: `MainWorkspaceView.java`
| Lines | Code / Block | Explanation |
| :--- | :--- | :--- |
| `1–31` | Imports | Imports AWT layouts/styles, JDBC interfaces, Swing widgets, and `JTableHeader`. |
| `33–62` | Constants & instance fields | Sets frame dimensions (`940x560`), UI color constants, `dbConfig`, `session`, input fields, and table/model references (`mainTableModel`, `reservationsTable`, `searchTableModel`, `searchResultsTable`). |
| `64–75` | `public MainWorkspaceView(DatabaseConfig dbConfig, UserSession session)` | Initializes the main `JFrame`, builds UI panels, enforces role rules, and loads existing reservations. |
| `77–86` | `private void buildUI()` | Places `createHeaderPanel()` at `NORTH`, `createSidebarPanel()` at `WEST`, and `createTabbedWorkspace()` at `CENTER` inside `root`. |
| `88–117` | `private JPanel createHeaderPanel()` | Builds the top banner with application title, subtitle, and a role badge colored `AMBER_RED` for admins or `CARD_WHITE` for students. |
| `119–171` | `private JPanel createSidebarPanel()` | Builds a vertical `BoxLayout` sidebar with `Reservations` and `Search` navigation buttons, `adminSectionPanel` (`Edit Selected`, `Cancel Selected`), `Box.createVerticalGlue()`, and `Log Out`. |
| `173–187` | `private JButton createSidebarButton(String text)` | Helper factory applying uniform font, background, cursor, sizing, and border styling to sidebar buttons. |
| `189–195` | `private void applySessionRules()` | Shows `adminSectionPanel` only when `session.isAdmin()` is `true`; for students, fills `txtStudentName` with `session.getFullName()` and sets `setEditable(false)`. |
| `197–200` | `private void logout()` | Closes `frame` and opens a fresh `LoginView`. |
| `202–222` | `createTabbedWorkspace()` & `createReservationsTab()` | Configures `JTabbedPane` with two tabs; the first tab pairs `createReserveFormCard()` (`NORTH`) with `createTableCard("Current Reservations", true)` (`CENTER`). |
| `224–285` | `private JPanel createSearchTab()` | Builds the search bar (`txtSearchKeyword`, `Search` button, `Show All` button) above `createTableCard("Search Results", false)`. |
| `287–349` | `private JPanel createReserveFormCard()` | Builds the booking form row with `txtStudentName`, `txtSpaceName`, and `Reserve Space` button (`e -> reserveSpace()`). |
| `351–402` | `private JPanel createTableCard(String headingText, boolean isMainTable)` | Creates a `ReadOnlyTableModel`, configures `JTable` with `SINGLE_SELECTION`, `ReservationRowRenderer`, and `ReservationHeaderRenderer`, wraps it in a `JScrollPane`, and stores references for either the main or search table. |
| `404–443` | `private void reserveSpace()` | Validates student and space names, constructs `Reservation`, executes `INSERT INTO public.reservations`, resets fields, calls `loadMainReservations()`, and shows a confirmation dialog. |
| `445–482` | `private int populateTable(DefaultTableModel targetModel, String keyword)` | Clears `targetModel`, executes a filtered (`ILIKE`) or unfiltered `SELECT` ordered by `id ASC`, maps `ResultSet` rows into `Reservation` objects, adds them to the table model, and returns row count. |
| `484–507` | `loadMainReservations()` & `runSearch()` | Populates the main table or search table and displays an info dialog if a search returns `0` matches. |
| `509–524` | `private Object[] getSelectedRowData()` | Checks which tab is active (`0` or `1`) and returns `{id, studentName, spaceName}` for the selected row, or `null` if no row is selected. |
| `526–592` | `private void editSelectedReservation()` | Admin-only action that opens a pre-filled edit dialog for the selected row, validates inputs, executes `UPDATE public.reservations ... WHERE id = ?`, and refreshes active tables. |
| `594–645` | `private void cancelSelectedReservation()` | Admin-only action that confirms deletion, executes `DELETE FROM public.reservations WHERE id = ?`, and refreshes active tables. |
| `647–658` | `showError(...)` & `show()` | Displays formatted `SQLException` dialogs and reveals the main window. |
| `660–702` | `ReadOnlyTableModel`, `ReservationRowRenderer`, `ReservationHeaderRenderer` | Static nested helper classes that disable direct cell editing, style selected/unselected rows, and style the blue table header. |