/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatBigArrays;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatIterators;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FloatOpenHashBigSet
extends AbstractFloatSet
implements Serializable,
Cloneable,
Hash,
Size64 {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient float[][] key;
    protected transient long mask;
    protected transient int segmentMask;
    protected transient int baseMask;
    protected transient boolean containsNull;
    protected transient long n;
    protected transient long maxFill;
    protected final transient long minN;
    protected final float f;
    protected long size;

    private void initMasks() {
        this.mask = this.n - 1L;
        this.segmentMask = this.key[0].length - 1;
        this.baseMask = this.key.length - 1;
    }

    public FloatOpenHashBigSet(long expected, float f) {
        if (f <= 0.0f || f > 1.0f) {
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
        }
        if (this.n < 0L) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
        }
        this.f = f;
        this.minN = this.n = HashCommon.bigArraySize(expected, f);
        this.maxFill = HashCommon.maxFill(this.n, f);
        this.key = FloatBigArrays.newBigArray(this.n);
        this.initMasks();
    }

    public FloatOpenHashBigSet(long expected) {
        this(expected, 0.75f);
    }

    public FloatOpenHashBigSet() {
        this(16L, 0.75f);
    }

    public FloatOpenHashBigSet(Collection<? extends Float> c, float f) {
        this(Size64.sizeOf(c), f);
        this.addAll(c);
    }

    public FloatOpenHashBigSet(Collection<? extends Float> c) {
        this(c, 0.75f);
    }

    public FloatOpenHashBigSet(FloatCollection c, float f) {
        this(Size64.sizeOf(c), f);
        this.addAll(c);
    }

    public FloatOpenHashBigSet(FloatCollection c) {
        this(c, 0.75f);
    }

    public FloatOpenHashBigSet(FloatIterator i, float f) {
        this(16L, f);
        while (i.hasNext()) {
            this.add(i.nextFloat());
        }
    }

    public FloatOpenHashBigSet(FloatIterator i) {
        this(i, 0.75f);
    }

    public FloatOpenHashBigSet(Iterator<?> i, float f) {
        this(FloatIterators.asFloatIterator(i), f);
    }

    public FloatOpenHashBigSet(Iterator<?> i) {
        this(FloatIterators.asFloatIterator(i));
    }

    public FloatOpenHashBigSet(float[] a, int offset, int length, float f) {
        this(length < 0 ? 0L : (long)length, f);
        FloatArrays.ensureOffsetLength(a, offset, length);
        for (int i = 0; i < length; ++i) {
            this.add(a[offset + i]);
        }
    }

    public FloatOpenHashBigSet(float[] a, int offset, int length) {
        this(a, offset, length, 0.75f);
    }

    public FloatOpenHashBigSet(float[] a, float f) {
        this(a, 0, a.length, f);
    }

    public FloatOpenHashBigSet(float[] a) {
        this(a, 0.75f);
    }

    private long realSize() {
        return this.containsNull ? this.size - 1L : this.size;
    }

    public void ensureCapacity(long capacity) {
        long needed = HashCommon.bigArraySize(capacity, this.f);
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    @Override
    public boolean addAll(Collection<? extends Float> c) {
        long size = Size64.sizeOf(c);
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(size);
        } else {
            this.ensureCapacity(this.size64() + size);
        }
        return super.addAll(c);
    }

    @Override
    public boolean addAll(FloatCollection c) {
        long size = Size64.sizeOf(c);
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(size);
        } else {
            this.ensureCapacity(this.size64() + size);
        }
        return super.addAll(c);
    }

    @Override
    public boolean add(float k) {
        if (Float.floatToIntBits(k) == 0) {
            if (this.containsNull) {
                return false;
            }
            this.containsNull = true;
        } else {
            int displ;
            float[][] key = this.key;
            long h = HashCommon.mix((long)HashCommon.float2int(k));
            int base = (int)((h & this.mask) >>> 27);
            float curr = key[base][displ = (int)(h & (long)this.segmentMask)];
            if (Float.floatToIntBits(curr) != 0) {
                if (Float.floatToIntBits(curr) == Float.floatToIntBits(k)) {
                    return false;
                }
                while (Float.floatToIntBits(curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0) {
                    if (Float.floatToIntBits(curr) != Float.floatToIntBits(k)) continue;
                    return false;
                }
            }
            key[base][displ] = k;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(2L * this.n);
        }
        return true;
    }

    protected final void shiftKeys(long pos) {
        float[][] key = this.key;
        while (true) {
            long last = pos;
            pos = last + 1L & this.mask;
            while (true) {
                if (Float.floatToIntBits(BigArrays.get(key, pos)) == 0) {
                    BigArrays.set(key, last, 0.0f);
                    return;
                }
                long slot = HashCommon.mix((long)HashCommon.float2int(BigArrays.get(key, pos))) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1L & this.mask;
            }
            BigArrays.set(key, last, BigArrays.get(key, pos));
        }
    }

    private boolean removeEntry(int base, int displ) {
        --this.size;
        this.shiftKeys((long)base * 0x8000000L + (long)displ);
        if (this.n > this.minN && this.size < this.maxFill / 4L && this.n > 16L) {
            this.rehash(this.n / 2L);
        }
        return true;
    }

    private boolean removeNullEntry() {
        this.containsNull = false;
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4L && this.n > 16L) {
            this.rehash(this.n / 2L);
        }
        return true;
    }

    @Override
    public boolean remove(float k) {
        int displ;
        if (Float.floatToIntBits(k) == 0) {
            if (this.containsNull) {
                return this.removeNullEntry();
            }
            return false;
        }
        float[][] key = this.key;
        long h = HashCommon.mix((long)HashCommon.float2int(k));
        int base = (int)((h & this.mask) >>> 27);
        float curr = key[base][displ = (int)(h & (long)this.segmentMask)];
        if (Float.floatToIntBits(curr) == 0) {
            return false;
        }
        if (Float.floatToIntBits(curr) == Float.floatToIntBits(k)) {
            return this.removeEntry(base, displ);
        }
        do {
            if (Float.floatToIntBits(curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0) continue;
            return false;
        } while (Float.floatToIntBits(curr) != Float.floatToIntBits(k));
        return this.removeEntry(base, displ);
    }

    @Override
    public boolean contains(float k) {
        int displ;
        if (Float.floatToIntBits(k) == 0) {
            return this.containsNull;
        }
        float[][] key = this.key;
        long h = HashCommon.mix((long)HashCommon.float2int(k));
        int base = (int)((h & this.mask) >>> 27);
        float curr = key[base][displ = (int)(h & (long)this.segmentMask)];
        if (Float.floatToIntBits(curr) == 0) {
            return false;
        }
        if (Float.floatToIntBits(curr) == Float.floatToIntBits(k)) {
            return true;
        }
        do {
            if (Float.floatToIntBits(curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0) continue;
            return false;
        } while (Float.floatToIntBits(curr) != Float.floatToIntBits(k));
        return true;
    }

    @Override
    public void clear() {
        if (this.size == 0L) {
            return;
        }
        this.size = 0L;
        this.containsNull = false;
        BigArrays.fill(this.key, 0.0f);
    }

    @Override
    public FloatIterator iterator() {
        return new SetIterator();
    }

    @Override
    public FloatSpliterator spliterator() {
        return new SetSpliterator();
    }

    @Override
    public void forEach(FloatConsumer action) {
        if (this.containsNull) {
            action.accept(0.0f);
        }
        long pos = 0L;
        long max = this.n;
        float[][] key = this.key;
        while (pos < max) {
            float gotten;
            if (Float.floatToIntBits(gotten = BigArrays.get(key, pos++)) == 0) continue;
            action.accept(gotten);
        }
    }

    public boolean trim() {
        return this.trim(this.size);
    }

    public boolean trim(long n) {
        long l = HashCommon.bigArraySize(n, this.f);
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

    protected void rehash(long newN) {
        float[][] key = this.key;
        float[][] newKey = FloatBigArrays.newBigArray(newN);
        long mask = newN - 1L;
        int newSegmentMask = newKey[0].length - 1;
        int newBaseMask = newKey.length - 1;
        int base = 0;
        int displ = 0;
        long i = this.realSize();
        while (i-- != 0L) {
            int d;
            while (Float.floatToIntBits(key[base][displ]) == 0) {
                base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
            }
            float k = key[base][displ];
            long h = HashCommon.mix((long)HashCommon.float2int(k));
            int b = (int)((h & mask) >>> 27);
            if (Float.floatToIntBits(newKey[b][d = (int)(h & (long)newSegmentMask)]) != 0) {
                while (Float.floatToIntBits(newKey[b = b + ((d = d + 1 & newSegmentMask) == 0 ? 1 : 0) & newBaseMask][d]) != 0) {
                }
            }
            newKey[b][d] = k;
            base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
        }
        this.n = newN;
        this.key = newKey;
        this.initMasks();
        this.maxFill = HashCommon.maxFill(this.n, this.f);
    }

    @Override
    @Deprecated
    public int size() {
        return (int)Math.min(Integer.MAX_VALUE, this.size);
    }

    @Override
    public long size64() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0L;
    }

    public FloatOpenHashBigSet clone() {
        FloatOpenHashBigSet c;
        try {
            c = (FloatOpenHashBigSet)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = BigArrays.copy(this.key);
        c.containsNull = this.containsNull;
        return c;
    }

    @Override
    public int hashCode() {
        float[][] key = this.key;
        int h = 0;
        int base = 0;
        int displ = 0;
        long j = this.realSize();
        while (j-- != 0L) {
            while (Float.floatToIntBits(key[base][displ]) == 0) {
                base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
            }
            h += HashCommon.float2int(key[base][displ]);
            base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        FloatIterator i = this.iterator();
        s.defaultWriteObject();
        long j = this.size;
        while (j-- != 0L) {
            s.writeFloat(i.nextFloat());
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.bigArraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = FloatBigArrays.newBigArray(this.n);
        float[][] key = this.key;
        this.initMasks();
        long i = this.size;
        while (i-- != 0L) {
            int displ;
            float k = s.readFloat();
            if (Float.floatToIntBits(k) == 0) {
                this.containsNull = true;
                continue;
            }
            long h = HashCommon.mix((long)HashCommon.float2int(k));
            int base = (int)((h & this.mask) >>> 27);
            if (Float.floatToIntBits(key[base][displ = (int)(h & (long)this.segmentMask)]) != 0) {
                while (Float.floatToIntBits(key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0) {
                }
            }
            key[base][displ] = k;
        }
    }

    private void checkTable() {
    }

    private class SetIterator
    implements FloatIterator {
        int base;
        int displ;
        long last;
        long c;
        boolean mustReturnNull;
        FloatArrayList wrapped;

        private SetIterator() {
            this.base = FloatOpenHashBigSet.this.key.length;
            this.last = -1L;
            this.c = FloatOpenHashBigSet.this.size;
            this.mustReturnNull = FloatOpenHashBigSet.this.containsNull;
        }

        @Override
        public boolean hasNext() {
            return this.c != 0L;
        }

        @Override
        public float nextFloat() {
            float k;
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.last = FloatOpenHashBigSet.this.n;
                return 0.0f;
            }
            float[][] key = FloatOpenHashBigSet.this.key;
            do {
                if (this.displ == 0 && this.base <= 0) {
                    this.last = Long.MIN_VALUE;
                    return this.wrapped.getFloat(-(--this.base) - 1);
                }
                if (this.displ-- != 0) continue;
                this.displ = key[--this.base].length - 1;
            } while (Float.floatToIntBits(k = key[this.base][this.displ]) == 0);
            this.last = (long)this.base * 0x8000000L + (long)this.displ;
            return k;
        }

        private final void shiftKeys(long pos) {
            float[][] key = FloatOpenHashBigSet.this.key;
            while (true) {
                float curr;
                long last = pos;
                pos = last + 1L & FloatOpenHashBigSet.this.mask;
                while (true) {
                    if (Float.floatToIntBits(curr = BigArrays.get(key, pos)) == 0) {
                        BigArrays.set(key, last, 0.0f);
                        return;
                    }
                    long slot = HashCommon.mix((long)HashCommon.float2int(curr)) & FloatOpenHashBigSet.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1L & FloatOpenHashBigSet.this.mask;
                }
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new FloatArrayList();
                    }
                    this.wrapped.add(BigArrays.get(key, pos));
                }
                BigArrays.set(key, last, curr);
            }
        }

        @Override
        public void remove() {
            if (this.last == -1L) {
                throw new IllegalStateException();
            }
            if (this.last == FloatOpenHashBigSet.this.n) {
                FloatOpenHashBigSet.this.containsNull = false;
            } else if (this.base >= 0) {
                this.shiftKeys(this.last);
            } else {
                FloatOpenHashBigSet.this.remove(this.wrapped.getFloat(-this.base - 1));
                this.last = -1L;
                return;
            }
            --FloatOpenHashBigSet.this.size;
            this.last = -1L;
        }
    }

    private class SetSpliterator
    implements FloatSpliterator {
        private static final int POST_SPLIT_CHARACTERISTICS = 257;
        long pos = 0L;
        long max;
        long c;
        boolean mustReturnNull;
        boolean hasSplit;

        SetSpliterator() {
            this.max = FloatOpenHashBigSet.this.n;
            this.c = 0L;
            this.mustReturnNull = FloatOpenHashBigSet.this.containsNull;
            this.hasSplit = false;
        }

        SetSpliterator(long pos, long max, boolean mustReturnNull, boolean hasSplit) {
            this.max = FloatOpenHashBigSet.this.n;
            this.c = 0L;
            this.mustReturnNull = FloatOpenHashBigSet.this.containsNull;
            this.hasSplit = false;
            this.pos = pos;
            this.max = max;
            this.mustReturnNull = mustReturnNull;
            this.hasSplit = hasSplit;
        }

        @Override
        public boolean tryAdvance(FloatConsumer action) {
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                ++this.c;
                action.accept(0.0f);
                return true;
            }
            float[][] key = FloatOpenHashBigSet.this.key;
            while (this.pos < this.max) {
                float gotten = BigArrays.get(key, this.pos);
                if (Float.floatToIntBits(gotten) != 0) {
                    ++this.c;
                    ++this.pos;
                    action.accept(gotten);
                    return true;
                }
                ++this.pos;
            }
            return false;
        }

        @Override
        public void forEachRemaining(FloatConsumer action) {
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                action.accept(0.0f);
                ++this.c;
            }
            float[][] key = FloatOpenHashBigSet.this.key;
            while (this.pos < this.max) {
                float gotten = BigArrays.get(key, this.pos);
                if (Float.floatToIntBits(gotten) != 0) {
                    action.accept(gotten);
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
                return FloatOpenHashBigSet.this.size - this.c;
            }
            return Math.min(FloatOpenHashBigSet.this.size - this.c, (long)((double)FloatOpenHashBigSet.this.realSize() / (double)FloatOpenHashBigSet.this.n * (double)(this.max - this.pos)) + (long)(this.mustReturnNull ? 1 : 0));
        }

        @Override
        public SetSpliterator trySplit() {
            if (this.pos >= this.max - 1L) {
                return null;
            }
            long retLen = this.max - this.pos >> 1;
            if (retLen <= 1L) {
                return null;
            }
            long myNewPos = this.pos + retLen;
            myNewPos = BigArrays.nearestSegmentStart(myNewPos, this.pos + 1L, this.max - 1L);
            long retPos = this.pos;
            long retMax = myNewPos;
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
            float[][] key = FloatOpenHashBigSet.this.key;
            while (this.pos < this.max && n > 0L) {
                if (Float.floatToIntBits(BigArrays.get(key, this.pos++)) == 0) continue;
                ++skipped;
                --n;
            }
            return skipped;
        }
    }
}

