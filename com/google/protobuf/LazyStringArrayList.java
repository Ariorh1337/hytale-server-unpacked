/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractProtobufList;
import com.google.protobuf.ByteString;
import com.google.protobuf.CanIgnoreReturnValue;
import com.google.protobuf.Internal;
import com.google.protobuf.LazyStringList;
import com.google.protobuf.UnmodifiableLazyStringList;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

public class LazyStringArrayList
extends AbstractProtobufList<String>
implements LazyStringList,
RandomAccess {
    private static final LazyStringArrayList EMPTY_LIST = new LazyStringArrayList(false);
    @Deprecated
    public static final LazyStringList EMPTY = LazyStringArrayList.emptyList();
    private final List<Object> list;

    public static LazyStringArrayList emptyList() {
        return EMPTY_LIST;
    }

    public LazyStringArrayList() {
        this(10);
    }

    private LazyStringArrayList(boolean isMutable) {
        super(isMutable);
        this.list = Collections.emptyList();
    }

    public LazyStringArrayList(int initialCapacity) {
        this(new ArrayList<Object>(initialCapacity));
    }

    public LazyStringArrayList(LazyStringList from) {
        this.list = new ArrayList<Object>(from.size());
        this.addAll(from);
    }

    public LazyStringArrayList(List<String> from) {
        this(new ArrayList<Object>(from));
    }

    private LazyStringArrayList(ArrayList<Object> list) {
        this.list = list;
    }

    public LazyStringArrayList mutableCopyWithCapacity(int capacity) {
        if (capacity < this.size()) {
            throw new IllegalArgumentException();
        }
        ArrayList<Object> newList = new ArrayList<Object>(capacity);
        newList.addAll(this.list);
        return new LazyStringArrayList(newList);
    }

    @Override
    public String get(int index) {
        Object o = this.list.get(index);
        if (o instanceof String) {
            return (String)o;
        }
        if (o instanceof ByteString) {
            ByteString bs = (ByteString)o;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.list.set(index, s);
            }
            return s;
        }
        byte[] ba = (byte[])o;
        String s = Internal.toStringUtf8(ba);
        if (Internal.isValidUtf8(ba)) {
            this.list.set(index, s);
        }
        return s;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public void add(int index, String element) {
        this.ensureIsMutable();
        this.list.add(index, element);
        ++this.modCount;
    }

    @Override
    private void add(int index, ByteString element) {
        this.ensureIsMutable();
        this.list.add(index, element);
        ++this.modCount;
    }

    @Override
    private void add(int index, byte[] element) {
        this.ensureIsMutable();
        this.list.add(index, element);
        ++this.modCount;
    }

    @Override
    @CanIgnoreReturnValue
    public boolean add(String element) {
        this.ensureIsMutable();
        this.list.add(element);
        ++this.modCount;
        return true;
    }

    @Override
    public void add(ByteString element) {
        this.ensureIsMutable();
        this.list.add(element);
        ++this.modCount;
    }

    @Override
    public void add(byte[] element) {
        this.ensureIsMutable();
        this.list.add(element);
        ++this.modCount;
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        return this.addAll(this.size(), c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends String> c) {
        this.ensureIsMutable();
        Collection<? extends String> collection = c instanceof LazyStringList ? ((LazyStringList)c).getUnderlyingElements() : c;
        boolean ret = this.list.addAll(index, collection);
        ++this.modCount;
        return ret;
    }

    @Override
    public boolean addAllByteString(Collection<? extends ByteString> values) {
        this.ensureIsMutable();
        boolean ret = this.list.addAll(values);
        ++this.modCount;
        return ret;
    }

    @Override
    public boolean addAllByteArray(Collection<byte[]> c) {
        this.ensureIsMutable();
        boolean ret = this.list.addAll(c);
        ++this.modCount;
        return ret;
    }

    @Override
    public String remove(int index) {
        this.ensureIsMutable();
        Object o = this.list.remove(index);
        ++this.modCount;
        return LazyStringArrayList.asString(o);
    }

    @Override
    public void clear() {
        this.ensureIsMutable();
        this.list.clear();
        ++this.modCount;
    }

    @Override
    public Object getRaw(int index) {
        return this.list.get(index);
    }

    @Override
    public ByteString getByteString(int index) {
        Object o = this.list.get(index);
        ByteString b = LazyStringArrayList.asByteString(o);
        if (b != o) {
            this.list.set(index, b);
        }
        return b;
    }

    @Override
    public byte[] getByteArray(int index) {
        Object o = this.list.get(index);
        byte[] b = LazyStringArrayList.asByteArray(o);
        if (b != o) {
            this.list.set(index, b);
        }
        return b;
    }

    @Override
    public String set(int index, String s) {
        this.ensureIsMutable();
        Object o = this.list.set(index, s);
        return LazyStringArrayList.asString(o);
    }

    @Override
    public void set(int index, ByteString s) {
        this.setAndReturn(index, s);
    }

    @Override
    public void set(int index, byte[] s) {
        this.setAndReturn(index, s);
    }

    private Object setAndReturn(int index, ByteString s) {
        this.ensureIsMutable();
        return this.list.set(index, s);
    }

    private Object setAndReturn(int index, byte[] s) {
        this.ensureIsMutable();
        return this.list.set(index, s);
    }

    private static String asString(Object o) {
        if (o instanceof String) {
            return (String)o;
        }
        if (o instanceof ByteString) {
            return ((ByteString)o).toStringUtf8();
        }
        return Internal.toStringUtf8((byte[])o);
    }

    private static ByteString asByteString(Object o) {
        if (o instanceof ByteString) {
            return (ByteString)o;
        }
        if (o instanceof String) {
            return ByteString.copyFromUtf8((String)o);
        }
        return ByteString.copyFrom((byte[])o);
    }

    private static byte[] asByteArray(Object o) {
        if (o instanceof byte[]) {
            return (byte[])o;
        }
        if (o instanceof String) {
            return Internal.toByteArray((String)o);
        }
        return ((ByteString)o).toByteArray();
    }

    @Override
    public List<?> getUnderlyingElements() {
        return Collections.unmodifiableList(this.list);
    }

    @Override
    public void mergeFrom(LazyStringList other) {
        this.ensureIsMutable();
        for (Object o : other.getUnderlyingElements()) {
            if (o instanceof byte[]) {
                byte[] b = (byte[])o;
                this.list.add(Arrays.copyOf(b, b.length));
                continue;
            }
            this.list.add(o);
        }
    }

    @Override
    public List<byte[]> asByteArrayList() {
        return new ByteArrayListView(this);
    }

    @Override
    public List<ByteString> asByteStringList() {
        return new ByteStringListView(this);
    }

    @Override
    public LazyStringList getUnmodifiableView() {
        if (this.isModifiable()) {
            return new UnmodifiableLazyStringList(this);
        }
        return this;
    }

    private static class ByteArrayListView
    extends AbstractList<byte[]>
    implements RandomAccess {
        private final LazyStringArrayList list;

        ByteArrayListView(LazyStringArrayList list) {
            this.list = list;
        }

        @Override
        public byte[] get(int index) {
            return this.list.getByteArray(index);
        }

        @Override
        public int size() {
            return this.list.size();
        }

        @Override
        public byte[] set(int index, byte[] s) {
            Object o = this.list.setAndReturn(index, s);
            ++this.modCount;
            return LazyStringArrayList.asByteArray(o);
        }

        @Override
        public void add(int index, byte[] s) {
            this.list.add(index, s);
            ++this.modCount;
        }

        @Override
        public byte[] remove(int index) {
            String o = this.list.remove(index);
            ++this.modCount;
            return LazyStringArrayList.asByteArray(o);
        }
    }

    private static class ByteStringListView
    extends AbstractList<ByteString>
    implements RandomAccess {
        private final LazyStringArrayList list;

        ByteStringListView(LazyStringArrayList list) {
            this.list = list;
        }

        @Override
        public ByteString get(int index) {
            return this.list.getByteString(index);
        }

        @Override
        public int size() {
            return this.list.size();
        }

        @Override
        public ByteString set(int index, ByteString s) {
            Object o = this.list.setAndReturn(index, s);
            ++this.modCount;
            return LazyStringArrayList.asByteString(o);
        }

        @Override
        public void add(int index, ByteString s) {
            this.list.add(index, s);
            ++this.modCount;
        }

        @Override
        public ByteString remove(int index) {
            String o = this.list.remove(index);
            ++this.modCount;
            return LazyStringArrayList.asByteString(o);
        }
    }
}

