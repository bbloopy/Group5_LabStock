public class ReceiptGenerator {
    public static String generate(Transaction t) {

        String number = "" + t.getTransactionId();
        while (number.length() < 6) {
            number = "0" + number;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================").append("\n");
        sb.append("   CHEMISTRY EQUIPMENT BORROWING SYSTEM\n");
        sb.append("              OFFICIAL RECEIPT\n");
        sb.append("==========================================").append("\n");
        sb.append("Receipt No. : R-").append(number).append("\n");
        sb.append("Date        : ").append(t.getDate()).append("\n");
        sb.append("Student ID  : ").append(t.getStudentId()).append("\n");
        sb.append("Type        : ").append(t.getType()).append("\n");
        sb.append("------------------------------------------").append("\n");
        sb.append("Item        : ").append(t.getItemName()).append("\n");
        sb.append("Quantity    : ").append(t.getQuantity()).append("\n");
        sb.append("==========================================");
        return sb.toString();
    }

    public static String generateStatement(String studentId, MyLinkedList<Transaction> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================").append("\n");
        sb.append("Receipts for Student ").append(studentId).append("\n");
        sb.append("==========================================").append("\n");
        if (list.isEmpty()) {
            sb.append("No transactions found.");
            return sb.toString();
        }
        for (int i = 0; i < list.size(); i++) {
            sb.append(generate(list.get(i))).append("\n\n");
        }
        sb.append("Total transactions: ").append(list.size());
        return sb.toString();
    }
}