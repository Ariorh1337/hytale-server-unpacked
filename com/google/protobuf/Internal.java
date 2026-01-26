/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.CanIgnoreReturnValue;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.Java8Compatibility;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Utf8;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;

public final class Internal {
    static final Charset UTF_8 = Charset.forName("UTF-8");
    static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.wrap(EMPTY_BYTE_ARRAY);
    public static final CodedInputStream EMPTY_CODED_INPUT_STREAM = CodedInputStream.newInstance(EMPTY_BYTE_ARRAY);

    private Internal() {
    }

    static <T> T checkNotNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    static <T> T checkNotNull(T obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
        return obj;
    }

    public static String stringDefaultValue(String bytes) {
        return new String(bytes.getBytes(ISO_8859_1), UTF_8);
    }

    public static ByteString bytesDefaultValue(String bytes) {
        return ByteString.copyFrom(bytes.getBytes(ISO_8859_1));
    }

    public static byte[] byteArrayDefaultValue(String bytes) {
        return bytes.getBytes(ISO_8859_1);
    }

    public static ByteBuffer byteBufferDefaultValue(String bytes) {
        return ByteBuffer.wrap(Internal.byteArrayDefaultValue(bytes));
    }

    public static ByteBuffer copyByteBuffer(ByteBuffer source) {
        ByteBuffer temp = source.duplicate();
        ((Buffer)temp).clear();
        ByteBuffer result = ByteBuffer.allocate(temp.capacity());
        result.put(temp);
        ((Buffer)result).clear();
        return result;
    }

    public static boolean isValidUtf8(ByteString byteString) {
        return byteString.isValidUtf8();
    }

    public static boolean isValidUtf8(byte[] byteArray) {
        return Utf8.isValidUtf8(byteArray);
    }

    public static byte[] toByteArray(String value) {
        return value.getBytes(UTF_8);
    }

    public static String toStringUtf8(byte[] bytes) {
        return new String(bytes, UTF_8);
    }

    public static int hashLong(long n) {
        return (int)(n ^ n >>> 32);
    }

    public static int hashBoolean(boolean b) {
        return b ? 1231 : 1237;
    }

    public static int hashEnum(EnumLite e) {
        return e.getNumber();
    }

    public static int hashEnumList(List<? extends EnumLite> list) {
        int hash = 1;
        for (EnumLite enumLite : list) {
            hash = 31 * hash + Internal.hashEnum(enumLite);
        }
        return hash;
    }

    public static boolean equals(List<byte[]> a, List<byte[]> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); ++i) {
            if (Arrays.equals(a.get(i), b.get(i))) continue;
            return false;
        }
        return true;
    }

    public static int hashCode(List<byte[]> list) {
        int hash = 1;
        for (byte[] bytes : list) {
            hash = 31 * hash + Internal.hashCode(bytes);
        }
        return hash;
    }

    public static int hashCode(byte[] bytes) {
        return Internal.hashCode(bytes, 0, bytes.length);
    }

    static int hashCode(byte[] bytes, int offset, int length) {
        int h = Internal.partialHash(length, bytes, offset, length);
        return h == 0 ? 1 : h;
    }

    static int partialHash(int h, byte[] bytes, int offset, int length) {
        for (int i = offset; i < offset + length; ++i) {
            h = h * 31 + bytes[i];
        }
        return h;
    }

    public static boolean equalsByteBuffer(ByteBuffer a, ByteBuffer b) {
        if (a.capacity() != b.capacity()) {
            return false;
        }
        ByteBuffer aDuplicate = a.duplicate();
        Java8Compatibility.clear(aDuplicate);
        ByteBuffer bDuplicate = b.duplicate();
        Java8Compatibility.clear(bDuplicate);
        return aDuplicate.equals(bDuplicate);
    }

    public static boolean equalsByteBuffer(List<ByteBuffer> a, List<ByteBuffer> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); ++i) {
            if (Internal.equalsByteBuffer(a.get(i), b.get(i))) continue;
            return false;
        }
        return true;
    }

    public static int hashCodeByteBuffer(List<ByteBuffer> list) {
        int hash = 1;
        for (ByteBuffer bytes : list) {
            hash = 31 * hash + Internal.hashCodeByteBuffer(bytes);
        }
        return hash;
    }

    public static int hashCodeByteBuffer(ByteBuffer bytes) {
        if (bytes.hasArray()) {
            int h = Internal.partialHash(bytes.capacity(), bytes.array(), bytes.arrayOffset(), bytes.capacity());
            return h == 0 ? 1 : h;
        }
        int bufferSize = bytes.capacity() > 4096 ? 4096 : bytes.capacity();
        byte[] buffer = new byte[bufferSize];
        ByteBuffer duplicated = bytes.duplicate();
        Java8Compatibility.clear(duplicated);
        int h = bytes.capacity();
        while (duplicated.remaining() > 0) {
            int length = duplicated.remaining() <= bufferSize ? duplicated.remaining() : bufferSize;
            duplicated.get(buffer, 0, length);
            h = Internal.partialHash(h, buffer, 0, length);
        }
        return h == 0 ? 1 : h;
    }

    public static <T extends MessageLite> T getDefaultInstance(Class<T> clazz) {
        try {
            Method method = clazz.getMethod("getDefaultInstance", new Class[0]);
            return (T)((MessageLite)method.invoke((Object)method, new Object[0]));
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to get default instance for " + clazz, e);
        }
    }

    static Object mergeMessage(Object destination, Object source) {
        return ((MessageLite)destination).toBuilder().mergeFrom((MessageLite)source).buildPartial();
    }

    public static interface EnumLite {
        public int getNumber();
    }

    public static interface FloatList
    extends ProtobufList<Float> {
        public float getFloat(int var1);

        public void addFloat(float var1);

        @CanIgnoreReturnValue
        public float setFloat(int var1, float var2);

        public FloatList mutableCopyWithCapacity(int var1);
    }

    public static interface DoubleList
    extends ProtobufList<Double> {
        public double getDouble(int var1);

        public void addDouble(double var1);

        @CanIgnoreReturnValue
        public double setDouble(int var1, double var2);

        public DoubleList mutableCopyWithCapacity(int var1);
    }

    public static interface LongList
    extends ProtobufList<Long> {
        public long getLong(int var1);

        public void addLong(long var1);

        @CanIgnoreReturnValue
        public long setLong(int var1, long var2);

        public LongList mutableCopyWithCapacity(int var1);
    }

    public static interface BooleanList
    extends ProtobufList<Boolean> {
        public boolean getBoolean(int var1);

        public void addBoolean(boolean var1);

        @CanIgnoreReturnValue
        public boolean setBoolean(int var1, boolean var2);

        public BooleanList mutableCopyWithCapacity(int var1);
    }

    public static interface IntList
    extends ProtobufList<Integer> {
        public int getInt(int var1);

        public void addInt(int var1);

        @CanIgnoreReturnValue
        public int setInt(int var1, int var2);

        public IntList mutableCopyWithCapacity(int var1);
    }

    public static interface ProtobufList<E>
    extends List<E>,
    RandomAccess {
        public void makeImmutable();

        public boolean isModifiable();

        public ProtobufList<E> mutableCopyWithCapacity(int var1);
    }

    public static class MapAdapter<K, V, RealValue>
    extends AbstractMap<K, V> {
        private final Map<K, RealValue> realMap;
        private final Converter<RealValue, V> valueConverter;

        public static <T extends EnumLite> Converter<Integer, T> newEnumConverter(final EnumLiteMap<T> enumMap, final T unrecognizedValue) {
            return new Converter<Integer, T>(){

                @Override
                public T doForward(Integer value) {
                    Object result = enumMap.findValueByNumber(value);
                    return result == null ? unrecognizedValue : result;
                }

                @Override
                public Integer doBackward(T value) {
                    return value.getNumber();
                }
            };
        }

        public MapAdapter(Map<K, RealValue> realMap, Converter<RealValue, V> valueConverter) {
            this.realMap = realMap;
            this.valueConverter = valueConverter;
        }

        @Override
        public V get(Object key) {
            RealValue result = this.realMap.get(key);
            if (result == null) {
                return null;
            }
            return this.valueConverter.doForward(result);
        }

        @Override
        public V put(K key, V value) {
            RealValue oldValue = this.realMap.put(key, this.valueConverter.doBackward(value));
            if (oldValue == null) {
                return null;
            }
            return this.valueConverter.doForward(oldValue);
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            return new SetAdapter(this.realMap.entrySet());
        }

        public static interface Converter<A, B> {
            public B doForward(A var1);

            public A doBackward(B var1);
        }

        private class SetAdapter
        extends AbstractSet<Map.Entry<K, V>> {
            private final Set<Map.Entry<K, RealValue>> realSet;

            public SetAdapter(Set<Map.Entry<K, RealValue>> realSet) {
                this.realSet = realSet;
            }

            @Override
            public Iterator<Map.Entry<K, V>> iterator() {
                return new IteratorAdapter(this.realSet.iterator());
            }

            @Override
            public int size() {
                return this.realSet.size();
            }
        }

        private class EntryAdapter
        implements Map.Entry<K, V> {
            private final Map.Entry<K, RealValue> realEntry;

            public EntryAdapter(Map.Entry<K, RealValue> realEntry) {
                this.realEntry = realEntry;
            }

            @Override
            public K getKey() {
                return this.realEntry.getKey();
            }

            @Override
            public V getValue() {
                return MapAdapter.this.valueConverter.doForward(this.realEntry.getValue());
            }

            @Override
            public V setValue(V value) {
                Object oldValue = this.realEntry.setValue(MapAdapter.this.valueConverter.doBackward(value));
                if (oldValue == null) {
                    return null;
                }
                return MapAdapter.this.valueConverter.doForward(oldValue);
            }

            @Override
            public boolean equals(Object o) {
                if (o == this) {
                    return true;
                }
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                Map.Entry other = (Map.Entry)o;
                return this.getKey().equals(other.getKey()) && this.getValue().equals(this.getValue());
            }

            @Override
            public int hashCode() {
                return this.realEntry.hashCode();
            }
        }

        private class IteratorAdapter
        implements Iterator<Map.Entry<K, V>> {
            private final Iterator<Map.Entry<K, RealValue>> realIterator;

            public IteratorAdapter(Iterator<Map.Entry<K, RealValue>> realIterator) {
                this.realIterator = realIterator;
            }

            @Override
            public boolean hasNext() {
                return this.realIterator.hasNext();
            }

            @Override
            public Map.Entry<K, V> next() {
                return new EntryAdapter(this.realIterator.next());
            }

            @Override
            public void remove() {
                this.realIterator.remove();
            }
        }
    }

    public static class ListAdapter<F, T>
    extends AbstractList<T> {
        private final List<F> fromList;
        private final Converter<F, T> converter;

        public ListAdapter(List<F> fromList, Converter<F, T> converter) {
            this.fromList = fromList;
            this.converter = converter;
        }

        @Override
        public T get(int index) {
            return this.converter.convert(this.fromList.get(index));
        }

        @Override
        public int size() {
            return this.fromList.size();
        }

        public static interface Converter<F, T> {
            public T convert(F var1);
        }
    }

    public static class IntListAdapter<T>
    extends AbstractList<T> {
        private final IntList fromList;
        private final IntConverter<T> converter;

        public IntListAdapter(IntList fromList, IntConverter<T> converter) {
            this.fromList = fromList;
            this.converter = converter;
        }

        @Override
        public T get(int index) {
            return this.converter.convert(this.fromList.getInt(index));
        }

        @Override
        public int size() {
            return this.fromList.size();
        }

        public static interface IntConverter<T> {
            public T convert(int var1);
        }
    }

    public static interface EnumVerifier {
        public boolean isInRange(int var1);
    }

    public static interface EnumLiteMap<T extends EnumLite> {
        public T findValueByNumber(int var1);
    }
}

