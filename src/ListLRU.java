/**
 * The ListLRU class for implementing a Least Recently Used (LRU) list.
 *
 * @param <T>
 *            the type of data stored in the list
 * @author Shubham Laxmikant Deshmukh
 * @version v1
 */
public class ListLRU<T> {

    private static class Node<T> {
        private Node<T> prev;
        private Node<T> next;
        private T buffData;

        public Node<T> prev() {
            return prev;
        }


        public Node<T> next() {
            return next;
        }


        public Node(T data) {
            buffData = data;
        }


        public void makePrev(Node<T> node) {
            prev = node;
        }


        public void makeNex(Node<T> node) {
            next = node;
        }


        public T getBuffData() {
            return buffData;
        }

    }

    private int size;
    private int maxsize;
    private Node<T> head;
    private Node<T> tail;

    /**
     * Constructor for the ListLRU class.
     *
     * @param maxSize
     *            the maximum size of the LRU list
     */
    public ListLRU(int maxSize) {
        initialize(maxSize);
    }


    private void initialize(int inpSize) {
        size = 0;
        maxsize = inpSize;
        head = new ListLRU.Node<T>(null);
        tail = new ListLRU.Node<T>(null);
        head.makeNex(tail);
        tail.makePrev(head);

    }


    /**
     * Get the current size of the LRU list.
     *
     * @return the current size of the list
     */
    public int size() {
        return size;
    }


    /**
     * Get the data at the specified index in the LRU list.
     *
     * @param ind
     *            the index to retrieve data from
     * @return the data at the specified index
     */
    public T get(int ind) {
        return getFirst(ind).getBuffData();
    }


    /**
     * Clear the LRU list.
     */
    public void clearing() {
        initialize(size);
    }


    /**
     * Add an element to the front of the LRU list.
     *
     * @param obj
     *            the element to add
     */
    public void addition(T obj) {
        Node<T> add = new Node<T>(obj);
        add.makeNex(head.next());
        add.makePrev(head);
        head.makeNex(add);
        head.next.makePrev(add);
        size++;
    }


    private Node<T> getFirst(int ind) {
        Node<T> curr = head.next();
        for (int i = 0; i < ind; i++) {
            curr = curr.next();
        }
        return curr;
    }


    /**
     * Push an element to the front of the LRU list at a specified index.
     *
     * @param ind
     *            the index of the element to push to the front
     */
    public void pushFront(int ind) {
        Node<T> curr = getFirst(ind);
        curr.prev.makeNex(curr.next);
        curr.next.makePrev(curr.prev);
        curr.makePrev(head);
        curr.makeNex(head.next);
    }


    /**
     * Check if the LRU list is full.
     *
     * @return true if the list is full, false otherwise
     */
    public boolean isFull() {
        return size == maxsize;
    }


    /**
     * Remove and return the element at the end of the LRU list.
     *
     * @return the element at the end of the list
     */
    public T removeLast() {
        Node<T> nodeBeRemoved = getFirst(size - 1);
        nodeBeRemoved.prev().makeNex(nodeBeRemoved.next());
        nodeBeRemoved.next().makePrev(nodeBeRemoved.prev());
        size--;
        return nodeBeRemoved.getBuffData();
    }

}
