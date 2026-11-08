import java.util.Scanner;

public class MainMenu {

    private static Scanner scanner = new Scanner(System.in);
    private static TransactionHistory history = new TransactionHistory();

    public static void main(String[] args) {
        System.out.println("LabStock: Chemistry Equipment Borrowing System (Placeholder for login system)");
        System.out.println("--------------------------------------------------------------------");
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
            System.out.println("5. View Transaction History");
            System.out.println("6. Logout");
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
                    showTransactionHistory();
                    break;
                case 6:
                    loggedIn = false;
                    System.out.println("Logging out.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void studentMenu() {
        String studentId = promptStudentId();
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println();
            System.out.println("Student Menu (ID: " + studentId + ")");
            System.out.println("Student Menu");
            System.out.println("1. Borrow Item");
            System.out.println("2. Return Item");
            System.out.println("3. View My Receipts");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    processTransaction(studentId, "BORROW");
                    break;
                case 2:
                    processTransaction(studentId, "RETURN");
                    break;
                case 3:
                    viewMyReceipts(studentId);
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
    private static String promptStudentId() {
        while (true) {
            System.out.print("Enter your 8-digit Student ID: ");
            String id = scanner.nextLine();
            if (id.length() == 8) {
                return id;
            }
            System.out.println("Invalid Student ID. Please enter exactly 8 digits.");
        }
    }

    private static void processTransaction(String studentId, String type) {
        System.out.print("Enter today's date (MM/DD/YYYY): ");
        String date = scanner.nextLine();

        System.out.print("Enter item name: ");
        String itemName = scanner.nextLine();
        if (itemName.equals("")) {
            System.out.println("Item name cannot be empty.");
            return;
        }

        System.out.print("Enter quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine());
        if (quantity <= 0) {
            System.out.println("Quantity must be at least 1.");
            return;
        }

        Transaction t = history.record(studentId, itemName, quantity, type, date);
        System.out.println();
        System.out.println(ReceiptGenerator.generate(t));
    }

    private static void viewMyReceipts(String studentId) {
        System.out.println();
        System.out.println(ReceiptGenerator.generateStatement(studentId, history.getByStudent(studentId)));
    }

    private static void showTransactionHistory() {
        System.out.println();
        System.out.println("Transaction History (oldest first):");
        if (history.isEmpty()) {
            System.out.println("No transactions recorded yet.");
            return;
        }
        for (int i = 0; i < history.size(); i++) {
            System.out.println(history.getAll().get(i));
        }
        System.out.println("Total transactions: " + history.size());
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