import java.util.Scanner;

public class MainMenu {

    private static final int CONSOLE_WIDTH = 230;

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

    private static String centerText(String text) {
        int padding = Math.max(0, (CONSOLE_WIDTH - text.length()) / 2);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < padding; i++) {
            sb.append(" ");
        }
        sb.append(text);
        return sb.toString();
    }

    /** Centers a line of text and prints it */
    private static void centerPrint(String text) {
        String[] lines = text.split("\\R", -1);
        for (int i = 0; i < lines.length; i++) {
            System.out.println(centerText(lines[i]));
        }
    }

    private static int blockMargin = 0;

    private static void blockPrint(String... lines) {
        int widest = 0;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() > widest) {
                widest = lines[i].length();
            }
        }
        blockMargin = Math.max(0, (CONSOLE_WIDTH - widest) / 2);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < blockMargin; i++) {
            sb.append(" ");
        }
        for (int i = 0; i < lines.length; i++) {
            System.out.println(sb + lines[i]);
        }
    }

    private static void blockPrompt(String prompt) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < blockMargin; i++) {
            sb.append(" ");
        }
        System.out.print(sb + prompt);
    }

    public static void main(String[] args) {
        DataStore.loadInventory(inventory);
        DataStore.loadTransactions(history);
        DataStore.loadWaitingList(waitingList);
        centerPrint("LabStock: Chemistry Equipment Borrowing System (Placeholder for login system)");
        centerPrint("--------------------------------------------------------------------");
        blockPrint("1. Lab Technician", "2. Student", "3. Exit");
        blockPrompt("Select your role: ");
        int roleChoice = Integer.parseInt(readLine());

        if (roleChoice == 1) {
            labTechnicianMenu();
        } else if (roleChoice == 2) {
            studentMenu();
        } else {
            centerPrint("Goodbye!");
        }
    }

    private static String truncate(String str, int maxLen) {
        if (str.length() <= maxLen) {
            return str;
        }
        return str.substring(0, maxLen - 3) + "...";
    }

    private static void showInventorySummary() {
        InventoryItem[] allItems = inventory.getAll();
        String border = "+------+-----------------------------------+---------------------------------------+--------------+----------+";
        String header = String.format("| %-4s | %-33s | %-37s | %-12s | %-8s |", "No.", "Item Name", "Category", "Hazard", "Quantity");

        centerPrint("CURRENT INVENTORY");
        centerPrint(border);
        centerPrint(header);
        centerPrint(border);

        if (allItems.length == 0) {
            centerPrint(String.format("| %-104s |", "No items in inventory yet."));
        } else {
            for (int i = 0; i < allItems.length; i++) {
                InventoryItem item = allItems[i];
                String row = String.format("| %-4d | %-33s | %-37s | %-12s | %8d |",
                        (i + 1),
                        truncate(item.getItemName(), 33),
                        truncate(item.getCategory(), 37),
                        truncate(item.getHazardLabel(), 12),
                        item.getQuantity());
                centerPrint(row);
            }
        }
        centerPrint(border);
        System.out.println();
    }

    private static void showInventoryByCategory(String category) {
        InventoryItem[] allItems = inventory.getAll();
        String border = "+------+----------------------------------------+--------------+----------+";
        String header = String.format("| %-4s | %-38s | %-12s | %-8s |", "No.", "Item Name", "Hazard", "Quantity");

        centerPrint("CATEGORY: " + category.toUpperCase());
        centerPrint(border);
        centerPrint(header);
        centerPrint(border);

        boolean found = false;
        int count = 1;

        for (int i = 0; i < allItems.length; i++) {
            if (allItems[i].getCategory().equals(category)) {
                InventoryItem item = allItems[i];
                String row = String.format("| %-4d | %-38s | %-12s | %8d |",
                        count,
                        truncate(item.getItemName(), 38),
                        truncate(item.getHazardLabel(), 12),
                        item.getQuantity());
                centerPrint(row);
                found = true;
                count++;
            }
        }

        if (!found) {
            centerPrint(String.format("| %-71s |", "No items found in this category yet."));
        }
        centerPrint(border);
        System.out.println();
    }

    private static void labTechnicianMenu() {
        boolean loggedIn = true;

        while (loggedIn) {
            centerPrint("Lab Technician Menu");
            blockPrint("1. Insert Item",
                    "2. Remove Item",
                    "3. Search Item",
                    "4. View Overdue Items",
                    "5. View Transaction History",
                    "6. View Waiting List",
                    "7. Serve Next in Waiting List",
                    "8. Sort Inventory",
                    "9. View Student List",
                    "10. Approve Transaction",
                    "11. Logout");
            blockPrompt("Choose an option: ");
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
                    sortInventory();
                    break;
                case 9:
                    showStudentList();
                    break;
                case 10:
                    approveTransaction();
                    break;
                case 11:
                    loggedIn = false;
                    centerPrint("Logging out.");
                    break;
                default:
                    centerPrint("Invalid option.");
            }
        }
    }

    private static void insertItem() {
        centerPrint("Select category:");
        blockPrint("1. Glassware",
                "2. Measuring and Analytical Instruments",
                "3. Personal Protective Equipment (PPE)",
                "4. Chemicals");
        blockPrompt("Choose a category: ");
        int categoryChoice = Integer.parseInt(readLine());
        String category = categoryFromChoice(categoryChoice);

        // Show the filtered inventory table for the selected category
        showInventoryByCategory(category);

        System.out.print(centerText("Enter item name: "));
        String itemName = readLine();
        if (itemName.equals("")) {
            centerPrint("Item name cannot be empty.");
            return;
        }

        System.out.print(centerText("Enter hazard label (toxic/flammable/fragile/none): "));
        String hazardLabel = readLine();
        if (hazardLabel.equals("")) {
            hazardLabel = "none";
        }

        System.out.print(centerText("Enter quantity: "));
        int quantity = Integer.parseInt(readLine());
        if (quantity <= 0) {
            centerPrint("Quantity must be at least 1.");
            return;
        }

        InventoryItem item = inventory.insertItem(itemName, category, hazardLabel, quantity);
        DataStore.saveInventory(inventory);
        centerPrint("Saved. Current stock for " + item.getItemName() + ": " + item.getQuantity());
    }

    private static String categoryFromChoice(int choice) {
        switch (choice) {
            case 1:
                return "Glassware";
            case 2:
                return "Measuring and Analytical Instruments";
            case 3:
                return "Personal Protective Equipment (PPE)";
            case 4:
                return "Chemicals";
            default:
                return "Chemicals";
        }
    }

    private static void removeItem() {
        centerPrint("Select category of the item to remove:");
        blockPrint("1. Glassware",
                "2. Measuring and Analytical Instruments",
                "3. Personal Protective Equipment (PPE)",
                "4. Chemicals");
        blockPrompt("Choose a category: ");
        int categoryChoice = Integer.parseInt(readLine());
        String category = categoryFromChoice(categoryChoice);

        // Show the filtered inventory table for the selected category
        showInventoryByCategory(category);

        System.out.print(centerText("Enter item name: "));
        String itemName = readLine();

        InventoryItem item = inventory.searchItem(itemName);
        if (item == null) {
            centerPrint("Item not found in inventory.");
            return;
        }

        System.out.print(centerText("Enter quantity to remove (current stock: " + item.getQuantity() + "): "));
        int quantity = Integer.parseInt(readLine());
        if (quantity <= 0) {
            centerPrint("Quantity must be at least 1.");
            return;
        }

        inventory.removeItem(itemName, quantity);
        DataStore.saveInventory(inventory);
        centerPrint("Removed " + quantity + " from " + itemName + ".");
    }

    private static void searchItem() {
        System.out.print(centerText("Enter item name: "));
        String itemName = readLine();

        InventoryItem item = inventory.searchItem(itemName);
        if (item == null) {
            centerPrint("Item not found in inventory.");
            return;
        }

        centerPrint(item.toString());
    }

    private static void studentMenu() {
        String studentId = promptStudentId();
        String studentName = promptStudentName();
        DataStore.addStudent(studentId);
        boolean loggedIn = true;

        while (loggedIn) {
            centerPrint("Student Menu (ID: " + studentId + ")");
            centerPrint("Student Menu");
            blockPrint("1. Borrow Item",
                    "2. Return Item",
                    "3. View My Receipts",
                    "4. Join Waiting List",
                    "5. Logout");
            blockPrompt("Choose an option: ");
            int choice = Integer.parseInt(readLine());

            switch (choice) {
                case 1:
                    showInventorySummary();
                    processTransaction(studentId, studentName, "BORROW");
                    break;
                case 2:
                    processTransaction(studentId, studentName, "RETURN");
                    break;
                case 3:
                    viewMyReceipts(studentId);
                    break;
                case 4:
                    joinWaitingList(studentId);
                    break;
                case 5:
                    loggedIn = false;
                    centerPrint("Logging out.");
                    break;
                default:
                    centerPrint("Invalid option.");
            }
        }
    }

    private static String promptStudentId() {
        while (true) {
            System.out.print(centerText("Enter your 8-digit Student ID: "));
            String id = readLine();
            if (id.length() == 8) {
                return id;
            }
            centerPrint("Invalid Student ID. Please enter exactly 8 digits.");
        }
    }

    private static String promptStudentName() {
        while (true) {
            System.out.print(centerText("Enter your full name: "));
            String name = readLine();
            if (!name.equals("")) {
                return name;
            }
            centerPrint("Name cannot be empty.");
        }
    }

    private static void processTransaction(String studentId, String studentName, String type) {
        System.out.print(centerText("Enter today's date (MM/DD/YYYY): "));
        String date = readLine();

        System.out.print(centerText("Enter time (HH:MM): "));
        String time = readLine();

        System.out.print(centerText("Enter item name: "));
        String itemName = readLine();
        if (itemName.equals("")) {
            centerPrint("Item name cannot be empty.");
            return;
        }

        System.out.print(centerText("Enter quantity: "));
        int quantity = Integer.parseInt(readLine());
        if (quantity <= 0) {
            centerPrint("Quantity must be at least 1.");
            return;
        }

        if (type.equals("BORROW")) {
            InventoryItem stock = inventory.searchItem(itemName);
            if (stock == null) {
                centerPrint("Item not found in inventory.");
                return;
            }
            if (quantity > stock.getQuantity()) {
                centerPrint("Not enough stock. Available: " + stock.getQuantity());
                return;
            }
        }

        System.out.print(centerText("Enter purpose: "));
        String purpose = readLine();
        if (purpose.equals("")) {
            centerPrint("Purpose cannot be empty.");
            return;
        }

        Transaction t = history.record(studentId, studentName, itemName, quantity, type, date, time, purpose);
        System.out.println();
        String receipt = ReceiptGenerator.generate(t);
        centerPrint(receipt);
        DataStore.saveTransactions(history);
        DataStore.appendReceipt(receipt);
    }

    private static void viewMyReceipts(String studentId) {
        System.out.println();
        centerPrint(ReceiptGenerator.generateStatement(studentId, history.getByStudent(studentId)));
    }

    private static void showTransactionHistory() {
        System.out.println();
        centerPrint("Transaction History (oldest first):");
        if (history.isEmpty()) {
            centerPrint("No transactions recorded yet.");
            return;
        }
        for (int i = 0; i < history.size(); i++) {
            centerPrint(history.getAll().get(i).toString());
        }
        centerPrint("Total transactions: " + history.size());
    }

    private static void joinWaitingList(String studentId) {
        System.out.print(centerText("Enter item name: "));
        String itemName = readLine();
        if (itemName.equals("")) {
            centerPrint("Item name cannot be empty.");
            return;
        }

        System.out.print(centerText("Enter quantity: "));
        int quantity = Integer.parseInt(readLine());
        if (quantity <= 0) {
            centerPrint("Quantity must be at least 1.");
            return;
        }

        waitingList.enqueue(new WaitingListEntry(studentId, itemName, quantity));
        DataStore.saveWaitingList(waitingList);
        centerPrint("Added to the waiting list. Your position: " + waitingList.size());
    }

    private static void viewWaitingList() {
        System.out.println();
        if (waitingList.isEmpty()) {
            centerPrint("The waiting list is empty.");
            return;
        }
        centerPrint("Waiting List (first come, first served):");
        for (int i = 0; i < waitingList.size(); i++) {
            centerPrint((i + 1) + ". " + waitingList.get(i));
        }
    }

    private static void serveNextInWaitingList() {
        System.out.println();
        if (waitingList.isEmpty()) {
            centerPrint("The waiting list is empty.");
            return;
        }
        WaitingListEntry next = waitingList.dequeue();
        DataStore.saveWaitingList(waitingList);
        centerPrint("Now serving: " + next);
    }

    private static void sortInventory() {
        centerPrint("Sort inventory by:");
        blockPrint("1. Name", "2. Category");
        blockPrompt("Choose an option: ");
        int choice = Integer.parseInt(readLine());

        if (choice == 1) {
            inventory.sortByName();
        } else if (choice == 2) {
            inventory.sortByCategory();
        } else {
            centerPrint("Invalid option.");
            return;
        }
        DataStore.saveInventory(inventory);
        centerPrint("Inventory sorted.");
    }

    private static void approveTransaction() {
        centerPrint("Pending Transactions:");
        int pending = 0;
        for (int i = 0; i < history.size(); i++) {
            Transaction t = history.getAll().get(i);
            if (t.getStatus().equals("PENDING")) {
                centerPrint(t.toString());
                pending++;
            }
        }
        if (pending == 0) {
            centerPrint("No pending transactions.");
            return;
        }

        System.out.print(centerText("Enter transaction number to approve: "));
        int id = Integer.parseInt(readLine());
        Transaction selected = history.findById(id);
        if (selected == null) {
            centerPrint("Transaction not found.");
            return;
        }
        if (selected.getStatus().equals("APPROVED")) {
            centerPrint("Transaction is already approved.");
            return;
        }

        if (selected.getType().equals("BORROW")) {
            if (!inventory.borrowItem(selected.getItemName(), selected.getQuantity())) {
                centerPrint("Cannot approve: item not found or not enough stock.");
                return;
            }
        } else {
            if (!inventory.returnItem(selected.getItemName(), selected.getQuantity())) {
                centerPrint("Cannot approve: item not found in inventory.");
                return;
            }
        }
        DataStore.saveInventory(inventory);

        selected.setStatus("APPROVED");
        DataStore.saveTransactions(history);
        centerPrint("Transaction #" + id + " approved.");
    }

    private static void showStudentList() {
        System.out.println();
        MyLinkedList<String> students = DataStore.getStudents();
        if (students.isEmpty()) {
            centerPrint("No students have logged in yet.");
            return;
        }
        centerPrint("Registered Students:");
        for (int i = 0; i < students.size(); i++) {
            centerPrint((i + 1) + ". " + students.get(i));
        }
        centerPrint("Total students: " + students.size());
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