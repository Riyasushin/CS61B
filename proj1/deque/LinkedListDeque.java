package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {

    private class Node {
        private T item;
        private Node next;
        private Node prev;

        /**
         * New node, with no init.
         */
        Node() {
        }

        /**
         * New node, with x as the data
         * @param x data of this node
         */
        Node(T x) {
            item = x;
        }
    }

    private class MyIterator implements Iterator<T> {
        private Node curr = sentinel.next;

        @Override
        public boolean hasNext() {
            return curr != sentinel;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            } else {
                T data = curr.item;
                curr = curr.next;
                return data;
            }
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new MyIterator();
    }


    private final Node sentinel;
    private int size;

    /**
     * Creates an empty linked list deque.
     */
    public LinkedListDeque() {
        sentinel = new Node();
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    /**
     * Add x as the last elem.
     * @param x the data to add at the tail
     */
    public void addLast(T x) {
        Node tmp = new Node(x);
        tmp.prev = sentinel.prev;
        sentinel.prev.next = tmp;
        sentinel.prev = tmp;
        tmp.next = sentinel;

        size += 1;

    }

    /**
     * Add x as the first elem.
     * @param x the data to add at the head
     */
    public void addFirst(T x) {
        Node tmp = new Node(x);
        sentinel.next.prev = tmp;
        tmp.next = sentinel.next;
        tmp.prev = sentinel;
        sentinel.next = tmp;

        size += 1;
    }

    public boolean isEmpty() {
        return size == 0;
    }

//    public void insert(T x, int pos) {
//
//    }

    /**
     * Helper method for getRecursive()
     * @param index the position of the ordered elem
     * @param pointer the head pointer
     * @return the value of elem in index
     */
    private T getRecur(int index, Node pointer) {
        if (pointer == sentinel) {
            return null;
        } else {
            if (index == 0) {
                return pointer.item;
            } else {
                return getRecur(index - 1, pointer.next);
            }
        }
    }

    public T get(int index) {
        assert index < size();
        Node tmp = getFirst();
        while (index > 0) {
            index -= 1;
            tmp = tmp.next;
        }
        return tmp.item;
    }

    public T getRecursive(int index) {
        assert index < size();
        return getRecur(index, getFirst());
    }

    public int size() {
        return size;
    }

    public T removeFirst() {
        if (size() == 0) {
            return null;
        } else {
            Node tmp = getFirst();
            sentinel.next = tmp.next;
            tmp.next.prev = sentinel;

            size -= 1;
            return tmp.item;
        }
    }

    public T removeLast() {
        if (size() == 0) {
            return null;
        } else {
            Node tmp = getLast();
            tmp.prev.next = tmp.next;
            tmp.next.prev = tmp.prev;

            size -= 1;
            return tmp.item;
        }
    }

    public void printDeque() {
        Node tmp = getFirst();
        boolean first = true;
        while (tmp != sentinel) {
            if (first) {
                first = false;
            } else {
                System.out.print(" ");
            }
            System.out.print(tmp.item);
            tmp = tmp.next;
        }
    }

    private Node getLast() {
        return sentinel.prev;
    }

    private Node getFirst() {
        return sentinel.next;
    }

    @Override
    public boolean equals(Object b) {
        if (b == null) {
            /// 防止之后cast导致NullPointerException
            return false;
        }
        /// 同一地址
        if (this == b) {
            return true;
        }
        if (!(b instanceof LinkedListDeque<?>)) {
            return false;
        }
        LinkedListDeque<T> other = (LinkedListDeque<T>) b;
        if (size() == other.size()) {
            for (int i = 0, len = size(); i < len; ++i) {
                if (get(i).equals(other.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(" ");
        for (int i = 0, len = size(); i < len; ++i) {
            sb.append(get(i));
            sb.append(" ");
        }
        return sb.toString();
    }

}
