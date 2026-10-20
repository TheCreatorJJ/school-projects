# Campus Laptop Lending Desk — Stack Manager

A simple Python program that simulates a laptop lending desk at a campus,
using **two stacks** to track available laptops and who borrowed them.

Built for a Data Structures & Algorithms (DSA) demonstration to show how
**Stacks (LIFO — Last In, First Out)** work in a real-world scenario.

---

## The Idea

Imagine a campus desk with a pile of laptops. Students borrow one, use it,
then return it. The desk needs to track:

- Which laptops are available
- Which student borrowed which laptop

The catch: **the most recently returned laptop is the next one handed out.**
That's exactly what a stack does — Last In, First Out.

---

## Two Stacks, One Desk

| Stack | Holds | Purpose |
|---|---|---|
| `laptop_stack` | Laptop IDs (e.g. `LAPTOP-101`) | Laptops available to borrow |
| `deposit_stack` | Tuples `(student_id, laptop_id)` | Which student has which laptop |

The "top" of each stack is what's most recently added.

---

## How the Operations Work

### 1. Borrow Laptop
- **POP** the top laptop from `laptop_stack`
- **PUSH** a new tuple `(student_id, laptop_id)` onto `deposit_stack`

> Student takes the top laptop. The desk records who has it.

### 2. Return Laptop
- **POP** the top tuple from `deposit_stack`
- **PUSH** the laptop back onto `laptop_stack`

> The most recent borrower returns their laptop. It goes back on top.

### 3. Inspect Stacks
- Views both stacks from top to bottom
- Shows everything currently in the desk, in LIFO order

### 4. Exit
- Ends the program

---

## Why Stacks? (The DSA Point)

A stack is a **LIFO** structure. Two operations:

| Operation | Meaning |
|---|---|
| `push` | Add to the top |
| `pop`  | Remove from the top |

In this program:
- Laptops form a stack — the next one handed out is always the last one returned
- Deposits form a stack — the most recent borrower is at the top

This mirrors how physical desks actually work: you take the top item,
and you put returned items back on top.

---

## Project Structure
dsa/
├── ascii_art.py # ASCII banner art (shown at the top of the screen)
├── stack.py # The main program — Stack class + menu loop
└── README.md # This file



### File Roles

- **`ascii_art.py`** — just holds one string: the ASCII art shown as the header.
- **`stack.py`** — everything else: the `stack` class, the menu, the loop.

---

## The `stack` Class

```python
class stack:
    def __init__(self):
        self.laptop_stack  = []   # available laptops
        self.deposit_stack = []   # (student_id, laptop_id) tuples


### Methods

Method	                                        What it does
load_initial_inventory(laptop_ids)	|   Pre-loads laptops onto the desk
is_laptops_empty()	                |   Returns True if no laptops are available
is_deposits_empty()                 |	Returns True if no active deposits
borrow_laptop(student_id)	        |   Pops a laptop, pushes a deposit record
return_laptop()	                    |   Pops a deposit record, pushes the laptop back
peek_status()	                    |   Returns the top laptop and top deposit (no removal)
display_stacks()	                |   Prints both stacks top-to-bottom

### Key Concepts to Remember for the Demo

Stack = LIFO. Last In, First Out.

Push = add to top. Pop = remove from top.

The top of the laptop stack = next laptop to be borrowed.

The top of the deposit stack = most recent borrower.

borrow_laptop does pop then push (pop laptop, push deposit).

return_laptop does pop then push (pop deposit, push laptop).

The stacks are just Python lists — append() is push, pop() is pop.

