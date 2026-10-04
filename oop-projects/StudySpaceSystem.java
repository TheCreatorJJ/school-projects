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
                        + "3. Search Reservation\n"
                        + "4. Edit Reservation\n"
                        + "5. Cancel Reservation\n"
                        + "6. Exit\n"
                        + "Enter Choice (1-6):";

            String inputChoice = JOptionPane.showInputDialog(
                null,                   //parent component
                menu,                   //Message text
                "Study Space System",   //title shown at the top bar
                JOptionPane.PLAIN_MESSAGE //Message type
                );

            // If user clicks Cancel or closes the pop-up window
            if (inputChoice == null) { //If the user clicks "Cancel" or closes the window, choice sets to 4 to exit cleanly without crashing.
                choice = 6;
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
                 JOptionPane.showMessageDialog(null, "No reservations to search.", "Search", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String searchName = JOptionPane.showInputDialog(null, "Enter Student Name to Search: ", "Search Reservation", JOptionPane.PLAIN_MESSAGE);
                    if (searchName != null && !searchName.trim().isEmpty()) {
                        boolean found = false;
                        StringBuilder result = new StringBuilder("--Search Result--\n");
                        for (int i = 0; i < count; i++) {
                            if (reservations[i].getStudentName().equalsIgnoreCase(searchName.trim())) {
                                result.append("Index ").append(i + 1).append(": ")
                                    .append(reservations[i].getStudentName()).append(" - ")
                                    .append(reservations[i].getSpace().getRoomName()).append(" [")
                                    .append(reservations[i].getSpace().getType()).append("]\n");
                                found = true;
                            }
                        }
                        if (found) {
                            JOptionPane.showMessageDialog(null, result.toString(), "Search Result", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Student not found in reservations.", "Search Result", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }   else if (choice == 4) {
                if (count == 0 ) {
                    JOptionPane.showMessageDialog(null, "No reservation to edit.", "Edit Reservation", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String searchName = JOptionPane.showInputDialog(null, "Enter Student Name whose reservation you want to edit: ", "Edit Reservation", JOptionPane.PLAIN_MESSAGE);

                    int matchIndex = -1;

                    if (searchName != null && !searchName.trim().isEmpty()) {
                        
                        for (int i = 0; i < count; i++) {
                            if (reservations[i].getStudentName().equalsIgnoreCase(searchName.trim())) {
                                matchIndex = i;
                                break;
                            }
                        }
                    }
                    
                    if (matchIndex != - 1){
                        String newName = JOptionPane.showInputDialog(null, "Enter New Student Name", reservations[matchIndex].getStudentName());
                        String newSpace = JOptionPane.showInputDialog(null, "Enter New Space Name", reservations[matchIndex].getSpace().getRoomName());
                    
                        if (newName != null && newSpace != null && !newName.trim().isEmpty() && !newSpace.trim().isEmpty()) {
                            reservations[matchIndex].setStudentName(newName.trim());
                            reservations[matchIndex].setSpace(new QuietZone(newSpace.trim()));
                            JOptionPane.showMessageDialog(null, "Reservation Updated Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                        JOptionPane.showMessageDialog(null, "Student Name not Found.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else if (choice == 5 ) {
                    if (count == 0) {
                        JOptionPane.showMessageDialog(null, "No reservation to cancel","Cancel Reservation", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        String cancelName = JOptionPane.showInputDialog(null, "Enter Student Name to Cancel Reservation", "Cancel Reservation", JOptionPane.PLAIN_MESSAGE);
                        if (cancelName != null && !cancelName.trim().isEmpty()){
                            int matchIndex = -1;
                            for (int i = 0; i < count; i++) {
                                if (reservations[i].getStudentName().equalsIgnoreCase(cancelName.trim())) {
                                    matchIndex = i;
                                    break;
                                }
                            }

                            if (matchIndex != -1) {
                                for (int i = matchIndex; i < count -1; i++) {
                                    reservations[i] = reservations[i + 1];
                                }
                                reservations[count - 1] = null;
                                count--;
                                JOptionPane.showMessageDialog(null, "Reservation Canceled Succesfully", "Sucess", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "Student Name not found", " Errror", JOptionPane.ERROR_MESSAGE);
                            }    
                        }
                    }
            } else if (choice != 6) {
                JOptionPane.showMessageDialog(null, "Invalid Option 1-6 only", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } while (choice != 6);

        JOptionPane.showMessageDialog(null, "Thank you for using Study Space System!", "Goodbye", JOptionPane.INFORMATION_MESSAGE);
    }
}