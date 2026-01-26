/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.longs.LongBigArrays;
import it.unimi.dsi.fastutil.objects.AbstractObjectBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigListIterator;
import it.unimi.dsi.fastutil.shorts.ShortArrayFrontCodedList;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortBigArrays;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class ShortArrayFrontCodedBigList
extends AbstractObjectBigList<short[]>
implements Serializable,
Cloneable,
RandomAccess {
    private static final long serialVersionUID = 1L;
    protected final long n;
    protected final int ratio;
    protected final short[][] array;
    protected transient long[][] p;

    public ShortArrayFrontCodedBigList(Iterator<short[]> arrays, int ratio) {
        if (ratio < 1) {
            throw new IllegalArgumentException("Illegal ratio (" + ratio + ")");
        }
        short[][] array = ShortBigArrays.EMPTY_BIG_ARRAY;
        long[][] p = LongBigArrays.EMPTY_BIG_ARRAY;
        short[][] a = new short[2][];
        long curSize = 0L;
        long n = 0L;
        int b = 0;
        while (arrays.hasNext()) {
            a[b] = arrays.next();
            int length = a[b].length;
            if (n % (long)ratio == 0L) {
                p = BigArrays.grow(p, n / (long)ratio + 1L);
                BigArrays.set(p, n / (long)ratio, curSize);
                array = BigArrays.grow(array, curSize + (long)ShortArrayFrontCodedList.count(length) + (long)length, curSize);
                curSize += (long)ShortArrayFrontCodedList.writeInt(array, length, curSize);
                BigArrays.copyToBig(a[b], 0, array, curSize, (long)length);
                curSize += (long)length;
            } else {
                int common;
                int minLength = Math.min(a[1 - b].length, length);
                for (common = 0; common < minLength && a[0][common] == a[1][common]; ++common) {
                }
                array = BigArrays.grow(array, curSize + (long)ShortArrayFrontCodedList.count(length -= common) + (long)ShortArrayFrontCodedList.count(common) + (long)length, curSize);
                curSize += (long)ShortArrayFrontCodedList.writeInt(array, length, curSize);
                curSize += (long)ShortArrayFrontCodedList.writeInt(array, common, curSize);
                BigArrays.copyToBig(a[b], common, array, curSize, (long)length);
                curSize += (long)length;
            }
            b = 1 - b;
            ++n;
        }
        this.n = n;
        this.ratio = ratio;
        this.array = BigArrays.trim(array, curSize);
        this.p = BigArrays.trim(p, (n + (long)ratio - 1L) / (long)ratio);
    }

    public ShortArrayFrontCodedBigList(Collection<short[]> c, int ratio) {
        this(c.iterator(), ratio);
    }

    public int ratio() {
        return this.ratio;
    }

    private int length(long index) {
        short[][] array = this.array;
        int delta = (int)(index % (long)this.ratio);
        long pos = BigArrays.get(this.p, index / (long)this.ratio);
        int length = ShortArrayFrontCodedList.readInt(array, pos);
        if (delta == 0) {
            return length;
        }
        pos += (long)(ShortArrayFrontCodedList.count(length) + length);
        length = ShortArrayFrontCodedList.readInt(array, pos);
        int common = ShortArrayFrontCodedList.readInt(array, pos + (long)ShortArrayFrontCodedList.count(length));
        for (int i = 0; i < delta - 1; ++i) {
            length = ShortArrayFrontCodedList.readInt(array, pos += (long)(ShortArrayFrontCodedList.count(length) + ShortArrayFrontCodedList.count(common) + length));
            common = ShortArrayFrontCodedList.readInt(array, pos + (long)ShortArrayFrontCodedList.count(length));
        }
        return length + common;
    }

    public int arrayLength(long index) {
        this.ensureRestrictedIndex(index);
        return this.length(index);
    }

    private int extract(long index, short[] a, int offset, int length) {
        long startPos;
        int delta = (int)(index % (long)this.ratio);
        long pos = startPos = BigArrays.get(this.p, index / (long)this.ratio);
        int arrayLength = ShortArrayFrontCodedList.readInt(this.array, pos);
        int currLen = 0;
        if (delta == 0) {
            pos = BigArrays.get(this.p, index / (long)this.ratio) + (long)ShortArrayFrontCodedList.count(arrayLength);
            BigArrays.copyFromBig(this.array, pos, a, offset, Math.min(length, arrayLength));
            return arrayLength;
        }
        int common = 0;
        for (int i = 0; i < delta; ++i) {
            long prevArrayPos = pos + (long)ShortArrayFrontCodedList.count(arrayLength) + (long)(i != 0 ? ShortArrayFrontCodedList.count(common) : 0);
            common = ShortArrayFrontCodedList.readInt(this.array, (pos = prevArrayPos + (long)arrayLength) + (long)ShortArrayFrontCodedList.count(arrayLength = ShortArrayFrontCodedList.readInt(this.array, pos)));
            int actualCommon = Math.min(common, length);
            if (actualCommon <= currLen) {
                currLen = actualCommon;
                continue;
            }
            BigArrays.copyFromBig(this.array, prevArrayPos, a, currLen + offset, actualCommon - currLen);
            currLen = actualCommon;
        }
        if (currLen < length) {
            BigArrays.copyFromBig(this.array, pos + (long)ShortArrayFrontCodedList.count(arrayLength) + (long)ShortArrayFrontCodedList.count(common), a, currLen + offset, Math.min(arrayLength, length - currLen));
        }
        return arrayLength + common;
    }

    @Override
    public short[] get(long index) {
        return this.getArray(index);
    }

    public short[] getArray(long index) {
        this.ensureRestrictedIndex(index);
        int length = this.length(index);
        short[] a = new short[length];
        this.extract(index, a, 0, length);
        return a;
    }

    public int get(long index, short[] a, int offset, int length) {
        this.ensureRestrictedIndex(index);
        ShortArrays.ensureOffsetLength(a, offset, length);
        int arrayLength = this.extract(index, a, offset, length);
        if (length >= arrayLength) {
            return arrayLength;
        }
        return length - arrayLength;
    }

    public int get(long index, short[] a) {
        return this.get(index, a, 0, a.length);
    }

    @Override
    public long size64() {
        return this.n;
    }

    @Override
    public ObjectBigListIterator<short[]> listIterator(final long start) {
        this.ensureIndex(start);
        return new ObjectBigListIterator<short[]>(){
            short[] s = ShortArrays.EMPTY_ARRAY;
            long i = 0L;
            long pos = 0L;
            boolean inSync;
            {
                if (start != 0L) {
                    if (start == ShortArrayFrontCodedBigList.this.n) {
                        this.i = start;
                    } else {
                        this.pos = BigArrays.get(ShortArrayFrontCodedBigList.this.p, start / (long)ShortArrayFrontCodedBigList.this.ratio);
                        int j = (int)(start % (long)ShortArrayFrontCodedBigList.this.ratio);
                        this.i = start - (long)j;
                        while (j-- != 0) {
                            this.next();
                        }
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return this.i < ShortArrayFrontCodedBigList.this.n;
            }

            @Override
            public boolean hasPrevious() {
                return this.i > 0L;
            }

            @Override
            public long previousIndex() {
                return this.i - 1L;
            }

            @Override
            public long nextIndex() {
                return this.i;
            }

            @Override
            public short[] next() {
                int length;
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                if (this.i % (long)ShortArrayFrontCodedBigList.this.ratio == 0L) {
                    this.pos = BigArrays.get(ShortArrayFrontCodedBigList.this.p, this.i / (long)ShortArrayFrontCodedBigList.this.ratio);
                    length = ShortArrayFrontCodedList.readInt(ShortArrayFrontCodedBigList.this.array, this.pos);
                    this.s = ShortArrays.ensureCapacity(this.s, length, 0);
                    BigArrays.copyFromBig(ShortArrayFrontCodedBigList.this.array, this.pos + (long)ShortArrayFrontCodedList.count(length), this.s, 0, length);
                    this.pos += (long)(length + ShortArrayFrontCodedList.count(length));
                    this.inSync = true;
                } else if (this.inSync) {
                    length = ShortArrayFrontCodedList.readInt(ShortArrayFrontCodedBigList.this.array, this.pos);
                    int common = ShortArrayFrontCodedList.readInt(ShortArrayFrontCodedBigList.this.array, this.pos + (long)ShortArrayFrontCodedList.count(length));
                    this.s = ShortArrays.ensureCapacity(this.s, length + common, common);
                    BigArrays.copyFromBig(ShortArrayFrontCodedBigList.this.array, this.pos + (long)ShortArrayFrontCodedList.count(length) + (long)ShortArrayFrontCodedList.count(common), this.s, common, length);
                    this.pos += (long)(ShortArrayFrontCodedList.count(length) + ShortArrayFrontCodedList.count(common) + length);
                    length += common;
                } else {
                    length = ShortArrayFrontCodedBigList.this.length(this.i);
                    this.s = ShortArrays.ensureCapacity(this.s, length, 0);
                    ShortArrayFrontCodedBigList.this.extract(this.i, this.s, 0, length);
                }
                ++this.i;
                return ShortArrays.copy(this.s, 0, length);
            }

            @Override
            public short[] previous() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.inSync = false;
                return ShortArrayFrontCodedBigList.this.getArray(--this.i);
            }
        };
    }

    public ShortArrayFrontCodedBigList clone() {
        return this;
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("[");
        for (long i = 0L; i < this.n; ++i) {
            if (i != 0L) {
                s.append(", ");
            }
            s.append(ShortArrayList.wrap(this.getArray(i)).toString());
        }
        s.append("]");
        return s.toString();
    }

    protected long[][] rebuildPointerArray() {
        long[][] p = LongBigArrays.newBigArray((this.n + (long)this.ratio - 1L) / (long)this.ratio);
        short[][] a = this.array;
        long pos = 0L;
        int skip = this.ratio - 1;
        long j = 0L;
        for (long i = 0L; i < this.n; ++i) {
            int length = ShortArrayFrontCodedList.readInt(a, pos);
            int count = ShortArrayFrontCodedList.count(length);
            if (++skip == this.ratio) {
                skip = 0;
                BigArrays.set(p, j++, pos);
                pos += (long)(count + length);
                continue;
            }
            pos += (long)(count + ShortArrayFrontCodedList.count(ShortArrayFrontCodedList.readInt(a, pos + (long)count)) + length);
        }
        return p;
    }

    public void dump(DataOutputStream array, DataOutputStream pointers) throws IOException {
        short[] s;
        int n;
        Object object = this.array;
        int n2 = ((short[][])object).length;
        for (n = 0; n < n2; ++n) {
            for (short e : s = object[n]) {
                array.writeShort(e);
            }
        }
        object = this.p;
        n2 = ((short[][])object).length;
        for (n = 0; n < n2; ++n) {
            for (short e : s = object[n]) {
                pointers.writeLong(e);
            }
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.p = this.rebuildPointerArray();
    }
}

