/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.rocksdb.CompressionType;
import org.rocksdb.MutableOptionKey;
import org.rocksdb.MutableOptionValue;
import org.rocksdb.OptionString;
import org.rocksdb.PrepopulateBlobCache;

public class AbstractMutableOptions {
    protected static final String KEY_VALUE_PAIR_SEPARATOR = ";";
    protected static final char KEY_VALUE_SEPARATOR = '=';
    static final String INT_ARRAY_INT_SEPARATOR = ":";
    private static final String HAS_NOT_BEEN_SET = " has not been set";
    protected final String[] keys;
    private final String[] values;

    protected AbstractMutableOptions(String[] stringArray, String[] stringArray2) {
        this.keys = stringArray;
        this.values = stringArray2;
    }

    String[] getKeys() {
        return this.keys;
    }

    String[] getValues() {
        return this.values;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.keys.length; ++i) {
            stringBuilder.append(this.keys[i]).append('=').append(this.values[i]);
            if (i + 1 >= this.keys.length) continue;
            stringBuilder.append(KEY_VALUE_PAIR_SEPARATOR);
        }
        return stringBuilder.toString();
    }

    public static abstract class AbstractMutableOptionsBuilder<T extends AbstractMutableOptions, U extends AbstractMutableOptionsBuilder<T, U, K>, K extends MutableOptionKey> {
        private final Map<K, MutableOptionValue<?>> options = new LinkedHashMap();
        private final List<OptionString.Entry> unknown = new ArrayList<OptionString.Entry>();

        protected abstract U self();

        protected abstract Map<String, K> allKeys();

        protected abstract T build(String[] var1, String[] var2);

        public T build() {
            String[] stringArray = new String[this.options.size()];
            String[] stringArray2 = new String[this.options.size()];
            int n = 0;
            for (Map.Entry<K, MutableOptionValue<?>> entry : this.options.entrySet()) {
                stringArray[n] = ((MutableOptionKey)entry.getKey()).name();
                stringArray2[n] = entry.getValue().asString();
                ++n;
            }
            return this.build(stringArray, stringArray2);
        }

        protected U setDouble(K k, double d) {
            if (k.getValueType() != MutableOptionKey.ValueType.DOUBLE) {
                throw new IllegalArgumentException(k + " does not accept a double value");
            }
            this.options.put(k, MutableOptionValue.fromDouble(d));
            return this.self();
        }

        protected double getDouble(K k) throws NoSuchElementException, NumberFormatException {
            MutableOptionValue<?> mutableOptionValue = this.options.get(k);
            if (mutableOptionValue == null) {
                throw new NoSuchElementException(k.name() + AbstractMutableOptions.HAS_NOT_BEEN_SET);
            }
            return mutableOptionValue.asDouble();
        }

        protected U setLong(K k, long l) {
            if (k.getValueType() != MutableOptionKey.ValueType.LONG) {
                throw new IllegalArgumentException(k + " does not accept a long value");
            }
            this.options.put(k, MutableOptionValue.fromLong(l));
            return this.self();
        }

        protected long getLong(K k) throws NoSuchElementException, NumberFormatException {
            MutableOptionValue<?> mutableOptionValue = this.options.get(k);
            if (mutableOptionValue == null) {
                throw new NoSuchElementException(k.name() + AbstractMutableOptions.HAS_NOT_BEEN_SET);
            }
            return mutableOptionValue.asLong();
        }

        protected U setInt(K k, int n) {
            if (k.getValueType() != MutableOptionKey.ValueType.INT) {
                throw new IllegalArgumentException(k + " does not accept an integer value");
            }
            this.options.put(k, MutableOptionValue.fromInt(n));
            return this.self();
        }

        protected int getInt(K k) throws NoSuchElementException, NumberFormatException {
            MutableOptionValue<?> mutableOptionValue = this.options.get(k);
            if (mutableOptionValue == null) {
                throw new NoSuchElementException(k.name() + AbstractMutableOptions.HAS_NOT_BEEN_SET);
            }
            return mutableOptionValue.asInt();
        }

        protected U setBoolean(K k, boolean bl) {
            if (k.getValueType() != MutableOptionKey.ValueType.BOOLEAN) {
                throw new IllegalArgumentException(k + " does not accept a boolean value");
            }
            this.options.put(k, MutableOptionValue.fromBoolean(bl));
            return this.self();
        }

        protected boolean getBoolean(K k) throws NoSuchElementException, NumberFormatException {
            MutableOptionValue<?> mutableOptionValue = this.options.get(k);
            if (mutableOptionValue == null) {
                throw new NoSuchElementException(k.name() + AbstractMutableOptions.HAS_NOT_BEEN_SET);
            }
            return mutableOptionValue.asBoolean();
        }

        protected U setIntArray(K k, int[] nArray) {
            if (k.getValueType() != MutableOptionKey.ValueType.INT_ARRAY) {
                throw new IllegalArgumentException(k + " does not accept an int array value");
            }
            this.options.put(k, MutableOptionValue.fromIntArray(nArray));
            return this.self();
        }

        protected int[] getIntArray(K k) throws NoSuchElementException, NumberFormatException {
            MutableOptionValue<?> mutableOptionValue = this.options.get(k);
            if (mutableOptionValue == null) {
                throw new NoSuchElementException(k.name() + AbstractMutableOptions.HAS_NOT_BEEN_SET);
            }
            return mutableOptionValue.asIntArray();
        }

        protected U setString(K k, String string) {
            if (k.getValueType() != MutableOptionKey.ValueType.STRING) {
                throw new IllegalArgumentException(k + " does not accept a string value");
            }
            this.options.put(k, MutableOptionValue.fromString(string));
            return this.self();
        }

        protected String getString(K k) {
            MutableOptionValue<?> mutableOptionValue = this.options.get(k);
            if (mutableOptionValue == null) {
                throw new NoSuchElementException(k.name() + AbstractMutableOptions.HAS_NOT_BEEN_SET);
            }
            return mutableOptionValue.asString();
        }

        protected <N extends Enum<N>> U setEnum(K k, N n) {
            if (k.getValueType() != MutableOptionKey.ValueType.ENUM) {
                throw new IllegalArgumentException(k + " does not accept a Enum value");
            }
            this.options.put(k, MutableOptionValue.fromEnum(n));
            return this.self();
        }

        protected <N extends Enum<N>> N getEnum(K k) throws NoSuchElementException, NumberFormatException {
            MutableOptionValue<?> mutableOptionValue = this.options.get(k);
            if (mutableOptionValue == null) {
                throw new NoSuchElementException(k.name() + AbstractMutableOptions.HAS_NOT_BEEN_SET);
            }
            if (!(mutableOptionValue instanceof MutableOptionValue.MutableOptionEnumValue)) {
                throw new NoSuchElementException(k.name() + " is not of Enum type");
            }
            return (N)((Enum)((MutableOptionValue.MutableOptionEnumValue)mutableOptionValue).asObject());
        }

        private long parseAsLong(String string) {
            try {
                return Long.parseLong(string);
            }
            catch (NumberFormatException numberFormatException) {
                double d = Double.parseDouble(string);
                if (d != (double)Math.round(d)) {
                    throw new IllegalArgumentException("Unable to parse or round " + string + " to long", numberFormatException);
                }
                return Math.round(d);
            }
        }

        private int parseAsInt(String string) {
            try {
                return Integer.parseInt(string);
            }
            catch (NumberFormatException numberFormatException) {
                double d = Double.parseDouble(string);
                if (d != (double)Math.round(d)) {
                    throw new IllegalArgumentException("Unable to parse or round " + string + " to int", numberFormatException);
                }
                return (int)Math.round(d);
            }
        }

        protected U fromParsed(List<OptionString.Entry> list, boolean bl) {
            Objects.requireNonNull(list);
            for (OptionString.Entry entry : list) {
                try {
                    if (entry.key.isEmpty()) {
                        throw new IllegalArgumentException("options string is invalid: " + entry);
                    }
                    this.fromOptionString(entry, bl);
                }
                catch (NumberFormatException numberFormatException) {
                    throw new IllegalArgumentException("" + entry.key + "=" + entry.value + " - not a valid value for its type", numberFormatException);
                }
            }
            return this.self();
        }

        private U fromOptionString(OptionString.Entry entry, boolean bl) throws IllegalArgumentException {
            Objects.requireNonNull(entry.key);
            Objects.requireNonNull(entry.value);
            MutableOptionKey mutableOptionKey = (MutableOptionKey)this.allKeys().get(entry.key);
            if (mutableOptionKey == null && bl) {
                this.unknown.add(entry);
                return this.self();
            }
            if (mutableOptionKey == null) {
                throw new IllegalArgumentException("Key: " + null + " is not a known option key");
            }
            if (!entry.value.isList()) {
                throw new IllegalArgumentException("Option: " + mutableOptionKey + " is not a simple value or list, don't know how to parse it");
            }
            if (mutableOptionKey.getValueType() != MutableOptionKey.ValueType.INT_ARRAY && mutableOptionKey.getValueType() != MutableOptionKey.ValueType.STRING && entry.value.list.size() != 1) {
                throw new IllegalArgumentException("Simple value does not have exactly 1 item: " + entry.value.list);
            }
            List<String> list = entry.value.list;
            String string = list.get(0);
            switch (mutableOptionKey.getValueType()) {
                case DOUBLE: {
                    return this.setDouble(mutableOptionKey, Double.parseDouble(string));
                }
                case LONG: {
                    return this.setLong(mutableOptionKey, this.parseAsLong(string));
                }
                case INT: {
                    return this.setInt(mutableOptionKey, this.parseAsInt(string));
                }
                case BOOLEAN: {
                    return this.setBoolean(mutableOptionKey, Boolean.parseBoolean(string));
                }
                case INT_ARRAY: {
                    int[] nArray = new int[list.size()];
                    for (int i = 0; i < list.size(); ++i) {
                        nArray[i] = Integer.parseInt(list.get(i));
                    }
                    return this.setIntArray(mutableOptionKey, nArray);
                }
                case ENUM: {
                    String string2 = mutableOptionKey.name();
                    if ("prepopulate_blob_cache".equals(string2)) {
                        PrepopulateBlobCache prepopulateBlobCache = PrepopulateBlobCache.getFromInternal(string);
                        return this.setEnum(mutableOptionKey, prepopulateBlobCache);
                    }
                    if ("compression".equals(string2) || "blob_compression_type".equals(string2)) {
                        CompressionType compressionType = CompressionType.getFromInternal(string);
                        return this.setEnum(mutableOptionKey, compressionType);
                    }
                    throw new IllegalArgumentException("Unknown enum type: " + mutableOptionKey.name());
                }
                case STRING: {
                    return this.setString(mutableOptionKey, entry.value.toString());
                }
            }
            throw new IllegalStateException(mutableOptionKey + " has unknown value type: " + (Object)((Object)mutableOptionKey.getValueType()));
        }

        public List<OptionString.Entry> getUnknown() {
            return new ArrayList<OptionString.Entry>(this.unknown);
        }
    }
}

