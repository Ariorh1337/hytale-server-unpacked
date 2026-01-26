/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractCharList;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharCollections;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharConsumer;
import it.unimi.dsi.fastutil.chars.CharIterators;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import it.unimi.dsi.fastutil.chars.CharPredicate;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;
import it.unimi.dsi.fastutil.chars.CharUnaryOperator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class CharLists {
    public static final EmptyList EMPTY_LIST = new EmptyList();

    private CharLists() {
    }

    public static CharList shuffle(CharList l, Random random) {
        int i = l.size();
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            char t = l.getChar(i);
            l.set(i, l.getChar(p));
            l.set(p, t);
        }
        return l;
    }

    public static CharList emptyList() {
        return EMPTY_LIST;
    }

    public static CharList singleton(char element) {
        return new Singleton(element);
    }

    public static CharList singleton(Object element) {
        return new Singleton(((Character)element).charValue());
    }

    public static CharList synchronize(CharList l) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l) : new SynchronizedList(l);
    }

    public static CharList synchronize(CharList l, Object sync) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l, sync) : new SynchronizedList(l, sync);
    }

    public static CharList unmodifiable(CharList l) {
        return l instanceof RandomAccess ? new UnmodifiableRandomAccessList(l) : new UnmodifiableList(l);
    }

    public static class EmptyList
    extends CharCollections.EmptyCollection
    implements CharList,
    RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyList() {
        }

        @Override
        public char getChar(int i) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char removeChar(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char set(int index, char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(char k) {
            return -1;
        }

        @Override
        public int lastIndexOf(char k) {
            return -1;
        }

        @Override
        public boolean addAll(int i, Collection<? extends Character> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Character> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(CharUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(CharList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, CharList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void add(int index, Character k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Character get(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean add(Character k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Character set(int index, Character k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Character remove(int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public int indexOf(Object k) {
            return -1;
        }

        @Override
        @Deprecated
        public int lastIndexOf(Object k) {
            return -1;
        }

        @Override
        public void sort(CharComparator comparator) {
        }

        @Override
        public void unstableSort(CharComparator comparator) {
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Character> comparator) {
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Character> comparator) {
        }

        @Override
        public CharListIterator listIterator() {
            return CharIterators.EMPTY_ITERATOR;
        }

        @Override
        public CharListIterator iterator() {
            return CharIterators.EMPTY_ITERATOR;
        }

        @Override
        public CharListIterator listIterator(int i) {
            if (i == 0) {
                return CharIterators.EMPTY_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }

        @Override
        public CharList subList(int from, int to) {
            if (from == 0 && to == 0) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void getElements(int from, char[] a, int offset, int length) {
            if (from == 0 && length == 0 && offset >= 0 && offset <= a.length) {
                return;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, char[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, char[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(char[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, char[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, char[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareTo(List<? extends Character> o) {
            if (o == this) {
                return 0;
            }
            return o.isEmpty() ? 0 : -1;
        }

        public Object clone() {
            return EMPTY_LIST;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof List && ((List)o).isEmpty();
        }

        @Override
        public String toString() {
            return "[]";
        }

        private Object readResolve() {
            return EMPTY_LIST;
        }
    }

    public static class Singleton
    extends AbstractCharList
    implements RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final char element;

        protected Singleton(char element) {
            this.element = element;
        }

        @Override
        public char getChar(int i) {
            if (i == 0) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char removeChar(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(char k) {
            return k == this.element;
        }

        @Override
        public int indexOf(char k) {
            return k == this.element ? 0 : -1;
        }

        @Override
        public char[] toCharArray() {
            return new char[]{this.element};
        }

        @Override
        public CharListIterator listIterator() {
            return CharIterators.singleton(this.element);
        }

        @Override
        public CharListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public CharSpliterator spliterator() {
            return CharSpliterators.singleton(this.element);
        }

        @Override
        public CharListIterator listIterator(int i) {
            if (i > 1 || i < 0) {
                throw new IndexOutOfBoundsException();
            }
            CharListIterator l = this.listIterator();
            if (i == 1) {
                l.nextChar();
            }
            return l;
        }

        @Override
        public CharList subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            if (from != 0 || to != 1) {
                return EMPTY_LIST;
            }
            return this;
        }

        @Override
        @Deprecated
        public void forEach(Consumer<? super Character> action) {
            action.accept(Character.valueOf(this.element));
        }

        @Override
        public boolean addAll(int i, Collection<? extends Character> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends Character> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean removeIf(Predicate<? super Character> filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Character> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(CharUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEach(CharConsumer action) {
            action.accept(this.element);
        }

        @Override
        public boolean addAll(CharList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, CharList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(CharPredicate filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public IntIterator intIterator() {
            return IntIterators.singleton(this.element);
        }

        @Override
        public IntSpliterator intSpliterator() {
            return IntSpliterators.singleton(this.element);
        }

        @Override
        @Deprecated
        public Object[] toArray() {
            return new Object[]{Character.valueOf(this.element)};
        }

        @Override
        public void sort(CharComparator comparator) {
        }

        @Override
        public void unstableSort(CharComparator comparator) {
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Character> comparator) {
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Character> comparator) {
        }

        @Override
        public void getElements(int from, char[] a, int offset, int length) {
            if (offset < 0) {
                throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
            }
            if (offset + length > a.length) {
                throw new ArrayIndexOutOfBoundsException("End index (" + (offset + length) + ") is greater than array length (" + a.length + ")");
            }
            if (from + length > this.size()) {
                throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size() + ")");
            }
            if (length <= 0) {
                return;
            }
            a[offset] = this.element;
        }

        @Override
        public void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, char[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, char[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(char[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, char[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, char[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public void size(int size) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return this;
        }
    }

    public static class SynchronizedRandomAccessList
    extends SynchronizedList
    implements RandomAccess,
    Serializable {
        private static final long serialVersionUID = 0L;

        protected SynchronizedRandomAccessList(CharList l, Object sync) {
            super(l, sync);
        }

        protected SynchronizedRandomAccessList(CharList l) {
            super(l);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public CharList subList(int from, int to) {
            Object object = this.sync;
            synchronized (object) {
                return new SynchronizedRandomAccessList(this.list.subList(from, to), this.sync);
            }
        }
    }

    public static class SynchronizedList
    extends CharCollections.SynchronizedCollection
    implements CharList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final CharList list;

        protected SynchronizedList(CharList l, Object sync) {
            super(l, sync);
            this.list = l;
        }

        protected SynchronizedList(CharList l) {
            super(l);
            this.list = l;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char getChar(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getChar(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char set(int i, char k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(int i, char k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char removeChar(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeChar(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int indexOf(char k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int lastIndexOf(char k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeIf(CharPredicate filter) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeIf(filter);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(CharUnaryOperator operator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.replaceAll(operator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, Collection<? extends Character> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(int from, char[] a, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.getElements(from, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeElements(int from, int to) {
            Object object = this.sync;
            synchronized (object) {
                this.list.removeElements(from, to);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(int index, char[] a, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(int index, char[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(char[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.setElements(a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(int index, char[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.setElements(index, a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setElements(int index, char[] a, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.setElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void size(int size) {
            Object object = this.sync;
            synchronized (object) {
                this.list.size(size);
            }
        }

        @Override
        public CharListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public CharListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public CharListIterator listIterator(int i) {
            return this.list.listIterator(i);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public CharList subList(int from, int to) {
            Object object = this.sync;
            synchronized (object) {
                return new SynchronizedList(this.list.subList(from, to), this.sync);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            Object object = this.sync;
            synchronized (object) {
                return this.collection.equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.hashCode();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int compareTo(List<? extends Character> o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, CharCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, CharList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(CharList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Character get(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.get(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public void add(int i, Character k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Character set(int index, Character k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(index, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Character remove(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.remove(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public int indexOf(Object o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public int lastIndexOf(Object o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void sort(CharComparator comparator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.sort(comparator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unstableSort(CharComparator comparator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.unstableSort(comparator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public void sort(Comparator<? super Character> comparator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.sort(comparator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Character> comparator) {
            Object object = this.sync;
            synchronized (object) {
                this.list.unstableSort(comparator);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void writeObject(ObjectOutputStream s) throws IOException {
            Object object = this.sync;
            synchronized (object) {
                s.defaultWriteObject();
            }
        }
    }

    public static class UnmodifiableRandomAccessList
    extends UnmodifiableList
    implements RandomAccess,
    Serializable {
        private static final long serialVersionUID = 0L;

        protected UnmodifiableRandomAccessList(CharList l) {
            super(l);
        }

        @Override
        public CharList subList(int from, int to) {
            return new UnmodifiableRandomAccessList(this.list.subList(from, to));
        }
    }

    public static class UnmodifiableList
    extends CharCollections.UnmodifiableCollection
    implements CharList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final CharList list;

        protected UnmodifiableList(CharList l) {
            super(l);
            this.list = l;
        }

        @Override
        public char getChar(int i) {
            return this.list.getChar(i);
        }

        @Override
        public char set(int i, char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int i, char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char removeChar(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(char k) {
            return this.list.indexOf(k);
        }

        @Override
        public int lastIndexOf(char k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Character> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void replaceAll(UnaryOperator<Character> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(int from, char[] a, int offset, int length) {
            this.list.getElements(from, a, offset, length);
        }

        @Override
        public void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, char[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, char[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(char[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, char[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setElements(int index, char[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int size) {
            this.list.size(size);
        }

        @Override
        public CharListIterator listIterator() {
            return CharIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public CharListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public CharListIterator listIterator(int i) {
            return CharIterators.unmodifiable(this.list.listIterator(i));
        }

        @Override
        public CharList subList(int from, int to) {
            return new UnmodifiableList(this.list.subList(from, to));
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return this.collection.equals(o);
        }

        @Override
        public int hashCode() {
            return this.collection.hashCode();
        }

        @Override
        public int compareTo(List<? extends Character> o) {
            return this.list.compareTo(o);
        }

        @Override
        public boolean addAll(int index, CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(CharList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, CharList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(IntUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Character get(int i) {
            return this.list.get(i);
        }

        @Override
        @Deprecated
        public void add(int i, Character k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Character set(int index, Character k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public Character remove(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public int indexOf(Object o) {
            return this.list.indexOf(o);
        }

        @Override
        @Deprecated
        public int lastIndexOf(Object o) {
            return this.list.lastIndexOf(o);
        }

        @Override
        public void sort(CharComparator comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void unstableSort(CharComparator comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void sort(Comparator<? super Character> comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public void unstableSort(Comparator<? super Character> comparator) {
            throw new UnsupportedOperationException();
        }
    }

    static abstract class ImmutableListBase
    extends AbstractCharList
    implements CharList {
        ImmutableListBase() {
        }

        @Override
        @Deprecated
        public final void add(int index, char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean add(char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(Collection<? extends Character> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, Collection<? extends Character> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final char removeChar(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean rem(char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeIf(Predicate<? super Character> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeIf(CharPredicate c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void replaceAll(UnaryOperator<Character> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void replaceAll(IntUnaryOperator operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void add(int index, Character k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean add(Character k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final Character remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean remove(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final Character set(int index, Character k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(CharList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean addAll(int index, CharList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean removeAll(CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final boolean retainAll(CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final char set(int index, char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void size(int size) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void addElements(int index, char[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void setElements(int index, char[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void sort(CharComparator comp) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void unstableSort(CharComparator comp) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void sort(Comparator<? super Character> comparator) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public final void unstableSort(Comparator<? super Character> comparator) {
            throw new UnsupportedOperationException();
        }
    }
}

