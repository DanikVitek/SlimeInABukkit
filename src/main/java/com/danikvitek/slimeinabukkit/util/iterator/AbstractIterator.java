package com.danikvitek.slimeinabukkit.util.iterator;

import java.util.NoSuchElementException;

public abstract class AbstractIterator<T> implements Iterator<T> {
    protected abstract T getNext();

    @Override
    public final T next() {
        if (!hasNext()) {
            throw new NoSuchElementException("next() on empty iterator");
        }
        return getNext();
    }
}
