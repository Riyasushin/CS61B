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

    public ArrayDeque(int capacity) {
        CAPACITY = capacity;
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


        if (size() + 2 == CAPACITY) {
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
        if (!(b instanceof Deque<?>)) {
            return false;
        }
        /// should be Deque, as compare LLD with AD
        Deque<T> other = (Deque<T>) b;
        if (size() == other.size()) {
            for (int i = 0, len = size(); i < len; ++i) {
                if (!get(i).equals(other.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void printDeque() {
        System.out.println(toString());
    }

    /**
     * 创建一个包含指定元素的 ArrayDeque 实例。
     *
     * @param <T>  元素的类型
     * @param args 要添加到 ArrayDeque 中的元素
     * @return 包含指定元素的 ArrayDeque 实例
     */
    public static <T> ArrayDeque<T> of(T... args) {
        // 创建一个新的 ArrayDeque 实例
        ArrayDeque<T> deque = new ArrayDeque<>();

        // 如果传入的参数不为空，则遍历参数并添加到 deque 中
        if (args != null) {
            for (T item : args) {
                deque.addLast(item);
            }
        }

        // 返回填充好元素的 ArrayDeque 实例
        return deque;
    }

}
