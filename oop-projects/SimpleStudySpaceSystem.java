import javax.swing.JOptionPane;//import JOptionPane 

//define the class
//applying encapsulation 
class Reservation {
    private String studentName; //define class variables
    private String spaceName;

    public Reservation(String studentName, String spaceName) {     //initialize the contructor
        this.studentName = studentName;                            //initialize the class variable
        this.spaceName = spaceName;                                //initialize the class variable
    }

    public String getStudentName() {      //get the student name variable
        return studentName;
    }

    public String getSpaceName() {        //get the space name variable
        return spaceName;
    }

    public void setStudentName(String studentName) {      //set the student name variable into public class variable
        this.studentName = studentName;
    }

    public void setSpaceName(String spaceName) {          //set the space name variable into public class variable
        this.spaceName = spaceName;
    }
}

public class SimpleStudySpaceSystem {     //define the main class            

    public static void main(String[] args) {      //main method

        //  instantiate or create an array with a size of 10
        Reservation[] reservations = new Reservation[10]; 

        int count = 0;          //set the count variable to 0 (empty)
        int choice = 0;         //set the choice variable to 0 (for menu )

        do {       //do while loop to handle enu options
            String menu = "STUDY SPACE SYSTEM\n\n" //display menu options
                        + "1. Reserve Space\n"
                        + "2. View Reservations\n"
                        + "3. Search Reservation\n"
                        + "4. Edit Reservation\n"
                        + "5. Cancel Reservation\n"
                        + "6. Exit\n\n"
                        + "Enter Choice (1-6):";

            String input = JOptionPane.showInputDialog(null, menu, "Study Space System", JOptionPane.PLAIN_MESSAGE);//display the menu in a dialog box using JOptionPane

            if (input == null) {    //if the user clicks none or cancel or close the dialog box, stop the program
                break;
            }

            //Check if input is a number
            boolean isNumber = true;    //intialize a boolean variable 
            if (input.trim().isEmpty()) {  //check if the input is empty
                isNumber = false; //set the boolean variable to false if empty
            } else {
                for (int i = 0; i < input.length(); i++) {   //loop through each character number in the input string
                    char c = input.charAt(i);   // get the character at the current index
                    if (c < '0' || c > '9') {   //checks if the character is a number between 0 and 9
                        isNumber = false;   //set the boolaean variable to false if the input number is not a number from 0 to 9
                        break;
                    }
                }
            }

            if (!isNumber) {     //if the input is not a number then printsan error message
                JOptionPane.showMessageDialog(null, "Invalid choice! Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

          choice = Integer.parseInt(input);    //

            // RESERVE SPACE
            if (choice == 1) {      //if the user choose option 1
                if (count < 10) {
                    String name = JOptionPane.showInputDialog(null, "Enter Student Name:");
                    String space = JOptionPane.showInputDialog(null, "Enter Study Space Name:");

                    if (name != null && space != null && !name.trim().isEmpty() && !space.trim().isEmpty()) {
                        reservations[count] = new Reservation(name.trim(), space.trim());   
                        count++;
                        JOptionPane.showMessageDialog(null, "Reservation Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Input cannot be empty!", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "System Full! Cannot add more reservations.", "Full Space", JOptionPane.WARNING_MESSAGE);
                }
            }

            // VIEW RESERVATIONS
            else if (choice == 2) {  // if user choose option 2
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

            // SEARCH RESERVATION
            else if (choice == 3) { //if user choose option 3
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

            // EDIT RESERVATION
            else if (choice == 4) {   // if user choose option 4
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

            // CANCEL RESERVATION
            else if (choice == 5) { // if user choose option 5
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
                        // Shift lift one object moves instead of two strings
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

            // EXIT
            else if (choice == 6) {  // if user choose option 6
                break;
            }

            // handle invalid input
            else {
                JOptionPane.showMessageDialog(null, "Invalid Option! Please choose 1-6 only.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } while (choice != 6);   // show exit message when user chooses to exit the program

        JOptionPane.showMessageDialog(null, "Thank you for using Study Space System!", "Goodbye", JOptionPane.INFORMATION_MESSAGE);
    }
}