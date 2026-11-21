public class ReceiptGenerator {

    public static String formatReceiptNumber(int transactionId) {
        String number = "" + transactionId;
        while (number.length() < 6) {
            number = "0" + number;
        }
        return number;
    }

    public static String generate(Transaction t) {
        String number = formatReceiptNumber(t.getTransactionId());

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================").append("\n");
        sb.append("   CHEMISTRY EQUIPMENT BORROWING SYSTEM\n");
        sb.append("              OFFICIAL RECEIPT\n");
        sb.append("==========================================").append("\n");
        sb.append("Receipt No. : R-").append(number).append("\n");
        sb.append("Date        : ").append(t.getDate()).append("\n");
        sb.append("Time        : ").append(t.getTime()).append("\n");
        sb.append("Student Name: ").append(t.getStudentName()).append("\n");
        sb.append("Student No. : ").append(t.getStudentId()).append("\n");
        sb.append("Type        : ").append(t.getType()).append("\n");
        sb.append("Purpose     : ").append(t.getPurpose()).append("\n");
        sb.append("Status      : ").append(t.getStatus()).append("\n");
        sb.append("------------------------------------------").append("\n");
        sb.append("Items:\n");
        MyLinkedList<ReceiptLine> items = t.getItems();
        for (int i = 0; i < items.size(); i++) {
            ReceiptLine line = items.get(i);
            sb.append("  ").append(i + 1).append(". ").append(line.getItemName())
                    .append(" x").append(line.getQuantity()).append("\n");
        }
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