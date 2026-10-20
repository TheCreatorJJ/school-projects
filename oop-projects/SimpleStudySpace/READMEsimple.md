
----
# 📚 Study Space System
A simple Java reservation manager using JOptionPane dialog boxes.

📖 Overview
The Study Space System is a Java application that manages study space reservations through dialog boxes. It supports up to 10 reservations stored in an array.

Features:

### Reserve a space

### View all reservations

### Search for a reservation

### Edit a reservation

### Cancel a reservation

### Exit

---

🧠 Core Concepts

| Concept | Where It's Used | 
|---|---| 
| `Encapsulation`	| Reservation class (private fields + getters/setters) |
| `Arrays` | 	Reservation[] reservations = new Reservation[10] |
| `Loops` |	do-while (menu), for (search, view, shift) |
| `Conditionals` |	if, else if, else |
| `Input Validation` |	Null, empty, non-numeric checks |
| `JOptionPane `|	All input and output dialogs |
| `String Methods`|	.trim(), .isEmpty(), .equalsIgnoreCase() |

---

### 🏗️ Code Structure
1. Import
java
import javax.swing.JOptionPane;
Brings in JOptionPane for all dialog boxes.

2. The Reservation Class
java
class Reservation {
    private String studentName;
    private String spaceName;
Private fields → cannot be accessed directly from outside.

This is encapsulation — protecting data.

Constructor
java
public Reservation(String studentName, String spaceName) {
    this.studentName = studentName;
    this.spaceName = spaceName;
}
this.studentName refers to the class field.

The parameter is the local variable.

Initializes a new reservation object.

Getters
java
public String getStudentName() { return studentName; }
public String getSpaceName() { return spaceName; }
Allow reading private fields from outside the class.

Setters
java
public void setStudentName(String studentName) {
    this.studentName = studentName;
}
public void setSpaceName(String spaceName) {
    this.spaceName = spaceName;
}
Allow modifying private fields from outside the class. Used in Edit Reservation.

3. Main Class & Main Method
java
public class SimpleStudySpaceSystem {
    public static void main(String[] args) {
public → accessible from anywhere.

static → no object needed to run.

void → returns nothing.

String[] args → command-line arguments (not used here).

Array & Variables
java
Reservation[] reservations = new Reservation[10];
int count = 0;
int choice = 0;
reservations → array of 10 Reservation objects (initially null).

count → tracks how many reservations exist.

choice → stores the user's menu selection.

4. The Menu Loop
java
do {
    String menu = "STUDY SPACE SYSTEM\n\n"
                + "1. Reserve Space\n"
                ...
                + "Enter Choice (1-6):";

    String input = JOptionPane.showInputDialog(null, menu, "Study Space System", JOptionPane.PLAIN_MESSAGE);

    if (input == null) {
        break;
    }
do-while → always shows the menu at least once.

JOptionPane.showInputDialog → shows input box with the menu.

if (input == null) break; → if user clicks Cancel/X, exit loop.

Numeric Input Validation
java
boolean isNumber = true;
if (input.trim().isEmpty()) {
    isNumber = false;
} else {
    for (int i = 0; i < input.length(); i++) {
        char c = input.charAt(i);
        if (c < '0' || c > '9') {
            isNumber = false;
            break;
        }
    }
}

if (!isNumber) {
    JOptionPane.showMessageDialog(null, "Invalid choice! Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
    continue;
}

choice = Integer.parseInt(input);
Checks if input is empty or contains non-digit characters.

If invalid → show error and continue (restart loop).

If valid → convert to integer with Integer.parseInt().

5. Option 1 — Reserve Space
java
if (choice == 1) {
    if (count < 10) {
        String name = JOptionPane.showInputDialog(null, "Enter Student Name:");
        String space = JOptionPane.showInputDialog(null, "Enter Study Space Name:");

        if (name == null || space == null) {
            // user cancelled
        } else if (name.trim().isEmpty() || space.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Input cannot be empty!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            reservations[count] = new Reservation(name.trim(), space.trim());
            count++;
            JOptionPane.showMessageDialog(null, "Reservation Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(null, "System Full! Cannot add more reservations.", "Full Space", JOptionPane.WARNING_MESSAGE);
    }
}
Step-by-step:

Check if array has space (count < 10).

Ask for student name and space name.

If user cancels → do nothing.

If input is empty/whitespace → show warning.

Otherwise → create new Reservation, store at reservations[count], increment count.

If array is full → show warning.

6. Option 2 — View Reservations
java
else if (choice == 2) {
    if (count == 0) {
        JOptionPane.showMessageDialog(null, "No reservations found.", "Reservations", JOptionPane.INFORMATION_MESSAGE);
    } else {
        String output = "--- CURRENT RESERVATIONS ---\n\n";
        for (int i = 0; i < count; i++) {
            output = output + (i + 1) + ". "
                   + reservations[i].getStudentName() + " - "
                   + reservations[i].getSpaceName() + "\n";
        }
        JOptionPane.showMessageDialog(null, output, "View Reservations", JOptionPane.PLAIN_MESSAGE);
    }
}
If count == 0 → no reservations.

Otherwise → loop through array and build a string.

getStudentName() and getSpaceName() → access private fields.

Display all reservations in one dialog.

7. Option 3 — Search Reservation
java
else if (choice == 3) {
    if (count == 0) {
        JOptionPane.showMessageDialog(null, "No reservations to search.", "Search", JOptionPane.INFORMATION_MESSAGE);
    } else {
        String searchName = JOptionPane.showInputDialog(null, "Enter Student Name to Search:");
        if (searchName != null && !searchName.trim().isEmpty()) {
            boolean found = false;
            String result = "-- Search Result --\n\n";

            for (int i = 0; i < count; i++) {
                if (reservations[i].getStudentName().equalsIgnoreCase(searchName.trim())) {
                    result = result + "Index " + (i + 1) + ": "
                           + reservations[i].getStudentName() + " - "
                           + reservations[i].getSpaceName() + "\n";
                    found = true;
                }
            }

            if (found) {
                JOptionPane.showMessageDialog(null, result, "Search Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Student not found.", "Search Result", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
Asks for a student name.

Loops through all reservations.

Uses .equalsIgnoreCase() → case-insensitive comparison.

If found → show all matches.

If not → show warning.

8. Option 4 — Edit Reservation
java
else if (choice == 4) {
    if (count == 0) {
        JOptionPane.showMessageDialog(null, "No reservation to edit.", "Edit Reservation", JOptionPane.INFORMATION_MESSAGE);
    } else {
        String searchName = JOptionPane.showInputDialog(null, "Enter Student Name to Edit:");
        int matchIndex = -1;

        if (searchName != null && !searchName.trim().isEmpty()) {
            for (int i = 0; i < count; i++) {
                if (reservations[i].getStudentName().equalsIgnoreCase(searchName.trim())) {
                    matchIndex = i;
                    break;
                }
            }
        }

        if (matchIndex != -1) {
            String newName  = JOptionPane.showInputDialog(null, "Enter New Student Name:", reservations[matchIndex].getStudentName());
            String newSpace = JOptionPane.showInputDialog(null, "Enter New Space Name:", reservations[matchIndex].getSpaceName());

            if (newName != null && newSpace != null && !newName.trim().isEmpty() && !newSpace.trim().isEmpty()) {
                reservations[matchIndex].setStudentName(newName.trim());
                reservations[matchIndex].setSpaceName(newSpace.trim());
                JOptionPane.showMessageDialog(null, "Reservation Updated Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Input cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Student not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
Step-by-step:

Check if there are reservations.

Ask for student name to edit.

Find the index (matchIndex) of the matching reservation.

If found → ask for new name and new space (pre-filled with old values).

Validate input.

Use setters to update the reservation.

If not found → show error.

9. Option 5 — Cancel Reservation
java
else if (choice == 5) {
    if (count == 0) {
        JOptionPane.showMessageDialog(null, "No reservation to cancel.", "Cancel Reservation", JOptionPane.INFORMATION_MESSAGE);
    } else {
        String cancelName = JOptionPane.showInputDialog(null, "Enter Student Name to Cancel:");
        int matchIndex = -1;

        if (cancelName != null && !cancelName.trim().isEmpty()) {
            for (int i = 0; i < count; i++) {
                if (reservations[i].getStudentName().equalsIgnoreCase(cancelName.trim())) {
                    matchIndex = i;
                    break;
                }
            }
        }

        if (matchIndex != -1) {
            for (int i = matchIndex; i < count - 1; i++) {
                reservations[i] = reservations[i + 1];
            }
            reservations[count - 1] = null;
            count--;
            JOptionPane.showMessageDialog(null, "Reservation Canceled Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Student not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
Step-by-step:

Check if there are reservations.

Ask for student name to cancel.

Find the index of the matching reservation.

If found:

Shift elements left → reservations[i] = reservations[i + 1]

Set last element to null

Decrement count

If not found → show error.

Why shift? Arrays have fixed size. To "remove" an element, we move everything after it one step to the left, then nullify the last slot.

10. Option 6 — Exit
java
else if (choice == 6) {
    break;
}
Breaks out of the do-while loop.

Invalid Option
java
else {
    JOptionPane.showMessageDialog(null, "Invalid Option! Please choose 1-6 only.", "Error", JOptionPane.ERROR_MESSAGE);
}
Handles numbers outside 1–6.

Loop Condition
java
} while (choice != 6);
Keeps looping until user chooses 6.

Goodbye Message
java
JOptionPane.showMessageDialog(null, "Thank you for using Study Space System!", "Goodbye", JOptionPane.INFORMATION_MESSAGE);
Shown after exiting the loop.

🔑 Key Methods
Method	Purpose
JOptionPane.showInputDialog(...)	Get user input
JOptionPane.showMessageDialog(...)	Show message
.trim()	Remove leading/trailing spaces
.isEmpty()	Check if string is empty
.equalsIgnoreCase()	Compare strings ignoring case
Integer.parseInt()	Convert String to int
input.charAt(i)	Get character at index i
input.length()	Get length of string
🧪 Example Flow
User runs program → menu appears.

---
Chooses 1 → enters "Alice" and "Room A" → reservation saved.

Chooses 2 → sees "1. Alice - Room A".

Chooses 3 → searches "alice" → finds it.

Chooses 4 → edits "Alice" to "Bob" and "Room B".

Chooses 5 → cancels "Bob" → reservation removed.

Chooses 6 → exits with goodbye message.

---

⚠️ Limitations
Maximum 10 reservations only.

No file saving (data lost when program closes).

Search/Edit/Cancel only matches the first found student.

Case-insensitive search may match unexpected names.


📌 Quick Reference
Reservation class → blueprint for a reservation.

Array → storage for up to 10 reservations.

count → how many are currently stored.

choice → which menu option was selected.

matchIndex → position of found reservation.

found → boolean flag for search.

isNumber → boolean flag for numeric validation.


--- 
this markdown file is created by benj by hand.
---