/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharIterators;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;

public abstract class AbstractCharCollection
extends AbstractCollection<Character>
implements CharCollection {
    protected AbstractCharCollection() {
    }

    @Override
    public abstract CharIterator iterator();

    @Override
    public boolean add(char k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(char k) {
        CharIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextChar()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean rem(char k) {
        CharIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextChar()) continue;
            iterator.remove();
            return true;
        }
        return false;
    }

    @Override
    @Deprecated
    public boolean add(Character key) {
        return CharCollection.super.add(key);
    }

    @Override
    @Deprecated
    public boolean contains(Object key) {
        return CharCollection.super.contains(key);
    }

    @Override
    @Deprecated
    public boolean remove(Object key) {
        return CharCollection.super.remove(key);
    }

    @Override
    public char[] toArray(char[] a) {
        int size = this.size();
        if (a == null) {
            a = new char[size];
        } else if (a.length < size) {
            a = Arrays.copyOf(a, size);
        }
        CharIterators.unwrap(this.iterator(), a);
        return a;
    }

    @Override
    public char[] toCharArray() {
        int size = this.size();
        if (size == 0) {
            return CharArrays.EMPTY_ARRAY;
        }
        char[] a = new char[size];
        CharIterators.unwrap(this.iterator(), a);
        return a;
    }

    @Override
    @Deprecated
    public char[] toCharArray(char[] a) {
        return this.toArray(a);
    }

    @Override
    public boolean addAll(CharCollection c) {
        boolean retVal = false;
        CharIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.add(i.nextChar())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean addAll(Collection<? extends Character> c) {
        if (c instanceof CharCollection) {
            return this.addAll((CharCollection)c);
        }
        return super.addAll(c);
    }

    @Override
    public boolean containsAll(CharCollection c) {
        CharIterator i = c.iterator();
        while (i.hasNext()) {
            if (this.contains(i.nextChar())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (c instanceof CharCollection) {
            return this.containsAll((CharCollection)c);
        }
        return super.containsAll(c);
    }

    @Override
    public boolean removeAll(CharCollection c) {
        boolean retVal = false;
        CharIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.rem(i.nextChar())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c instanceof CharCollection) {
            return this.removeAll((CharCollection)c);
        }
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(CharCollection c) {
        boolean retVal = false;
        CharIterator i = this.iterator();
        while (i.hasNext()) {
            if (c.contains(i.nextChar())) continue;
            i.remove();
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c instanceof CharCollection) {
            return this.retainAll((CharCollection)c);
        }
        return super.retainAll(c);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        CharIterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            char k = i.nextChar();
            s.append(String.valueOf(k));
        }
        s.append("}");
        return s.toString();
    }
}

