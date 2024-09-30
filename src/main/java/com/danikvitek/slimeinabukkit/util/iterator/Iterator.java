package com.danikvitek.slimeinabukkit.util.iterator;

import com.danikvitek.slimeinabukkit.util.Option;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Iterator<T> extends java.util.Iterator<T> {

    @Contract(value = "_ -> new", pure = true)
    static <T> @NotNull Iterator<T> of(T element) {
        return new AbstractIterator<>() {

            boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public T getNext() {
                hasNext = false;
                return element;
            }
        };
    }

    @Contract(value = "_ -> new", pure = true)
    @SafeVarargs
    static <T> @NotNull Iterator<T> of(T... elements) {
        return new AbstractIterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < elements.length;
            }

            @Override
            public T getNext() {
                return elements[index++];
            }
        };
    }

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    static <T> Iterator<T> empty() {
        return (Iterator<T>) EmptyIterator.INSTANCE;
    }

    default boolean isEmpty() {
        return !hasNext();
    }

    default @NotNull Iterator<@NotNull Indexed<T>> zipWithIndex() {
        if (isEmpty()) {
            return empty();
        }
        return new AbstractIterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return Iterator.this.hasNext();
            }

            @Override
            public Indexed<T> getNext() {
                return new Indexed<>(index++, Iterator.this.next());
            }
        };
    }

    default @NotNull Iterator<T> filter(@NotNull Predicate<? super T> predicate) {
        if (isEmpty()) {
            return empty();
        }
        return new AbstractIterator<>() {
            private Option<T> next = Option.none();

            @Override
            @SuppressWarnings("IteratorHasNextCallsIteratorNext")
            public boolean hasNext() {
                while (next.isEmpty() && Iterator.this.hasNext()) {
                    final T candidate = Iterator.this.next();
                    if (predicate.test(candidate)) {
                        next = Option.some(candidate);
                    }
                }
                return next.isDefined();
            }

            @Override
            protected T getNext() {
                final T result;
                try {
                    result = next.get();
                } catch (NoSuchElementException e) {
                    throw new NoSuchElementException("next() on empty iterator");
                }
                next = Option.none();
                return result;
            }
        };
    }

    default <U> @NotNull Iterator<U> map(@NotNull Function<? super T, ? extends U> mapper) {
        if (isEmpty()) {
            return empty();
        }
        return new AbstractIterator<>() {

            @Override
            public boolean hasNext() {
                return Iterator.this.hasNext();
            }

            @Override
            public U getNext() {
                return mapper.apply(Iterator.this.next());
            }
        };
    }

    default <U> @NotNull Iterator<U> filterMap(@NotNull Function<? super T, ? extends Option<? extends U>> mapper) {
        if (isEmpty()) {
            return empty();
        }
        return new AbstractIterator<>() {
            private Option<? extends U> next = Option.none();

            @SuppressWarnings("IteratorHasNextCallsIteratorNext")
            @Override
            public boolean hasNext() {
                while (next.isEmpty() && Iterator.this.hasNext()) {
                    final T candidate = Iterator.this.next();
                    next = mapper.apply(candidate);
                }
                return next.isDefined();
            }

            @Override
            protected U getNext() {
                final U result;
                try {
                    result = next.get();
                } catch (NoSuchElementException e) {
                    throw new NoSuchElementException("next() on empty iterator");
                }
                next = Option.none();
                return result;
            }
        };
    }

    default void forEach(Consumer<? super T> action) {
        while (hasNext()) {
            action.accept(next());
        }
    }

    final class EmptyIterator implements Iterator<Object> {
        static final EmptyIterator INSTANCE = new EmptyIterator();

        @Contract(pure = true)
        @Override
        public boolean hasNext() {
            return false;
        }

        @Contract(value = " -> fail", pure = true)
        @Override
        public Object next() {
            throw new NoSuchElementException("next() on empty iterator");
        }
    }
}
