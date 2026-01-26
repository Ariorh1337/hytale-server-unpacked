/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterators;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ShortOpenHashSet
extends AbstractShortSet
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient short[] key;
    protected transient int mask;
    protected transient boolean containsNull;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;

    public ShortOpenHashSet(int expected, float f) {
        if (f <= 0.0f || f >= 1.0f) {
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
        }
        if (expected < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
        }
        this.f = f;
        this.minN = this.n = HashCommon.arraySize(expected, f);
        this.mask = this.n - 1;
        this.maxFill = HashCommon.maxFill(this.n, f);
        this.key = new short[this.n + 1];
    }

    public ShortOpenHashSet(int expected) {
        this(expected, 0.75f);
    }

    public ShortOpenHashSet() {
        this(16, 0.75f);
    }

    public ShortOpenHashSet(Collection<? extends Short> c, float f) {
        this(c.size(), f);
        this.addAll(c);
    }

    public ShortOpenHashSet(Collection<? extends Short> c) {
        this(c, 0.75f);
    }

    public ShortOpenHashSet(ShortCollection c, float f) {
        this(c.size(), f);
        this.addAll(c);
    }

    public ShortOpenHashSet(ShortCollection c) {
        this(c, 0.75f);
    }

    public ShortOpenHashSet(ShortIterator i, float f) {
        this(16, f);
        while (i.hasNext()) {
            this.add(i.nextShort());
        }
    }

    public ShortOpenHashSet(ShortIterator i) {
        this(i, 0.75f);
    }

    public ShortOpenHashSet(Iterator<?> i, float f) {
        this(ShortIterators.asShortIterator(i), f);
    }

    public ShortOpenHashSet(Iterator<?> i) {
        this(ShortIterators.asShortIterator(i));
    }

    public ShortOpenHashSet(short[] a, int offset, int length, float f) {
        this(length < 0 ? 0 : length, f);
        ShortArrays.ensureOffsetLength(a, offset, length);
        for (int i = 0; i < length; ++i) {
            this.add(a[offset + i]);
        }
    }

    public ShortOpenHashSet(short[] a, int offset, int length) {
        this(a, offset, length, 0.75f);
    }

    public ShortOpenHashSet(short[] a, float f) {
        this(a, 0, a.length, f);
    }

    public ShortOpenHashSet(short[] a) {
        this(a, 0.75f);
    }

    public static ShortOpenHashSet of() {
        return new ShortOpenHashSet();
    }

    public static ShortOpenHashSet of(short e) {
        ShortOpenHashSet result = new ShortOpenHashSet(1, 0.75f);
        result.add(e);
        return result;
    }

    public static ShortOpenHashSet of(short e0, short e1) {
        ShortOpenHashSet result = new ShortOpenHashSet(2, 0.75f);
        result.add(e0);
        if (!result.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        return result;
    }

    public static ShortOpenHashSet of(short e0, short e1, short e2) {
        ShortOpenHashSet result = new ShortOpenHashSet(3, 0.75f);
        result.add(e0);
        if (!result.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        if (!result.add(e2)) {
            throw new IllegalArgumentException("Duplicate element: " + e2);
        }
        return result;
    }

    public static ShortOpenHashSet of(short ... a) {
        ShortOpenHashSet result = new ShortOpenHashSet(a.length, 0.75f);
        for (short element : a) {
            if (result.add(element)) continue;
            throw new IllegalArgumentException("Duplicate element " + element);
        }
        return result;
    }

    private int realSize() {
        return this.containsNull ? this.size - 1 : this.size;
    }

    public void ensureCapacity(int capacity) {
        int needed = HashCommon.arraySize(capacity, this.f);
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    private void tryCapacity(long capacity) {
        int needed = (int)Math.min(0x40000000L, Math.max(2L, HashCommon.nextPowerOfTwo((long)Math.ceil((float)capacity / this.f))));
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    @Override
    public boolean addAll(ShortCollection c) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c.size());
        } else {
            this.tryCapacity(this.size() + c.size());
        }
        return super.addAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Short> c) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c.size());
        } else {
            this.tryCapacity(this.size() + c.size());
        }
        return super.addAll(c);
    }

    @Override
    public boolean add(short k) {
        if (k == 0) {
            if (this.containsNull) {
                return false;
            }
            this.containsNull = true;
        } else {
            short[] key = this.key;
            int pos = HashCommon.mix(k) & this.mask;
            short curr = key[pos];
            if (curr != 0) {
                if (curr == k) {
                    return false;
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
                    if (curr != k) continue;
                    return false;
                }
            }
            key[pos] = k;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return true;
    }

    protected final void shiftKeys(int pos) {
        short[] key = this.key;
        while (true) {
            short curr;
            int last = pos;
            pos = last + 1 & this.mask;
            while (true) {
                if ((curr = key[pos]) == 0) {
                    key[last] = 0;
                    return;
                }
                int slot = HashCommon.mix(curr) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            }
            key[last] = curr;
        }
    }

    private boolean removeEntry(int pos) {
        --this.size;
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return true;
    }

    private boolean removeNullEntry() {
        this.containsNull = false;
        this.key[this.n] = 0;
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return true;
    }

    @Override
    public boolean remove(short k) {
        if (k == 0) {
            if (this.containsNull) {
                return this.removeNullEntry();
            }
            return false;
        }
        short[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        short curr = key[pos];
        if (curr == 0) {
            return false;
        }
        if (k == curr) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (k != curr);
        return this.removeEntry(pos);
    }

    @Override
    public boolean contains(short k) {
        if (k == 0) {
            return this.containsNull;
        }
        short[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        short curr = key[pos];
        if (curr == 0) {
            return false;
        }
        if (k == curr) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (k != curr);
        return true;
    }

    @Override
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        this.containsNull = false;
        Arrays.fill(this.key, (short)0);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public ShortIterator iterator() {
        return new SetIterator();
    }

    @Override
    public ShortSpliterator spliterator() {
        return new SetSpliterator();
    }

    @Override
    public void forEach(ShortConsumer action) {
        if (this.containsNull) {
            action.accept(this.key[this.n]);
        }
        short[] key = this.key;
        int pos = this.n;
        while (pos-- != 0) {
            if (key[pos] == 0) continue;
            action.accept(key[pos]);
        }
    }

    public boolean trim() {
        return this.trim(this.size);
    }

    public boolean trim(int n) {
        int l = HashCommon.nextPowerOfTwo((int)Math.ceil((float)n / this.f));
        if (l >= this.n || this.size > HashCommon.maxFill(l, this.f)) {
            return true;
        }
        try {
            this.rehash(l);
        }
        catch (OutOfMemoryError cantDoIt) {
            return false;
        }
        return true;
    }

    protected void rehash(int newN) {
        short[] key = this.key;
        int mask = newN - 1;
        short[] newKey = new short[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (key[--i] == 0) {
            }
            int pos = HashCommon.mix(key[i]) & mask;
            if (newKey[pos] != 0) {
                while (newKey[pos = pos + 1 & mask] != 0) {
                }
            }
            newKey[pos] = key[i];
        }
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
    }

    public ShortOpenHashSet clone() {
        ShortOpenHashSet c;
        try {
            c = (ShortOpenHashSet)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = (short[])this.key.clone();
        c.containsNull = this.containsNull;
        return c;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        while (j-- != 0) {
            while (this.key[i] == 0) {
                ++i;
            }
            h += this.key[i];
            ++i;
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        ShortIterator i = this.iterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            s.writeShort(i.nextShort());
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new short[this.n + 1];
        short[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            short k = s.readShort();
            if (k == 0) {
                pos = this.n;
                this.containsNull = true;
            } else {
                pos = HashCommon.mix(k) & this.mask;
                if (key[pos] != 0) {
                    while (key[pos = pos + 1 & this.mask] != 0) {
                    }
                }
            }
            key[pos] = k;
        }
    }

    private void checkTable() {
    }

    private final class SetIterator
    implements ShortIterator {
        int pos;
        int last;
        int c;
        boolean mustReturnNull;
        ShortArrayList wrapped;

        private SetIterator() {
            this.pos = ShortOpenHashSet.this.n;
            this.last = -1;
            this.c = ShortOpenHashSet.this.size;
            this.mustReturnNull = ShortOpenHashSet.this.containsNull;
        }

        @Override
        public boolean hasNext() {
            return this.c != 0;
        }

        @Override
        public short nextShort() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.last = ShortOpenHashSet.this.n;
                return ShortOpenHashSet.this.key[ShortOpenHashSet.this.n];
            }
            short[] key = ShortOpenHashSet.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                return this.wrapped.getShort(-this.pos - 1);
            } while (key[this.pos] == 0);
            this.last = this.pos;
            return key[this.last];
        }

        private final void shiftKeys(int pos) {
            short[] key = ShortOpenHashSet.this.key;
            while (true) {
                short curr;
                int last = pos;
                pos = last + 1 & ShortOpenHashSet.this.mask;
                while (true) {
                    if ((curr = key[pos]) == 0) {
                        key[last] = 0;
                        return;
                    }
                    int slot = HashCommon.mix(curr) & ShortOpenHashSet.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & ShortOpenHashSet.this.mask;
                }
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new ShortArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
            }
        }

        @Override
        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == ShortOpenHashSet.this.n) {
                ShortOpenHashSet.this.containsNull = false;
                ShortOpenHashSet.this.key[ShortOpenHashSet.this.n] = 0;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                ShortOpenHashSet.this.remove(this.wrapped.getShort(-this.pos - 1));
                this.last = -1;
                return;
            }
            --ShortOpenHashSet.this.size;
            this.last = -1;
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            short[] key = ShortOpenHashSet.this.key;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.last = ShortOpenHashSet.this.n;
                action.accept(key[ShortOpenHashSet.this.n]);
                --this.c;
            }
            while (this.c != 0) {
                if (--this.pos < 0) {
                    this.last = Integer.MIN_VALUE;
                    action.accept(this.wrapped.getShort(-this.pos - 1));
                    --this.c;
                    continue;
                }
                if (key[this.pos] == 0) continue;
                this.last = this.pos;
                action.accept(key[this.last]);
                --this.c;
            }
        }
    }

    private final class SetSpliterator
    implements ShortSpliterator {
        private static final int POST_SPLIT_CHARACTERISTICS = 257;
        int pos = 0;
        int max;
        int c;
        boolean mustReturnNull;
        boolean hasSplit;

        SetSpliterator() {
            this.max = ShortOpenHashSet.this.n;
            this.c = 0;
            this.mustReturnNull = ShortOpenHashSet.this.containsNull;
            this.hasSplit = false;
        }

        SetSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
            this.max = ShortOpenHashSet.this.n;
            this.c = 0;
            this.mustReturnNull = ShortOpenHashSet.this.containsNull;
            this.hasSplit = false;
            this.pos = pos;
            this.max = max;
            this.mustReturnNull = mustReturnNull;
            this.hasSplit = hasSplit;
        }

        @Override
        public boolean tryAdvance(ShortConsumer action) {
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                ++this.c;
                action.accept(ShortOpenHashSet.this.key[ShortOpenHashSet.this.n]);
                return true;
            }
            short[] key = ShortOpenHashSet.this.key;
            while (this.pos < this.max) {
                if (key[this.pos] != 0) {
                    ++this.c;
                    action.accept(key[this.pos++]);
                    return true;
                }
                ++this.pos;
            }
            return false;
        }

        @Override
        public void forEachRemaining(ShortConsumer action) {
            short[] key = ShortOpenHashSet.this.key;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                action.accept(key[ShortOpenHashSet.this.n]);
                ++this.c;
            }
            while (this.pos < this.max) {
                if (key[this.pos] != 0) {
                    action.accept(key[this.pos]);
                    ++this.c;
                }
                ++this.pos;
            }
        }

        @Override
        public int characteristics() {
            return this.hasSplit ? 257 : 321;
        }

        @Override
        public long estimateSize() {
            if (!this.hasSplit) {
                return ShortOpenHashSet.this.size - this.c;
            }
            return Math.min((long)(ShortOpenHashSet.this.size - this.c), (long)((double)ShortOpenHashSet.this.realSize() / (double)ShortOpenHashSet.this.n * (double)(this.max - this.pos)) + (long)(this.mustReturnNull ? 1 : 0));
        }

        @Override
        public SetSpliterator trySplit() {
            if (this.pos >= this.max - 1) {
                return null;
            }
            int retLen = this.max - this.pos >> 1;
            if (retLen <= 1) {
                return null;
            }
            int myNewPos = this.pos + retLen;
            int retPos = this.pos;
            int retMax = myNewPos;
            SetSpliterator split = new SetSpliterator(retPos, retMax, this.mustReturnNull, true);
            this.pos = myNewPos;
            this.mustReturnNull = false;
            this.hasSplit = true;
            return split;
        }

        @Override
        public long skip(long n) {
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            if (n == 0L) {
                return 0L;
            }
            long skipped = 0L;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                ++skipped;
                --n;
            }
            short[] key = ShortOpenHashSet.this.key;
            while (this.pos < this.max && n > 0L) {
                if (key[this.pos++] == 0) continue;
                ++skipped;
                --n;
            }
            return skipped;
        }
    }
}

