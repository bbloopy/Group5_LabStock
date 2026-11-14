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

    public static void saveInventory(InventoryManager inventory) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(INVENTORY_FILE));
            InventoryItem[] all = inventory.getAll();
            for (int i = 0; i < all.length; i++) {
                InventoryItem item = all[i];
                out.println(clean(item.getItemName()) + "|" + clean(item.getCategory()) + "|"
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
                if (p.length == 4) {
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
                        + clean(t.getItemName()) + "|" + t.getQuantity() + "|"
                        + clean(t.getType()) + "|" + clean(t.getDate()));
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
                String[] p = in.nextLine().split("\\|");
                if (p.length == 6) {
                    history.restore(new Transaction(Integer.parseInt(p[0].trim()), p[1], p[2],
                            Integer.parseInt(p[3].trim()), p[4], p[5]));
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