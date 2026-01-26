/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteIterators;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;

public abstract class AbstractByteCollection
extends AbstractCollection<Byte>
implements ByteCollection {
    protected AbstractByteCollection() {
    }

    @Override
    public abstract ByteIterator iterator();

    @Override
    public boolean add(byte k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(byte k) {
        ByteIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextByte()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean rem(byte k) {
        ByteIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextByte()) continue;
            iterator.remove();
            return true;
        }
        return false;
    }

    @Override
    @Deprecated
    public boolean add(Byte key) {
        return ByteCollection.super.add(key);
    }

    @Override
    @Deprecated
    public boolean contains(Object key) {
        return ByteCollection.super.contains(key);
    }

    @Override
    @Deprecated
    public boolean remove(Object key) {
        return ByteCollection.super.remove(key);
    }

    @Override
    public byte[] toArray(byte[] a) {
        int size = this.size();
        if (a == null) {
            a = new byte[size];
        } else if (a.length < size) {
            a = Arrays.copyOf(a, size);
        }
        ByteIterators.unwrap(this.iterator(), a);
        return a;
    }

    @Override
    public byte[] toByteArray() {
        int size = this.size();
        if (size == 0) {
            return ByteArrays.EMPTY_ARRAY;
        }
        byte[] a = new byte[size];
        ByteIterators.unwrap(this.iterator(), a);
        return a;
    }

    @Override
    @Deprecated
    public byte[] toByteArray(byte[] a) {
        return this.toArray(a);
    }

    @Override
    public boolean addAll(ByteCollection c) {
        boolean retVal = false;
        ByteIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.add(i.nextByte())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        if (c instanceof ByteCollection) {
            return this.addAll((ByteCollection)c);
        }
        return super.addAll(c);
    }

    @Override
    public boolean containsAll(ByteCollection c) {
        ByteIterator i = c.iterator();
        while (i.hasNext()) {
            if (this.contains(i.nextByte())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (c instanceof ByteCollection) {
            return this.containsAll((ByteCollection)c);
        }
        return super.containsAll(c);
    }

    @Override
    public boolean removeAll(ByteCollection c) {
        boolean retVal = false;
        ByteIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.rem(i.nextByte())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c instanceof ByteCollection) {
            return this.removeAll((ByteCollection)c);
        }
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(ByteCollection c) {
        boolean retVal = false;
        ByteIterator i = this.iterator();
        while (i.hasNext()) {
            if (c.contains(i.nextByte())) continue;
            i.remove();
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c instanceof ByteCollection) {
            return this.retainAll((ByteCollection)c);
        }
        return super.retainAll(c);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        ByteIterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            byte k = i.nextByte();
            s.append(String.valueOf(k));
        }
        s.append("}");
        return s.toString();
    }
}

