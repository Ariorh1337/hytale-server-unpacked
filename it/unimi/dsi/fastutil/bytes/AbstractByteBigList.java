/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteBigList;
import it.unimi.dsi.fastutil.bytes.ByteBigListIterator;
import it.unimi.dsi.fastutil.bytes.ByteBigListIterators;
import it.unimi.dsi.fastutil.bytes.ByteBigSpliterators;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterators;
import it.unimi.dsi.fastutil.bytes.ByteStack;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;

public abstract class AbstractByteBigList
extends AbstractByteCollection
implements ByteBigList,
ByteStack {
    protected AbstractByteBigList() {
    }

    protected void ensureIndex(long index) {
        if (index < 0L) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index > this.size64()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size64() + ")");
        }
    }

    protected void ensureRestrictedIndex(long index) {
        if (index < 0L) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index >= this.size64()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size64() + ")");
        }
    }

    @Override
    public void add(long index, byte k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(byte k) {
        this.add(this.size64(), k);
        return true;
    }

    @Override
    public byte removeByte(long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte set(long index, byte k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(long index, Collection<? extends Byte> c) {
        this.ensureIndex(index);
        Iterator<? extends Byte> i = c.iterator();
        boolean retVal = i.hasNext();
        while (i.hasNext()) {
            this.add(index++, i.next());
        }
        return retVal;
    }

    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        return this.addAll(this.size64(), c);
    }

    @Override
    public ByteBigListIterator iterator() {
        return this.listIterator();
    }

    @Override
    public ByteBigListIterator listIterator() {
        return this.listIterator(0L);
    }

    @Override
    public ByteBigListIterator listIterator(long index) {
        this.ensureIndex(index);
        return new ByteBigListIterators.AbstractIndexBasedBigListIterator(0L, index){

            @Override
            protected final byte get(long i) {
                return AbstractByteBigList.this.getByte(i);
            }

            @Override
            protected final void add(long i, byte k) {
                AbstractByteBigList.this.add(i, k);
            }

            @Override
            protected final void set(long i, byte k) {
                AbstractByteBigList.this.set(i, k);
            }

            @Override
            protected final void remove(long i) {
                AbstractByteBigList.this.removeByte(i);
            }

            @Override
            protected final long getMaxPos() {
                return AbstractByteBigList.this.size64();
            }
        };
    }

    @Override
    public IntSpliterator intSpliterator() {
        if (this instanceof RandomAccess) {
            return ByteSpliterators.widen(this.spliterator());
        }
        return super.intSpliterator();
    }

    @Override
    public boolean contains(byte k) {
        return this.indexOf(k) >= 0L;
    }

    @Override
    public long indexOf(byte k) {
        ByteBigListIterator i = this.listIterator();
        while (i.hasNext()) {
            byte e = i.nextByte();
            if (k != e) continue;
            return i.previousIndex();
        }
        return -1L;
    }

    @Override
    public long lastIndexOf(byte k) {
        ByteBigListIterator i = this.listIterator(this.size64());
        while (i.hasPrevious()) {
            byte e = i.previousByte();
            if (k != e) continue;
            return i.nextIndex();
        }
        return -1L;
    }

    @Override
    public void size(long size) {
        long i = this.size64();
        if (size > i) {
            while (i++ < size) {
                this.add((byte)0);
            }
        } else {
            while (i-- != size) {
                this.remove(i);
            }
        }
    }

    @Override
    public ByteBigList subList(long from, long to) {
        this.ensureIndex(from);
        this.ensureIndex(to);
        if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        return this instanceof RandomAccess ? new ByteRandomAccessSubList(this, from, to) : new ByteSubList(this, from, to);
    }

    @Override
    public void forEach(ByteConsumer action) {
        if (this instanceof RandomAccess) {
            long max = this.size64();
            for (long i = 0L; i < max; ++i) {
                action.accept(this.getByte(i));
            }
        } else {
            super.forEach(action);
        }
    }

    @Override
    public void removeElements(long from, long to) {
        this.ensureIndex(to);
        ByteBigListIterator i = this.listIterator(from);
        long n = to - from;
        if (n < 0L) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        while (n-- != 0L) {
            i.nextByte();
            i.remove();
        }
    }

    @Override
    public void addElements(long index, byte[][] a, long offset, long length) {
        this.ensureIndex(index);
        BigArrays.ensureOffsetLength(a, offset, length);
        if (this instanceof RandomAccess) {
            while (length-- != 0L) {
                this.add(index++, BigArrays.get(a, offset++));
            }
        } else {
            ByteBigListIterator iter = this.listIterator(index);
            while (length-- != 0L) {
                iter.add(BigArrays.get(a, offset++));
            }
        }
    }

    @Override
    public void addElements(long index, byte[][] a) {
        this.addElements(index, a, 0L, BigArrays.length(a));
    }

    @Override
    public void getElements(long from, byte[][] a, long offset, long length) {
        this.ensureIndex(from);
        BigArrays.ensureOffsetLength(a, offset, length);
        if (from + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size64() + ")");
        }
        if (this instanceof RandomAccess) {
            long current = from;
            while (length-- != 0L) {
                BigArrays.set(a, offset++, this.getByte(current++));
            }
        } else {
            ByteBigListIterator i = this.listIterator(from);
            while (length-- != 0L) {
                BigArrays.set(a, offset++, i.nextByte());
            }
        }
    }

    @Override
    public void setElements(long index, byte[][] a, long offset, long length) {
        this.ensureIndex(index);
        BigArrays.ensureOffsetLength(a, offset, length);
        if (index + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size64() + ")");
        }
        if (this instanceof RandomAccess) {
            for (long i = 0L; i < length; ++i) {
                this.set(i + index, BigArrays.get(a, i + offset));
            }
        } else {
            ByteBigListIterator iter = this.listIterator(index);
            long i = 0L;
            while (i < length) {
                iter.nextByte();
                iter.set(BigArrays.get(a, offset + i++));
            }
        }
    }

    @Override
    public void clear() {
        this.removeElements(0L, this.size64());
    }

    @Override
    @Deprecated
    public int size() {
        return (int)Math.min(Integer.MAX_VALUE, this.size64());
    }

    @Override
    public int hashCode() {
        ByteBigListIterator i = this.iterator();
        int h = 1;
        long s = this.size64();
        while (s-- != 0L) {
            byte k = i.nextByte();
            h = 31 * h + k;
        }
        return h;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BigList)) {
            return false;
        }
        BigList l = (BigList)o;
        long s = this.size64();
        if (s != l.size64()) {
            return false;
        }
        if (l instanceof ByteBigList) {
            ByteBigListIterator i1 = this.listIterator();
            ByteBigListIterator i2 = ((ByteBigList)l).listIterator();
            while (s-- != 0L) {
                if (i1.nextByte() == i2.nextByte()) continue;
                return false;
            }
            return true;
        }
        ByteBigListIterator i1 = this.listIterator();
        BigListIterator i2 = l.listIterator();
        while (s-- != 0L) {
            if (Objects.equals(i1.next(), i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(BigList<? extends Byte> l) {
        if (l == this) {
            return 0;
        }
        if (l instanceof ByteBigList) {
            ByteBigListIterator i1 = this.listIterator();
            ByteBigListIterator i2 = ((ByteBigList)l).listIterator();
            while (i1.hasNext() && i2.hasNext()) {
                byte e2;
                byte e1 = i1.nextByte();
                int r = Byte.compare(e1, e2 = i2.nextByte());
                if (r == 0) continue;
                return r;
            }
            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
        }
        ByteBigListIterator i1 = this.listIterator();
        BigListIterator<? extends Byte> i2 = l.listIterator();
        while (i1.hasNext() && i2.hasNext()) {
            int r = ((Comparable)i1.next()).compareTo(i2.next());
            if (r == 0) continue;
            return r;
        }
        return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
    }

    @Override
    public void push(byte o) {
        this.add(o);
    }

    @Override
    public byte popByte() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.removeByte(this.size64() - 1L);
    }

    @Override
    public byte topByte() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.getByte(this.size64() - 1L);
    }

    @Override
    public byte peekByte(int i) {
        return this.getByte(this.size64() - 1L - (long)i);
    }

    @Override
    public boolean rem(byte k) {
        long index = this.indexOf(k);
        if (index == -1L) {
            return false;
        }
        this.removeByte(index);
        return true;
    }

    @Override
    public boolean addAll(long index, ByteCollection c) {
        return this.addAll(index, (Collection<? extends Byte>)c);
    }

    @Override
    public boolean addAll(ByteCollection c) {
        return this.addAll(this.size64(), c);
    }

    @Override
    @Deprecated
    public void add(long index, Byte ok) {
        this.add(index, (byte)ok);
    }

    @Override
    @Deprecated
    public Byte set(long index, Byte ok) {
        return this.set(index, (byte)ok);
    }

    @Override
    @Deprecated
    public Byte get(long index) {
        return this.getByte(index);
    }

    @Override
    @Deprecated
    public long indexOf(Object ok) {
        return this.indexOf((Byte)ok);
    }

    @Override
    @Deprecated
    public long lastIndexOf(Object ok) {
        return this.lastIndexOf((Byte)ok);
    }

    @Override
    @Deprecated
    public Byte remove(long index) {
        return this.removeByte(index);
    }

    @Override
    @Deprecated
    public void push(Byte o) {
        this.push((byte)o);
    }

    @Override
    @Deprecated
    public Byte pop() {
        return this.popByte();
    }

    @Override
    @Deprecated
    public Byte top() {
        return this.topByte();
    }

    @Override
    @Deprecated
    public Byte peek(int i) {
        return this.peekByte(i);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        ByteBigListIterator i = this.iterator();
        long n = this.size64();
        boolean first = true;
        s.append("[");
        while (n-- != 0L) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            byte k = i.nextByte();
            s.append(String.valueOf(k));
        }
        s.append("]");
        return s.toString();
    }

    public static class ByteRandomAccessSubList
    extends ByteSubList
    implements RandomAccess {
        private static final long serialVersionUID = -107070782945191929L;

        public ByteRandomAccessSubList(ByteBigList l, long from, long to) {
            super(l, from, to);
        }

        @Override
        public ByteBigList subList(long from, long to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new ByteRandomAccessSubList(this, from, to);
        }
    }

    public static class ByteSubList
    extends AbstractByteBigList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteBigList l;
        protected final long from;
        protected long to;

        public ByteSubList(ByteBigList l, long from, long to) {
            this.l = l;
            this.from = from;
            this.to = to;
        }

        private boolean assertRange() {
            assert (this.from <= this.l.size64());
            assert (this.to <= this.l.size64());
            assert (this.to >= this.from);
            return true;
        }

        @Override
        public boolean add(byte k) {
            this.l.add(this.to, k);
            ++this.to;
            assert (this.assertRange());
            return true;
        }

        @Override
        public void add(long index, byte k) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k);
            ++this.to;
            assert (this.assertRange());
        }

        @Override
        public boolean addAll(long index, Collection<? extends Byte> c) {
            this.ensureIndex(index);
            this.to += (long)c.size();
            return this.l.addAll(this.from + index, c);
        }

        @Override
        public byte getByte(long index) {
            this.ensureRestrictedIndex(index);
            return this.l.getByte(this.from + index);
        }

        @Override
        public byte removeByte(long index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return this.l.removeByte(this.from + index);
        }

        @Override
        public byte set(long index, byte k) {
            this.ensureRestrictedIndex(index);
            return this.l.set(this.from + index, k);
        }

        @Override
        public long size64() {
            return this.to - this.from;
        }

        @Override
        public void getElements(long from, byte[][] a, long offset, long length) {
            this.ensureIndex(from);
            if (from + length > this.size64()) {
                throw new IndexOutOfBoundsException("End index (" + from + length + ") is greater than list size (" + this.size64() + ")");
            }
            this.l.getElements(this.from + from, a, offset, length);
        }

        @Override
        public void removeElements(long from, long to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            this.l.removeElements(this.from + from, this.from + to);
            this.to -= to - from;
            assert (this.assertRange());
        }

        @Override
        public void addElements(long index, byte[][] a, long offset, long length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a, offset, length);
            this.to += length;
            assert (this.assertRange());
        }

        @Override
        public ByteBigListIterator listIterator(long index) {
            this.ensureIndex(index);
            return this.l instanceof RandomAccess ? new RandomAccessIter(index) : new ParentWrappingIter(this.l.listIterator(index + this.from));
        }

        @Override
        public ByteSpliterator spliterator() {
            return this.l instanceof RandomAccess ? new IndexBasedSpliterator(this.l, this.from, this.to) : super.spliterator();
        }

        @Override
        public IntSpliterator intSpliterator() {
            if (this.l instanceof RandomAccess) {
                return ByteSpliterators.widen(this.spliterator());
            }
            return super.intSpliterator();
        }

        @Override
        public ByteBigList subList(long from, long to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new ByteSubList(this, from, to);
        }

        @Override
        public boolean rem(byte k) {
            long index = this.indexOf(k);
            if (index == -1L) {
                return false;
            }
            --this.to;
            this.l.removeByte(this.from + index);
            assert (this.assertRange());
            return true;
        }

        @Override
        public boolean addAll(long index, ByteCollection c) {
            return super.addAll(index, c);
        }

        @Override
        public boolean addAll(long index, ByteBigList l) {
            return super.addAll(index, l);
        }

        private final class RandomAccessIter
        extends ByteBigListIterators.AbstractIndexBasedBigListIterator {
            RandomAccessIter(long pos) {
                super(0L, pos);
            }

            @Override
            protected final byte get(long i) {
                return ByteSubList.this.l.getByte(ByteSubList.this.from + i);
            }

            @Override
            protected final void add(long i, byte k) {
                ByteSubList.this.add(i, k);
            }

            @Override
            protected final void set(long i, byte k) {
                ByteSubList.this.set(i, k);
            }

            @Override
            protected final void remove(long i) {
                ByteSubList.this.removeByte(i);
            }

            @Override
            protected final long getMaxPos() {
                return ByteSubList.this.to - ByteSubList.this.from;
            }

            @Override
            public void add(byte k) {
                super.add(k);
                assert (ByteSubList.this.assertRange());
            }

            @Override
            public void remove() {
                super.remove();
                assert (ByteSubList.this.assertRange());
            }
        }

        private class ParentWrappingIter
        implements ByteBigListIterator {
            private ByteBigListIterator parent;

            ParentWrappingIter(ByteBigListIterator parent) {
                this.parent = parent;
            }

            @Override
            public long nextIndex() {
                return this.parent.nextIndex() - ByteSubList.this.from;
            }

            @Override
            public long previousIndex() {
                return this.parent.previousIndex() - ByteSubList.this.from;
            }

            @Override
            public boolean hasNext() {
                return this.parent.nextIndex() < ByteSubList.this.to;
            }

            @Override
            public boolean hasPrevious() {
                return this.parent.previousIndex() >= ByteSubList.this.from;
            }

            @Override
            public byte nextByte() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return this.parent.nextByte();
            }

            @Override
            public byte previousByte() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                return this.parent.previousByte();
            }

            @Override
            public void add(byte k) {
                this.parent.add(k);
            }

            @Override
            public void set(byte k) {
                this.parent.set(k);
            }

            @Override
            public void remove() {
                this.parent.remove();
            }

            @Override
            public long back(long n) {
                if (n < 0L) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                long currentPos = this.parent.previousIndex();
                long parentNewPos = currentPos - n;
                if (parentNewPos < ByteSubList.this.from - 1L) {
                    parentNewPos = ByteSubList.this.from - 1L;
                }
                long toSkip = parentNewPos - currentPos;
                return this.parent.back(toSkip);
            }

            @Override
            public long skip(long n) {
                if (n < 0L) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                long currentPos = this.parent.nextIndex();
                long parentNewPos = currentPos + n;
                if (parentNewPos > ByteSubList.this.to) {
                    parentNewPos = ByteSubList.this.to;
                }
                long toSkip = parentNewPos - currentPos;
                return this.parent.skip(toSkip);
            }
        }
    }

    static final class IndexBasedSpliterator
    extends ByteBigSpliterators.LateBindingSizeIndexBasedSpliterator {
        final ByteBigList l;

        IndexBasedSpliterator(ByteBigList l, long pos) {
            super(pos);
            this.l = l;
        }

        IndexBasedSpliterator(ByteBigList l, long pos, long maxPos) {
            super(pos, maxPos);
            this.l = l;
        }

        @Override
        protected final long getMaxPosFromBackingStore() {
            return this.l.size64();
        }

        @Override
        protected final byte get(long i) {
            return this.l.getByte(i);
        }

        @Override
        protected final IndexBasedSpliterator makeForSplit(long pos, long maxPos) {
            return new IndexBasedSpliterator(this.l, pos, maxPos);
        }
    }
}

