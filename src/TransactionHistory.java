public class TransactionHistory {

    private MyLinkedList<Transaction> transactions = new MyLinkedList<>();
    private int nextId = 1;

    public Transaction record(String studentId, String studentName, MyLinkedList<ReceiptLine> items,
                              String type, String date, String time, String purpose) {
        Transaction t = new Transaction(nextId, studentId, studentName, items, type, date, time, purpose);
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

    public MyLinkedList<Transaction> getPending() {
        MyLinkedList<Transaction> result = new MyLinkedList<>();
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            if (t.getStatus().equals("PENDING")) {
                result.add(t);
            }
        }
        return result;
    }

    public MyLinkedList<Transaction> getPendingByStudent(String studentId) {
        MyLinkedList<Transaction> result = new MyLinkedList<>();
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            if (t.getStudentId().equals(studentId) && t.getStatus().equals("PENDING")) {
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