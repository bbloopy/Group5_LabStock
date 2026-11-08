public class WaitingListEntry {

    private String studentId;
    private String itemName;
    private int quantity;

    public WaitingListEntry(String studentId, String itemName, int quantity) {
        this.studentId = studentId;
        this.itemName = itemName;
        this.quantity = quantity;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(studentId);
        sb.append(" - ");
        sb.append(itemName);
        sb.append(" x");
        sb.append(quantity);
        return sb.toString();
    }
}