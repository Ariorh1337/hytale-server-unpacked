/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;

public class ShortArraySet
extends AbstractShortSet
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    protected transient short[] a;
    protected int size;

    public ShortArraySet(short[] a) {
        this.a = a;
        this.size = a.length;
    }

    public ShortArraySet() {
        this.a = ShortArrays.EMPTY_ARRAY;
    }

    public ShortArraySet(int capacity) {
        this.a = new short[capacity];
    }

    public ShortArraySet(ShortCollection c) {
        this(c.size());
        this.addAll(c);
    }

    public ShortArraySet(Collection<? extends Short> c) {
        this(c.size());
        this.addAll(c);
    }

    public ShortArraySet(ShortSet c) {
        this(c.size());
        int i = 0;
        ShortIterator shortIterator = c.iterator();
        while (shortIterator.hasNext()) {
            short x;
            this.a[i] = x = ((Short)shortIterator.next()).shortValue();
            ++i;
        }
        this.size = i;
    }

    public ShortArraySet(Set<? extends Short> c) {
        this(c.size());
        int i = 0;
        for (Short s : c) {
            this.a[i] = s;
            ++i;
        }
        this.size = i;
    }

    public ShortArraySet(short[] a, int size) {
        this.a = a;
        this.size = size;
        if (size > a.length) {
            throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
        }
    }

    public static ShortArraySet of() {
        return ShortArraySet.ofUnchecked();
    }

    public static ShortArraySet of(short e) {
        return ShortArraySet.ofUnchecked(e);
    }

    public static ShortArraySet of(short ... a) {
        if (a.length == 2) {
            if (a[0] == a[1]) {
                throw new IllegalArgumentException("Duplicate element: " + a[1]);
            }
        } else if (a.length > 2) {
            ShortOpenHashSet.of(a);
        }
        return ShortArraySet.ofUnchecked(a);
    }

    public static ShortArraySet ofUnchecked() {
        return new ShortArraySet();
    }

    public static ShortArraySet ofUnchecked(short ... a) {
        return new ShortArraySet(a);
    }

    private int findKey(short o) {
        int i = this.size;
        while (i-- != 0) {
            if (this.a[i] != o) continue;
            return i;
        }
        return -1;
    }

    @Override
    public ShortIterator iterator() {
        return new ShortIterator(){
            int next = 0;

            @Override
            public boolean hasNext() {
                return this.next < ShortArraySet.this.size;
            }

            @Override
            public short nextShort() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return ShortArraySet.this.a[this.next++];
            }

            @Override
            public void remove() {
                int tail = ShortArraySet.this.size-- - this.next--;
                System.arraycopy(ShortArraySet.this.a, this.next + 1, ShortArraySet.this.a, this.next, tail);
            }

            @Override
            public int skip(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int remaining = ShortArraySet.this.size - this.next;
                if (n < remaining) {
                    this.next += n;
                    return n;
                }
                n = remaining;
                this.next = ShortArraySet.this.size;
                return n;
            }
        };
    }

    @Override
    public ShortSpliterator spliterator() {
        return new Spliterator();
    }

    @Override
    public boolean contains(short k) {
        return this.findKey(k) != -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean remove(short k) {
        int pos = this.findKey(k);
        if (pos == -1) {
            return false;
        }
        int tail = this.size - pos - 1;
        for (int i = 0; i < tail; ++i) {
            this.a[pos + i] = this.a[pos + i + 1];
        }
        --this.size;
        return true;
    }

    @Override
    public boolean add(short k) {
        int pos = this.findKey(k);
        if (pos != -1) {
            return false;
        }
        if (this.size == this.a.length) {
            short[] b = new short[this.size == 0 ? 2 : this.size * 2];
            int i = this.size;
            while (i-- != 0) {
                b[i] = this.a[i];
            }
            this.a = b;
        }
        this.a[this.size++] = k;
        return true;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public short[] toShortArray() {
        if (this.size == 0) {
            return ShortArrays.EMPTY_ARRAY;
        }
        return Arrays.copyOf(this.a, this.size);
    }

    @Override
    public short[] toArray(short[] a) {
        if (a == null || a.length < this.size) {
            a = new short[this.size];
        }
        System.arraycopy(this.a, 0, a, 0, this.size);
        return a;
    }

    public ShortArraySet clone() {
        ShortArraySet c;
        try {
            c = (ShortArraySet)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.a = (short[])this.a.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeShort(this.a[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = new short[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.a[i] = s.readShort();
        }
    }

    private final class Spliterator
    implements ShortSpliterator {
        boolean hasSplit = false;
        int pos;
        int max;

        public Spliterator() {
            this(0, shortArraySet.size, false);
        }

        private Spliterator(int pos, int max, boolean hasSplit) {
            assert (pos <= max) : "pos " + pos + " must be <= max " + max;
            this.pos = pos;
            this.max = max;
            this.hasSplit = hasSplit;
        }

        private int getWorkingMax() {
            return this.hasSplit ? this.max : ShortArraySet.this.size;
        }

        @Override
        public int characteristics() {
            return 16721;
        }

        @Override
        public long estimateSize() {
            return this.getWorkingMax() - this.pos;
        }

        @Override
        public boolean tryAdvance(ShortConsumer action) {
            if (this.pos >= this.getWorkingMax()) {
                return false;
            }
            action.accept(ShortArraySet.this.a[this.pos++]);
            return true;
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            int max = this.getWorkingMax();
            while (this.pos < max) {
                action.accept(ShortArraySet.this.a[this.pos]);
                ++this.pos;
            }
        }

        @Override
        public long skip(long n) {
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            int max = this.getWorkingMax();
            if (this.pos >= max) {
                return 0L;
            }
            int remaining = max - this.pos;
            if (n < (long)remaining) {
                this.pos = SafeMath.safeLongToInt((long)this.pos + n);
                return n;
            }
            n = remaining;
            this.pos = max;
            return n;
        }

        @Override
        public ShortSpliterator trySplit() {
            int myNewPos;
            int max = this.getWorkingMax();
            int retLen = max - this.pos >> 1;
            if (retLen <= 1) {
                return null;
            }
            this.max = max;
            int retMax = myNewPos = this.pos + retLen;
            int oldPos = this.pos;
            this.pos = myNewPos;
            this.hasSplit = true;
            return new Spliterator(oldPos, retMax, true);
        }
    }
}

