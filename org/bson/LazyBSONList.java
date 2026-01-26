/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.bson.BsonBinaryReader;
import org.bson.BsonType;
import org.bson.LazyBSONCallback;
import org.bson.LazyBSONObject;

public class LazyBSONList
extends LazyBSONObject
implements List {
    public LazyBSONList(byte[] bytes, LazyBSONCallback callback) {
        super(bytes, callback);
    }

    public LazyBSONList(byte[] bytes, int offset, LazyBSONCallback callback) {
        super(bytes, offset, callback);
    }

    @Override
    public int size() {
        return this.keySet().size();
    }

    @Override
    public boolean contains(Object o) {
        return this.indexOf(o) > -1;
    }

    @Override
    public Iterator iterator() {
        return new LazyBSONListIterator();
    }

    @Override
    public boolean containsAll(Collection collection) {
        HashSet values = new HashSet();
        for (Object o : this) {
            values.add(o);
        }
        return values.containsAll(collection);
    }

    public Object get(int index) {
        return this.get(String.valueOf(index));
    }

    @Override
    public int indexOf(Object o) {
        Iterator it = this.iterator();
        int pos = 0;
        while (it.hasNext()) {
            if (o.equals(it.next())) {
                return pos;
            }
            ++pos;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int lastFound = -1;
        Iterator it = this.iterator();
        int pos = 0;
        while (it.hasNext()) {
            if (o.equals(it.next())) {
                lastFound = pos;
            }
            ++pos;
        }
        return lastFound;
    }

    public ListIterator listIterator() {
        throw new UnsupportedOperationException("Operation is not supported instance of this type");
    }

    public ListIterator listIterator(int index) {
        throw new UnsupportedOperationException("Operation is not supported instance of this type");
    }

    @Override
    public boolean add(Object o) {
        throw new UnsupportedOperationException("Object is read only");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Object is read only");
    }

    @Override
    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException("Object is read only");
    }

    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException("Object is read only");
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException("Object is read only");
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException("Object is read only");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Object is read only");
    }

    public Object set(int index, Object element) {
        throw new UnsupportedOperationException("Object is read only");
    }

    public void add(int index, Object element) {
        throw new UnsupportedOperationException("Object is read only");
    }

    public Object remove(int index) {
        throw new UnsupportedOperationException("Object is read only");
    }

    public List subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Operation is not supported");
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Operation is not supported");
    }

    @Override
    public Object[] toArray(Object[] a) {
        throw new UnsupportedOperationException("Operation is not supported");
    }

    public class LazyBSONListIterator
    implements Iterator {
        private final BsonBinaryReader reader;
        private BsonType cachedBsonType;

        public LazyBSONListIterator() {
            this.reader = LazyBSONList.this.getBsonReader();
            this.reader.readStartDocument();
        }

        @Override
        public boolean hasNext() {
            if (this.cachedBsonType == null) {
                this.cachedBsonType = this.reader.readBsonType();
            }
            return this.cachedBsonType != BsonType.END_OF_DOCUMENT;
        }

        public Object next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.cachedBsonType = null;
            this.reader.readName();
            return LazyBSONList.this.readValue(this.reader);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Operation is not supported");
        }
    }
}

