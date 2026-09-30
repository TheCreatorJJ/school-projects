import os
import sys
from ascii_art import ASCII_ART


class stack:
    def __init__(self):
        # Stack 1 - Available Laptops
        self.laptop_stack = []
        # Stack 2 - Student ID deposits stored as tuples: (student_id, laptop_id)
        self.deposit_stack = []

    def load_initial_inventory(self, laptop_ids):
        """Pre-load laptops onto the desk stack"""
        for laptop in laptop_ids:
            self.laptop_stack.append(laptop)

    def is_laptops_empty(self):
        return len(self.laptop_stack) == 0

    def is_deposits_empty(self):
        return len(self.deposit_stack) == 0

    def borrow_laptop(self, student_id):
        """
                             BORROW OPERATION: POP top Laptop from laptop_stack
                             PUSH student ID & laptop pair onto deposit_stack """

        if self.is_laptops_empty():
            print("\n[ERROR] No laptops available in the stack!")
            return

        # POP laptop from top of stack
        assigned_laptop = self.laptop_stack.pop()

        # PUSH deposit record onto top of deposit stack
        deposit_record = (student_id, assigned_laptop)
        self.deposit_stack.append(deposit_record)

        print("\n[SUCCESS] ID Deposited & Laptop Issued!")
        print("Student ID Deposited : " + student_id)
        print("Laptop Handed Out   : " + assigned_laptop)

    def return_laptop(self):
        """
                        RETURN OPERATION: POP top deposit record from deposit_stack
                        - PUSH laptop back onto laptop_stack  """

        if self.is_deposits_empty():
            print("\n[ERROR] No active deposits found! All laptops are returned.")
            return

        # POP deposit record off top of stack
        returned_student_id, returned_laptop = self.deposit_stack.pop()

        # PUSH laptop back onto top of laptop stack
        self.laptop_stack.append(returned_laptop)

        print("\n[RETURN PROCESSED] Item Returned Successfully!")
        print("Student ID Returned : " + returned_student_id)
        print("Laptop Restocked   : " + returned_laptop)

    def peek_status(self):
        top_laptop = self.laptop_stack[-1] if not self.is_laptops_empty() else "None (Empty)"
        top_deposit = self.deposit_stack[-1] if not self.is_deposits_empty() else "None (Empty)"
        return top_laptop, top_deposit

    def display_stacks(self):
        print()
        print("=" * 60)
        print("                CURRENT STACK STATES")
        print("=" * 60)

        # Laptop Stack View (Top to Bottom)
        print("Available Laptops Stack (Top -> Bottom):")
        if self.is_laptops_empty():
            print("  [ Empty ]")
        else:
            for idx, lap in enumerate(reversed(self.laptop_stack)):
                tag = " (TOP / NEXT)" if idx == 0 else ""
                print("  | " + str(lap) + " |" + tag)

        print("-" * 60)

        # Deposit Stack View (Top to Bottom)
        print("Deposited IDs Stack (Top -> Bottom):")
        if self.is_deposits_empty():
            print("  [ Empty ]")
        else:
            for idx, (s_id, lap) in enumerate(reversed(self.deposit_stack)):
                tag = " (TOP / MOST RECENT)" if idx == 0 else ""
                print("  | Student ID: " + str(s_id) + " <-> Laptop: " + str(lap) + " |" + tag)

        print("=" * 60)


def clear_screen():
    os.system("cls" if os.name == "nt" else "clear")


def run_program():
    desk = stack()

    # Pre-populate desk with 3 charged laptops
    desk.load_initial_inventory(["LAPTOP-101", "LAPTOP-102", "LAPTOP-103"])

    running = True
    while running:
        clear_screen()

        # ---- ASCII ART HEADER ----
        print(ASCII_ART)
        print("=" * 60)

        top_lap, top_dep = desk.peek_status()

        print(" Next Laptop Ready to Borrow (PEEK) : " + str(top_lap))
        print(" Top Active ID Deposit (PEEK)       : " + str(top_dep))
        print("-" * 60)
        print("  1. Borrow Laptop   (Deposit ID  -> PUSH ID Stack, POP Laptop Stack)")
        print("  2. Return Laptop   (Retrieve ID -> POP ID Stack,  PUSH Laptop Stack)")
        print("  3. Inspect Stacks  (View full stack contents in LIFO order)")
        print("  4. Exit Application")
        print("-" * 60)

        user_choice = input("Select an operation (1-4): ").strip()

        if user_choice == "1":
            student_id = input("\nEnter Student ID to deposit (e.g., 2025-0001): ").strip().upper()
            if student_id:
                desk.borrow_laptop(student_id)
            else:
                print("\n[ERROR] Student ID cannot be empty.")
            input("\nPress Enter to continue...")

        elif user_choice == "2":
            desk.return_laptop()
            input("\nPress Enter to continue...")

        elif user_choice == "3":
            desk.display_stacks()
            input("\nPress Enter to continue...")

        elif user_choice == "4":
            print("\nExiting Laptop Lending Desk program. Thank you!\n")
            running = False

        else:
            print("\n[INVALID INPUT] Please enter a number from 1 to 4.")
            input("\nPress Enter to continue...")


if __name__ == "__main__":
    run_program()