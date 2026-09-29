#Benjamin Era
#BSCS 2A


class BrowserStack:
    "A fixed-capacity LIFO stack simulating browser back-button history."

    def __init__(self, capacity): #internal state
        self.capacity = capacity
        # Pre-allocate fixed-size storage (acts like a raw array)
        self.storage = [None] * capacity # None = empty slot. we cant add append()
        self.top = -1  #stock is envy

    #Core Operations
    def visit_page(self, url): #push function
        "Push equivalent: visiting a new page."
        if self.top == self.capacity - 1:
            print(f"[Page Visit] Attempted to open '{url}'")
            print("Error: Stack Overflow! History limit reached "
                  f"(Capacity: {self.capacity}). Close a tab or clear history first.")
            return

        self.top += 1
        self.storage[self.top] = url
        print(f"[Page Visit] Opened '{url}'")
        self._show_state()

    def go_back(self): #pop function like going back in the web
        
        if self.top == -1:
            print("[Go Back]")
            print("Error: Stack Underflow! No previous page to go back to.")
            return None

        page = self.storage[self.top]
        self.storage[self.top] = None  # clear the slot manually
        self.top -= 1
        print(f"[Go Back] Left '{page}'")
        self._show_state()
        return page

    def current_page(self):
        """Peek equivalent: see the page currently displayed, no change."""
        if self.top == -1:
            print("[Current Page Check] No page is currently open.")
            return None

        page = self.storage[self.top]
        print(f"[Current Page Check] You are viewing: '{page}'")
        return page

    def is_empty(self):
        """isEmpty check."""
        empty = self.top == -1
        print(f"[Empty Check] Is history empty? {empty}")
        return empty

    #Helper

    def _show_state(self):
        active = [item for item in self.storage if item is not None]
        current = self.storage[self.top] if self.top != -1 else None
        print(f"Stack: {active} | Current Page: {current}")



# SIMULATION my experience in MUN DEBATE

if __name__ == "__main__":
    print("=== Scenario: Browser Navigation Back-Button Stack (Capacity: 4) ===\n")

    history = BrowserStack(capacity=4)

    # 1. Push - visit first page
    history.visit_page("google.com/search?q=MUN+resolution+topics")
    print()

    # 2. Push - visit second page
    history.visit_page("un.org/resolutions/sample-format")
    print()

    # 3. Push - visit third page
    history.visit_page("canva.com/design/conference-poster")
    print()

    # 4. Peek - check current page without modifying the stack
    history.current_page()
    print()

    # 5. Pop - click Back once
    history.go_back()
    print()

    # 6. isEmpty check while items remain
    history.is_empty()
    print()

    # 7. Push two more pages to test near-overflow behavior
    history.visit_page("gmail.com/inbox")
    print()
    history.visit_page("docs.google.com/spreadsheet/outreach-list")
    print()

    # 8. Boundary test - trigger Stack Overflow (capacity is 4, already holds 4)
    history.visit_page("drive.google.com/folder/certificates")
    print()

    # 9. Pop everything to empty the stack
    history.go_back()
    history.go_back()
    history.go_back()
    history.go_back()
    print()

    # 10. Boundary test - trigger Stack Underflow
    history.go_back()
    print()

    # 11. Final isEmpty confirmation
    history.is_empty()