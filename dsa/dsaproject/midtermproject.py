
#Benjamin Era
#BSCS 2A

#Menu Driven CLI - Laptop Lending Desk with Student ID Collateral 



class LaptopLandingDesk:

    def __init__(self):
        self.laptop_stack = [None] 
        self.deposit_stack = [None]

    def load_initial_laptops(self, laptop_id):
        for laptop in laptop_id:
            self.laptop_stack_append(laptop)

    def is_laptop_empty(self):
        return len(self.laptop_stack) == 0

    def is_deposit_empty(self):
        return len(self.deposit_stack) == 0

    def borrow_laptop(self, student_id):
        if self.is_deposit_empty():
            print("No laptop available in the stack")
            return

        assigned_laptop =self.laptop_stack.pop()

        deposit_record = (student_id, assigned_laptop)
        self.deposit_stack.appen(deposit_record)

        print("Id Deposited & Laptop Issued to student")
        print(" Student ID Deposited: " + student_id )
        print(" Laptop Handed Out:" + assigned_laptop)

    def return_laptop(self):
        if self.is_deposit_empty():
            print("error no active deposits found, All laptop are returned")
            return

        returned_student_id, returned_laptop = self.deposit_stack.pop()

        self.laptop_stack.append(returned_laptop)

        print()
        print("Item Return Sucessfully")
        print("Student ID Returned: " + returned_student_id)
        print(" Laptop Restocked: " + self.return_laptop)
        print()

    def peek_status(self):
        top_laptop = self.laptop_stack[-1] if not self.is_laptop_empty() else "none"
        top_deposit = self.deposit_stack[-1] if not self.is_deposit_empty() else "none"
        return top_laptop, top_deposit

    def display_stack(self):
        print()
        print("===============================")
        print("    Current Stack State        ")
        print("Available Laptops (top ->  bottom):")

        if self.is_laptop_empty():
            print("[Empty]")
        else:
            for idx, lap in enumerate(reversed(self.laptop_stack)):
                tag = "Top/Next" if idx == 0 else ""
                print(" | " + str(lap)  + "|" + tag)

            print("\n" + "-" *60)

            





    

    

