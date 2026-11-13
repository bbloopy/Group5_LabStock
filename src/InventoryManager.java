public class InventoryManager {

    private InventoryItem[] items;
    private int size;
    private MyHashTable<String, Integer> nameIndex;

    public InventoryManager() {
        items = new InventoryItem[10];
        size = 0;
        nameIndex = new MyHashTable<>();
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public InventoryItem insertItem(String itemName, String category, String hazardLabel, int quantity) {
        Integer index = nameIndex.get(itemName);

        if (index != null) {
            InventoryItem existing = items[index];
            existing.addQuantity(quantity);
            return existing;
        }

        if (size == items.length) {
            resize();
        }

        InventoryItem newItem = new InventoryItem(itemName, category, hazardLabel, quantity);
        items[size] = newItem;
        nameIndex.put(itemName, size);
        size++;
        return newItem;
    }

    public boolean removeItem(String itemName, int quantity) {
        Integer index = nameIndex.get(itemName);
        if (index == null) {
            return false;
        }

        InventoryItem item = items[index];
        if (quantity >= item.getQuantity()) {
            removeAt(index);
        } else {
            item.addQuantity(-quantity);
        }
        return true;
    }

    public InventoryItem searchItem(String itemName) {
        Integer index = nameIndex.get(itemName);
        if (index == null) {
            return null;
        }
        return items[index];
    }

    public InventoryItem[] getAll() {
        InventoryItem[] result = new InventoryItem[size];
        for (int i = 0; i < size; i++) {
            result[i] = items[i];
        }
        return result;
    }

    public void sortByCategory() {
        System.out.println("Sort Inventory feature coming soon.");
    }

    private void removeAt(int index) {
        String removedName = items[index].getItemName();
        int lastIndex = size - 1;

        if (index != lastIndex) {
            InventoryItem lastItem = items[lastIndex];
            items[index] = lastItem;
            nameIndex.put(lastItem.getItemName(), index);
        }

        items[lastIndex] = null;
        size--;
        nameIndex.remove(removedName);
    }

    private void resize() {
        InventoryItem[] bigger = new InventoryItem[items.length * 2];
        for (int i = 0; i < items.length; i++) {
            bigger[i] = items[i];
        }
        items = bigger;
    }
}