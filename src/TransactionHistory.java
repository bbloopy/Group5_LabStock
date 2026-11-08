/**
 * Stores every borrow/return transaction in a linked list (oldest first).
 */
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
}