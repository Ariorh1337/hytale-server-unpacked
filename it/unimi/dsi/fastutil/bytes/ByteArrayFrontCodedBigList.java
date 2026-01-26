/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.bytes.ByteArrayFrontCodedList;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import it.unimi.dsi.fastutil.longs.LongBigArrays;
import it.unimi.dsi.fastutil.objects.AbstractObjectBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigListIterator;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class ByteArrayFrontCodedBigList
extends AbstractObjectBigList<byte[]>
implements Serializable,
Cloneable,
RandomAccess {
    private static final long serialVersionUID = 1L;
    protected final long n;
    protected final int ratio;
    protected final byte[][] array;
    protected transient long[][] p;

    public ByteArrayFrontCodedBigList(Iterator<byte[]> arrays, int ratio) {
        if (ratio < 1) {
            throw new IllegalArgumentException("Illegal ratio (" + ratio + ")");
        }
        byte[][] array = ByteBigArrays.EMPTY_BIG_ARRAY;
        long[][] p = LongBigArrays.EMPTY_BIG_ARRAY;
        byte[][] a = new byte[2][];
        long curSize = 0L;
        long n = 0L;
        int b = 0;
        while (arrays.hasNext()) {
            a[b] = arrays.next();
            int length = a[b].length;
            if (n % (long)ratio == 0L) {
                p = BigArrays.grow(p, n / (long)ratio + 1L);
                BigArrays.set(p, n / (long)ratio, curSize);
                array = BigArrays.grow(array, curSize + (long)ByteArrayFrontCodedList.count(length) + (long)length, curSize);
                curSize += (long)ByteArrayFrontCodedList.writeInt(array, length, curSize);
                BigArrays.copyToBig(a[b], 0, array, curSize, (long)length);
                curSize += (long)length;
            } else {
                int common;
                int minLength = Math.min(a[1 - b].length, length);
                for (common = 0; common < minLength && a[0][common] == a[1][common]; ++common) {
                }
                array = BigArrays.grow(array, curSize + (long)ByteArrayFrontCodedList.count(length -= common) + (long)ByteArrayFrontCodedList.count(common) + (long)length, curSize);
                curSize += (long)ByteArrayFrontCodedList.writeInt(array, length, curSize);
                curSize += (long)ByteArrayFrontCodedList.writeInt(array, common, curSize);
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

    public ByteArrayFrontCodedBigList(Collection<byte[]> c, int ratio) {
        this(c.iterator(), ratio);
    }

    public int ratio() {
        return this.ratio;
    }

    private int length(long index) {
        byte[][] array = this.array;
        int delta = (int)(index % (long)this.ratio);
        long pos = BigArrays.get(this.p, index / (long)this.ratio);
        int length = ByteArrayFrontCodedList.readInt(array, pos);
        if (delta == 0) {
            return length;
        }
        pos += (long)(ByteArrayFrontCodedList.count(length) + length);
        length = ByteArrayFrontCodedList.readInt(array, pos);
        int common = ByteArrayFrontCodedList.readInt(array, pos + (long)ByteArrayFrontCodedList.count(length));
        for (int i = 0; i < delta - 1; ++i) {
            length = ByteArrayFrontCodedList.readInt(array, pos += (long)(ByteArrayFrontCodedList.count(length) + ByteArrayFrontCodedList.count(common) + length));
            common = ByteArrayFrontCodedList.readInt(array, pos + (long)ByteArrayFrontCodedList.count(length));
        }
        return length + common;
    }

    public int arrayLength(long index) {
        this.ensureRestrictedIndex(index);
        return this.length(index);
    }

    private int extract(long index, byte[] a, int offset, int length) {
        long startPos;
        int delta = (int)(index % (long)this.ratio);
        long pos = startPos = BigArrays.get(this.p, index / (long)this.ratio);
        int arrayLength = ByteArrayFrontCodedList.readInt(this.array, pos);
        int currLen = 0;
        if (delta == 0) {
            pos = BigArrays.get(this.p, index / (long)this.ratio) + (long)ByteArrayFrontCodedList.count(arrayLength);
            BigArrays.copyFromBig(this.array, pos, a, offset, Math.min(length, arrayLength));
            return arrayLength;
        }
        int common = 0;
        for (int i = 0; i < delta; ++i) {
            long prevArrayPos = pos + (long)ByteArrayFrontCodedList.count(arrayLength) + (long)(i != 0 ? ByteArrayFrontCodedList.count(common) : 0);
            common = ByteArrayFrontCodedList.readInt(this.array, (pos = prevArrayPos + (long)arrayLength) + (long)ByteArrayFrontCodedList.count(arrayLength = ByteArrayFrontCodedList.readInt(this.array, pos)));
            int actualCommon = Math.min(common, length);
            if (actualCommon <= currLen) {
                currLen = actualCommon;
                continue;
            }
            BigArrays.copyFromBig(this.array, prevArrayPos, a, currLen + offset, actualCommon - currLen);
            currLen = actualCommon;
        }
        if (currLen < length) {
            BigArrays.copyFromBig(this.array, pos + (long)ByteArrayFrontCodedList.count(arrayLength) + (long)ByteArrayFrontCodedList.count(common), a, currLen + offset, Math.min(arrayLength, length - currLen));
        }
        return arrayLength + common;
    }

    @Override
    public byte[] get(long index) {
        return this.getArray(index);
    }

    public byte[] getArray(long index) {
        this.ensureRestrictedIndex(index);
        int length = this.length(index);
        byte[] a = new byte[length];
        this.extract(index, a, 0, length);
        return a;
    }

    public int get(long index, byte[] a, int offset, int length) {
        this.ensureRestrictedIndex(index);
        ByteArrays.ensureOffsetLength(a, offset, length);
        int arrayLength = this.extract(index, a, offset, length);
        if (length >= arrayLength) {
            return arrayLength;
        }
        return length - arrayLength;
    }

    public int get(long index, byte[] a) {
        return this.get(index, a, 0, a.length);
    }

    @Override
    public long size64() {
        return this.n;
    }

    @Override
    public ObjectBigListIterator<byte[]> listIterator(final long start) {
        this.ensureIndex(start);
        return new ObjectBigListIterator<byte[]>(){
            byte[] s = ByteArrays.EMPTY_ARRAY;
            long i = 0L;
            long pos = 0L;
            boolean inSync;
            {
                if (start != 0L) {
                    if (start == ByteArrayFrontCodedBigList.this.n) {
                        this.i = start;
                    } else {
                        this.pos = BigArrays.get(ByteArrayFrontCodedBigList.this.p, start / (long)ByteArrayFrontCodedBigList.this.ratio);
                        int j = (int)(start % (long)ByteArrayFrontCodedBigList.this.ratio);
                        this.i = start - (long)j;
                        while (j-- != 0) {
                            this.next();
                        }
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return this.i < ByteArrayFrontCodedBigList.this.n;
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
            public byte[] next() {
                int length;
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                if (this.i % (long)ByteArrayFrontCodedBigList.this.ratio == 0L) {
                    this.pos = BigArrays.get(ByteArrayFrontCodedBigList.this.p, this.i / (long)ByteArrayFrontCodedBigList.this.ratio);
                    length = ByteArrayFrontCodedList.readInt(ByteArrayFrontCodedBigList.this.array, this.pos);
                    this.s = ByteArrays.ensureCapacity(this.s, length, 0);
                    BigArrays.copyFromBig(ByteArrayFrontCodedBigList.this.array, this.pos + (long)ByteArrayFrontCodedList.count(length), this.s, 0, length);
                    this.pos += (long)(length + ByteArrayFrontCodedList.count(length));
                    this.inSync = true;
                } else if (this.inSync) {
                    length = ByteArrayFrontCodedList.readInt(ByteArrayFrontCodedBigList.this.array, this.pos);
                    int common = ByteArrayFrontCodedList.readInt(ByteArrayFrontCodedBigList.this.array, this.pos + (long)ByteArrayFrontCodedList.count(length));
                    this.s = ByteArrays.ensureCapacity(this.s, length + common, common);
                    BigArrays.copyFromBig(ByteArrayFrontCodedBigList.this.array, this.pos + (long)ByteArrayFrontCodedList.count(length) + (long)ByteArrayFrontCodedList.count(common), this.s, common, length);
                    this.pos += (long)(ByteArrayFrontCodedList.count(length) + ByteArrayFrontCodedList.count(common) + length);
                    length += common;
                } else {
                    length = ByteArrayFrontCodedBigList.this.length(this.i);
                    this.s = ByteArrays.ensureCapacity(this.s, length, 0);
                    ByteArrayFrontCodedBigList.this.extract(this.i, this.s, 0, length);
                }
                ++this.i;
                return ByteArrays.copy(this.s, 0, length);
            }

            @Override
            public byte[] previous() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.inSync = false;
                return ByteArrayFrontCodedBigList.this.getArray(--this.i);
            }
        };
    }

    public ByteArrayFrontCodedBigList clone() {
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
            s.append(ByteArrayList.wrap(this.getArray(i)).toString());
        }
        s.append("]");
        return s.toString();
    }

    protected long[][] rebuildPointerArray() {
        long[][] p = LongBigArrays.newBigArray((this.n + (long)this.ratio - 1L) / (long)this.ratio);
        byte[][] a = this.array;
        long pos = 0L;
        int skip = this.ratio - 1;
        long j = 0L;
        for (long i = 0L; i < this.n; ++i) {
            int length = ByteArrayFrontCodedList.readInt(a, pos);
            int count = ByteArrayFrontCodedList.count(length);
            if (++skip == this.ratio) {
                skip = 0;
                BigArrays.set(p, j++, pos);
                pos += (long)(count + length);
                continue;
            }
            pos += (long)(count + ByteArrayFrontCodedList.count(ByteArrayFrontCodedList.readInt(a, pos + (long)count)) + length);
        }
        return p;
    }

    public void dump(DataOutputStream array, DataOutputStream pointers) throws IOException {
        byte[] s;
        int n;
        Object object = this.array;
        int n2 = ((byte[][])object).length;
        for (n = 0; n < n2; ++n) {
            for (byte e : s = object[n]) {
                array.writeByte(e);
            }
        }
        object = this.p;
        n2 = ((byte[][])object).length;
        for (n = 0; n < n2; ++n) {
            for (byte e : s = object[n]) {
                pointers.writeLong(e);
            }
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.p = this.rebuildPointerArray();
    }
}

