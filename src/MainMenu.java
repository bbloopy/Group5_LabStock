import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class MainMenu {

    private static final int CONSOLE_WIDTH = 120;
    private static final int MAX_QUANTITY = 9999;

    private static Scanner scanner = new Scanner(System.in);
    private static TransactionHistory history = new TransactionHistory();
    private static MyQueue<WaitingListEntry> waitingList = new MyQueue<>();
    private static InventoryManager inventory = new InventoryManager();

    private static class BackException extends RuntimeException {
    }

    private static String readLine() {
        String input = scanner.nextLine();
        clearScreen();
        if (input.trim().equalsIgnoreCase("back")) {
            throw new BackException();
        }
        return input;
    }

    private static String readLineNoClear() {
        String input = scanner.nextLine();
        if (input.trim().equalsIgnoreCase("back")) {
            clearScreen();
            throw new BackException();
        }
        return input;
    }

    private static String promptRequired(String question) {
        while (true) {
            System.out.print(centerText(question + " (or 'back'): "));
            String value = readLineNoClear().trim();
            if (value.equals("")) {
                centerPrint("This field cannot be empty.");
            } else if (!value.matches("[A-Za-z0-9]+")) {
                centerPrint("Use letters and numbers only, with no spaces or symbols.");
            } else {
                return value.toUpperCase();
            }
        }
    }

    private static int toInt(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String promptHazard() {
        while (true) {
            System.out.print(centerText("Enter hazard label (toxic/flammable/fragile/none, or 'back'): "));
            String value = readLine().trim().toLowerCase();
            if (value.equals("")) {
                return "none";
            }
            if (value.equals("toxic") || value.equals("flammable") || value.equals("fragile") || value.equals("none")) {
                return value;
            }
            centerPrint("Invalid hazard label. Choose toxic, flammable, fragile, or none.");
        }
    }

    private static int findLine(MyLinkedList<ReceiptLine> items, String itemName) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getItemName().equals(itemName)) {
                return i;
            }
        }
        return -1;
    }

    private static String checkLine(MyLinkedList<ReceiptLine> items, String type, String itemName, int quantity) {
        InventoryItem stock = inventory.searchItem(itemName);
        if (stock == null) {
            return "Item not found in inventory.";
        }
        if (type.equals("BORROW")) {
            int total = quantity;
            int existing = findLine(items, itemName);
            if (existing != -1) {
                total += items.get(existing).getQuantity();
            }
            if (total > stock.getQuantity()) {
                return "Not enough stock. Available: " + stock.getQuantity();
            }
        }
        return null;
    }

    private static void addLine(MyLinkedList<ReceiptLine> items, String itemName, int quantity) {
        int existing = findLine(items, itemName);
        if (existing == -1) {
            items.add(new ReceiptLine(itemName, quantity));
        } else {
            ReceiptLine line = items.get(existing);
            line.setQuantity(line.getQuantity() + quantity);
        }
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

    private static void drawWindow(String title, String[] lines) {
        int windowWidth = 60;
        if (title.length() > windowWidth) {
            title = title.substring(0, windowWidth - 2);
        }

        int leftPad = Math.max(0, (CONSOLE_WIDTH - windowWidth - 2) / 2);
        StringBuilder marginSb = new StringBuilder();
        for (int i = 0; i < leftPad; i++) marginSb.append(" ");
        String m = marginSb.toString();

        StringBuilder borderSb = new StringBuilder();
        for (int i = 0; i < windowWidth; i++) borderSb.append("=");
        String border = m + "+" + borderSb.toString() + "+";
        String separator = m + "+" + borderSb.toString().replace('=', '-') + "+";

        System.out.println(border);

        int titlePadLeft = (windowWidth - title.length()) / 2;
        int titlePadRight = windowWidth - title.length() - titlePadLeft;
        StringBuilder titleLine = new StringBuilder();
        for (int i = 0; i < titlePadLeft; i++) titleLine.append(" ");
        titleLine.append(title);
        for (int i = 0; i < titlePadRight; i++) titleLine.append(" ");
        System.out.println(m + "|" + titleLine.toString() + "|");

        System.out.println(separator);

        for (int i = 0; i < lines.length; i++) {
            String lineText = "   " + lines[i];
            int rightPad = Math.max(0, windowWidth - lineText.length());
            StringBuilder lineSb = new StringBuilder(lineText);
            for (int j = 0; j < rightPad; j++) lineSb.append(" ");

            if (lineSb.length() > windowWidth) {
                lineSb.setLength(windowWidth);
            }
            System.out.println(m + "|" + lineSb.toString() + "|");
        }

        System.out.println(border);
        System.out.println();
    }

    public static void main(String[] args) {
        DataStore.loadInventory(inventory);
        DataStore.loadTransactions(history);
        DataStore.loadWaitingList(waitingList);

        MyHashTable<String, String> labTechs = new MyHashTable<>();
        labTechs.put("19910858", "SuperSuperPass");
        labTechs.put("12414234", "Password123");
        labTechs.put("11485305", "qwertyuiop");

        MyHashTable<String, String> students = new MyHashTable<>();
        students.put("12512353", "SuperPass");
        students.put("12510092", "WeakPassword");
        students.put("12275934", "asdfghjkl");

        while (true) {
            String[] mainOptions = {
                    "1. Lab Technician",
                    "2. Student",
                    "3. Exit"
            };
            drawWindow("LabStock: Chemistry Equipment Borrowing System", mainOptions);
            System.out.print(centerText("Select your role: "));

            int roleChoice;
            try {
                roleChoice = toInt(readLine());
            } catch (BackException e) {
                continue;
            }

            if (roleChoice == 1) {
                String loggedInId = authenticate(labTechs, "Lab Technician");
                if (loggedInId != null) {
                    labTechnicianMenu();
                }
            } else if (roleChoice == 2) {
                String loggedInId = authenticate(students, "Student");
                if (loggedInId != null) {
                    studentMenu(loggedInId);
                }
            } else if (roleChoice == 3) {
                centerPrint("Goodbye!");
                break;
            } else {
                centerPrint("Invalid option.");
            }
        }
    }

    private static String authenticate(MyHashTable<String, String> credentials, String roleName) {
        while (true) {
            String[] loginInfo = {
                    "Authorized personnel only.",
                    "Type 'back' at any prompt to cancel."
            };
            drawWindow(roleName + " Login", loginInfo);

            String id;
            String pass;
            try {
                System.out.print(centerText("Enter ID: "));
                id = readLineNoClear();

                System.out.print(centerText("Enter Password: "));
                java.io.Console console = System.console();
                if (console != null) {
                    char[] passArray = console.readPassword();
                    if (passArray == null) return null;
                    pass = new String(passArray);
                    clearScreen();
                    if (pass.trim().equalsIgnoreCase("back")) throw new BackException();
                } else {
                    pass = readLine();
                }
            } catch (BackException e) {
                return null;
            }

            if (credentials.containsKey(id) && credentials.get(id).equals(pass)) {
                centerPrint("Login successful! Welcome, " + roleName + ".");
                return id;
            } else {
                centerPrint("Invalid ID or Password. Please try again.");
                System.out.println();
            }
        }
    }

    private static String truncate(String str, int maxLen) {
        if (str.length() <= maxLen) {
            return str;
        }
        return str.substring(0, maxLen - 3) + "...";
    }

    private static InventoryItem findItemById(String raw) {
        String input = raw.trim().toUpperCase();
        if (input.startsWith("ITM-")) {
            input = input.substring(4);
        }
        try {
            return inventory.searchItemById(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static void showInventorySummary() {
        InventoryItem[] allItems = inventory.getAll();
        String border = "+---------+--------------------------------+---------------------------------------+--------------+----------+";
        String header = String.format("| %-7s | %-30s | %-37s | %-12s | %-8s |", "Item ID", "Item Name", "Category", "Hazard", "Quantity");

        centerPrint("CURRENT INVENTORY");
        centerPrint(border);
        centerPrint(header);
        centerPrint(border);

        if (allItems.length == 0) {
            centerPrint(String.format("| %-106s |", "No items in inventory yet."));
        } else {
            for (int i = 0; i < allItems.length; i++) {
                InventoryItem item = allItems[i];
                String row = String.format("| %-7s | %-30s | %-37s | %-12s | %8d |",
                        item.getFormattedId(),
                        truncate(item.getItemName(), 30),
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
        String border = "+---------+-----------------------------------+--------------+----------+";
        String header = String.format("| %-7s | %-35s | %-12s | %-8s |", "Item ID", "Item Name", "Hazard", "Quantity");

        centerPrint("CATEGORY: " + category.toUpperCase());
        centerPrint(border);
        centerPrint(header);
        centerPrint(border);

        boolean found = false;

        for (int i = 0; i < allItems.length; i++) {
            if (allItems[i].getCategory().equals(category)) {
                InventoryItem item = allItems[i];
                String row = String.format("| %-7s | %-35s | %-12s | %8d |",
                        item.getFormattedId(),
                        truncate(item.getItemName(), 35),
                        truncate(item.getHazardLabel(), 12),
                        item.getQuantity());
                centerPrint(row);
                found = true;
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
            String[] labOptions = {
                    "1. Insert Item", "2. Remove Item", "3. Edit Item", "4. Search Item",
                    "5. View Overdue Items", "6. View Transaction History",
                    "7. View Waiting List", "8. Serve Next in Waiting List",
                    "9. View Inventory", "10. Sort Inventory",
                    "11. View Student List", "12. Approve Transaction", "13. Logout"
            };
            drawWindow("Lab Technician Menu", labOptions);
            System.out.print(centerText("Choose an option (or 'back' to log out): "));
            int choice;
            try {
                choice = toInt(readLine());
            } catch (BackException e) {
                loggedIn = false;
                centerPrint("Logging out.");
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        insertItem();
                        break;
                    case 2:
                        removeItem();
                        break;
                    case 3:
                        editItem();
                        break;
                    case 4:
                        searchItem();
                        break;
                    case 5:
                        showOverdueItems();
                        break;
                    case 6:
                        showTransactionHistory();
                        break;
                    case 7:
                        viewWaitingList();
                        break;
                    case 8:
                        serveNextInWaitingList();
                        break;
                    case 9:
                        showInventorySummary();
                        break;
                    case 10:
                        sortInventory();
                        break;
                    case 11:
                        showStudentList();
                        break;
                    case 12:
                        approveTransaction();
                        break;
                    case 13:
                        loggedIn = false;
                        centerPrint("Logging out.");
                        break;
                    default:
                        centerPrint("Invalid option.");
                }
            } catch (BackException e) {
                centerPrint("Cancelled.");
            }
        }
    }

    private static void insertItem() {
        centerPrint("Select category:");
        blockPrint("1. Glassware",
                "2. Measuring and Analytical Instruments",
                "3. Personal Protective Equipment (PPE)",
                "4. Chemicals");
        blockPrompt("Choose a category (or 'back'): ");
        int categoryChoice = toInt(readLine());
        String category = categoryFromChoice(categoryChoice);
        if (category == null) {
            centerPrint("Invalid category.");
            return;
        }

        showInventoryByCategory(category);

        System.out.print(centerText("Enter item ID to restock, or type NEW to add a new item (or 'back'): "));
        String idInput = readLine().trim();

        InventoryItem existing;
        String itemName;
        if (idInput.equalsIgnoreCase("new")) {
            System.out.print(centerText("Enter new item name (or 'back'): "));
            itemName = readLine().trim();
            if (itemName.equals("")) {
                centerPrint("Item name cannot be empty.");
                return;
            }
            existing = inventory.searchItem(itemName);
        } else {
            existing = findItemById(idInput);
            if (existing == null) {
                centerPrint("Item ID not found. Type NEW to add a new item.");
                return;
            }
            itemName = existing.getItemName();
        }

        if (existing != null) {
            centerPrint("Item already exists (Category: " + existing.getCategory() + ", Hazard: " + existing.getHazardLabel() + ", Current Stock: " + existing.getQuantity() + ").");
            System.out.print(centerText("Enter quantity to add (or 'back'): "));
            int quantity = toInt(readLine());
            if (quantity <= 0 || quantity > MAX_QUANTITY) {
                centerPrint("Invalid quantity. Enter a whole number from 1 to 9999.");
                return;
            }

            InventoryItem item = inventory.insertItem(existing.getItemName(), existing.getCategory(), existing.getHazardLabel(), quantity);
            DataStore.saveInventory(inventory);
            centerPrint("Saved. Current stock for " + item.getItemName() + ": " + item.getQuantity());
            return;
        }

        String hazardLabel = promptHazard();

        System.out.print(centerText("Enter quantity (or 'back'): "));
        int quantity = toInt(readLine());
        if (quantity <= 0 || quantity > MAX_QUANTITY) {
            centerPrint("Invalid quantity. Enter a whole number from 1 to 9999.");
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
                return null;
        }
    }

    private static void removeItem() {
        centerPrint("Select category of the item to remove:");
        blockPrint("1. Glassware",
                "2. Measuring and Analytical Instruments",
                "3. Personal Protective Equipment (PPE)",
                "4. Chemicals");
        blockPrompt("Choose a category (or 'back'): ");
        int categoryChoice = toInt(readLine());
        String category = categoryFromChoice(categoryChoice);
        if (category == null) {
            centerPrint("Invalid category.");
            return;
        }

        showInventoryByCategory(category);

        System.out.print(centerText("Enter item ID, e.g. ITM-001 or 1 (or 'back'): "));
        InventoryItem item = findItemById(readLine());
        if (item == null) {
            centerPrint("Item ID not found in inventory.");
            return;
        }
        String itemName = item.getItemName();

        System.out.print(centerText("Enter quantity to remove (current stock: " + item.getQuantity() + ", or 'back'): "));
        int quantity = toInt(readLine());
        if (quantity <= 0 || quantity > MAX_QUANTITY) {
            centerPrint("Invalid quantity. Enter a whole number from 1 to 9999.");
            return;
        }

        if (quantity > item.getQuantity()) {
            centerPrint("Cannot remove more than the current stock (" + item.getQuantity() + ").");
            return;
        }

        inventory.removeItem(itemName, quantity);
        DataStore.saveInventory(inventory);
        centerPrint("Removed " + quantity + " from " + itemName + ".");
    }

    private static void editItem() {
        centerPrint("Select category of the item to edit:");
        blockPrint("1. Glassware",
                "2. Measuring and Analytical Instruments",
                "3. Personal Protective Equipment (PPE)",
                "4. Chemicals");
        blockPrompt("Choose a category (or 'back'): ");
        int categoryChoice = toInt(readLine());
        String category = categoryFromChoice(categoryChoice);
        if (category == null) {
            centerPrint("Invalid category.");
            return;
        }

        showInventoryByCategory(category);

        System.out.print(centerText("Enter item ID, e.g. ITM-001 or 1 (or 'back'): "));
        InventoryItem item = findItemById(readLine());
        if (item == null) {
            centerPrint("Item ID not found in inventory.");
            return;
        }

        boolean editing = true;
        while (editing) {
            centerPrint("Editing: " + item);
            blockPrint("1. Edit Name",
                    "2. Edit Category",
                    "3. Edit Hazard Label",
                    "4. Edit Quantity",
                    "5. Done");
            blockPrompt("Choose an option (or 'back' to finish): ");
            int choice;
            try {
                choice = toInt(readLine());
            } catch (BackException e) {
                editing = false;
                continue;
            }

            try {
                switch (choice) {
                    case 1: {
                        System.out.print(centerText("Enter new name (or 'back'): "));
                        String newName = readLine().trim();
                        if (newName.equals("")) {
                            centerPrint("Name cannot be empty.");
                            break;
                        }
                        String oldName = item.getItemName();
                        boolean renamed = inventory.renameItem(oldName, newName);
                        if (renamed) centerPrint("Renamed '" + oldName + "' to '" + newName + "'.");
                        else centerPrint("An item named '" + newName + "' already exists.");
                        break;
                    }
                    case 2: {
                        centerPrint("Select new category:");
                        blockPrint("1. Glassware",
                                "2. Measuring and Analytical Instruments",
                                "3. Personal Protective Equipment (PPE)",
                                "4. Chemicals");
                        blockPrompt("Choose a category (or 'back'): ");
                        int newCategoryChoice = toInt(readLine());
                        String newCategory = categoryFromChoice(newCategoryChoice);
                        if (newCategory == null) {
                            centerPrint("Invalid category.");
                            break;
                        }
                        inventory.editCategory(item.getItemName(), newCategory);
                        centerPrint("Category updated to '" + newCategory + "'.");
                        break;
                    }
                    case 3: {
                        String newHazard = promptHazard();
                        inventory.editHazardLabel(item.getItemName(), newHazard);
                        centerPrint("Hazard label updated to '" + newHazard + "'.");
                        break;
                    }
                    case 4: {
                        System.out.print(centerText("Enter new quantity (or 'back'): "));
                        int newQuantity = toInt(readLine());
                        if (newQuantity <= 0 || newQuantity > MAX_QUANTITY) {
                            centerPrint("Invalid quantity. Enter a whole number from 1 to 9999.");
                            break;
                        }
                        inventory.editQuantity(item.getItemName(), newQuantity);
                        centerPrint("Quantity updated to " + newQuantity + ".");
                        break;
                    }
                    case 5:
                        editing = false;
                        break;
                    default:
                        centerPrint("Invalid option.");
                }
            } catch (BackException e) {
                centerPrint("Cancelled.");
            }
        }

        DataStore.saveInventory(inventory);
        centerPrint("Done editing " + item.getItemName() + ".");
    }

    private static void searchItem() {
        centerPrint("Search item by:");
        blockPrint("1. Item Name", "2. Item ID");
        blockPrompt("Choose an option (or 'back'): ");
        int choice = toInt(readLine());

        InventoryItem item;
        if (choice == 1) {
            System.out.print(centerText("Enter item name (or 'back'): "));
            item = inventory.searchItem(readLine().trim());
        } else if (choice == 2) {
            System.out.print(centerText("Enter item ID (e.g. ITM-001 or 1, or 'back'): "));
            String input = readLine().trim().toUpperCase();
            if (input.startsWith("ITM-")) {
                input = input.substring(4);
            }
            try {
                item = inventory.searchItemById(Integer.parseInt(input));
            } catch (NumberFormatException e) {
                centerPrint("Invalid item ID.");
                return;
            }
        } else {
            centerPrint("Invalid option.");
            return;
        }

        if (item == null) {
            centerPrint("Item not found in inventory.");
            return;
        }
        centerPrint(item.toString());
    }

    private static void studentMenu(String studentId) {
        String studentName;
        try {
            studentName = promptStudentName();
        } catch (BackException e) {
            return;
        }
        DataStore.addStudent(studentId);
        boolean loggedIn = true;

        while (loggedIn) {
            String[] studentOptions = {
                    "1. Borrow Item", "2. Return Item", "3. View My Receipts",
                    "4. Join Waiting List", "5. Edit Pending Receipt", "6. Logout"
            };
            drawWindow("Student Menu (ID: " + studentId + ")", studentOptions);
            System.out.print(centerText("Choose an option (or 'back' to log out): "));
            int choice;
            try {
                choice = toInt(readLine());
            } catch (BackException e) {
                loggedIn = false;
                centerPrint("Logging out.");
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        centerPrint("Select category to borrow from:");
                        blockPrint("1. Glassware",
                                "2. Measuring and Analytical Instruments",
                                "3. Personal Protective Equipment (PPE)",
                                "4. Chemicals");
                        blockPrompt("Choose a category (or 'back'): ");
                        int categoryChoice = toInt(readLine());
                        String category = categoryFromChoice(categoryChoice);
                        if (category == null) {
                            centerPrint("Invalid category.");
                            break;
                        }
                        showInventoryByCategory(category);
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
                        editPendingReceipt(studentId);
                        break;
                    case 6:
                        loggedIn = false;
                        centerPrint("Logging out.");
                        break;
                    default:
                        centerPrint("Invalid option.");
                }
            } catch (BackException e) {
                centerPrint("Cancelled.");
            }
        }
    }

    private static String promptStudentName() {
        while (true) {
            System.out.print(centerText("Enter your full name (or 'back'): "));
            String name = readLine().trim();
            if (name.equals("")) {
                centerPrint("Name cannot be empty.");
            } else if (!name.matches("[\\p{L} .'-]+")) {
                centerPrint("Name can only contain letters, spaces, periods, hyphens, and apostrophes.");
            } else {
                return name;
            }
        }
    }

    private static void processTransaction(String studentId, String studentName, String type) {
        MyLinkedList<ReceiptLine> items = new MyLinkedList<>();

        centerPrint("Building your " + type.toLowerCase() + " receipt. Add items one at a time.");
        while (true) {
            System.out.print(centerText("Enter item ID (or 'done' to finish, or 'back' to cancel): "));
            String idInput = readLineNoClear().trim();
            if (idInput.equalsIgnoreCase("done")) {
                break;
            }
            if (idInput.equals("")) {
                centerPrint("Item ID cannot be empty.");
                continue;
            }
            InventoryItem chosen = findItemById(idInput);
            if (chosen == null) {
                centerPrint("Item ID not found in inventory.");
                continue;
            }
            String itemName = chosen.getItemName();

            System.out.print(centerText("Enter quantity (or 'back'): "));
            int quantity = toInt(readLineNoClear());
            if (quantity <= 0 || quantity > MAX_QUANTITY) {
                centerPrint("Invalid quantity. Enter a whole number from 1 to 9999.");
                continue;
            }

            String problem = checkLine(items, type, itemName, quantity);
            if (problem != null) {
                centerPrint(problem);
                continue;
            }

            addLine(items, itemName, quantity);
            centerPrint("Added: " + itemName + " x" + quantity + " (" + items.size() + " item(s) so far)");
        }

        clearScreen();

        if (items.isEmpty()) {
            centerPrint("No items added. Request cancelled.");
            return;
        }

        centerPrint("Items in this receipt:");
        for (int i = 0; i < items.size(); i++) {
            centerPrint((i + 1) + ". " + items.get(i));
        }

        System.out.print(centerText("Enter today's date (MM/DD/YYYY, or 'back'): "));
        String date = readLineNoClear();

        System.out.print(centerText("Enter time (HH:MM, or 'back'): "));
        String time = readLineNoClear();

        String courseId = promptRequired("Enter course ID, e.g. LBYCH1A");
        String section = promptRequired("Enter section, e.g. E28");

        System.out.print(centerText("Enter purpose (or 'back'): "));
        String purpose = readLine().trim();
        if (purpose.equals("")) {
            centerPrint("Purpose cannot be empty.");
            return;
        }

        Transaction t = history.record(studentId, studentName, items, type, date, time, purpose);
        t.setCourseId(courseId);
        t.setSection(section);
        DataStore.saveTransactions(history);

        String number = ReceiptGenerator.formatReceiptNumber(t.getTransactionId());
        centerPrint("Request submitted as Receipt No. R-" + number + ". Pending lab technician approval.");
    }

    private static void editPendingReceipt(String studentId) {
        MyLinkedList<Transaction> pending = history.getPendingByStudent(studentId);
        if (pending.isEmpty()) {
            centerPrint("You have no pending receipts to edit.");
            return;
        }

        centerPrint("Your Pending Receipts:");
        for (int i = 0; i < pending.size(); i++) {
            centerPrint(pending.get(i).toString());
        }

        System.out.print(centerText("Enter the receipt number to edit (e.g. R-000002 or just 2, or 'back'): "));
        String input = readLine().replace("R-", "").replace("r-", "").trim();
        int id;
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            centerPrint("Invalid receipt number.");
            return;
        }

        Transaction t = history.findById(id);
        if (t == null || !t.getStudentId().equals(studentId)) {
            centerPrint("Receipt not found.");
            return;
        }
        if (!t.getStatus().equals("PENDING")) {
            centerPrint("This receipt has already been " + t.getStatus().toLowerCase() + " and can no longer be edited.");
            return;
        }

        boolean editing = true;
        while (editing) {
            centerPrint(t.toString());
            centerPrint("Course / Section: " + t.getCourseId() + " / " + t.getSection());
            centerPrint("Items:");
            for (int i = 0; i < t.getItems().size(); i++) {
                centerPrint((i + 1) + ". " + t.getItems().get(i));
            }
            blockPrint("1. Add an item",
                    "2. Edit an item's quantity",
                    "3. Remove an item",
                    "4. Edit purpose",
                    "5. Edit course ID and section",
                    "6. Done");
            blockPrompt("Choose an option (or 'back' to finish): ");
            int choice;
            try {
                choice = toInt(readLine());
            } catch (BackException e) {
                editing = false;
                continue;
            }

            try {
                switch (choice) {
                    case 1: {
                        System.out.print(centerText("Enter item ID (or 'back'): "));
                        InventoryItem newItem = findItemById(readLineNoClear());
                        if (newItem == null) {
                            centerPrint("Item ID not found in inventory.");
                            break;
                        }
                        String newItemName = newItem.getItemName();
                        System.out.print(centerText("Enter quantity (or 'back'): "));
                        int newQuantity = toInt(readLine());
                        if (newQuantity <= 0 || newQuantity > MAX_QUANTITY) {
                            centerPrint("Invalid quantity. Enter a whole number from 1 to 9999.");
                            break;
                        }
                        String problem = checkLine(t.getItems(), t.getType(), newItemName, newQuantity);
                        if (problem != null) {
                            centerPrint(problem);
                            break;
                        }
                        addLine(t.getItems(), newItemName, newQuantity);
                        centerPrint("Item added.");
                        break;
                    }
                    case 2: {
                        System.out.print(centerText("Enter the item number to edit (or 'back'): "));
                        int lineNum = toInt(readLineNoClear());
                        if (lineNum < 1 || lineNum > t.getItems().size()) {
                            clearScreen();
                            centerPrint("Invalid item number.");
                            break;
                        }
                        System.out.print(centerText("Enter new quantity (or 'back'): "));
                        int newQuantity = toInt(readLine());
                        if (newQuantity <= 0 || newQuantity > MAX_QUANTITY) {
                            centerPrint("Invalid quantity. Enter a whole number from 1 to 9999.");
                            break;
                        }
                        ReceiptLine line = t.getItems().get(lineNum - 1);
                        InventoryItem stock = inventory.searchItem(line.getItemName());
                        if (t.getType().equals("BORROW") && (stock == null || newQuantity > stock.getQuantity())) {
                            centerPrint("Not enough stock. Available: " + (stock == null ? 0 : stock.getQuantity()));
                            break;
                        }
                        line.setQuantity(newQuantity);
                        centerPrint("Quantity updated.");
                        break;
                    }
                    case 3: {
                        if (t.getItems().size() <= 1) {
                            centerPrint("A receipt needs at least one item, so this one can't be removed. Use 'Done' to cancel the whole receipt instead.");
                            break;
                        }
                        System.out.print(centerText("Enter the item number to remove (or 'back'): "));
                        int lineNum = toInt(readLine());
                        if (lineNum < 1 || lineNum > t.getItems().size()) {
                            centerPrint("Invalid item number.");
                            break;
                        }
                        t.removeItem(lineNum - 1);
                        centerPrint("Item removed.");
                        break;
                    }
                    case 4: {
                        System.out.print(centerText("Enter new purpose (or 'back'): "));
                        String newPurpose = readLine().trim();
                        if (newPurpose.equals("")) {
                            centerPrint("Purpose cannot be empty.");
                            break;
                        }
                        t.setPurpose(newPurpose);
                        centerPrint("Purpose updated.");
                        break;
                    }
                    case 5: {
                        String newCourseId = promptRequired("Enter new course ID, e.g. LBYCH1A");
                        String newSection = promptRequired("Enter new section, e.g. E28");
                        t.setCourseId(newCourseId);
                        t.setSection(newSection);
                        centerPrint("Course ID and section updated.");
                        break;
                    }
                    case 6:
                        editing = false;
                        break;
                    default:
                        centerPrint("Invalid option.");
                }
            } catch (BackException e) {
                centerPrint("Cancelled.");
            }
        }

        DataStore.saveTransactions(history);
        centerPrint("Receipt updated. Still pending lab technician approval.");
        centerPrint(t.toString());
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
        System.out.print(centerText("Enter item ID, e.g. ITM-001 or 1 (or 'back'): "));
        InventoryItem waitItem = findItemById(readLine());
        if (waitItem == null) {
            centerPrint("Item ID not found in inventory.");
            return;
        }
        String itemName = waitItem.getItemName();

        System.out.print(centerText("Enter quantity (or 'back'): "));
        int quantity = toInt(readLine());
        if (quantity <= 0 || quantity > MAX_QUANTITY) {
            centerPrint("Invalid quantity. Enter a whole number from 1 to 9999.");
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
        blockPrint("1. Name", "2. Category", "3. Item Number");
        blockPrompt("Choose an option (or 'back'): ");
        int choice = toInt(readLine());

        if (choice == 1) {
            inventory.sortByName();
        } else if (choice == 2) {
            inventory.sortByCategory();
        } else if (choice == 3) {
            inventory.sortById();
        } else {
            centerPrint("Invalid option.");
            return;
        }
        DataStore.saveInventory(inventory);
        centerPrint("Inventory sorted.");
    }

    private static void approveTransaction() {
        while (true) {
            MyLinkedList<Transaction> pending = history.getPending();
            if (pending.isEmpty()) {
                centerPrint("No pending transactions.");
                return;
            }

            centerPrint("Pending Transactions:");
            for (int i = 0; i < pending.size(); i++) {
                centerPrint(pending.get(i).toString());
            }
            System.out.print(centerText("Enter transaction number to review (or 'back'): "));
            int id = toInt(readLine());

            Transaction selected = history.findById(id);
            if (selected == null || !selected.getStatus().equals("PENDING")) {
                centerPrint("That transaction isn't pending (it may already be decided, or doesn't exist).");
                continue;
            }

            centerPrint("Reviewing:");
            centerPrint(selected.toString());
            centerPrint("Items:");
            for (int i = 0; i < selected.getItems().size(); i++) {
                centerPrint((i + 1) + ". " + selected.getItems().get(i));
            }
            blockPrint("1. Approve", "2. Reject", "3. Skip");
            blockPrompt("Choose an option (or 'back'): ");
            int action;
            try {
                action = toInt(readLine());
            } catch (BackException e) {
                continue;
            }

            if (action == 1) {
                approveSelectedTransaction(selected);
            } else if (action == 2) {
                selected.setStatus("REJECTED");
                DataStore.saveTransactions(history);
                centerPrint("Transaction #" + id + " rejected.");
            }
        }
    }

    private static void approveSelectedTransaction(Transaction t) {
        MyLinkedList<ReceiptLine> items = t.getItems();

        for (int i = 0; i < items.size(); i++) {
            ReceiptLine line = items.get(i);
            if (t.getType().equals("BORROW")) {
                InventoryItem stock = inventory.searchItem(line.getItemName());
                if (stock == null || stock.getQuantity() < line.getQuantity()) {
                    centerPrint("Cannot approve: not enough stock for " + line.getItemName() + ".");
                    return;
                }
            } else {
                if (inventory.searchItem(line.getItemName()) == null) {
                    centerPrint("Cannot approve: " + line.getItemName() + " is not a recognized inventory item.");
                    return;
                }
            }
        }

        for (int i = 0; i < items.size(); i++) {
            ReceiptLine line = items.get(i);
            if (t.getType().equals("BORROW")) {
                inventory.borrowItem(line.getItemName(), line.getQuantity());
            } else {
                inventory.returnItem(line.getItemName(), line.getQuantity());
            }
        }
        DataStore.saveInventory(inventory);

        t.setStatus("APPROVED");
        DataStore.saveTransactions(history);

        String receipt = ReceiptGenerator.generate(t);
        centerPrint("Approved. Receipt given to student:");
        centerPrint(receipt);
        DataStore.appendReceipt(receipt);
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
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        LocalDate todayDate = null;
        while (todayDate == null) {
            System.out.print(centerText("Enter today's date (MM/DD/YYYY, or 'back'): "));
            String todayInput = readLineNoClear().trim();
            try {
                todayDate = LocalDate.parse(todayInput, fmt);
            } catch (DateTimeParseException e) {
                centerPrint("Invalid date. Please use MM/DD/YYYY.");
            }
        }
        clearScreen();
        centerPrint("Reference date: " + todayDate.format(fmt));

        MyPriorityQueue<OverdueItem> overdueQueue = new MyPriorityQueue<>();

        centerPrint("Log each overdue item one at a time.");
        while (true) {
            System.out.print(centerText("Enter borrowed item ID (or 'done' to finish, or 'back' to cancel): "));
            String idInput = readLineNoClear().trim();
            if (idInput.equalsIgnoreCase("done")) {
                break;
            }
            InventoryItem item = findItemById(idInput);
            if (item == null) {
                centerPrint("Item ID not found in inventory.");
                continue;
            }

            System.out.print(centerText("Enter the student ID who borrowed it (or 'back'): "));
            String studentId = readLineNoClear().trim();
            if (studentId.equals("")) {
                centerPrint("Student ID cannot be empty.");
                continue;
            }

            System.out.print(centerText("Enter quantity (or 'back'): "));
            int quantity = toInt(readLineNoClear());
            if (quantity <= 0 || quantity > MAX_QUANTITY) {
                centerPrint("Invalid quantity. Enter a whole number from 1 to 9999.");
                continue;
            }

            System.out.print(centerText("Enter the due date (MM/DD/YYYY, or 'back'): "));
            String dueInput = readLineNoClear().trim();
            LocalDate dueDate;
            try {
                dueDate = LocalDate.parse(dueInput, fmt);
            } catch (DateTimeParseException e) {
                centerPrint("Invalid date. Please use MM/DD/YYYY.");
                continue;
            }

            long daysOverdue = ChronoUnit.DAYS.between(dueDate, todayDate);
            if (daysOverdue <= 0) {
                centerPrint("Not overdue yet (due " + dueDate.format(fmt) + "). Not added.");
                continue;
            }

            overdueQueue.insert(new OverdueItem(studentId, item.getItemName(), quantity, (int) daysOverdue));
            centerPrint("Added: " + item.getItemName() + ", " + daysOverdue + " day(s) overdue.");
        }

        clearScreen();
        centerPrint("Overdue Items (most urgent first):");
        if (overdueQueue.isEmpty()) {
            centerPrint("No overdue items entered.");
            return;
        }
        while (!overdueQueue.isEmpty()) {
            centerPrint(overdueQueue.extractMin().toString());
        }
    }
}