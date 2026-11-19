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

    public boolean borrowItem(String itemName, int quantity) {
        InventoryItem item = searchItem(itemName);
        if (item == null || quantity > item.getQuantity()) {
            return false;
        }
        item.addQuantity(-quantity);
        return true;
    }

    public boolean returnItem(String itemName, int quantity) {
        InventoryItem item = searchItem(itemName);
        if (item == null) {
            return false;
        }
        item.addQuantity(quantity);
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

    public void sortByName() {
        sort(true);
    }

    public void sortByCategory() {
        sort(false);
    }

    private void sort(boolean byName) {
        for (int i = 1; i < size; i++) {
            InventoryItem key = items[i];
            int j = i - 1;
            while (j >= 0 && compareItems(items[j], key, byName) > 0) {
                items[j + 1] = items[j];
                j--;
            }
            items[j + 1] = key;
        }

        for (int i = 0; i < size; i++) {
            nameIndex.put(items[i].getItemName(), i);
        }
    }

    private int compareItems(InventoryItem a, InventoryItem b, boolean byName) {
        if (byName) {
            return a.getItemName().compareToIgnoreCase(b.getItemName());
        }
        int result = a.getCategory().compareToIgnoreCase(b.getCategory());
        if (result == 0) {
            result = a.getItemName().compareToIgnoreCase(b.getItemName());
        }
        return result;
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