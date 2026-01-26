/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.cache.tape;

import io.sentry.cache.tape.ObjectQueue;
import io.sentry.cache.tape.QueueFile;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class EmptyObjectQueue<T>
extends ObjectQueue<T> {
    EmptyObjectQueue() {
    }

    @Override
    @Nullable
    public QueueFile file() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void add(T entry) throws IOException {
    }

    @Override
    @Nullable
    public T peek() throws IOException {
        return null;
    }

    @Override
    public void remove(int n) throws IOException {
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    @NotNull
    public Iterator<T> iterator() {
        return new EmptyIterator();
    }

    private static final class EmptyIterator<T>
    implements Iterator<T> {
        private EmptyIterator() {
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            throw new NoSuchElementException("No elements in EmptyIterator!");
        }
    }
}

