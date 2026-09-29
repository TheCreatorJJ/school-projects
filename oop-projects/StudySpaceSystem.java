import javax.swing.JOptionPane; //Import Statement

/**
 * Base Class representing a Study Space.
 * Demonstrates Abstraction & Encapsulation.
 */
class StudySpace { //Base or parent class for studyrooms
    private String roomName; //encasuplating roomName


    public StudySpace(String roomName) { //It accepts a room name when creating an object and assign to this.roomName
        this.roomName = roomName;
    }

    public String getRoomName() { //getter asking to get the roonName
        return roomName;
    }

    public String getType() {  //a method returning the space classification string
        return "General Study Area";
    }
}

/**
 * Subclass representing a Quiet Zone.
 * Demonstrates Inheritance & Polymorphism.
 */
class QuietZone extends StudySpace {  //extends the studySpace where StudySpace is the parent and QuietZone is the child
                                    //we get access to roomName and getRoomname

    public QuietZone(String roomName) { //Uses super(roomName) to invoke the parent constructor and set roomName.
        super(roomName);
    }

    public String getType() { //Demonstrates Polymorphism by overriding the getType() method to return a specific string ("Quiet Zone (Silent Area)") instead of the default base class string.
        return "Quiet Zone (Silent Area)";
    }
}

/**
 * Class representing a single Reservation entity.
 * Demonstrates Encapsulation.
 */
class Reservation {
    private String studentName;
    private StudySpace space;

    public Reservation(String studentName, StudySpace space) { //Constructor that initializes a new Reservation instance with a student name and a space object.
        this.studentName = studentName;
        this.space = space;
    }

    public String getStudentName() {
        return studentName;
    }

    public StudySpace getSpace() {
        return space;
    }

    public void setStudentName(String studentName) { //Setter methods allowing controlled modification of private values.
        this.studentName = studentName;
    }

    public void setSpace(StudySpace space) {
        this.space = space;
    }
}

/**
 * Main Class with GUI Pop-ups (JOptionPane).
 */
public class StudySpaceSystem { //Class for the Main program

    public static void main(String[] args) {

        Reservation[] reservations = new Reservation[10]; //limited to capacity of ten
        int count = 0; //racks the current number of active reservations (starts at 0).
        int choice = 0; //stores the menu selection parsed from user input.

        do { //do-while loop keeping the menu active until the sure pick option 4
            // Main Menu Box with Input Text Field
            String menu = "Group 2 STUDY SPACE SYSTEM \n\n"
                        + "1. Reserve Space\n"
                        + "2. View Reservations\n"
                        + "3. Sort Names\n"
                        + "4. Exit\n\n"
                        + "Enter Choice (1-4):";

            String inputChoice = JOptionPane.showInputDialog(null, menu);

            // If user clicks Cancel or closes the pop-up window
            if (inputChoice == null) { //If the user clicks "Cancel" or closes the window, choice sets to 4 to exit cleanly without crashing.
                choice = 4;
                break;
            }

            // Validate if input consists only of numbers (No try-catch)
            boolean isNumber = true;

            if (inputChoice.trim().isEmpty()) {
                isNumber = false;
            } else {
                for (int i = 0; i < inputChoice.length(); i++) {
                    char c = inputChoice.charAt(i);
                    if (c < '0' || c > '9') {
                        isNumber = false;
                        break;
                    }
                }
            }

            if (isNumber) {
                choice = Integer.parseInt(inputChoice);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid choice! Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (choice == 1) {

                if (count < 10) {
                    String name = JOptionPane.showInputDialog(null, "Enter Student Name:");
                    String spaceName = JOptionPane.showInputDialog(null, "Enter Study Space Name:");

                    if (name != null && spaceName != null && !name.trim().isEmpty() && !spaceName.trim().isEmpty()) {
                        StudySpace space = new QuietZone(spaceName);
                        reservations[count] = new Reservation(name, space);
                        count++;

                        JOptionPane.showMessageDialog(null, "Reservation Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Input cannot be empty!", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "System Full! Cannot add more reservations.", "Full Space", JOptionPane.WARNING_MESSAGE);
                }

            } else if (choice == 2) {

                if (count == 0) {
                    JOptionPane.showMessageDialog(null, "No reservations found.", "Reservations", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String output = "--- CURRENT RESERVATIONS ---\n\n";

                    for (int i = 0; i < count; i++) {
                        String student = reservations[i].getStudentName();
                        String space = reservations[i].getSpace().getRoomName();
                        String type = reservations[i].getSpace().getType();

                        output = output + (i + 1) + ". " + student + " - " + space + " [" + type + "]\n";
                    }

                    JOptionPane.showMessageDialog(null, output, "View Reservations", JOptionPane.PLAIN_MESSAGE);
                }

            } else if (choice == 3) {

                if (count == 0) {
                    JOptionPane.showMessageDialog(null, "No names to sort.", "Sort Names", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Standard Bubble Sort (Exact same logic)
                    for (int i = 0; i < count - 1; i++) {
                        for (int j = 0; j < count - i - 1; j++) { //nested loops

                            String name1 = reservations[j].getStudentName();
                            String name2 = reservations[j + 1].getStudentName();

                            if (name1.compareToIgnoreCase(name2) > 0) {
                                Reservation temp = reservations[j];
                                reservations[j] = reservations[j + 1];
                                reservations[j + 1] = temp; 
                            }
                        }
                    }

                    JOptionPane.showMessageDialog(null, "Names Sorted Alphabetically!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }

            } else if (choice != 4) {
                JOptionPane.showMessageDialog(null, "Invalid option! Choose between 1 and 4.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } while (choice != 4);

        JOptionPane.showMessageDialog(null, "Thank you for using Study Space System!", "Goodbye", JOptionPane.INFORMATION_MESSAGE);
    }
}