package bearmaps.proj2ab;

import java.util.HashMap;
import java.util.NoSuchElementException;


public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T>{

    private HeapNode[] minHeap =  new ArrayHeapMinPQ.HeapNode[8];
    private HashMap<T, Integer> itemIndexMap = new HashMap<>();

    private class HeapNode {
        T item;
        double priority;

        public HeapNode(T item, double priority) {
            this.item = item;
            this.priority = priority;
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != this.getClass()) return false;
            HeapNode other = (HeapNode) o;
            if (this.item.equals(other.item) && this.priority == other.priority) return true;
            else return false;
        }
    }

    @Override
    public void add(T item, double priority) {
        if (contains(item)) {
            throw new IllegalArgumentException("Item already exists");
        }

        if (size() == minHeap.length) resize(minHeap.length * 2);

        int index = size();
        itemIndexMap.put(item, index);
        minHeap[size()-1] = new HeapNode(item, priority);
        swim(index);
    }

    private void swim (int index) {
        int parent = (index-1)/2;
        if (index == 0 || minHeap[index].priority >= minHeap[parent].priority) {
            return;
        }
        swap(index, parent);
        swim(parent);
    }

    private void swap (int index, int parent) {
        itemIndexMap.put(minHeap[index].item, parent);
        itemIndexMap.put(minHeap[parent].item, index);
        HeapNode temp = minHeap[parent];
        minHeap[parent] = minHeap[index];
        minHeap[index] = temp;
    }

    private void resize(int newN) {
        int size = size();
        HeapNode[] newHeap = new ArrayHeapMinPQ.HeapNode[newN];
        System.arraycopy(minHeap, 0, newHeap, 0, size);
        minHeap = newHeap;
    }

    @Override
    public boolean contains(T item) {
        return itemIndexMap.containsKey(item);
    }

    @Override
    public T getSmallest() {
        if (size() == 0) {
            throw new NoSuchElementException("Inquiring from an empty heap");
        }
        return minHeap[0].item;
    }

    @Override
    public T removeSmallest() {
        if (size() == 0) {
            throw new NoSuchElementException("Cannot remove from an empty heap");
        }

        HeapNode smallestNode = minHeap[0];
        minHeap[0] = minHeap[size()-1];
        minHeap[size()-1] = null;
        itemIndexMap.remove(smallestNode.item);
        sink(0);

        if (minHeap.length > 8 && size() <= (minHeap.length/2)) {
            resize(minHeap.length/2);
        }

        return smallestNode.item;
    }

    private void sink(int index) {

        int left = 2*index+1;
        int right = 2*index+2;
        if (left >= size()) return;
        if (right >= size()) {
            if (minHeap[left].priority < minHeap[index].priority) {
                swap(left, index);
            }
            return;
        } else {
            if (minHeap[left].priority < minHeap[index].priority &&
                    minHeap[left].priority <= minHeap[right].priority) {
                swap(left, index);
                sink(left);
            } else if (minHeap[right].priority < minHeap[index].priority &&
                    minHeap[right].priority <= minHeap[left].priority) {
                swap(right, index);
                sink(right);
            }
        }
    }

    @Override
    public int size() {
        return itemIndexMap.size();
    }

    @Override
    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException("This item does not exist");
        }

        int index = itemIndexMap.get(item);
        minHeap[index].priority = priority;

        swim(index);
        sink(index);
    }
}
