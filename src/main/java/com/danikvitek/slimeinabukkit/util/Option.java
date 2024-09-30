package com.danikvitek.slimeinabukkit.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Objects;

public sealed interface Option<T> {
    static <T> Option<T> some(T value) {
        return new Some<>(value);
    }

    @SuppressWarnings("unchecked")
    static <T> Option<T> none() {
        return (None<T>) None.INSTANCE;
    }

    T get() throws NoSuchElementException;

    boolean isEmpty();

    default boolean isDefined() {
        return !isEmpty();
    }

    final class Some<T> implements Option<T> {
        private final T value;

        @Contract(pure = true)
        private Some(T value) {
            this.value = value;
        }

        @Contract(pure = true)
        @Override
        public T get() {
            return value;
        }

        @Contract(pure = true)
        @Override
        public boolean isEmpty() {
            return false;
        }

        @Contract(value = "null -> false", pure = true)
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Some<?> some)) return false;

            return Objects.equals(value, some.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Contract(pure = true)
        @Override
        public @NotNull String toString() {
            return "Some(" + value + ")";
        }
    }

    final class None<T> implements Option<T> {
        private static final None<?> INSTANCE = new None<>();

        @Contract(pure = true)
        private None() {
        }

        @Contract(value = " -> fail", pure = true)
        @Override
        public T get() throws NoSuchElementException {
            throw new NoSuchElementException("No value present");
        }

        @Contract(pure = true)
        @Override
        public boolean isEmpty() {
            return true;
        }

        @Contract(pure = true)
        @Override
        public int hashCode() {
            return 1;
        }

        @Contract(pure = true)
        @Override
        public @NotNull String toString() {
            return "None";
        }
    }
}
