public class TransactionHistory {

    private MyLinkedList<Transaction> transactions = new MyLinkedList<>();
    private int nextId = 1;

    public Transaction record(String studentId, String itemName, int quantity,
                              String type, String date) {
        Transaction t = new Transaction(nextId, studentId, itemName, quantity, type, date);
        nextId++;
        transactions.add(t);
        return t;
    }

    public void restore(Transaction t) {
        transactions.add(t);
        if (t.getTransactionId() >= nextId) {
            nextId = t.getTransactionId() + 1;
        }
    }

    public int size() {
        return transactions.size();
    }

    public boolean isEmpty() {
        return transactions.isEmpty();
    }

    public MyLinkedList<Transaction> getAll() {
        return transactions;
    }

    public MyLinkedList<Transaction> getByStudent(String studentId) {
        MyLinkedList<Transaction> result = new MyLinkedList<>();
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            if (t.getStudentId().equals(studentId)) {
                result.add(t);
            }
        }
        return result;
    }

    public Transaction findById(int transactionId) {
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            if (t.getTransactionId() == transactionId) {
                return t;
            }
        }
        return null;
    }

    // Quick test
    public static void main(String[] args) {
        TransactionHistory history = new TransactionHistory();


        history.record("11111111", "Beaker 250mL", 2, "BORROW", "11/08/2026");
        history.record("22222222", "Flask", 1, "BORROW", "11/08/2026");
        history.record("11111111", "Beaker 250mL", 2, "RETURN", "11/09/2026");

        System.out.println("=== All transactions ===");
        for (int i = 0; i < history.size(); i++) {
            System.out.println(history.getAll().get(i));
        }


        System.out.println();
        System.out.println("=== Checks (actual vs expected) ===");
        System.out.println("Total transactions  : " + history.size() + " (expected 3)");
        System.out.println("Student 11111111    : " + history.getByStudent("11111111").size() + " (expected 2)");
        System.out.println("Student 22222222    : " + history.getByStudent("22222222").size() + " (expected 1)");
        System.out.println("Student 99999999    : " + history.getByStudent("99999999").size() + " (expected 0)");
        System.out.println("Transaction #2 item : " + history.findById(2).getItemName() + " (expected Flask)");
        System.out.println("Transaction #99     : " + history.findById(99) + " (expected null)");

        System.out.println();
        System.out.println("=== Transaction #1 ===");
        System.out.println(ReceiptGenerator.generate(history.findById(1)));
    }
}