public class OverdueItem implements Comparable<OverdueItem> {

    private String studentId;
    private String itemName;
    private int quantity;
    private int daysOverdue;

    public OverdueItem(String studentId, String itemName, int quantity, int daysOverdue) {
        this.studentId = studentId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.daysOverdue = daysOverdue;
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

    public int getDaysOverdue() {
        return daysOverdue;
    }

    @Override
    public int compareTo(OverdueItem other) {
        return other.daysOverdue - this.daysOverdue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(studentId);
        sb.append(" - ");
        sb.append(itemName);
        sb.append(" x");
        sb.append(quantity);
        sb.append(" (");
        sb.append(daysOverdue);
        sb.append(" days overdue)");
        return sb.toString();
    }
}