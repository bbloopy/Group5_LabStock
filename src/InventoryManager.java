public class InventoryManager {

    private InventoryItem[] items;
    private int size;
    private MyHashTable<String, Integer> nameIndex;
    private MyHashTable<Integer, Integer> idIndex;
    private int nextItemId;

    public InventoryManager() {
        items = new InventoryItem[10];
        size = 0;
        nameIndex = new MyHashTable<>();
        idIndex = new MyHashTable<>();
        nextItemId = 1;
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

        InventoryItem newItem = new InventoryItem(nextItemId, itemName, category, hazardLabel, quantity);
        nextItemId++;
        items[size] = newItem;
        nameIndex.put(itemName, size);
        idIndex.put(newItem.getItemId(), size);
        size++;
        return newItem;
    }

    public void restoreItem(int itemId, String itemName, String category, String hazardLabel, int quantity) {
        if (size == items.length) {
            resize();
        }

        InventoryItem item = new InventoryItem(itemId, itemName, category, hazardLabel, quantity);
        items[size] = item;
        nameIndex.put(itemName, size);
        idIndex.put(itemId, size);
        size++;

        if (itemId >= nextItemId) {
            nextItemId = itemId + 1;
        }
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
        if (item == null || item.getQuantity() < quantity) {
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

    public boolean renameItem(String oldName, String newName) {
        Integer index = nameIndex.get(oldName);
        if (index == null) {
            return false;
        }
        Integer collidingIndex = nameIndex.get(newName);
        if (collidingIndex != null && !collidingIndex.equals(index)) {
            return false;
        }
        InventoryItem item = items[index];
        nameIndex.remove(oldName);
        item.setItemName(newName);
        nameIndex.put(newName, index);
        return true;
    }

    public boolean editCategory(String itemName, String newCategory) {
        InventoryItem item = searchItem(itemName);
        if (item == null) {
            return false;
        }
        item.setCategory(newCategory);
        return true;
    }

    public boolean editHazardLabel(String itemName, String newHazardLabel) {
        InventoryItem item = searchItem(itemName);
        if (item == null) {
            return false;
        }
        item.setHazardLabel(newHazardLabel);
        return true;
    }

    public boolean editQuantity(String itemName, int newQuantity) {
        InventoryItem item = searchItem(itemName);
        if (item == null) {
            return false;
        }
        item.setQuantity(newQuantity);
        return true;
    }

    public InventoryItem searchItem(String itemName) {
        Integer index = nameIndex.get(itemName);
        if (index == null) {
            return null;
        }
        return items[index];
    }

    public InventoryItem searchItemById(int itemId) {
        Integer index = idIndex.get(itemId);
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
        for (int i = 1; i < size; i++) {
            InventoryItem key = items[i];
            int j = i - 1;
            while (j >= 0 && items[j].getCategory().compareTo(key.getCategory()) > 0) {
                items[j + 1] = items[j];
                j--;
            }
            items[j + 1] = key;
        }
        rebuildIndex();
    }

    public void sortByName() {
        for (int i = 1; i < size; i++) {
            InventoryItem key = items[i];
            int j = i - 1;
            while (j >= 0 && items[j].getItemName().compareTo(key.getItemName()) > 0) {
                items[j + 1] = items[j];
                j--;
            }
            items[j + 1] = key;
        }
        rebuildIndex();
    }

    public void sortById() {
        for (int i = 1; i < size; i++) {
            InventoryItem key = items[i];
            int j = i - 1;
            while (j >= 0 && items[j].getItemId() > key.getItemId()) {
                items[j + 1] = items[j];
                j--;
            }
            items[j + 1] = key;
        }
        rebuildIndex();
    }

    private void rebuildIndex() {
        for (int i = 0; i < size; i++) {
            nameIndex.put(items[i].getItemName(), i);
            idIndex.put(items[i].getItemId(), i);
        }
    }

    private void removeAt(int index) {
        String removedName = items[index].getItemName();
        int removedId = items[index].getItemId();

        // Shift items left (instead of swapping in the last item) so the current sort order is kept.
        for (int i = index; i < size - 1; i++) {
            items[i] = items[i + 1];
        }
        items[size - 1] = null;
        size--;

        nameIndex.remove(removedName);
        idIndex.remove(removedId);
        rebuildIndex();
    }

    private void resize() {
        InventoryItem[] bigger = new InventoryItem[items.length * 2];
        for (int i = 0; i < items.length; i++) {
            bigger[i] = items[i];
        }
        items = bigger;
    }
}
