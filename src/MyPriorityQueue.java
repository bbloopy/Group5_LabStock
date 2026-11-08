public class MyPriorityQueue<T extends Comparable<T>> {

    private Object[] heap;
    private int size;

    public MyPriorityQueue() {
        heap = new Object[10];
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void insert(T item) {
        if (size == heap.length) {
            resize();
        }
        heap[size] = item;
        siftUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Priority queue is empty");
        }
        return (T) heap[0];
    }

    @SuppressWarnings("unchecked")
    public T extractMin() {
        if (isEmpty()) {
            throw new IllegalStateException("Priority queue is empty");
        }
        T min = (T) heap[0];
        size--;
        heap[0] = heap[size];
        heap[size] = null;
        if (size > 0) {
            siftDown(0);
        }
        return min;
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (compare(index, parent) < 0) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    private void siftDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < size && compare(left, smallest) < 0) {
                smallest = left;
            }
            if (right < size && compare(right, smallest) < 0) {
                smallest = right;
            }
            if (smallest == index) {
                break;
            }
            swap(index, smallest);
            index = smallest;
        }
    }

    @SuppressWarnings("unchecked")
    private int compare(int i, int j) {
        T a = (T) heap[i];
        T b = (T) heap[j];
        return a.compareTo(b);
    }

    private void swap(int i, int j) {
        Object temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private void resize() {
        Object[] bigger = new Object[heap.length * 2];
        for (int i = 0; i < heap.length; i++) {
            bigger[i] = heap[i];
        }
        heap = bigger;
    }
}