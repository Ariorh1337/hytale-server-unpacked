/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharConsumer;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;

public class CharArraySet
extends AbstractCharSet
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    protected transient char[] a;
    protected int size;

    public CharArraySet(char[] a) {
        this.a = a;
        this.size = a.length;
    }

    public CharArraySet() {
        this.a = CharArrays.EMPTY_ARRAY;
    }

    public CharArraySet(int capacity) {
        this.a = new char[capacity];
    }

    public CharArraySet(CharCollection c) {
        this(c.size());
        this.addAll(c);
    }

    public CharArraySet(Collection<? extends Character> c) {
        this(c.size());
        this.addAll(c);
    }

    public CharArraySet(CharSet c) {
        this(c.size());
        int i = 0;
        CharIterator charIterator = c.iterator();
        while (charIterator.hasNext()) {
            char x;
            this.a[i] = x = ((Character)charIterator.next()).charValue();
            ++i;
        }
        this.size = i;
    }

    public CharArraySet(Set<? extends Character> c) {
        this(c.size());
        int i = 0;
        for (Character c2 : c) {
            this.a[i] = c2.charValue();
            ++i;
        }
        this.size = i;
    }

    public CharArraySet(char[] a, int size) {
        this.a = a;
        this.size = size;
        if (size > a.length) {
            throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
        }
    }

    public static CharArraySet of() {
        return CharArraySet.ofUnchecked();
    }

    public static CharArraySet of(char e) {
        return CharArraySet.ofUnchecked(e);
    }

    public static CharArraySet of(char ... a) {
        if (a.length == 2) {
            if (a[0] == a[1]) {
                throw new IllegalArgumentException("Duplicate element: " + a[1]);
            }
        } else if (a.length > 2) {
            CharOpenHashSet.of(a);
        }
        return CharArraySet.ofUnchecked(a);
    }

    public static CharArraySet ofUnchecked() {
        return new CharArraySet();
    }

    public static CharArraySet ofUnchecked(char ... a) {
        return new CharArraySet(a);
    }

    private int findKey(char o) {
        int i = this.size;
        while (i-- != 0) {
            if (this.a[i] != o) continue;
            return i;
        }
        return -1;
    }

    @Override
    public CharIterator iterator() {
        return new CharIterator(){
            int next = 0;

            @Override
            public boolean hasNext() {
                return this.next < CharArraySet.this.size;
            }

            @Override
            public char nextChar() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return CharArraySet.this.a[this.next++];
            }

            @Override
            public void remove() {
                int tail = CharArraySet.this.size-- - this.next--;
                System.arraycopy(CharArraySet.this.a, this.next + 1, CharArraySet.this.a, this.next, tail);
            }

            @Override
            public int skip(int n) {
                if (n < 0) {
                    throw new IllegalArgumentException("Argument must be nonnegative: " + n);
                }
                int remaining = CharArraySet.this.size - this.next;
                if (n < remaining) {
                    this.next += n;
                    return n;
                }
                n = remaining;
                this.next = CharArraySet.this.size;
                return n;
            }
        };
    }

    @Override
    public CharSpliterator spliterator() {
        return new Spliterator();
    }

    @Override
    public boolean contains(char k) {
        return this.findKey(k) != -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean remove(char k) {
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
    public boolean add(char k) {
        int pos = this.findKey(k);
        if (pos != -1) {
            return false;
        }
        if (this.size == this.a.length) {
            char[] b = new char[this.size == 0 ? 2 : this.size * 2];
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
    public char[] toCharArray() {
        if (this.size == 0) {
            return CharArrays.EMPTY_ARRAY;
        }
        return Arrays.copyOf(this.a, this.size);
    }

    @Override
    public char[] toArray(char[] a) {
        if (a == null || a.length < this.size) {
            a = new char[this.size];
        }
        System.arraycopy(this.a, 0, a, 0, this.size);
        return a;
    }

    public CharArraySet clone() {
        CharArraySet c;
        try {
            c = (CharArraySet)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.a = (char[])this.a.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeChar(this.a[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = new char[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.a[i] = s.readChar();
        }
    }

    private final class Spliterator
    implements CharSpliterator {
        boolean hasSplit = false;
        int pos;
        int max;

        public Spliterator() {
            this(0, charArraySet.size, false);
        }

        private Spliterator(int pos, int max, boolean hasSplit) {
            assert (pos <= max) : "pos " + pos + " must be <= max " + max;
            this.pos = pos;
            this.max = max;
            this.hasSplit = hasSplit;
        }

        private int getWorkingMax() {
            return this.hasSplit ? this.max : CharArraySet.this.size;
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
        public boolean tryAdvance(CharConsumer action) {
            if (this.pos >= this.getWorkingMax()) {
                return false;
            }
            action.accept(CharArraySet.this.a[this.pos++]);
            return true;
        }

        @Override
        public void forEachRemaining(CharConsumer action) {
            int max = this.getWorkingMax();
            while (this.pos < max) {
                action.accept(CharArraySet.this.a[this.pos]);
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
        public CharSpliterator trySplit() {
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

