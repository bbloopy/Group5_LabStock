public class Transaction {

    private int transactionId;
    private String studentId;
    private String itemName;
    private int quantity;
    private String type;
    private String date;

    public Transaction(int transactionId, String studentId, String itemName,
                       int quantity, String type, String date) {
        this.transactionId = transactionId;
        this.studentId = studentId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.type = type;
        this.date = date;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "#" + transactionId + " | " + date + " | " + studentId + " | "
                + itemName + " x" + quantity + " | " + type;
    }
}