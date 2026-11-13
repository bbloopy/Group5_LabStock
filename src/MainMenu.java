import java.util.Scanner;

public class MainMenu {

    private static Scanner scanner = new Scanner(System.in);
    private static TransactionHistory history = new TransactionHistory();
    private static MyQueue<WaitingListEntry> waitingList = new MyQueue<>();
    private static InventoryManager inventory = new InventoryManager();

    private static String readLine() {
        String input = scanner.nextLine();
        clearScreen();
        return input;
    }

    private static void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    /** Centers the given text assuming a standard console width of 80 characters */
    private static String centerText(String text) {
        int padding = (80 - text.length()) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < padding; i++) {
            sb.append(" ");
        }
        sb.append(text);
        return sb.toString();
    }

    /** Centers a line of text and prints it */
    private static void centerPrint(String text) {
        System.out.println(centerText(text));
    }

    public static void main(String[] args) {
        centerPrint("LabStock: Chemistry Equipment Borrowing System (Placeholder for login system)");
        centerPrint("--------------------------------------------------------------------");
        centerPrint("1. Lab Technician");
        centerPrint("2. Student");
        centerPrint("3. Exit");
        System.out.print(centerText("Select your role: "));
        int roleChoice = Integer.parseInt(readLine());

        if (roleChoice == 1) {
            labTechnicianMenu();
        } else if (roleChoice == 2) {
            studentMenu();
        } else {
            centerPrint("Goodbye!");
        }
    }

    private static void labTechnicianMenu() {
        boolean loggedIn = true;

        while (loggedIn) {
            centerPrint("Lab Technician Menu");
            centerPrint("1. Insert Item");
            centerPrint("2. Remove Item");
            centerPrint("3. Search Item");
            centerPrint("4. View Overdue Items");
            centerPrint("5. View Transaction History");
            centerPrint("6. View Waiting List");
            centerPrint("7. Serve Next in Waiting List");
            centerPrint("8. Sort Inventory");
            centerPrint("9. Logout");
            System.out.print(centerText("Choose an option: "));
            int choice = Integer.parseInt(readLine());

            switch (choice) {
                case 1:
                    insertItem();
                    break;
                case 2:
                    removeItem();
                    break;
                case 3:
                    searchItem();
                    break;
                case 4:
                    showOverdueItems();
                    break;
                case 5:
                    showTransactionHistory();
                    break;
                case 6:
                    viewWaitingList();
                    break;
                case 7:
                    serveNextInWaitingList();
                    break;
                case 8:
                    inventory.sortByCategory();
                    break;
                case 9:
                    loggedIn = false;
                    System.out.println("Logging out.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void insertItem() {
        System.out.print("Enter item name: ");
        String itemName = readLine();
        if (itemName.equals("")) {
            System.out.println("Item name cannot be empty.");
            return;
        }

        System.out.println("Select category:");
        centerPrint("1. Glassware");
        centerPrint("2. Measuring and Analytical Instruments");
        centerPrint("3. Personal Protective Equipment (PPE)");
        System.out.print("Choose a category: ");
        int categoryChoice = Integer.parseInt(readLine());
        String category = categoryFromChoice(categoryChoice);

        System.out.print("Enter hazard label (toxic/flammable/fragile/none): ");
        String hazardLabel = readLine();
        if (hazardLabel.equals("")) {
            hazardLabel = "none";
        }

        System.out.print("Enter quantity: ");
        int quantity = Integer.parseInt(readLine());
        if (quantity <= 0) {
            System.out.println("Quantity must be at least 1.");
            return;
        }

        InventoryItem item = inventory.insertItem(itemName, category, hazardLabel, quantity);
        System.out.println("Saved. Current stock for " + item.getItemName() + ": " + item.getQuantity());
    }

    private static String categoryFromChoice(int choice) {
        switch (choice) {
            case 1:
                return "Glassware";
            case 2:
                return "Measuring and Analytical Instruments";
            case 3:
                return "Personal Protective Equipment (PPE)";
            default:
                return "Uncategorized";
        }
    }

    private static void removeItem() {
        System.out.print("Enter item name: ");
        String itemName = readLine();

        InventoryItem item = inventory.searchItem(itemName);
        if (item == null) {
            System.out.println("Item not found in inventory.");
            return;
        }

        System.out.print("Enter quantity to remove (current stock: " + item.getQuantity() + "): ");
        int quantity = Integer.parseInt(readLine());
        if (quantity <= 0) {
            System.out.println("Quantity must be at least 1.");
            return;
        }

        inventory.removeItem(itemName, quantity);
        System.out.println("Removed " + quantity + " from " + itemName + ".");
    }

    private static void searchItem() {
        System.out.print("Enter item name: ");
        String itemName = readLine();

        InventoryItem item = inventory.searchItem(itemName);
        if (item == null) {
            System.out.println("Item not found in inventory.");
            return;
        }

        System.out.println(item);
    }

    private static void studentMenu() {
        String studentId = promptStudentId();
        boolean loggedIn = true;

        while (loggedIn) {
            centerPrint("Student Menu (ID: " + studentId + ")");
            centerPrint("Student Menu");
            centerPrint("1. Borrow Item");
            centerPrint("2. Return Item");
            centerPrint("3. View My Receipts");
            centerPrint("4. Join Waiting List");
            centerPrint("5. Logout");
            System.out.print(centerText("Choose an option: "));
            int choice = Integer.parseInt(readLine());

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
                    joinWaitingList(studentId);
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

    private static String promptStudentId() {
        while (true) {
            System.out.print("Enter your 8-digit Student ID: ");
            String id = readLine();
            if (id.length() == 8) {
                return id;
            }
            System.out.println("Invalid Student ID. Please enter exactly 8 digits.");
        }
    }

    private static void processTransaction(String studentId, String type) {
        System.out.print("Enter today's date (MM/DD/YYYY): ");
        String date = readLine();

        System.out.print("Enter item name: ");
        String itemName = readLine();
        if (itemName.equals("")) {
            System.out.println("Item name cannot be empty.");
            return;
        }

        System.out.print("Enter quantity: ");
        int quantity = Integer.parseInt(readLine());
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

    private static void joinWaitingList(String studentId) {
        System.out.print("Enter item name: ");
        String itemName = readLine();
        if (itemName.equals("")) {
            System.out.println("Item name cannot be empty.");
            return;
        }

        System.out.print("Enter quantity: ");
        int quantity = Integer.parseInt(readLine());
        if (quantity <= 0) {
            System.out.println("Quantity must be at least 1.");
            return;
        }

        waitingList.enqueue(new WaitingListEntry(studentId, itemName, quantity));
        System.out.println("Added to the waiting list. Your position: " + waitingList.size());
    }

    private static void viewWaitingList() {
        System.out.println();
        if (waitingList.isEmpty()) {
            System.out.println("The waiting list is empty.");
            return;
        }
        System.out.println("Waiting List (first come, first served):");
        for (int i = 0; i < waitingList.size(); i++) {
            System.out.println((i + 1) + ". " + waitingList.get(i));
        }
    }

    private static void serveNextInWaitingList() {
        System.out.println();
        if (waitingList.isEmpty()) {
            System.out.println("The waiting list is empty.");
            return;
        }
        WaitingListEntry next = waitingList.dequeue();
        System.out.println("Now serving: " + next);
    }

    private static void showOverdueItems() {
        MyPriorityQueue<OverdueItem> overdueQueue = new MyPriorityQueue<>();

        // Example Overdue Items
        overdueQueue.insert(new OverdueItem("12345678", "Beaker 250mL", 2, 3));
        overdueQueue.insert(new OverdueItem("23456789", "Bunsen Burner", 1, 10));
        overdueQueue.insert(new OverdueItem("34567890", "Graduated Cylinder", 1, 1));
        overdueQueue.insert(new OverdueItem("45678901", "Safety Goggles", 3, 7));

        centerPrint("Overdue Items (most urgent first):");
        while (!overdueQueue.isEmpty()) {
            centerPrint(overdueQueue.extractMin().toString());
        }
    }
}