/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public abstract class MutableOptionValue<T> {
    abstract double asDouble() throws NumberFormatException;

    abstract long asLong() throws NumberFormatException;

    abstract int asInt() throws NumberFormatException;

    abstract boolean asBoolean() throws IllegalStateException;

    abstract int[] asIntArray() throws IllegalStateException;

    abstract String asString();

    abstract T asObject();

    static MutableOptionValue<String> fromString(String string) {
        return new MutableOptionStringValue(string);
    }

    static MutableOptionValue<Double> fromDouble(double d) {
        return new MutableOptionDoubleValue(d);
    }

    static MutableOptionValue<Long> fromLong(long l) {
        return new MutableOptionLongValue(l);
    }

    static MutableOptionValue<Integer> fromInt(int n) {
        return new MutableOptionIntValue(n);
    }

    static MutableOptionValue<Boolean> fromBoolean(boolean bl) {
        return new MutableOptionBooleanValue(bl);
    }

    static MutableOptionValue<int[]> fromIntArray(int[] nArray) {
        return new MutableOptionIntArrayValue(nArray);
    }

    static <N extends Enum<N>> MutableOptionValue<N> fromEnum(N n) {
        return new MutableOptionEnumValue<N>(n);
    }

    static class MutableOptionEnumValue<T extends Enum<T>>
    extends MutableOptionValueObject<T> {
        MutableOptionEnumValue(T t) {
            super(t);
        }

        @Override
        double asDouble() throws NumberFormatException {
            throw new NumberFormatException("Enum is not applicable as double");
        }

        @Override
        long asLong() throws NumberFormatException {
            throw new NumberFormatException("Enum is not applicable as long");
        }

        @Override
        int asInt() throws NumberFormatException {
            throw new NumberFormatException("Enum is not applicable as int");
        }

        @Override
        boolean asBoolean() throws IllegalStateException {
            throw new NumberFormatException("Enum is not applicable as boolean");
        }

        @Override
        int[] asIntArray() throws IllegalStateException {
            throw new NumberFormatException("Enum is not applicable as int[]");
        }

        @Override
        String asString() {
            return ((Enum)this.value).name();
        }
    }

    static class MutableOptionIntArrayValue
    extends MutableOptionValueObject<int[]> {
        MutableOptionIntArrayValue(int[] nArray) {
            super(nArray);
        }

        @Override
        double asDouble() {
            throw new NumberFormatException("int[] is not applicable as double");
        }

        @Override
        long asLong() throws NumberFormatException {
            throw new NumberFormatException("int[] is not applicable as Long");
        }

        @Override
        int asInt() throws NumberFormatException {
            throw new NumberFormatException("int[] is not applicable as int");
        }

        @Override
        boolean asBoolean() {
            throw new NumberFormatException("int[] is not applicable as boolean");
        }

        @Override
        int[] asIntArray() throws IllegalStateException {
            return (int[])this.value;
        }

        @Override
        String asString() {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < ((int[])this.value).length; ++i) {
                stringBuilder.append(((int[])this.value)[i]);
                if (i + 1 >= ((int[])this.value).length) continue;
                stringBuilder.append(":");
            }
            return stringBuilder.toString();
        }
    }

    static class MutableOptionBooleanValue
    extends MutableOptionValue<Boolean> {
        private final boolean value;

        MutableOptionBooleanValue(boolean bl) {
            this.value = bl;
        }

        @Override
        double asDouble() {
            throw new NumberFormatException("boolean is not applicable as double");
        }

        @Override
        long asLong() throws NumberFormatException {
            throw new NumberFormatException("boolean is not applicable as Long");
        }

        @Override
        int asInt() throws NumberFormatException {
            throw new NumberFormatException("boolean is not applicable as int");
        }

        @Override
        boolean asBoolean() {
            return this.value;
        }

        @Override
        int[] asIntArray() throws IllegalStateException {
            throw new IllegalStateException("boolean is not applicable as int[]");
        }

        @Override
        String asString() {
            return String.valueOf(this.value);
        }

        @Override
        Boolean asObject() {
            return this.value;
        }
    }

    static class MutableOptionIntValue
    extends MutableOptionValue<Integer> {
        private final int value;

        MutableOptionIntValue(int n) {
            this.value = n;
        }

        @Override
        double asDouble() {
            return Integer.valueOf(this.value).doubleValue();
        }

        @Override
        long asLong() throws NumberFormatException {
            return this.value;
        }

        @Override
        int asInt() throws NumberFormatException {
            return this.value;
        }

        @Override
        boolean asBoolean() throws IllegalStateException {
            throw new IllegalStateException("int is not applicable as boolean");
        }

        @Override
        int[] asIntArray() throws IllegalStateException {
            return new int[]{this.value};
        }

        @Override
        String asString() {
            return String.valueOf(this.value);
        }

        @Override
        Integer asObject() {
            return this.value;
        }
    }

    static class MutableOptionLongValue
    extends MutableOptionValue<Long> {
        private final long value;

        MutableOptionLongValue(long l) {
            this.value = l;
        }

        @Override
        double asDouble() {
            return Long.valueOf(this.value).doubleValue();
        }

        @Override
        long asLong() throws NumberFormatException {
            return this.value;
        }

        @Override
        int asInt() throws NumberFormatException {
            if (this.value > Integer.MAX_VALUE || this.value < Integer.MIN_VALUE) {
                throw new NumberFormatException("long value lies outside the bounds of int");
            }
            return Long.valueOf(this.value).intValue();
        }

        @Override
        boolean asBoolean() throws IllegalStateException {
            throw new IllegalStateException("long is not applicable as boolean");
        }

        @Override
        int[] asIntArray() throws IllegalStateException {
            if (this.value > Integer.MAX_VALUE || this.value < Integer.MIN_VALUE) {
                throw new NumberFormatException("long value lies outside the bounds of int");
            }
            return new int[]{Long.valueOf(this.value).intValue()};
        }

        @Override
        String asString() {
            return String.valueOf(this.value);
        }

        @Override
        Long asObject() {
            return this.value;
        }
    }

    static class MutableOptionDoubleValue
    extends MutableOptionValue<Double> {
        private final double value;

        MutableOptionDoubleValue(double d) {
            this.value = d;
        }

        @Override
        double asDouble() {
            return this.value;
        }

        @Override
        long asLong() throws NumberFormatException {
            return Double.valueOf(this.value).longValue();
        }

        @Override
        int asInt() throws NumberFormatException {
            if (this.value > 2.147483647E9 || this.value < -2.147483648E9) {
                throw new NumberFormatException("double value lies outside the bounds of int");
            }
            return Double.valueOf(this.value).intValue();
        }

        @Override
        boolean asBoolean() throws IllegalStateException {
            throw new IllegalStateException("double is not applicable as boolean");
        }

        @Override
        int[] asIntArray() throws IllegalStateException {
            if (this.value > 2.147483647E9 || this.value < -2.147483648E9) {
                throw new NumberFormatException("double value lies outside the bounds of int");
            }
            return new int[]{Double.valueOf(this.value).intValue()};
        }

        @Override
        String asString() {
            return String.valueOf(this.value);
        }

        @Override
        Double asObject() {
            return this.value;
        }
    }

    static class MutableOptionStringValue
    extends MutableOptionValueObject<String> {
        MutableOptionStringValue(String string) {
            super(string);
        }

        @Override
        double asDouble() throws NumberFormatException {
            return Double.parseDouble((String)this.value);
        }

        @Override
        long asLong() throws NumberFormatException {
            return Long.parseLong((String)this.value);
        }

        @Override
        int asInt() throws NumberFormatException {
            return Integer.parseInt((String)this.value);
        }

        @Override
        boolean asBoolean() throws IllegalStateException {
            return Boolean.parseBoolean((String)this.value);
        }

        @Override
        int[] asIntArray() throws IllegalStateException {
            throw new IllegalStateException("String is not applicable as int[]");
        }

        @Override
        String asString() {
            return (String)this.value;
        }
    }

    private static abstract class MutableOptionValueObject<T>
    extends MutableOptionValue<T> {
        protected final T value;

        protected MutableOptionValueObject(T t) {
            this.value = t;
        }

        @Override
        T asObject() {
            return this.value;
        }
    }
}

