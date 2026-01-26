/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Iterator;
import java.util.ListIterator;
import org.jline.reader.LineReader;

public interface History
extends Iterable<Entry> {
    public void attach(LineReader var1);

    public void load() throws IOException;

    public void save() throws IOException;

    public void write(Path var1, boolean var2) throws IOException;

    public void append(Path var1, boolean var2) throws IOException;

    public void read(Path var1, boolean var2) throws IOException;

    public void purge() throws IOException;

    public int size();

    default public boolean isEmpty() {
        return this.size() == 0;
    }

    public int index();

    public int first();

    public int last();

    public String get(int var1);

    default public void add(String line) {
        this.add(Instant.now(), line);
    }

    public void add(Instant var1, String var2);

    default public boolean isPersistable(Entry entry) {
        return true;
    }

    public ListIterator<Entry> iterator(int var1);

    @Override
    default public ListIterator<Entry> iterator() {
        return this.iterator(this.first());
    }

    default public Iterator<Entry> reverseIterator() {
        return this.reverseIterator(this.last());
    }

    default public Iterator<Entry> reverseIterator(final int index) {
        return new Iterator<Entry>(){
            private final ListIterator<Entry> it;
            final /* synthetic */ History this$0;
            {
                this.this$0 = this$0;
                this.it = this.this$0.iterator(index + 1);
            }

            @Override
            public boolean hasNext() {
                return this.it.hasPrevious();
            }

            @Override
            public Entry next() {
                return this.it.previous();
            }

            @Override
            public void remove() {
                this.it.remove();
                this.this$0.resetIndex();
            }
        };
    }

    public String current();

    public boolean previous();

    public boolean next();

    public boolean moveToFirst();

    public boolean moveToLast();

    public boolean moveTo(int var1);

    public void moveToEnd();

    public void resetIndex();

    public static interface Entry {
        public int index();

        public Instant time();

        public String line();
    }
}

