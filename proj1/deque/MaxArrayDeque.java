package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> cmp;

    public MaxArrayDeque() {
        super();
        cmp = new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                } else if (o1 == null) {
                    return -1;
                } else if (o2 == null) {
                    return 1;
                } else {
                    return ((Comparable<T>) o1).compareTo(o2);
                }
            }
        };
    }

    public MaxArrayDeque(Comparator<T> c) {
        super();
        cmp = c;
    }

    public T max() {
        return max(cmp);
    }

    public T max(Comparator<T> c) {
        if (size() == 0) {
            return null;
        }
        T res = get(0);
        for (int i = 1, len = size(); i < len; ++i) {
            if (c.compare(res, get(i)) < 0) {
                res = get(i);
            }
        }
        return res;
    }
}
