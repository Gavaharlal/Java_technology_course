package ru.ifmo.rain.dolgikh.arrayset;

import java.util.*;

public class ArraySet<T> extends AbstractSet<T> implements SortedSet<T> {

    private final List<T> mData;
    private final Comparator<? super T> mComparator;

    public ArraySet() {
        mComparator = null;
        mData = Collections.emptyList();
    }

    public ArraySet(Collection<? extends T> other) {
        this(other, null);
    }

    public ArraySet(Collection<? extends T> other, Comparator<? super T> cmp) {
        mComparator = cmp;
        Set<T> tmp = new TreeSet<>(cmp);
        tmp.addAll(other);
        mData = new ArrayList<>(tmp);
    }

    private ArraySet(List<T> data, Comparator<? super T> comparator) {
        this.mData = data;
        this.mComparator = comparator;
    }

    @Override
    public int size() {
        return mData.size();
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(mData).iterator();
    }

    @Override
    public boolean contains(Object o) {
        return Collections.binarySearch(mData, (T) o, mComparator) >= 0;
    }

    private int getIndexOfElement(T t, int ifContains, int ifNotContains) {
        int res = Collections.binarySearch(mData, t, mComparator);
        if (res < 0) {
            res = -res - 1;
            return res - ifNotContains;
        }
        return res - ifContains;
    }

    private int lowerIndex(T t) {
        return getIndexOfElement(t, 1, 1);
    }


    private int floorIndex(T t) {
        return getIndexOfElement(t, 0, 1);
    }

    private int ceilingIndex(T t) {
        return getIndexOfElement(t, 0, 0);
    }

    @Override
    public T first() {
        emptyChecker();
        return mData.get(0);
    }

    @Override
    public T last() {
        emptyChecker();
        return mData.get(size() - 1);
    }

    private void emptyChecker() {
        if (mData.isEmpty()) {
            throw new NoSuchElementException();
        }
    }

    private SortedSet<T> subSet(T fromElement, T toElement, boolean includingLast) {
        int l = ceilingIndex(fromElement);
        int r;
        if (includingLast) {
            r = floorIndex(toElement);
        } else {
            r = lowerIndex(toElement);
        }
        return new ArraySet<>(mData.subList(l, r + 1), mComparator);
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return subSet(fromElement, toElement, false);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        if (mData.isEmpty()) {
            return new ArraySet<>(Collections.emptyList(), mComparator);
        }
        return subSet(first(), toElement, false);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        if (mData.isEmpty()) {
            return new ArraySet<>(Collections.emptyList(), mComparator);
        }
        return subSet(fromElement, last(), true);
    }

    @Override
    public Comparator<? super T> comparator() {
        return mComparator;
    }
}