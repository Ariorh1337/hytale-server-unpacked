/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.cache.tape;

import io.sentry.cache.tape.EmptyObjectQueue;
import io.sentry.cache.tape.FileObjectQueue;
import io.sentry.cache.tape.QueueFile;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public abstract class ObjectQueue<T>
implements Iterable<T>,
Closeable {
    public static <T> ObjectQueue<T> create(QueueFile qf, Converter<T> converter) {
        return new FileObjectQueue<T>(qf, converter);
    }

    public static <T> ObjectQueue<T> createEmpty() {
        return new EmptyObjectQueue();
    }

    @Nullable
    public abstract QueueFile file();

    public abstract int size();

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public abstract void add(T var1) throws IOException;

    @Nullable
    public abstract T peek() throws IOException;

    public List<T> peek(int max) throws IOException {
        int end = Math.min(max, this.size());
        ArrayList subList = new ArrayList(end);
        Iterator iterator = this.iterator();
        for (int i = 0; i < end; ++i) {
            subList.add(iterator.next());
        }
        return Collections.unmodifiableList(subList);
    }

    public List<T> asList() throws IOException {
        return this.peek(this.size());
    }

    public void remove() throws IOException {
        this.remove(1);
    }

    public abstract void remove(int var1) throws IOException;

    public void clear() throws IOException {
        this.remove(this.size());
    }

    public static interface Converter<T> {
        @Nullable
        public T from(byte[] var1) throws IOException;

        public void toStream(T var1, OutputStream var2) throws IOException;
    }
}

