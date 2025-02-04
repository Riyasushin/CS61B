package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private T[] items;
    private int head, tail;
    private int CAPACITY;

    private static final int INIT_CAPACITY = 8;
    private static final double MIN_USAGE = 0.25;

    private int size;

    /**
     * Creates an empty array deque.
     */
    public ArrayDeque() {
        CAPACITY = INIT_CAPACITY;
        items = (T[]) new Object[CAPACITY];
        size = 0;
        head = CAPACITY - 1;
        tail = 0;
    }

    private void resize(int newCapacity) {
        T[] newItems = (T[]) new Object[newCapacity];
        int pointer = (head + 1) % CAPACITY;
        int index = 0;
        while (pointer != tail) {
            newItems[index] = items[pointer];
            pointer = (pointer + 1) % CAPACITY;
            index += 1;
        }

        head = newCapacity - 1;
        tail = index;

        items = newItems;
        CAPACITY = newCapacity;
    }

    public void addFirst(T x) {
        if (size() + 1 == CAPACITY) {
            resize(CAPACITY * 2);
        }

        size += 1;
        items[head] = x;
        head = (head - 1 + CAPACITY) % CAPACITY;
    }

    public void addLast(T x) {
        if (size() + 2 == CAPACITY) {
            resize(CAPACITY * 2);
        }

        size += 1;
        items[tail] = x;
        tail = (tail + 1 + CAPACITY) % CAPACITY;
    }

    public T removeFirst() {
        if (size() == 0) {
            return null;
        }
        head = (head + 1) % CAPACITY;
        T data = items[head];
        size -= 1;

        if (CAPACITY > 25 && ((double) CAPACITY * MIN_USAGE) > size()) {
            int newLen = (int) (CAPACITY * (2 * MIN_USAGE));
            resize(newLen);
        }

        return data;
    }

    public T removeLast() {
        if (size() == 0) {
            return null;
        }
        tail = (tail - 1 + CAPACITY) % CAPACITY;
        /// 忘记改了，cv的锅
        T data = items[tail];
        size -= 1;

        if (CAPACITY > 25 && ((double) CAPACITY * MIN_USAGE) > size()) {
            int newLen = (int) (CAPACITY * (2 * MIN_USAGE));
            resize(newLen);
        }

        return data;
    }

    public T get(int index) {
        if (size() > index) {
            return items[(head + 1 + index + CAPACITY) % CAPACITY];
        } else {
            return null;
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    private class MyIterator implements Iterator<T> {
        private int curIndex = 0;

        @Override
        public boolean hasNext() {
            return size() > curIndex;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            } else {
                curIndex += 1;
                return get(curIndex - 1);
            }
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new MyIterator();
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

    public void printDeque() {
        System.out.println(toString());
    }

    public ArrayDeque<T> of(T...args) {
        ArrayDeque<T> tmp = new ArrayDeque<>();
        for (T item : args) {
            tmp.addLast(item);
        }
        return tmp;
    }
}
