/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharConsumer;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharIterators;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import it.unimi.dsi.fastutil.chars.CharLists;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class CharImmutableList
extends CharLists.ImmutableListBase
implements CharList,
RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = 0L;
    static final CharImmutableList EMPTY = new CharImmutableList(CharArrays.EMPTY_ARRAY);
    private final char[] a;

    public CharImmutableList(char[] a) {
        this.a = a;
    }

    public CharImmutableList(Collection<? extends Character> c) {
        this(c.isEmpty() ? CharArrays.EMPTY_ARRAY : CharIterators.unwrap(CharIterators.asCharIterator(c.iterator())));
    }

    public CharImmutableList(CharCollection c) {
        this(c.isEmpty() ? CharArrays.EMPTY_ARRAY : CharIterators.unwrap(c.iterator()));
    }

    public CharImmutableList(CharList l) {
        this(l.isEmpty() ? CharArrays.EMPTY_ARRAY : new char[l.size()]);
        l.getElements(0, this.a, 0, l.size());
    }

    public CharImmutableList(char[] a, int offset, int length) {
        this(length == 0 ? CharArrays.EMPTY_ARRAY : new char[length]);
        System.arraycopy(a, offset, this.a, 0, length);
    }

    public CharImmutableList(CharIterator i) {
        this(i.hasNext() ? CharIterators.unwrap(i) : CharArrays.EMPTY_ARRAY);
    }

    public static CharImmutableList of() {
        return EMPTY;
    }

    public static CharImmutableList of(char ... init) {
        return init.length == 0 ? CharImmutableList.of() : new CharImmutableList(init);
    }

    @Override
    public char getChar(int index) {
        if (index >= this.a.length) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.a.length + ")");
        }
        return this.a[index];
    }

    @Override
    public int indexOf(char k) {
        int size = this.a.length;
        for (int i = 0; i < size; ++i) {
            if (k != this.a[i]) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(char k) {
        int i = this.a.length;
        while (i-- != 0) {
            if (k != this.a[i]) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int size() {
        return this.a.length;
    }

    @Override
    public boolean isEmpty() {
        return this.a.length == 0;
    }

    @Override
    public void getElements(int from, char[] a, int offset, int length) {
        CharArrays.ensureOffsetLength(a, offset, length);
        System.arraycopy(this.a, from, a, offset, length);
    }

    @Override
    public void forEach(CharConsumer action) {
        for (int i = 0; i < this.a.length; ++i) {
            action.accept(this.a[i]);
        }
    }

    @Override
    public char[] toCharArray() {
        if (this.a.length == 0) {
            return CharArrays.EMPTY_ARRAY;
        }
        return (char[])this.a.clone();
    }

    @Override
    public char[] toArray(char[] a) {
        if (a == null || a.length < this.size()) {
            a = new char[this.a.length];
        }
        System.arraycopy(this.a, 0, a, 0, a.length);
        return a;
    }

    @Override
    public CharListIterator listIterator(final int index) {
        this.ensureIndex(index);
        return new CharListIterator(){
            int pos;
            {
                this.pos = index;
            }

            @Override
            public boolean hasNext() {
                return this.pos < CharImmutableList.this.a.length;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public char nextChar() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return CharImmutableList.this.a[this.pos++];
            }

            @Override
            public char previousChar() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                return CharImmutableList.this.a[--this.pos];
            }

            @Override
            public int nextIndex() {
                return this.pos;
            }

            @Override
            public int previousIndex() {
                return this.pos - 1;
            }

            @Override
            public void forEachRemaining(CharConsumer action) {
                while (this.pos < CharImmutableList.this.a.length) {
                    action.accept(CharImmutableList.this.a[this.pos++]);
                }
            }

            @Override
            public void add(char k) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(char k) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public int back(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int remaining = CharImmutableList.this.a.length - this.pos;
                if (n < remaining) {
                    this.pos -= n;
                } else {
                    n = remaining;
                    this.pos = 0;
                }
                return n;
            }

            @Override
            public int skip(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int remaining = CharImmutableList.this.a.length - this.pos;
                if (n < remaining) {
                    this.pos += n;
                } else {
                    n = remaining;
                    this.pos = CharImmutableList.this.a.length;
                }
                return n;
            }
        };
    }

    @Override
    public CharSpliterator spliterator() {
        return new Spliterator();
    }

    @Override
    public CharList subList(int from, int to) {
        if (from == 0 && to == this.size()) {
            return this;
        }
        this.ensureIndex(from);
        this.ensureIndex(to);
        if (from == to) {
            return EMPTY;
        }
        if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        return new ImmutableSubList(this, from, to);
    }

    public CharImmutableList clone() {
        return this;
    }

    public boolean equals(CharImmutableList l) {
        if (l == this) {
            return true;
        }
        if (this.a == l.a) {
            return true;
        }
        int s = this.size();
        if (s != l.size()) {
            return false;
        }
        char[] a1 = this.a;
        char[] a2 = l.a;
        return Arrays.equals(a1, a2);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof List)) {
            return false;
        }
        if (o instanceof CharImmutableList) {
            return this.equals((CharImmutableList)o);
        }
        if (o instanceof ImmutableSubList) {
            return ((ImmutableSubList)o).equals(this);
        }
        return super.equals(o);
    }

    @Override
    public int compareTo(CharImmutableList l) {
        int i;
        if (this.a == l.a) {
            return 0;
        }
        int s1 = this.size();
        int s2 = l.size();
        char[] a1 = this.a;
        char[] a2 = l.a;
        for (i = 0; i < s1 && i < s2; ++i) {
            char e1 = a1[i];
            char e2 = a2[i];
            int r = Character.compare(e1, e2);
            if (r == 0) continue;
            return r;
        }
        return i < s2 ? -1 : (i < s1 ? 1 : 0);
    }

    @Override
    public int compareTo(List<? extends Character> l) {
        if (l instanceof CharImmutableList) {
            return this.compareTo((CharImmutableList)l);
        }
        if (l instanceof ImmutableSubList) {
            ImmutableSubList other = (ImmutableSubList)l;
            return -other.compareTo(this);
        }
        return super.compareTo(l);
    }

    private final class Spliterator
    implements CharSpliterator {
        int pos;
        int max;

        public Spliterator() {
            this(0, charImmutableList.a.length);
        }

        private Spliterator(int pos, int max) {
            assert (pos <= max) : "pos " + pos + " must be <= max " + max;
            this.pos = pos;
            this.max = max;
        }

        @Override
        public int characteristics() {
            return 17744;
        }

        @Override
        public long estimateSize() {
            return this.max - this.pos;
        }

        @Override
        public boolean tryAdvance(CharConsumer action) {
            if (this.pos >= this.max) {
                return false;
            }
            action.accept(CharImmutableList.this.a[this.pos++]);
            return true;
        }

        @Override
        public void forEachRemaining(CharConsumer action) {
            while (this.pos < this.max) {
                action.accept(CharImmutableList.this.a[this.pos]);
                ++this.pos;
            }
        }

        @Override
        public long skip(long n) {
            if (n < 0L) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            if (this.pos >= this.max) {
                return 0L;
            }
            int remaining = this.max - this.pos;
            if (n < (long)remaining) {
                this.pos = SafeMath.safeLongToInt((long)this.pos + n);
                return n;
            }
            n = remaining;
            this.pos = this.max;
            return n;
        }

        @Override
        public CharSpliterator trySplit() {
            int myNewPos;
            int retLen = this.max - this.pos >> 1;
            if (retLen <= 1) {
                return null;
            }
            int retMax = myNewPos = this.pos + retLen;
            int oldPos = this.pos;
            this.pos = myNewPos;
            return new Spliterator(oldPos, retMax);
        }
    }

    private static final class ImmutableSubList
    extends CharLists.ImmutableListBase
    implements RandomAccess,
    Serializable {
        private static final long serialVersionUID = 7054639518438982401L;
        final CharImmutableList innerList;
        final int from;
        final int to;
        final transient char[] a;

        ImmutableSubList(CharImmutableList innerList, int from, int to) {
            this.innerList = innerList;
            this.from = from;
            this.to = to;
            this.a = innerList.a;
        }

        @Override
        public char getChar(int index) {
            this.ensureRestrictedIndex(index);
            return this.a[index + this.from];
        }

        @Override
        public int indexOf(char k) {
            for (int i = this.from; i < this.to; ++i) {
                if (k != this.a[i]) continue;
                return i - this.from;
            }
            return -1;
        }

        @Override
        public int lastIndexOf(char k) {
            int i = this.to;
            while (i-- != this.from) {
                if (k != this.a[i]) continue;
                return i - this.from;
            }
            return -1;
        }

        @Override
        public int size() {
            return this.to - this.from;
        }

        @Override
        public boolean isEmpty() {
            return this.to <= this.from;
        }

        @Override
        public void getElements(int fromSublistIndex, char[] a, int offset, int length) {
            CharArrays.ensureOffsetLength(a, offset, length);
            this.ensureRestrictedIndex(fromSublistIndex);
            if (this.from + length > this.to) {
                throw new IndexOutOfBoundsException("Final index " + (this.from + length) + " (startingIndex: " + this.from + " + length: " + length + ") is greater then list length " + this.size());
            }
            System.arraycopy(this.a, fromSublistIndex + this.from, a, offset, length);
        }

        @Override
        public void forEach(CharConsumer action) {
            for (int i = this.from; i < this.to; ++i) {
                action.accept(this.a[i]);
            }
        }

        @Override
        public char[] toCharArray() {
            return Arrays.copyOfRange(this.a, this.from, this.to);
        }

        @Override
        public char[] toArray(char[] a) {
            if (a == null || a.length < this.size()) {
                a = new char[this.size()];
            }
            System.arraycopy(this.a, this.from, a, 0, this.size());
            return a;
        }

        @Override
        public CharListIterator listIterator(final int index) {
            this.ensureIndex(index);
            return new CharListIterator(){
                int pos;
                {
                    this.pos = index;
                }

                @Override
                public boolean hasNext() {
                    return this.pos < to;
                }

                @Override
                public boolean hasPrevious() {
                    return this.pos > from;
                }

                @Override
                public char nextChar() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return a[this.pos++ + from];
                }

                @Override
                public char previousChar() {
                    if (!this.hasPrevious()) {
                        throw new NoSuchElementException();
                    }
                    return a[--this.pos + from];
                }

                @Override
                public int nextIndex() {
                    return this.pos;
                }

                @Override
                public int previousIndex() {
                    return this.pos - 1;
                }

                @Override
                public void forEachRemaining(CharConsumer action) {
                    while (this.pos < to) {
                        action.accept(a[this.pos++ + from]);
                    }
                }

                @Override
                public void add(char k) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void set(char k) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public int back(int n) {
                    if (n < 0) {
                        throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                    }
                    int remaining = to - this.pos;
                    if (n < remaining) {
                        this.pos -= n;
                    } else {
                        n = remaining;
                        this.pos = 0;
                    }
                    return n;
                }

                @Override
                public int skip(int n) {
                    if (n < 0) {
                        throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                    }
                    int remaining = to - this.pos;
                    if (n < remaining) {
                        this.pos += n;
                    } else {
                        n = remaining;
                        this.pos = to;
                    }
                    return n;
                }
            };
        }

        @Override
        public CharSpliterator spliterator() {
            return new SubListSpliterator();
        }

        boolean contentsEquals(char[] otherA, int otherAFrom, int otherATo) {
            if (this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
                return true;
            }
            if (otherATo - otherAFrom != this.size()) {
                return false;
            }
            int pos = this.from;
            int otherPos = otherAFrom;
            while (pos < this.to) {
                if (this.a[pos++] == otherA[otherPos++]) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (!(o instanceof List)) {
                return false;
            }
            if (o instanceof CharImmutableList) {
                CharImmutableList other = (CharImmutableList)o;
                return this.contentsEquals(other.a, 0, other.size());
            }
            if (o instanceof ImmutableSubList) {
                ImmutableSubList other = (ImmutableSubList)o;
                return this.contentsEquals(other.a, other.from, other.to);
            }
            return super.equals(o);
        }

        int contentsCompareTo(char[] otherA, int otherAFrom, int otherATo) {
            if (this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
                return 0;
            }
            int i = this.from;
            int j = otherAFrom;
            while (i < this.to && i < otherATo) {
                char e1 = this.a[i];
                char e2 = otherA[j];
                int r = Character.compare(e1, e2);
                if (r != 0) {
                    return r;
                }
                ++i;
                ++j;
            }
            return i < otherATo ? -1 : (i < this.to ? 1 : 0);
        }

        @Override
        public int compareTo(List<? extends Character> l) {
            if (l instanceof CharImmutableList) {
                CharImmutableList other = (CharImmutableList)l;
                return this.contentsCompareTo(other.a, 0, other.size());
            }
            if (l instanceof ImmutableSubList) {
                ImmutableSubList other = (ImmutableSubList)l;
                return this.contentsCompareTo(other.a, other.from, other.to);
            }
            return super.compareTo(l);
        }

        private Object readResolve() throws ObjectStreamException {
            try {
                return this.innerList.subList(this.from, this.to);
            }
            catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
                throw (InvalidObjectException)new InvalidObjectException(ex.getMessage()).initCause(ex);
            }
        }

        @Override
        public CharList subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from == to) {
                return EMPTY;
            }
            if (from > to) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            return new ImmutableSubList(this.innerList, from + this.from, to + this.from);
        }

        private final class SubListSpliterator
        extends CharSpliterators.EarlyBindingSizeIndexBasedSpliterator {
            SubListSpliterator() {
                super(ImmutableSubList.this.from, ImmutableSubList.this.to);
            }

            private SubListSpliterator(int pos, int maxPos) {
                super(pos, maxPos);
            }

            @Override
            protected final char get(int i) {
                return ImmutableSubList.this.a[i];
            }

            @Override
            protected final SubListSpliterator makeForSplit(int pos, int maxPos) {
                return new SubListSpliterator(pos, maxPos);
            }

            @Override
            public boolean tryAdvance(CharConsumer action) {
                if (this.pos >= this.maxPos) {
                    return false;
                }
                action.accept(ImmutableSubList.this.a[this.pos++]);
                return true;
            }

            @Override
            public void forEachRemaining(CharConsumer action) {
                int max = this.maxPos;
                while (this.pos < max) {
                    action.accept(ImmutableSubList.this.a[this.pos++]);
                }
            }

            @Override
            public int characteristics() {
                return 17744;
            }
        }
    }
}

