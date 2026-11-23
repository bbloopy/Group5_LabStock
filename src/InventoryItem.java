public class InventoryItem {

    private int itemId;
    private String itemName;
    private String category;
    private String hazardLabel;
    private int quantity;

    public InventoryItem(int itemId, String itemName, String category, String hazardLabel, int quantity) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.category = category;
        this.hazardLabel = hazardLabel;
        this.quantity = quantity;
    }

    public int getItemId() {
        return itemId;
    }

    public String getFormattedId() {
        String num = "" + itemId;
        while (num.length() < 3) {
            num = "0" + num;
        }
        return "ITM-" + num;
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

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setHazardLabel(String hazardLabel) {
        this.hazardLabel = hazardLabel;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getFormattedId());
        sb.append(" | ");
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