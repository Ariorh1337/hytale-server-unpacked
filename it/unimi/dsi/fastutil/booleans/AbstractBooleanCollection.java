/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanIterators;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;

public abstract class AbstractBooleanCollection
extends AbstractCollection<Boolean>
implements BooleanCollection {
    protected AbstractBooleanCollection() {
    }

    @Override
    public abstract BooleanIterator iterator();

    @Override
    public boolean add(boolean k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(boolean k) {
        BooleanIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextBoolean()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean rem(boolean k) {
        BooleanIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextBoolean()) continue;
            iterator.remove();
            return true;
        }
        return false;
    }

    @Override
    @Deprecated
    public boolean add(Boolean key) {
        return BooleanCollection.super.add(key);
    }

    @Override
    @Deprecated
    public boolean contains(Object key) {
        return BooleanCollection.super.contains(key);
    }

    @Override
    @Deprecated
    public boolean remove(Object key) {
        return BooleanCollection.super.remove(key);
    }

    @Override
    public boolean[] toArray(boolean[] a) {
        int size = this.size();
        if (a == null) {
            a = new boolean[size];
        } else if (a.length < size) {
            a = Arrays.copyOf(a, size);
        }
        BooleanIterators.unwrap(this.iterator(), a);
        return a;
    }

    @Override
    public boolean[] toBooleanArray() {
        int size = this.size();
        if (size == 0) {
            return BooleanArrays.EMPTY_ARRAY;
        }
        boolean[] a = new boolean[size];
        BooleanIterators.unwrap(this.iterator(), a);
        return a;
    }

    @Override
    @Deprecated
    public boolean[] toBooleanArray(boolean[] a) {
        return this.toArray(a);
    }

    @Override
    public boolean addAll(BooleanCollection c) {
        boolean retVal = false;
        BooleanIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.add(i.nextBoolean())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean addAll(Collection<? extends Boolean> c) {
        if (c instanceof BooleanCollection) {
            return this.addAll((BooleanCollection)c);
        }
        return super.addAll(c);
    }

    @Override
    public boolean containsAll(BooleanCollection c) {
        BooleanIterator i = c.iterator();
        while (i.hasNext()) {
            if (this.contains(i.nextBoolean())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (c instanceof BooleanCollection) {
            return this.containsAll((BooleanCollection)c);
        }
        return super.containsAll(c);
    }

    @Override
    public boolean removeAll(BooleanCollection c) {
        boolean retVal = false;
        BooleanIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.rem(i.nextBoolean())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c instanceof BooleanCollection) {
            return this.removeAll((BooleanCollection)c);
        }
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(BooleanCollection c) {
        boolean retVal = false;
        BooleanIterator i = this.iterator();
        while (i.hasNext()) {
            if (c.contains(i.nextBoolean())) continue;
            i.remove();
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c instanceof BooleanCollection) {
            return this.retainAll((BooleanCollection)c);
        }
        return super.retainAll(c);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        BooleanIterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            boolean k = i.nextBoolean();
            s.append(String.valueOf(k));
        }
        s.append("}");
        return s.toString();
    }
}

