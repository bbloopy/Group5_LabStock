public class InventoryItem {

    private String itemName;
    private String category;
    private String hazardLabel;
    private int quantity;

    public InventoryItem(String itemName, String category, String hazardLabel, int quantity) {
        this.itemName = itemName;
        this.category = category;
        this.hazardLabel = hazardLabel;
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public String getCategory() {
        return category;
    }

    public String getHazardLabel() {
        return hazardLabel;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(itemName);
        sb.append(" | ");
        sb.append(category);
        sb.append(" | Hazard: ");
        sb.append(hazardLabel);
        sb.append(" | Qty: ");
        sb.append(quantity);
        return sb.toString();
    }
}