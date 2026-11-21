public class Transaction {

    private int transactionId;
    private String studentId;
    private String studentName;
    private MyLinkedList<ReceiptLine> items;
    private String type;
    private String date;
    private String time;
    private String purpose;
    private String status = "PENDING";

    public Transaction(int transactionId, String studentId, String studentName, MyLinkedList<ReceiptLine> items,
                       String type, String date, String time, String purpose) {
        this.transactionId = transactionId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.items = items;
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

    public MyLinkedList<ReceiptLine> getItems() {
        return items;
    }

    public void addItem(String itemName, int quantity) {
        items.add(new ReceiptLine(itemName, quantity));
    }

    public void removeItem(int index) {
        items.remove(index);
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("#").append(transactionId).append(" | ").append(date).append(" ").append(time)
                .append(" | ").append(studentName).append(" (").append(studentId).append(") | ");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(items.get(i));
        }
        sb.append(" | ").append(type).append(" | ").append(purpose).append(" | ").append(status);
        return sb.toString();
    }
}