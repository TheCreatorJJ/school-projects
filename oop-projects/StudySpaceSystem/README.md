# Study Space System

A Java Swing (JOptionPane) application for managing study room reservations.
Built to demonstrate OOP principles: **abstraction, encapsulation, inheritance, and polymorphism**.

## Features

- Reserve a study space (up to 10 active reservations)
- View all current reservations
- Search for a reservation by student name
- Edit an existing reservation (name and space)
- Cancel a reservation (with array-shift deletion)
- Sort reservations alphabetically by student name (Bubble Sort)
- Menu-driven GUI using `JOptionPane` pop-ups

# Study Space System

A Java Swing (JOptionPane) application for managing study room reservations.
Built to demonstrate core OOP principles: **abstraction, encapsulation, inheritance, and polymorphism**.

---



## Overview

This program lets a user:

- Reserve a study space for a student
- View all current reservations
- Search for a reservation by student name
- Edit an existing reservation
- Cancel a reservation
- Sort reservations alphabetically by student name

All interaction happens through **pop-up dialog boxes** (`JOptionPane`), so there is no console typing — every prompt, input, and message appears as a small window.

The program can hold at most **10 reservations** at a time.

---

## Project Structure

The program contains four classes inside one file:

| Class | Role |
|---|---|
| `StudySpace` | Parent class — the general concept of a study area |
| `QuietZone` | Child class — a specific type of study area (silent zone) |
| `Reservation` | Holds a student name and a `StudySpace` object |
| `StudySpaceSystem` | Main class — contains the menu loop and all operations |

---

## How the Program Works

### Program Flow

When you run the program, this is what happens step by step:

1. **`main()` starts.**
2. An array of 10 empty `Reservation` slots is created.
3. A `count` variable is set to `0` — it tracks how many slots are actually filled.
4. A `do-while` loop begins and keeps running until the user chooses **7 (Exit)**.
5. Inside the loop, a **menu pop-up** asks the user to pick an option from 1 to 7.
6. The user's input is validated, then the matching `if/else if` branch runs.
7. After each option finishes, the loop returns to the menu.
8. When the user picks **7** (or clicks Cancel/Close), the loop ends and a goodbye message appears.

### Data Storage

Reservations are stored in a **fixed-size array**:

```java
Reservation[] reservations = new Reservation[10];
int count = 0;
```

- The array has 10 slots, but **empty slots are `null`**.
- `count` tells the program how many slots contain real data.
- Every loop uses `i < count`, **never** `i < reservations.length`, so null slots are never accessed.

Deleting a reservation uses a **shift-left** algorithm so the array has no gaps:

```
Before delete index 1: [A, B, C, D, null, ...]
After delete index 1:  [A, C, D, null, null, ...]
```

Steps:
1. Copy each element one slot to the left, starting from the deleted index.
2. Null out the last used slot.
3. Decrement `count`.

### Menu Loop & Input Validation

The menu is built as a single string and shown inside a `JOptionPane.showInputDialog`. The user types a number.

Validation happens in three checks:

1. **Cancel/Close detection** — if the user closes the dialog, `inputChoice` is `null`, and the program exits cleanly.
2. **Empty check** — `inputChoice.trim().isEmpty()` catches blank input.
3. **Digit check** — every character is compared against the ASCII range `'0'`–`'9'`. If any character isn't a digit, the input is rejected with an error message.

If the input passes all three checks, it's converted with `Integer.parseInt()` and used as the menu choice.

### Each Menu Option Explained

#### Option 1 — Reserve Space

1. Checks that `count < 10` (there's still room).
2. Prompts for **Student Name** and **Study Space Name**.
3. Validates both fields are non-null and non-empty.
4. Creates a `QuietZone` object with the space name.
5. Wraps it in a `Reservation` with the student name.
6. Stores it at `reservations[count]` and increments `count`.

If the array is full, it shows a "System Full!" warning.

#### Option 2 — View Reservations

- If `count == 0`, shows "No reservations found."
- Otherwise builds a formatted string listing each reservation's:
  - Index (starting at 1)
  - Student name
  - Space name (via `getSpace().getRoomName()`)
  - Space type (via `getSpace().getType()` — polymorphic call)

#### Option 3 — Search Reservation

- Prompts for a student name.
- Loops through `reservations[0]` to `reservations[count - 1]`.
- Uses `equalsIgnoreCase` to compare, so "alice" matches "Alice".
- Uses a `StringBuilder` to accumulate results.
- Uses a `found` flag: if no match, shows a "not found" message.

#### Option 4 — Edit Reservation

1. Prompts for the student name to edit.
2. Finds the first matching index (`matchIndex = -1` means "not found").
3. If found, shows two `showInputDialog` boxes pre-filled with the current values (the third argument acts as the default text).
4. If both new values are valid, calls the setters to update the reservation in place.
5. Note: `setSpace(new QuietZone(...))` replaces the space entirely with a new object.

#### Option 5 — Cancel Reservation

1. Prompts for the student name to cancel.
2. Finds the matching index.
3. If found, runs the **shift-left deletion**:
   ```java
   for (int i = matchIndex; i < count - 1; i++) {
       reservations[i] = reservations[i + 1];
   }
   reservations[count - 1] = null;
   count--;
   ```
4. Shows a success message.

#### Option 6 — Sort Names

Sorts the reservations alphabetically by student name using **Bubble Sort** (explained in detail below), then shows a success message.

#### Option 7 — Exit

Ends the loop and shows a goodbye message.

#### Any Other Number

Shows an "Invalid Option 1-7 only" error and returns to the menu.

---

## The Sorting Algorithm (Bubble Sort)

Bubble Sort works by repeatedly stepping through the list, comparing each pair of adjacent items, and swapping them if they're in the wrong order. After each full pass, the largest unsorted value "bubbles up" to its correct position at the end.

### The Code

```java
for (int i = 0; i < count - 1; i++) {
    for (int j = 0; j < count - i - 1; j++) {
        String name1 = reservations[j].getStudentName();
        String name2 = reservations[j + 1].getStudentName();

        if (name1.compareToIgnoreCase(name2) > 0) {
            Reservation temp    = reservations[j];
            reservations[j]     = reservations[j + 1];
            reservations[j + 1] = temp;
        }
    }
}
```

### How the Comparison Works

`a.compareToIgnoreCase(b)` returns:

| Result | Meaning |
|---|---|
| Negative number | `a` comes before `b` alphabetically |
| Zero | `a` and `b` are the same word (ignoring case) |
| Positive number | `a` comes after `b` alphabetically |

So `compareToIgnoreCase(...) > 0` means **"the left name is alphabetically bigger — swap them."**

### How the Swap Works

You can't simply write `a = b; b = a;` — the first line overwrites `a` and the original value is lost. The standard three-line pattern uses a temporary variable:

```java
Reservation temp    = reservations[j];       // save left
reservations[j]     = reservations[j + 1];   // move right to left
reservations[j + 1] = temp;                  // put saved left on right
```

### Why the Loops Look Like That

- **Inner loop** (`j`) compares adjacent pairs: index `j` and index `j + 1`.
- **Outer loop** (`i`) repeats the pass `count - 1` times.
- The `- i` in `j < count - i - 1` is an optimization: after pass `i`, the largest `i` elements are already in place at the end, so they don't need to be compared again.

### Step-by-Step Trace

Starting with `["Charlie", "Alice", "Bob"]`:

**Pass i = 0**
```
j = 0: Charlie vs Alice → swap → [Alice, Charlie, Bob]
j = 1: Charlie vs Bob   → swap → [Alice, Bob, Charlie]
```

**Pass i = 1**
```
j = 0: Alice vs Bob → no swap → [Alice, Bob, Charlie]
```

Sorted. ✅

### Performance

- Worst case: **O(n²)** comparisons.
- For the maximum of 10 reservations this is instant.
- For larger datasets, replace with `Arrays.sort(...)` and a custom comparator:
  ```java
  Arrays.sort(reservations, 0, count,
      (a, b) -> a.getStudentName().compareToIgnoreCase(b.getStudentName()));
  ```

---

## OOP Concepts Demonstrated

### Encapsulation

Private fields in `StudySpace` and `Reservation` are only accessible through public getters and setters. Outside code can't accidentally corrupt them.

```java
private String roomName;
public String getRoomName() { return roomName; }
```

### Inheritance

`QuietZone extends StudySpace`. The child automatically inherits `roomName`, `getRoomName()`, and the parent constructor's behavior.

```java
class QuietZone extends StudySpace {
    public QuietZone(String roomName) {
        super(roomName);  // call parent constructor
    }
}
```

### Polymorphism

`QuietZone` overrides `getType()`. When you call `getType()` on a `StudySpace` reference that actually points to a `QuietZone`, Java uses the child's version at runtime.

```java
StudySpace s = new QuietZone("Room A");
System.out.println(s.getType());  // "Quiet Zone (Silent Area)"
```

### Abstraction

`StudySpace` models the **general idea** of a study area (it has a name and a type). `QuietZone` provides the **concrete implementation**. This separation lets you add more room types later (e.g., `GroupRoom`, `ComputerLab`) without rewriting existing code.

### Composition

A `Reservation` **has-a** student name and **has-a** `StudySpace`. This is composition — building complex objects from simpler ones.

---

## Key Variables Reference

| Variable | Type | Purpose |
|---|---|---|
| `reservations` | `Reservation[]` | Fixed array of 10 reservation slots |
| `count` | `int` | Number of filled slots — the "true size" of the list |
| `choice` | `int` | The user's current menu selection |
| `inputChoice` | `String` | Raw text from the menu input box |
| `isNumber` | `boolean` | Tracks whether `inputChoice` contains only digits |
| `matchIndex` | `int` | Index of a found reservation; `-1` if not found |
| `found` | `boolean` | Used in search to know whether anything matched |
| `temp` | `Reservation` | Temporary holder used during the sort swap |

---

## Known Limitations

- Maximum of **10 reservations** (fixed array).
- Only `QuietZone` spaces can be created through the menu — no other room types are selectable.
- Data is **not saved** — everything is lost when the program closes.
- No prevention of **duplicate student names**.
- No prevention of **duplicate space names**.
- Menu can only be navigated with keyboard input — no mouse-click menu buttons.

---

## Possible Enhancements

- Replace the fixed array with `ArrayList<Reservation>` for unlimited capacity.
- Add more `StudySpace` subclasses (e.g., `GroupRoom`, `ComputerLab`) and let the user select a type when reserving.
- Save and load reservations from a file (`.txt` or `.csv`).
- Replace stacked pop-ups with a single `JFrame` window using `JTable`.
- Prevent duplicate student names, or allow multiple reservations per student with a unique ID.
- Add a "modify space type" option in Edit Reservation.
- Show reservation timestamps.

---

## Authors

benj — Study Space System