/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongConsumer;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongIterators;
import it.unimi.dsi.fastutil.longs.LongPredicate;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;

public abstract class AbstractLongCollection
extends AbstractCollection<Long>
implements LongCollection {
    protected AbstractLongCollection() {
    }

    @Override
    public abstract LongIterator iterator();

    @Override
    public boolean add(long k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(long k) {
        LongIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextLong()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean rem(long k) {
        LongIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextLong()) continue;
            iterator.remove();
            return true;
        }
        return false;
    }

    @Override
    @Deprecated
    public boolean add(Long key) {
        return LongCollection.super.add(key);
    }

    @Override
    @Deprecated
    public boolean contains(Object key) {
        return LongCollection.super.contains(key);
    }

    @Override
    @Deprecated
    public boolean remove(Object key) {
        return LongCollection.super.remove(key);
    }

    @Override
    public long[] toArray(long[] a) {
        int size = this.size();
        if (a == null) {
            a = new long[size];
        } else if (a.length < size) {
            a = Arrays.copyOf(a, size);
        }
        LongIterators.unwrap(this.iterator(), a);
        return a;
    }

    @Override
    public long[] toLongArray() {
        int size = this.size();
        if (size == 0) {
            return LongArrays.EMPTY_ARRAY;
        }
        long[] a = new long[size];
        LongIterators.unwrap(this.iterator(), a);
        return a;
    }

    @Override
    @Deprecated
    public long[] toLongArray(long[] a) {
        return this.toArray(a);
    }

    @Override
    public final void forEach(LongConsumer action) {
        LongCollection.super.forEach(action);
    }

    @Override
    public final boolean removeIf(LongPredicate filter) {
        return LongCollection.super.removeIf(filter);
    }

    @Override
    public boolean addAll(LongCollection c) {
        boolean retVal = false;
        LongIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.add(i.nextLong())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean addAll(Collection<? extends Long> c) {
        if (c instanceof LongCollection) {
            return this.addAll((LongCollection)c);
        }
        return super.addAll(c);
    }

    @Override
    public boolean containsAll(LongCollection c) {
        LongIterator i = c.iterator();
        while (i.hasNext()) {
            if (this.contains(i.nextLong())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (c instanceof LongCollection) {
            return this.containsAll((LongCollection)c);
        }
        return super.containsAll(c);
    }

    @Override
    public boolean removeAll(LongCollection c) {
        boolean retVal = false;
        LongIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.rem(i.nextLong())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c instanceof LongCollection) {
            return this.removeAll((LongCollection)c);
        }
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(LongCollection c) {
        boolean retVal = false;
        LongIterator i = this.iterator();
        while (i.hasNext()) {
            if (c.contains(i.nextLong())) continue;
            i.remove();
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c instanceof LongCollection) {
            return this.retainAll((LongCollection)c);
        }
        return super.retainAll(c);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        LongIterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            long k = i.nextLong();
            s.append(String.valueOf(k));
        }
        s.append("}");
        return s.toString();
    }
}

