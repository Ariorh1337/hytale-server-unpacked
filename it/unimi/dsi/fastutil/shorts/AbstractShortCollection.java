/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterators;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;

public abstract class AbstractShortCollection
extends AbstractCollection<Short>
implements ShortCollection {
    protected AbstractShortCollection() {
    }

    @Override
    public abstract ShortIterator iterator();

    @Override
    public boolean add(short k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(short k) {
        ShortIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextShort()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean rem(short k) {
        ShortIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextShort()) continue;
            iterator.remove();
            return true;
        }
        return false;
    }

    @Override
    @Deprecated
    public boolean add(Short key) {
        return ShortCollection.super.add(key);
    }

    @Override
    @Deprecated
    public boolean contains(Object key) {
        return ShortCollection.super.contains(key);
    }

    @Override
    @Deprecated
    public boolean remove(Object key) {
        return ShortCollection.super.remove(key);
    }

    @Override
    public short[] toArray(short[] a) {
        int size = this.size();
        if (a == null) {
            a = new short[size];
        } else if (a.length < size) {
            a = Arrays.copyOf(a, size);
        }
        ShortIterators.unwrap(this.iterator(), a);
        return a;
    }

    @Override
    public short[] toShortArray() {
        int size = this.size();
        if (size == 0) {
            return ShortArrays.EMPTY_ARRAY;
        }
        short[] a = new short[size];
        ShortIterators.unwrap(this.iterator(), a);
        return a;
    }

    @Override
    @Deprecated
    public short[] toShortArray(short[] a) {
        return this.toArray(a);
    }

    @Override
    public boolean addAll(ShortCollection c) {
        boolean retVal = false;
        ShortIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.add(i.nextShort())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean addAll(Collection<? extends Short> c) {
        if (c instanceof ShortCollection) {
            return this.addAll((ShortCollection)c);
        }
        return super.addAll(c);
    }

    @Override
    public boolean containsAll(ShortCollection c) {
        ShortIterator i = c.iterator();
        while (i.hasNext()) {
            if (this.contains(i.nextShort())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (c instanceof ShortCollection) {
            return this.containsAll((ShortCollection)c);
        }
        return super.containsAll(c);
    }

    @Override
    public boolean removeAll(ShortCollection c) {
        boolean retVal = false;
        ShortIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.rem(i.nextShort())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c instanceof ShortCollection) {
            return this.removeAll((ShortCollection)c);
        }
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(ShortCollection c) {
        boolean retVal = false;
        ShortIterator i = this.iterator();
        while (i.hasNext()) {
            if (c.contains(i.nextShort())) continue;
            i.remove();
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c instanceof ShortCollection) {
            return this.retainAll((ShortCollection)c);
        }
        return super.retainAll(c);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        ShortIterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            short k = i.nextShort();
            s.append(String.valueOf(k));
        }
        s.append("}");
        return s.toString();
    }
}

