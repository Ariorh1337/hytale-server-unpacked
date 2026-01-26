/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Android;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

final class UnsafeUtil {
    private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();
    private static final Class<?> MEMORY_CLASS = Android.getMemoryClass();
    private static final boolean IS_ANDROID_64 = UnsafeUtil.determineAndroidSupportByAddressSize(Long.TYPE);
    private static final boolean IS_ANDROID_32 = UnsafeUtil.determineAndroidSupportByAddressSize(Integer.TYPE);
    private static final MemoryAccessor MEMORY_ACCESSOR = UnsafeUtil.getMemoryAccessor();
    private static final boolean HAS_UNSAFE_BYTEBUFFER_OPERATIONS = UnsafeUtil.supportsUnsafeByteBufferOperations();
    private static final boolean HAS_UNSAFE_ARRAY_OPERATIONS = UnsafeUtil.supportsUnsafeArrayOperations();
    static final long BYTE_ARRAY_BASE_OFFSET = UnsafeUtil.arrayBaseOffset(byte[].class);
    private static final long BOOLEAN_ARRAY_BASE_OFFSET = UnsafeUtil.arrayBaseOffset(boolean[].class);
    private static final long BOOLEAN_ARRAY_INDEX_SCALE = UnsafeUtil.arrayIndexScale(boolean[].class);
    private static final long INT_ARRAY_BASE_OFFSET = UnsafeUtil.arrayBaseOffset(int[].class);
    private static final long INT_ARRAY_INDEX_SCALE = UnsafeUtil.arrayIndexScale(int[].class);
    private static final long LONG_ARRAY_BASE_OFFSET = UnsafeUtil.arrayBaseOffset(long[].class);
    private static final long LONG_ARRAY_INDEX_SCALE = UnsafeUtil.arrayIndexScale(long[].class);
    private static final long FLOAT_ARRAY_BASE_OFFSET = UnsafeUtil.arrayBaseOffset(float[].class);
    private static final long FLOAT_ARRAY_INDEX_SCALE = UnsafeUtil.arrayIndexScale(float[].class);
    private static final long DOUBLE_ARRAY_BASE_OFFSET = UnsafeUtil.arrayBaseOffset(double[].class);
    private static final long DOUBLE_ARRAY_INDEX_SCALE = UnsafeUtil.arrayIndexScale(double[].class);
    private static final long OBJECT_ARRAY_BASE_OFFSET = UnsafeUtil.arrayBaseOffset(Object[].class);
    private static final long OBJECT_ARRAY_INDEX_SCALE = UnsafeUtil.arrayIndexScale(Object[].class);
    private static final long BUFFER_ADDRESS_OFFSET = UnsafeUtil.fieldOffset(UnsafeUtil.bufferAddressField());
    private static final int STRIDE = 8;
    private static final int STRIDE_ALIGNMENT_MASK = 7;
    private static final int BYTE_ARRAY_ALIGNMENT = (int)(BYTE_ARRAY_BASE_OFFSET & 7L);
    static final boolean IS_BIG_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;

    private UnsafeUtil() {
    }

    static boolean hasUnsafeArrayOperations() {
        return HAS_UNSAFE_ARRAY_OPERATIONS;
    }

    static boolean hasUnsafeByteBufferOperations() {
        return HAS_UNSAFE_BYTEBUFFER_OPERATIONS;
    }

    static <T> T allocateInstance(Class<T> clazz) {
        try {
            return (T)UNSAFE.allocateInstance(clazz);
        }
        catch (InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    static long objectFieldOffset(Field field) {
        return MEMORY_ACCESSOR.objectFieldOffset(field);
    }

    private static int arrayBaseOffset(Class<?> clazz) {
        return HAS_UNSAFE_ARRAY_OPERATIONS ? MEMORY_ACCESSOR.arrayBaseOffset(clazz) : -1;
    }

    private static int arrayIndexScale(Class<?> clazz) {
        return HAS_UNSAFE_ARRAY_OPERATIONS ? MEMORY_ACCESSOR.arrayIndexScale(clazz) : -1;
    }

    static byte getByte(Object target, long offset) {
        return MEMORY_ACCESSOR.getByte(target, offset);
    }

    static void putByte(Object target, long offset, byte value) {
        MEMORY_ACCESSOR.putByte(target, offset, value);
    }

    static int getInt(Object target, long offset) {
        return MEMORY_ACCESSOR.getInt(target, offset);
    }

    static void putInt(Object target, long offset, int value) {
        MEMORY_ACCESSOR.putInt(target, offset, value);
    }

    static long getLong(Object target, long offset) {
        return MEMORY_ACCESSOR.getLong(target, offset);
    }

    static void putLong(Object target, long offset, long value) {
        MEMORY_ACCESSOR.putLong(target, offset, value);
    }

    static boolean getBoolean(Object target, long offset) {
        return MEMORY_ACCESSOR.getBoolean(target, offset);
    }

    static void putBoolean(Object target, long offset, boolean value) {
        MEMORY_ACCESSOR.putBoolean(target, offset, value);
    }

    static float getFloat(Object target, long offset) {
        return MEMORY_ACCESSOR.getFloat(target, offset);
    }

    static void putFloat(Object target, long offset, float value) {
        MEMORY_ACCESSOR.putFloat(target, offset, value);
    }

    static double getDouble(Object target, long offset) {
        return MEMORY_ACCESSOR.getDouble(target, offset);
    }

    static void putDouble(Object target, long offset, double value) {
        MEMORY_ACCESSOR.putDouble(target, offset, value);
    }

    static Object getObject(Object target, long offset) {
        return MEMORY_ACCESSOR.getObject(target, offset);
    }

    static void putObject(Object target, long offset, Object value) {
        MEMORY_ACCESSOR.putObject(target, offset, value);
    }

    static byte getByte(byte[] target, long index) {
        return MEMORY_ACCESSOR.getByte(target, BYTE_ARRAY_BASE_OFFSET + index);
    }

    static void putByte(byte[] target, long index, byte value) {
        MEMORY_ACCESSOR.putByte(target, BYTE_ARRAY_BASE_OFFSET + index, value);
    }

    static int getInt(int[] target, long index) {
        return MEMORY_ACCESSOR.getInt(target, INT_ARRAY_BASE_OFFSET + index * INT_ARRAY_INDEX_SCALE);
    }

    static void putInt(int[] target, long index, int value) {
        MEMORY_ACCESSOR.putInt(target, INT_ARRAY_BASE_OFFSET + index * INT_ARRAY_INDEX_SCALE, value);
    }

    static long getLong(long[] target, long index) {
        return MEMORY_ACCESSOR.getLong(target, LONG_ARRAY_BASE_OFFSET + index * LONG_ARRAY_INDEX_SCALE);
    }

    static void putLong(long[] target, long index, long value) {
        MEMORY_ACCESSOR.putLong(target, LONG_ARRAY_BASE_OFFSET + index * LONG_ARRAY_INDEX_SCALE, value);
    }

    static boolean getBoolean(boolean[] target, long index) {
        return MEMORY_ACCESSOR.getBoolean(target, BOOLEAN_ARRAY_BASE_OFFSET + index * BOOLEAN_ARRAY_INDEX_SCALE);
    }

    static void putBoolean(boolean[] target, long index, boolean value) {
        MEMORY_ACCESSOR.putBoolean(target, BOOLEAN_ARRAY_BASE_OFFSET + index * BOOLEAN_ARRAY_INDEX_SCALE, value);
    }

    static float getFloat(float[] target, long index) {
        return MEMORY_ACCESSOR.getFloat(target, FLOAT_ARRAY_BASE_OFFSET + index * FLOAT_ARRAY_INDEX_SCALE);
    }

    static void putFloat(float[] target, long index, float value) {
        MEMORY_ACCESSOR.putFloat(target, FLOAT_ARRAY_BASE_OFFSET + index * FLOAT_ARRAY_INDEX_SCALE, value);
    }

    static double getDouble(double[] target, long index) {
        return MEMORY_ACCESSOR.getDouble(target, DOUBLE_ARRAY_BASE_OFFSET + index * DOUBLE_ARRAY_INDEX_SCALE);
    }

    static void putDouble(double[] target, long index, double value) {
        MEMORY_ACCESSOR.putDouble(target, DOUBLE_ARRAY_BASE_OFFSET + index * DOUBLE_ARRAY_INDEX_SCALE, value);
    }

    static Object getObject(Object[] target, long index) {
        return MEMORY_ACCESSOR.getObject(target, OBJECT_ARRAY_BASE_OFFSET + index * OBJECT_ARRAY_INDEX_SCALE);
    }

    static void putObject(Object[] target, long index, Object value) {
        MEMORY_ACCESSOR.putObject(target, OBJECT_ARRAY_BASE_OFFSET + index * OBJECT_ARRAY_INDEX_SCALE, value);
    }

    static void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
        MEMORY_ACCESSOR.copyMemory(src, srcIndex, targetOffset, length);
    }

    static void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
        MEMORY_ACCESSOR.copyMemory(srcOffset, target, targetIndex, length);
    }

    static void copyMemory(byte[] src, long srcIndex, byte[] target, long targetIndex, long length) {
        System.arraycopy(src, (int)srcIndex, target, (int)targetIndex, (int)length);
    }

    static byte getByte(long address) {
        return MEMORY_ACCESSOR.getByte(address);
    }

    static void putByte(long address, byte value) {
        MEMORY_ACCESSOR.putByte(address, value);
    }

    static int getInt(long address) {
        return MEMORY_ACCESSOR.getInt(address);
    }

    static void putInt(long address, int value) {
        MEMORY_ACCESSOR.putInt(address, value);
    }

    static long getLong(long address) {
        return MEMORY_ACCESSOR.getLong(address);
    }

    static void putLong(long address, long value) {
        MEMORY_ACCESSOR.putLong(address, value);
    }

    static long addressOffset(ByteBuffer buffer) {
        return MEMORY_ACCESSOR.getLong(buffer, BUFFER_ADDRESS_OFFSET);
    }

    static Object getStaticObject(Field field) {
        return MEMORY_ACCESSOR.getStaticObject(field);
    }

    static Unsafe getUnsafe() {
        Unsafe unsafe = null;
        try {
            unsafe = AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>(){

                @Override
                public Unsafe run() throws Exception {
                    Class<Unsafe> k = Unsafe.class;
                    for (Field f : k.getDeclaredFields()) {
                        f.setAccessible(true);
                        Object x = f.get(null);
                        if (!k.isInstance(x)) continue;
                        return (Unsafe)k.cast(x);
                    }
                    return null;
                }
            });
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return unsafe;
    }

    private static MemoryAccessor getMemoryAccessor() {
        if (UNSAFE == null) {
            return null;
        }
        if (Android.isOnAndroidDevice()) {
            if (IS_ANDROID_64) {
                return new Android64MemoryAccessor(UNSAFE);
            }
            if (IS_ANDROID_32) {
                return new Android32MemoryAccessor(UNSAFE);
            }
            return null;
        }
        return new JvmMemoryAccessor(UNSAFE);
    }

    private static boolean supportsUnsafeArrayOperations() {
        if (MEMORY_ACCESSOR == null) {
            return false;
        }
        return MEMORY_ACCESSOR.supportsUnsafeArrayOperations();
    }

    private static boolean supportsUnsafeByteBufferOperations() {
        if (MEMORY_ACCESSOR == null) {
            return false;
        }
        return MEMORY_ACCESSOR.supportsUnsafeByteBufferOperations();
    }

    static boolean determineAndroidSupportByAddressSize(Class<?> addressClass) {
        if (!Android.isOnAndroidDevice()) {
            return false;
        }
        try {
            Class<?> clazz = MEMORY_CLASS;
            clazz.getMethod("peekLong", addressClass, Boolean.TYPE);
            clazz.getMethod("pokeLong", addressClass, Long.TYPE, Boolean.TYPE);
            clazz.getMethod("pokeInt", addressClass, Integer.TYPE, Boolean.TYPE);
            clazz.getMethod("peekInt", addressClass, Boolean.TYPE);
            clazz.getMethod("pokeByte", addressClass, Byte.TYPE);
            clazz.getMethod("peekByte", addressClass);
            clazz.getMethod("pokeByteArray", addressClass, byte[].class, Integer.TYPE, Integer.TYPE);
            clazz.getMethod("peekByteArray", addressClass, byte[].class, Integer.TYPE, Integer.TYPE);
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }

    private static Field bufferAddressField() {
        Field field;
        if (Android.isOnAndroidDevice() && (field = UnsafeUtil.field(Buffer.class, "effectiveDirectAddress")) != null) {
            return field;
        }
        field = UnsafeUtil.field(Buffer.class, "address");
        return field != null && field.getType() == Long.TYPE ? field : null;
    }

    private static int firstDifferingByteIndexNativeEndian(long left, long right) {
        int n = IS_BIG_ENDIAN ? Long.numberOfLeadingZeros(left ^ right) : Long.numberOfTrailingZeros(left ^ right);
        return n >> 3;
    }

    static int mismatch(byte[] left, int leftOff, byte[] right, int rightOff, int length) {
        if (leftOff < 0 || rightOff < 0 || length < 0 || leftOff + length > left.length || rightOff + length > right.length) {
            throw new IndexOutOfBoundsException();
        }
        int index = 0;
        if (HAS_UNSAFE_ARRAY_OPERATIONS) {
            int leftAlignment = BYTE_ARRAY_ALIGNMENT + leftOff & 7;
            while (index < length && (leftAlignment & 7) != 0) {
                if (left[leftOff + index] != right[rightOff + index]) {
                    return index;
                }
                ++index;
                ++leftAlignment;
            }
            int strideLength = (length - index & 0xFFFFFFF8) + index;
            while (index < strideLength) {
                long rightLongWord;
                long leftLongWord = UnsafeUtil.getLong((Object)left, BYTE_ARRAY_BASE_OFFSET + (long)leftOff + (long)index);
                if (leftLongWord != (rightLongWord = UnsafeUtil.getLong((Object)right, BYTE_ARRAY_BASE_OFFSET + (long)rightOff + (long)index))) {
                    return index + UnsafeUtil.firstDifferingByteIndexNativeEndian(leftLongWord, rightLongWord);
                }
                index += 8;
            }
        }
        while (index < length) {
            if (left[leftOff + index] != right[rightOff + index]) {
                return index;
            }
            ++index;
        }
        return -1;
    }

    private static long fieldOffset(Field field) {
        return field == null || MEMORY_ACCESSOR == null ? -1L : MEMORY_ACCESSOR.objectFieldOffset(field);
    }

    private static Field field(Class<?> clazz, String fieldName) {
        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
        }
        catch (Throwable t) {
            field = null;
        }
        return field;
    }

    private static byte getByteBigEndian(Object target, long offset) {
        return (byte)(UnsafeUtil.getInt(target, offset & 0xFFFFFFFFFFFFFFFCL) >>> (int)(((offset ^ 0xFFFFFFFFFFFFFFFFL) & 3L) << 3) & 0xFF);
    }

    private static byte getByteLittleEndian(Object target, long offset) {
        return (byte)(UnsafeUtil.getInt(target, offset & 0xFFFFFFFFFFFFFFFCL) >>> (int)((offset & 3L) << 3) & 0xFF);
    }

    private static void putByteBigEndian(Object target, long offset, byte value) {
        int intValue = UnsafeUtil.getInt(target, offset & 0xFFFFFFFFFFFFFFFCL);
        int shift = (~((int)offset) & 3) << 3;
        int output = intValue & ~(255 << shift) | (0xFF & value) << shift;
        UnsafeUtil.putInt(target, offset & 0xFFFFFFFFFFFFFFFCL, output);
    }

    private static void putByteLittleEndian(Object target, long offset, byte value) {
        int intValue = UnsafeUtil.getInt(target, offset & 0xFFFFFFFFFFFFFFFCL);
        int shift = ((int)offset & 3) << 3;
        int output = intValue & ~(255 << shift) | (0xFF & value) << shift;
        UnsafeUtil.putInt(target, offset & 0xFFFFFFFFFFFFFFFCL, output);
    }

    private static boolean getBooleanBigEndian(Object target, long offset) {
        return UnsafeUtil.getByteBigEndian(target, offset) != 0;
    }

    private static boolean getBooleanLittleEndian(Object target, long offset) {
        return UnsafeUtil.getByteLittleEndian(target, offset) != 0;
    }

    private static void putBooleanBigEndian(Object target, long offset, boolean value) {
        UnsafeUtil.putByteBigEndian(target, offset, (byte)(value ? 1 : 0));
    }

    private static void putBooleanLittleEndian(Object target, long offset, boolean value) {
        UnsafeUtil.putByteLittleEndian(target, offset, (byte)(value ? 1 : 0));
    }

    private static void logMissingMethod(Throwable e) {
        Logger.getLogger(UnsafeUtil.class.getName()).log(Level.WARNING, "platform method missing - proto runtime falling back to safer methods: " + e);
    }

    private static abstract class MemoryAccessor {
        Unsafe unsafe;

        MemoryAccessor(Unsafe unsafe) {
            this.unsafe = unsafe;
        }

        public final long objectFieldOffset(Field field) {
            return this.unsafe.objectFieldOffset(field);
        }

        public final int arrayBaseOffset(Class<?> clazz) {
            return this.unsafe.arrayBaseOffset(clazz);
        }

        public final int arrayIndexScale(Class<?> clazz) {
            return this.unsafe.arrayIndexScale(clazz);
        }

        public abstract Object getStaticObject(Field var1);

        public boolean supportsUnsafeArrayOperations() {
            if (this.unsafe == null) {
                return false;
            }
            try {
                Class<?> clazz = this.unsafe.getClass();
                clazz.getMethod("objectFieldOffset", Field.class);
                clazz.getMethod("arrayBaseOffset", Class.class);
                clazz.getMethod("arrayIndexScale", Class.class);
                clazz.getMethod("getInt", Object.class, Long.TYPE);
                clazz.getMethod("putInt", Object.class, Long.TYPE, Integer.TYPE);
                clazz.getMethod("getLong", Object.class, Long.TYPE);
                clazz.getMethod("putLong", Object.class, Long.TYPE, Long.TYPE);
                clazz.getMethod("getObject", Object.class, Long.TYPE);
                clazz.getMethod("putObject", Object.class, Long.TYPE, Object.class);
                return true;
            }
            catch (Throwable e) {
                UnsafeUtil.logMissingMethod(e);
                return false;
            }
        }

        public abstract byte getByte(Object var1, long var2);

        public abstract void putByte(Object var1, long var2, byte var4);

        public final int getInt(Object target, long offset) {
            return this.unsafe.getInt(target, offset);
        }

        public final void putInt(Object target, long offset, int value) {
            this.unsafe.putInt(target, offset, value);
        }

        public final long getLong(Object target, long offset) {
            return this.unsafe.getLong(target, offset);
        }

        public final void putLong(Object target, long offset, long value) {
            this.unsafe.putLong(target, offset, value);
        }

        public abstract boolean getBoolean(Object var1, long var2);

        public abstract void putBoolean(Object var1, long var2, boolean var4);

        public abstract float getFloat(Object var1, long var2);

        public abstract void putFloat(Object var1, long var2, float var4);

        public abstract double getDouble(Object var1, long var2);

        public abstract void putDouble(Object var1, long var2, double var4);

        public final Object getObject(Object target, long offset) {
            return this.unsafe.getObject(target, offset);
        }

        public final void putObject(Object target, long offset, Object value) {
            this.unsafe.putObject(target, offset, value);
        }

        public boolean supportsUnsafeByteBufferOperations() {
            if (this.unsafe == null) {
                return false;
            }
            try {
                Class<?> clazz = this.unsafe.getClass();
                clazz.getMethod("objectFieldOffset", Field.class);
                clazz.getMethod("getLong", Object.class, Long.TYPE);
                return UnsafeUtil.bufferAddressField() != null;
            }
            catch (Throwable e) {
                UnsafeUtil.logMissingMethod(e);
                return false;
            }
        }

        public abstract byte getByte(long var1);

        public abstract void putByte(long var1, byte var3);

        public abstract int getInt(long var1);

        public abstract void putInt(long var1, int var3);

        public abstract long getLong(long var1);

        public abstract void putLong(long var1, long var3);

        public abstract void copyMemory(long var1, byte[] var3, long var4, long var6);

        public abstract void copyMemory(byte[] var1, long var2, long var4, long var6);
    }

    private static final class Android64MemoryAccessor
    extends MemoryAccessor {
        Android64MemoryAccessor(Unsafe unsafe) {
            super(unsafe);
        }

        @Override
        public Object getStaticObject(Field field) {
            try {
                return field.get(null);
            }
            catch (IllegalAccessException e) {
                return null;
            }
        }

        @Override
        public byte getByte(Object target, long offset) {
            if (IS_BIG_ENDIAN) {
                return UnsafeUtil.getByteBigEndian(target, offset);
            }
            return UnsafeUtil.getByteLittleEndian(target, offset);
        }

        @Override
        public void putByte(Object target, long offset, byte value) {
            if (IS_BIG_ENDIAN) {
                UnsafeUtil.putByteBigEndian(target, offset, value);
            } else {
                UnsafeUtil.putByteLittleEndian(target, offset, value);
            }
        }

        @Override
        public boolean getBoolean(Object target, long offset) {
            if (IS_BIG_ENDIAN) {
                return UnsafeUtil.getBooleanBigEndian(target, offset);
            }
            return UnsafeUtil.getBooleanLittleEndian(target, offset);
        }

        @Override
        public void putBoolean(Object target, long offset, boolean value) {
            if (IS_BIG_ENDIAN) {
                UnsafeUtil.putBooleanBigEndian(target, offset, value);
            } else {
                UnsafeUtil.putBooleanLittleEndian(target, offset, value);
            }
        }

        @Override
        public float getFloat(Object target, long offset) {
            return Float.intBitsToFloat(this.getInt(target, offset));
        }

        @Override
        public void putFloat(Object target, long offset, float value) {
            this.putInt(target, offset, Float.floatToIntBits(value));
        }

        @Override
        public double getDouble(Object target, long offset) {
            return Double.longBitsToDouble(this.getLong(target, offset));
        }

        @Override
        public void putDouble(Object target, long offset, double value) {
            this.putLong(target, offset, Double.doubleToLongBits(value));
        }

        @Override
        public boolean supportsUnsafeByteBufferOperations() {
            return false;
        }

        @Override
        public byte getByte(long address) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putByte(long address, byte value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getInt(long address) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putInt(long address, int value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLong(long address) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putLong(long address, long value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
            throw new UnsupportedOperationException();
        }
    }

    private static final class Android32MemoryAccessor
    extends MemoryAccessor {
        private static final long SMALL_ADDRESS_MASK = -1L;

        private static int smallAddress(long address) {
            return (int)(0xFFFFFFFFFFFFFFFFL & address);
        }

        Android32MemoryAccessor(Unsafe unsafe) {
            super(unsafe);
        }

        @Override
        public Object getStaticObject(Field field) {
            try {
                return field.get(null);
            }
            catch (IllegalAccessException e) {
                return null;
            }
        }

        @Override
        public byte getByte(Object target, long offset) {
            if (IS_BIG_ENDIAN) {
                return UnsafeUtil.getByteBigEndian(target, offset);
            }
            return UnsafeUtil.getByteLittleEndian(target, offset);
        }

        @Override
        public void putByte(Object target, long offset, byte value) {
            if (IS_BIG_ENDIAN) {
                UnsafeUtil.putByteBigEndian(target, offset, value);
            } else {
                UnsafeUtil.putByteLittleEndian(target, offset, value);
            }
        }

        @Override
        public boolean getBoolean(Object target, long offset) {
            if (IS_BIG_ENDIAN) {
                return UnsafeUtil.getBooleanBigEndian(target, offset);
            }
            return UnsafeUtil.getBooleanLittleEndian(target, offset);
        }

        @Override
        public void putBoolean(Object target, long offset, boolean value) {
            if (IS_BIG_ENDIAN) {
                UnsafeUtil.putBooleanBigEndian(target, offset, value);
            } else {
                UnsafeUtil.putBooleanLittleEndian(target, offset, value);
            }
        }

        @Override
        public float getFloat(Object target, long offset) {
            return Float.intBitsToFloat(this.getInt(target, offset));
        }

        @Override
        public void putFloat(Object target, long offset, float value) {
            this.putInt(target, offset, Float.floatToIntBits(value));
        }

        @Override
        public double getDouble(Object target, long offset) {
            return Double.longBitsToDouble(this.getLong(target, offset));
        }

        @Override
        public void putDouble(Object target, long offset, double value) {
            this.putLong(target, offset, Double.doubleToLongBits(value));
        }

        @Override
        public boolean supportsUnsafeByteBufferOperations() {
            return false;
        }

        @Override
        public byte getByte(long address) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putByte(long address, byte value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getInt(long address) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putInt(long address, int value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLong(long address) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putLong(long address, long value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
            throw new UnsupportedOperationException();
        }
    }

    private static final class JvmMemoryAccessor
    extends MemoryAccessor {
        JvmMemoryAccessor(Unsafe unsafe) {
            super(unsafe);
        }

        @Override
        public Object getStaticObject(Field field) {
            return this.getObject(this.unsafe.staticFieldBase(field), this.unsafe.staticFieldOffset(field));
        }

        @Override
        public boolean supportsUnsafeArrayOperations() {
            if (!super.supportsUnsafeArrayOperations()) {
                return false;
            }
            try {
                Class<?> clazz = this.unsafe.getClass();
                clazz.getMethod("getByte", Object.class, Long.TYPE);
                clazz.getMethod("putByte", Object.class, Long.TYPE, Byte.TYPE);
                clazz.getMethod("getBoolean", Object.class, Long.TYPE);
                clazz.getMethod("putBoolean", Object.class, Long.TYPE, Boolean.TYPE);
                clazz.getMethod("getFloat", Object.class, Long.TYPE);
                clazz.getMethod("putFloat", Object.class, Long.TYPE, Float.TYPE);
                clazz.getMethod("getDouble", Object.class, Long.TYPE);
                clazz.getMethod("putDouble", Object.class, Long.TYPE, Double.TYPE);
                return true;
            }
            catch (Throwable e) {
                UnsafeUtil.logMissingMethod(e);
                return false;
            }
        }

        @Override
        public byte getByte(Object target, long offset) {
            return this.unsafe.getByte(target, offset);
        }

        @Override
        public void putByte(Object target, long offset, byte value) {
            this.unsafe.putByte(target, offset, value);
        }

        @Override
        public boolean getBoolean(Object target, long offset) {
            return this.unsafe.getBoolean(target, offset);
        }

        @Override
        public void putBoolean(Object target, long offset, boolean value) {
            this.unsafe.putBoolean(target, offset, value);
        }

        @Override
        public float getFloat(Object target, long offset) {
            return this.unsafe.getFloat(target, offset);
        }

        @Override
        public void putFloat(Object target, long offset, float value) {
            this.unsafe.putFloat(target, offset, value);
        }

        @Override
        public double getDouble(Object target, long offset) {
            return this.unsafe.getDouble(target, offset);
        }

        @Override
        public void putDouble(Object target, long offset, double value) {
            this.unsafe.putDouble(target, offset, value);
        }

        @Override
        public boolean supportsUnsafeByteBufferOperations() {
            if (!super.supportsUnsafeByteBufferOperations()) {
                return false;
            }
            try {
                Class<?> clazz = this.unsafe.getClass();
                clazz.getMethod("getByte", Long.TYPE);
                clazz.getMethod("putByte", Long.TYPE, Byte.TYPE);
                clazz.getMethod("getInt", Long.TYPE);
                clazz.getMethod("putInt", Long.TYPE, Integer.TYPE);
                clazz.getMethod("getLong", Long.TYPE);
                clazz.getMethod("putLong", Long.TYPE, Long.TYPE);
                clazz.getMethod("copyMemory", Long.TYPE, Long.TYPE, Long.TYPE);
                clazz.getMethod("copyMemory", Object.class, Long.TYPE, Object.class, Long.TYPE, Long.TYPE);
                return true;
            }
            catch (Throwable e) {
                UnsafeUtil.logMissingMethod(e);
                return false;
            }
        }

        @Override
        public byte getByte(long address) {
            return this.unsafe.getByte(address);
        }

        @Override
        public void putByte(long address, byte value) {
            this.unsafe.putByte(address, value);
        }

        @Override
        public int getInt(long address) {
            return this.unsafe.getInt(address);
        }

        @Override
        public void putInt(long address, int value) {
            this.unsafe.putInt(address, value);
        }

        @Override
        public long getLong(long address) {
            return this.unsafe.getLong(address);
        }

        @Override
        public void putLong(long address, long value) {
            this.unsafe.putLong(address, value);
        }

        @Override
        public void copyMemory(long srcOffset, byte[] target, long targetIndex, long length) {
            this.unsafe.copyMemory(null, srcOffset, target, BYTE_ARRAY_BASE_OFFSET + targetIndex, length);
        }

        @Override
        public void copyMemory(byte[] src, long srcIndex, long targetOffset, long length) {
            this.unsafe.copyMemory(src, BYTE_ARRAY_BASE_OFFSET + srcIndex, null, targetOffset, length);
        }
    }
}

