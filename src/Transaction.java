public class Transaction {

    private int transactionId;
    private String studentId;
    private String studentName;
    private String itemName;
    private int quantity;
    private String type;
    private String date;
    private String time;
    private String purpose;
    private String status = "PENDING";

    public Transaction(int transactionId, String studentId, String studentName, String itemName,
                       int quantity, String type, String date, String time, String purpose) {
        this.transactionId = transactionId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.itemName = itemName;
        this.quantity = quantity;
        this.type = type;
        this.date = date;
        this.time = time;
        this.purpose = purpose;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
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

    public String getTime() {
        return time;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "#" + transactionId + " | " + date + " " + time + " | " + studentName + " (" + studentId + ") | "
                + itemName + " x" + quantity + " | " + type + " | " + purpose + " | " + status;
    }
}