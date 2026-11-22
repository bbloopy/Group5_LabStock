import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class DataStore {

    private static final String INVENTORY_FILE = "inventory.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private static final String WAITING_FILE = "waitinglist.txt";
    private static final String STUDENTS_FILE = "students.txt";
    private static final String RECEIPTS_FILE = "receipts.txt";

    private static String clean(String s) {
        return s.replace("|", "/");
    }

    // Item names inside the encoded item list also can't contain ';' or ':',
    // since those are the separators used for that list specifically.
    private static String cleanItemName(String s) {
        return clean(s).replace(";", ",").replace(":", "-");
    }

    private static String encodeItems(MyLinkedList<ReceiptLine> items) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(";");
            }
            ReceiptLine line = items.get(i);
            sb.append(cleanItemName(line.getItemName())).append(":").append(line.getQuantity());
        }
        return sb.toString();
    }

    private static MyLinkedList<ReceiptLine> decodeItems(String encoded) {
        MyLinkedList<ReceiptLine> items = new MyLinkedList<>();
        if (encoded.equals("")) {
            return items;
        }
        String[] parts = encoded.split(";");
        for (int i = 0; i < parts.length; i++) {
            String[] kv = parts[i].split(":");
            if (kv.length == 2) {
                items.add(new ReceiptLine(kv[0], Integer.parseInt(kv[1].trim())));
            }
        }
        return items;
    }

    public static void saveInventory(InventoryManager inventory) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(INVENTORY_FILE));
            InventoryItem[] all = inventory.getAll();
            for (int i = 0; i < all.length; i++) {
                InventoryItem item = all[i];
                out.println(item.getItemId() + "|" + clean(item.getItemName()) + "|" + clean(item.getCategory()) + "|"
                        + clean(item.getHazardLabel()) + "|" + item.getQuantity());
            }
            out.close();
        } catch (Exception e) {
            System.out.println("Could not save inventory.");
        }
    }

    public static void loadInventory(InventoryManager inventory) {
        File file = new File(INVENTORY_FILE);
        if (!file.exists()) {
            return;
        }
        try {
            Scanner in = new Scanner(file);
            while (in.hasNextLine()) {
                String[] p = in.nextLine().split("\\|");
                if (p.length == 5) {
                    inventory.restoreItem(Integer.parseInt(p[0].trim()), p[1], p[2], p[3],
                            Integer.parseInt(p[4].trim()));
                } else if (p.length == 4) {
                    inventory.insertItem(p[0], p[1], p[2], Integer.parseInt(p[3].trim()));
                }
            }
            in.close();
        } catch (Exception e) {
            System.out.println("Could not load inventory.");
        }
    }

    public static void saveTransactions(TransactionHistory history) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(TRANSACTIONS_FILE));
            MyLinkedList<Transaction> all = history.getAll();
            for (int i = 0; i < all.size(); i++) {
                Transaction t = all.get(i);
                out.println(t.getTransactionId() + "|" + clean(t.getStudentId()) + "|"
                        + clean(t.getStudentName()) + "|" + encodeItems(t.getItems()) + "|"
                        + clean(t.getType()) + "|" + clean(t.getDate()) + "|"
                        + clean(t.getTime()) + "|" + clean(t.getPurpose()) + "|" + clean(t.getStatus()) + "|"
                        + clean(t.getCourseId()) + "|" + clean(t.getSection()));
            }
            out.close();
        } catch (Exception e) {
            System.out.println("Could not save transactions.");
        }
    }

    public static void loadTransactions(TransactionHistory history) {
        File file = new File(TRANSACTIONS_FILE);
        if (!file.exists()) {
            return;
        }
        try {
            Scanner in = new Scanner(file);
            while (in.hasNextLine()) {
                String[] p = in.nextLine().split("\\|", -1);
                if (p.length == 9 || p.length == 11) {
                    Transaction t = new Transaction(Integer.parseInt(p[0].trim()), p[1], p[2], decodeItems(p[3]),
                            p[4], p[5], p[6], p[7]);
                    t.setStatus(p[8]);
                    if (p.length == 11) {
                        t.setCourseId(p[9]);
                        t.setSection(p[10]);
                    }
                    history.restore(t);
                }
            }
            in.close();
        } catch (Exception e) {
            System.out.println("Could not load transactions.");
        }
    }

    public static void saveWaitingList(MyQueue<WaitingListEntry> waitingList) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(WAITING_FILE));
            for (int i = 0; i < waitingList.size(); i++) {
                WaitingListEntry e = waitingList.get(i);
                out.println(clean(e.getStudentId()) + "|" + clean(e.getItemName()) + "|" + e.getQuantity());
            }
            out.close();
        } catch (Exception e) {
            System.out.println("Could not save waiting list.");
        }
    }

    public static void loadWaitingList(MyQueue<WaitingListEntry> waitingList) {
        File file = new File(WAITING_FILE);
        if (!file.exists()) {
            return;
        }
        try {
            Scanner in = new Scanner(file);
            while (in.hasNextLine()) {
                String[] p = in.nextLine().split("\\|");
                if (p.length == 3) {
                    waitingList.enqueue(new WaitingListEntry(p[0], p[1], Integer.parseInt(p[2].trim())));
                }
            }
            in.close();
        } catch (Exception e) {
            System.out.println("Could not load waiting list.");
        }
    }

    public static MyLinkedList<String> getStudents() {
        MyLinkedList<String> students = new MyLinkedList<>();
        File file = new File(STUDENTS_FILE);
        if (!file.exists()) {
            return students;
        }
        try {
            Scanner in = new Scanner(file);
            while (in.hasNextLine()) {
                String line = in.nextLine().trim();
                if (!line.equals("")) {
                    students.add(line);
                }
            }
            in.close();
        } catch (Exception e) {
            System.out.println("Could not load students.");
        }
        return students;
    }

    public static void addStudent(String studentId) {
        MyLinkedList<String> students = getStudents();
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).equals(studentId)) {
                return;
            }
        }
        try {
            PrintWriter out = new PrintWriter(new FileWriter(STUDENTS_FILE, true));
            out.println(studentId);
            out.close();
        } catch (Exception e) {
            System.out.println("Could not save student.");
        }
    }

    public static void appendReceipt(String receipt) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(RECEIPTS_FILE, true));
            out.println(receipt);
            out.println();
            out.close();
        } catch (Exception e) {
            System.out.println("Could not save receipt.");
        }
    }
}