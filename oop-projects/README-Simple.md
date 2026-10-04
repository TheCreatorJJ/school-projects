# Simple Study Space System (Non-OOP Version)

A Java Swing (JOptionPane) application for managing study room reservations.
Built to demonstrate **procedural programming** and **parallel array** data storage.

## Features

- Reserve a study space (up to 10 active reservations)
- View all current reservations
- Search for a reservation by student name
- Edit an existing reservation (name and space)
- Cancel a reservation (with array-shift deletion)
- Menu-driven GUI using `JOptionPane` pop-ups

---

## Overview

This program lets a user:

- Reserve a study space for a student
- View all current reservations
- Search for a reservation by student name
- Edit an existing reservation
- Cancel a reservation

All interaction happens through **pop-up dialog boxes** (`JOptionPane`), so there is no console typing — every prompt, input, and message appears as a small window.

The program can hold at most **10 reservations** at a time.

---

## Project Structure

Unlike the OOP version, this program is contained entirely inside **one single class**:

- **`SimpleStudySpaceSystem`**: Main class — contains the menu loop, data arrays, and all operations.

There are no custom objects, constructors, or getters/setters. All data is stored directly in parallel arrays inside the `main` method.

---

## How the Program Works

### Program Flow

When you run the program, this is what happens step by step:

1. **`main()` starts.**
2. Two arrays of 10 empty string slots are created (`studentNames` and `spaceNames`).
3. A `count` variable is set to `0` — it tracks how many slots are actually filled.
4. A `do-while` loop begins and keeps running until the user chooses **6 (Exit)**.
5. Inside the loop, a **menu pop-up** asks the user to pick an option from 1 to 6.
6. The user's input is validated, then the matching `if/else if` branch runs.
7. After each option finishes, the loop returns to the menu.
8. When the user picks **6** (or clicks Cancel/Close), the loop ends and a goodbye message appears.

### Data Storage

Reservations are stored in **two fixed-size parallel arrays**:

```java
String[] studentNames = new String[10];
String[] spaceNames = new String[10];
int count = 0;
studentNames[0] and spaceNames[0] belong to the same reservation.

The arrays have 10 slots, but empty slots are null.

count tells the program how many slots contain real data.

Every loop uses i < count, never i < studentNames.length, so null slots are never accessed.

Deleting a reservation uses a shift-left algorithm on both arrays so there are no gaps:

text
Before delete index 1:
studentNames: [Alice, Bob, Charlie, null, ...]
spaceNames:   [RoomA, RoomB, RoomC,   null, ...]

After delete index 1:
studentNames: [Alice, Charlie, null, null, ...]
spaceNames:   [RoomA, RoomC,   null, null, ...]
Steps:

Copy each element one slot to the left, starting from the deleted index.

Null out the last used slot in both arrays.

Decrement count.

Menu Loop & Input Validation
The menu is built as a single string and shown inside a JOptionPane.showInputDialog. The user types a number.

Validation happens in three checks:

Cancel/Close detection — if the user closes the dialog, input is null, and the program exits cleanly.

Empty check — input.trim().isEmpty() catches blank input.

Digit check — every character is compared against the ASCII range '0'–'9'. If any character isn't a digit, the input is rejected with an error message.

If the input passes all three checks, it's converted with Integer.parseInt() and used as the menu choice.

Each Menu Option Explained
Option 1 — Reserve Space
Checks that count < 10 (there's still room).

Prompts for Student Name and Study Space Name.

Validates both fields are non-null and non-empty.

Stores the name at studentNames[count] and the space at spaceNames[count].

Increments count.

If the array is full, it shows a "System Full!" warning.

Option 2 — View Reservations
If count == 0, shows "No reservations found."

Otherwise builds a formatted string listing each reservation's:

Index (starting at 1)

Student name (studentNames[i])

Space name (spaceNames[i])

Option 3 — Search Reservation
Prompts for a student name.

Loops through studentNames[0] to studentNames[count - 1].

Uses equalsIgnoreCase to compare, so "alice" matches "Alice".

Accumulates results in a String result.

Uses a found flag: if no match, shows a "not found" message.

Option 4 — Edit Reservation
Prompts for the student name to edit.

Finds the first matching index (matchIndex = -1 means "not found").

If found, shows two showInputDialog boxes pre-filled with the current values (the third argument acts as the default text).

If both new values are valid, overwrites the array elements directly:

java
studentNames[matchIndex] = newName.trim();
spaceNames[matchIndex] = newSpace.trim();
Option 5 — Cancel Reservation
Prompts for the student name to cancel.

Finds the matching index.

If found, runs the shift-left deletion:

java
for (int i = matchIndex; i < count - 1; i++) {
    studentNames[i] = studentNames[i + 1];
    spaceNames[i] = spaceNames[i + 1];
}
studentNames[count - 1] = null;
spaceNames[count - 1] = null;
count--;
Shows a success message.

Option 6 — Exit
Ends the loop and shows a goodbye message.

Any Other Number
Shows an "Invalid Option! Please choose 1-6 only." error and returns to the menu.

Programming Concepts Demonstrated
Procedural Programming
This version uses a step-by-step, linear approach inside a single method. Logic is written directly inside if/else blocks rather than being split into separate methods or objects.

Parallel Arrays
Instead of creating a Reservation object, related data is stored in two separate arrays at the same index. This is a classic introductory programming technique for grouping related data without objects.

Input Validation
A manual loop checks each character of the input string using ASCII values ('0' to '9') to ensure the user typed a valid number.

Array Manipulation
The shift-left algorithm is a standard way to delete an item from an array while keeping the remaining items contiguous.

OOP vs. Non-OOP: Key Differences
Data Storage

OOP Version: Array of Reservation objects.

Simple Version: Two parallel String arrays (studentNames and spaceNames).

Encapsulation

OOP Version: Private fields accessed via getters and setters.

Simple Version: Direct access via array indices.

Structure

OOP Version: 4 classes (demonstrates Inheritance + Polymorphism).

Simple Version: 1 class (Procedural programming).

Sorting Feature

OOP Version: Yes (Bubble Sort on objects).

Simple Version: No.

Edit Logic

OOP Version: Calls setters on a Reservation object.

Simple Version: Overwrites array elements directly.

Cancel Logic

OOP Version: Shifts 1 object array to fill the gap.

Simple Version: Shifts 2 string arrays simultaneously to fill the gap.

Key Variables Reference
studentNames (String[]): Fixed array of 10 student names.

spaceNames (String[]): Fixed array of 10 study space names.

count (int): Number of filled slots — the "true size" of the list.

choice (int): The user's current menu selection.

input (String): Raw text from the menu input box.

isNumber (boolean): Tracks whether input contains only digits.

matchIndex (int): Index of a found reservation; -1 if not found.

found (boolean): Used in search to know whether anything matched.

Known Limitations
Maximum of 10 reservations (fixed arrays).

Data is not saved — everything is lost when the program closes.

No prevention of duplicate student names.

No prevention of duplicate space names.

Menu can only be navigated with keyboard input — no mouse-click menu buttons.

No sorting feature (unlike the OOP version).

Adding more features (like different room types) would require rewriting large chunks of code because there are no objects.

Possible Enhancements
Replace the fixed arrays with ArrayList<String> for unlimited capacity.

Save and load reservations from a file (.txt or .csv).

Replace stacked pop-ups with a single JFrame window using JTable.

Prevent duplicate student names.

Add a sorting feature (e.g., Bubble Sort on studentNames).

Refactor into the OOP version to make the code more scalable and organized.

Authors
benj — Simple Study Space System