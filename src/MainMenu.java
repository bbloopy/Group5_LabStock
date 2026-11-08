import java.util.Scanner;

public class MainMenu {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Chemistry Equipment Borrowing System (Placeholder for login system)");
        System.out.println("-------------------------------------");
        System.out.println("1. Lab Technician");
        System.out.println("2. Student");
        System.out.println("3. Exit");
        System.out.print("Select your role: ");
        int roleChoice = Integer.parseInt(scanner.nextLine());

        if (roleChoice == 1) {
            labTechnicianMenu();
        } else if (roleChoice == 2) {
            studentMenu();
        } else {
            System.out.println("Goodbye!");
        }
    }

    private static void labTechnicianMenu() {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println();
            System.out.println("Lab Technician Menu");
            System.out.println("1. Insert Item");
            System.out.println("2. Remove Item");
            System.out.println("3. Search Item");
            System.out.println("4. View Overdue Items");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    System.out.println("Insert Item feature coming soon.");
                    break;
                case 2:
                    System.out.println("Remove Item feature coming soon.");
                    break;
                case 3:
                    System.out.println("Search Item feature coming soon.");
                    break;
                case 4:
                    showOverdueItems();
                    break;
                case 5:
                    loggedIn = false;
                    System.out.println("Logging out.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void studentMenu() {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println();
            System.out.println("Student Menu");
            System.out.println("1. Borrow Item");
            System.out.println("2. Return Item");
            System.out.println("3. View My Receipts");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    System.out.println("Borrow Item feature coming soon.");
                    break;
                case 2:
                    System.out.println("Return Item feature coming soon.");
                    break;
                case 3:
                    System.out.println("View My Receipts feature coming soon.");
                    break;
                case 4:
                    loggedIn = false;
                    System.out.println("Logging out.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void showOverdueItems() {
        MyPriorityQueue<OverdueItem> overdueQueue = new MyPriorityQueue<>();

        // Example Overdue Items
        overdueQueue.insert(new OverdueItem("12345678", "Beaker 250mL", 2, 3));
        overdueQueue.insert(new OverdueItem("23456789", "Bunsen Burner", 1, 10));
        overdueQueue.insert(new OverdueItem("34567890", "Graduated Cylinder", 1, 1));
        overdueQueue.insert(new OverdueItem("45678901", "Safety Goggles", 3, 7));

        System.out.println();
        System.out.println("Overdue Items (most urgent first):");
        while (!overdueQueue.isEmpty()) {
            System.out.println(overdueQueue.extractMin());
        }
    }
}