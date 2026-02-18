/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  [F
 */
package org.joml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import org.joml.ConfigurationException;
import org.joml.Matrix2d;
import org.joml.Matrix2dc;
import org.joml.Matrix2f;
import org.joml.Matrix2fc;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix3x2d;
import org.joml.Matrix3x2dc;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3d;
import org.joml.Matrix4x3dc;
import org.joml.Matrix4x3f;
import org.joml.Matrix4x3fc;
import org.joml.Options;
import org.joml.Quaternionf;
import org.joml.Vector2L;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3L;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4L;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.joml.Vector4i;
import sun.misc.Unsafe;

abstract class MemUtil {
    public static final MemUtil INSTANCE = MemUtil.createInstance();

    MemUtil() {
    }

    private static MemUtil createInstance() {
        MemUtilNIO accessor;
        try {
            if (Options.NO_UNSAFE && Options.FORCE_UNSAFE) {
                throw new ConfigurationException("Cannot enable both -Djoml.nounsafe and -Djoml.forceUnsafe", null);
            }
            accessor = Options.NO_UNSAFE ? new MemUtilNIO() : new MemUtilUnsafe();
        }
        catch (Throwable e) {
            if (Options.FORCE_UNSAFE) {
                throw new ConfigurationException("Unsafe is not supported but its use was forced via -Djoml.forceUnsafe", e);
            }
            accessor = new MemUtilNIO();
        }
        return accessor;
    }

    public abstract void put(Matrix4f var1, int var2, FloatBuffer var3);

    public abstract void put(Matrix4f var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix4x3f var1, int var2, FloatBuffer var3);

    public abstract void put(Matrix4x3f var1, int var2, ByteBuffer var3);

    public abstract void put4x4(Matrix4x3f var1, int var2, FloatBuffer var3);

    public abstract void put4x4(Matrix4x3f var1, int var2, ByteBuffer var3);

    public abstract void put4x4(Matrix4x3d var1, int var2, DoubleBuffer var3);

    public abstract void put4x4(Matrix4x3d var1, int var2, ByteBuffer var3);

    public abstract void put4x4(Matrix3x2f var1, int var2, FloatBuffer var3);

    public abstract void put4x4(Matrix3x2f var1, int var2, ByteBuffer var3);

    public abstract void put4x4(Matrix3x2d var1, int var2, DoubleBuffer var3);

    public abstract void put4x4(Matrix3x2d var1, int var2, ByteBuffer var3);

    public abstract void put3x3(Matrix3x2f var1, int var2, FloatBuffer var3);

    public abstract void put3x3(Matrix3x2f var1, int var2, ByteBuffer var3);

    public abstract void put3x3(Matrix3x2d var1, int var2, DoubleBuffer var3);

    public abstract void put3x3(Matrix3x2d var1, int var2, ByteBuffer var3);

    public abstract void put4x3(Matrix4f var1, int var2, FloatBuffer var3);

    public abstract void put4x3(Matrix4f var1, int var2, ByteBuffer var3);

    public abstract void put3x4(Matrix4f var1, int var2, FloatBuffer var3);

    public abstract void put3x4(Matrix4f var1, int var2, ByteBuffer var3);

    public abstract void put3x4(Matrix4x3f var1, int var2, FloatBuffer var3);

    public abstract void put3x4(Matrix4x3f var1, int var2, ByteBuffer var3);

    public abstract void put3x4(Matrix3f var1, int var2, FloatBuffer var3);

    public abstract void put3x4(Matrix3f var1, int var2, ByteBuffer var3);

    public abstract void putTransposed(Matrix4f var1, int var2, FloatBuffer var3);

    public abstract void putTransposed(Matrix4f var1, int var2, ByteBuffer var3);

    public abstract void put4x3Transposed(Matrix4f var1, int var2, FloatBuffer var3);

    public abstract void put4x3Transposed(Matrix4f var1, int var2, ByteBuffer var3);

    public abstract void putTransposed(Matrix4x3f var1, int var2, FloatBuffer var3);

    public abstract void putTransposed(Matrix4x3f var1, int var2, ByteBuffer var3);

    public abstract void putTransposed(Matrix3x2f var1, int var2, FloatBuffer var3);

    public abstract void putTransposed(Matrix3f var1, int var2, FloatBuffer var3);

    public abstract void putTransposed(Matrix3f var1, int var2, ByteBuffer var3);

    public abstract void putTransposed(Matrix2f var1, int var2, FloatBuffer var3);

    public abstract void putTransposed(Matrix2f var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix4d var1, int var2, DoubleBuffer var3);

    public abstract void put(Matrix4d var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix4x3d var1, int var2, DoubleBuffer var3);

    public abstract void put(Matrix4x3d var1, int var2, ByteBuffer var3);

    public abstract void putf(Matrix4d var1, int var2, FloatBuffer var3);

    public abstract void putf(Matrix4d var1, int var2, ByteBuffer var3);

    public abstract void putf(Matrix4x3d var1, int var2, FloatBuffer var3);

    public abstract void putf(Matrix4x3d var1, int var2, ByteBuffer var3);

    public abstract void put4x3Transposed(Matrix4d var1, int var2, DoubleBuffer var3);

    public abstract void put4x3Transposed(Matrix4d var1, int var2, ByteBuffer var3);

    public abstract void putTransposed(Matrix4d var1, int var2, DoubleBuffer var3);

    public abstract void putTransposed(Matrix4d var1, int var2, ByteBuffer var3);

    public abstract void putTransposed(Matrix4x3d var1, int var2, DoubleBuffer var3);

    public abstract void putTransposed(Matrix4x3d var1, int var2, ByteBuffer var3);

    public abstract void putTransposed(Matrix3d var1, int var2, DoubleBuffer var3);

    public abstract void putTransposed(Matrix3d var1, int var2, ByteBuffer var3);

    public abstract void putTransposed(Matrix3x2d var1, int var2, DoubleBuffer var3);

    public abstract void putTransposed(Matrix3x2d var1, int var2, ByteBuffer var3);

    public abstract void putTransposed(Matrix2d var1, int var2, DoubleBuffer var3);

    public abstract void putTransposed(Matrix2d var1, int var2, ByteBuffer var3);

    public abstract void putfTransposed(Matrix4d var1, int var2, FloatBuffer var3);

    public abstract void putfTransposed(Matrix4d var1, int var2, ByteBuffer var3);

    public abstract void putfTransposed(Matrix4x3d var1, int var2, FloatBuffer var3);

    public abstract void putfTransposed(Matrix4x3d var1, int var2, ByteBuffer var3);

    public abstract void putfTransposed(Matrix3d var1, int var2, FloatBuffer var3);

    public abstract void putfTransposed(Matrix3d var1, int var2, ByteBuffer var3);

    public abstract void putfTransposed(Matrix3x2d var1, int var2, FloatBuffer var3);

    public abstract void putfTransposed(Matrix3x2d var1, int var2, ByteBuffer var3);

    public abstract void putfTransposed(Matrix2d var1, int var2, FloatBuffer var3);

    public abstract void putfTransposed(Matrix2d var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix3f var1, int var2, FloatBuffer var3);

    public abstract void put(Matrix3f var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix3d var1, int var2, DoubleBuffer var3);

    public abstract void put(Matrix3d var1, int var2, ByteBuffer var3);

    public abstract void putf(Matrix3d var1, int var2, FloatBuffer var3);

    public abstract void putf(Matrix3d var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix3x2f var1, int var2, FloatBuffer var3);

    public abstract void put(Matrix3x2f var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix3x2d var1, int var2, DoubleBuffer var3);

    public abstract void put(Matrix3x2d var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix2f var1, int var2, FloatBuffer var3);

    public abstract void put(Matrix2f var1, int var2, ByteBuffer var3);

    public abstract void put(Matrix2d var1, int var2, DoubleBuffer var3);

    public abstract void put(Matrix2d var1, int var2, ByteBuffer var3);

    public abstract void putf(Matrix2d var1, int var2, FloatBuffer var3);

    public abstract void putf(Matrix2d var1, int var2, ByteBuffer var3);

    public abstract void put(Vector4d var1, int var2, DoubleBuffer var3);

    public abstract void put(Vector4d var1, int var2, FloatBuffer var3);

    public abstract void put(Vector4d var1, int var2, ByteBuffer var3);

    public abstract void putf(Vector4d var1, int var2, ByteBuffer var3);

    public abstract void put(Vector4f var1, int var2, FloatBuffer var3);

    public abstract void put(Vector4f var1, int var2, ByteBuffer var3);

    public abstract void put(Vector4i var1, int var2, IntBuffer var3);

    public abstract void put(Vector4i var1, int var2, ByteBuffer var3);

    public abstract void put(Vector4L var1, int var2, LongBuffer var3);

    public abstract void put(Vector4L var1, int var2, ByteBuffer var3);

    public abstract void put(Vector3f var1, int var2, FloatBuffer var3);

    public abstract void put(Vector3f var1, int var2, ByteBuffer var3);

    public abstract void put(Vector3d var1, int var2, DoubleBuffer var3);

    public abstract void put(Vector3d var1, int var2, FloatBuffer var3);

    public abstract void put(Vector3d var1, int var2, ByteBuffer var3);

    public abstract void putf(Vector3d var1, int var2, ByteBuffer var3);

    public abstract void put(Vector3i var1, int var2, IntBuffer var3);

    public abstract void put(Vector3i var1, int var2, ByteBuffer var3);

    public abstract void put(Vector3L var1, int var2, LongBuffer var3);

    public abstract void put(Vector3L var1, int var2, ByteBuffer var3);

    public abstract void put(Vector2f var1, int var2, FloatBuffer var3);

    public abstract void put(Vector2f var1, int var2, ByteBuffer var3);

    public abstract void put(Vector2d var1, int var2, DoubleBuffer var3);

    public abstract void put(Vector2d var1, int var2, ByteBuffer var3);

    public abstract void put(Vector2i var1, int var2, IntBuffer var3);

    public abstract void put(Vector2i var1, int var2, ByteBuffer var3);

    public abstract void put(Vector2L var1, int var2, LongBuffer var3);

    public abstract void put(Vector2L var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix4f var1, int var2, FloatBuffer var3);

    public abstract void get(Matrix4f var1, int var2, ByteBuffer var3);

    public abstract void getTransposed(Matrix4f var1, int var2, FloatBuffer var3);

    public abstract void getTransposed(Matrix4f var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix4x3f var1, int var2, FloatBuffer var3);

    public abstract void get(Matrix4x3f var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix4d var1, int var2, DoubleBuffer var3);

    public abstract void get(Matrix4d var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix4x3d var1, int var2, DoubleBuffer var3);

    public abstract void get(Matrix4x3d var1, int var2, ByteBuffer var3);

    public abstract void getf(Matrix4d var1, int var2, FloatBuffer var3);

    public abstract void getf(Matrix4d var1, int var2, ByteBuffer var3);

    public abstract void getf(Matrix4x3d var1, int var2, FloatBuffer var3);

    public abstract void getf(Matrix4x3d var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix3f var1, int var2, FloatBuffer var3);

    public abstract void get(Matrix3f var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix3d var1, int var2, DoubleBuffer var3);

    public abstract void get(Matrix3d var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix3x2f var1, int var2, FloatBuffer var3);

    public abstract void get(Matrix3x2f var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix3x2d var1, int var2, DoubleBuffer var3);

    public abstract void get(Matrix3x2d var1, int var2, ByteBuffer var3);

    public abstract void getf(Matrix3d var1, int var2, FloatBuffer var3);

    public abstract void getf(Matrix3d var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix2f var1, int var2, FloatBuffer var3);

    public abstract void get(Matrix2f var1, int var2, ByteBuffer var3);

    public abstract void get(Matrix2d var1, int var2, DoubleBuffer var3);

    public abstract void get(Matrix2d var1, int var2, ByteBuffer var3);

    public abstract void getf(Matrix2d var1, int var2, FloatBuffer var3);

    public abstract void getf(Matrix2d var1, int var2, ByteBuffer var3);

    public abstract void get(Vector4d var1, int var2, DoubleBuffer var3);

    public abstract void get(Vector4d var1, int var2, ByteBuffer var3);

    public abstract void get(Vector4f var1, int var2, FloatBuffer var3);

    public abstract void get(Vector4f var1, int var2, ByteBuffer var3);

    public abstract void get(Vector4i var1, int var2, IntBuffer var3);

    public abstract void get(Vector4i var1, int var2, ByteBuffer var3);

    public abstract void get(Vector4L var1, int var2, LongBuffer var3);

    public abstract void get(Vector4L var1, int var2, ByteBuffer var3);

    public abstract void get(Vector3f var1, int var2, FloatBuffer var3);

    public abstract void get(Vector3f var1, int var2, ByteBuffer var3);

    public abstract void get(Vector3d var1, int var2, DoubleBuffer var3);

    public abstract void get(Vector3d var1, int var2, ByteBuffer var3);

    public abstract void get(Vector3i var1, int var2, IntBuffer var3);

    public abstract void get(Vector3i var1, int var2, ByteBuffer var3);

    public abstract void get(Vector3L var1, int var2, LongBuffer var3);

    public abstract void get(Vector3L var1, int var2, ByteBuffer var3);

    public abstract void get(Vector2f var1, int var2, FloatBuffer var3);

    public abstract void get(Vector2f var1, int var2, ByteBuffer var3);

    public abstract void get(Vector2d var1, int var2, DoubleBuffer var3);

    public abstract void get(Vector2d var1, int var2, ByteBuffer var3);

    public abstract void get(Vector2i var1, int var2, IntBuffer var3);

    public abstract void get(Vector2i var1, int var2, ByteBuffer var3);

    public abstract void get(Vector2L var1, int var2, LongBuffer var3);

    public abstract void get(Vector2L var1, int var2, ByteBuffer var3);

    public abstract void putMatrix3f(Quaternionf var1, int var2, ByteBuffer var3);

    public abstract void putMatrix3f(Quaternionf var1, int var2, FloatBuffer var3);

    public abstract void putMatrix4f(Quaternionf var1, int var2, ByteBuffer var3);

    public abstract void putMatrix4f(Quaternionf var1, int var2, FloatBuffer var3);

    public abstract void putMatrix4x3f(Quaternionf var1, int var2, ByteBuffer var3);

    public abstract void putMatrix4x3f(Quaternionf var1, int var2, FloatBuffer var3);

    public abstract float get(Matrix4f var1, int var2, int var3);

    public abstract Matrix4f set(Matrix4f var1, int var2, int var3, float var4);

    public abstract double get(Matrix4d var1, int var2, int var3);

    public abstract Matrix4d set(Matrix4d var1, int var2, int var3, double var4);

    public abstract float get(Matrix3f var1, int var2, int var3);

    public abstract Matrix3f set(Matrix3f var1, int var2, int var3, float var4);

    public abstract double get(Matrix3d var1, int var2, int var3);

    public abstract Matrix3d set(Matrix3d var1, int var2, int var3, double var4);

    public abstract Vector4f getColumn(Matrix4f var1, int var2, Vector4f var3);

    public abstract Matrix4f setColumn(Vector4f var1, int var2, Matrix4f var3);

    public abstract Matrix4f setColumn(Vector4fc var1, int var2, Matrix4f var3);

    public abstract void copy(Matrix4fc var1, Matrix4f var2);

    public abstract void copy(Matrix4x3fc var1, Matrix4x3f var2);

    public abstract void copy(Matrix4fc var1, Matrix4x3f var2);

    public abstract void copy(Matrix4x3fc var1, Matrix4f var2);

    public abstract void copy(Matrix3fc var1, Matrix3f var2);

    public abstract void copy(Matrix3fc var1, Matrix4f var2);

    public abstract void copy(Matrix4fc var1, Matrix3f var2);

    public abstract void copy(Matrix3fc var1, Matrix4x3f var2);

    public abstract void copy(Matrix3x2fc var1, Matrix3x2f var2);

    public abstract void copy(Matrix3x2dc var1, Matrix3x2d var2);

    public abstract void copy(Matrix2fc var1, Matrix2f var2);

    public abstract void copy(Matrix2dc var1, Matrix2d var2);

    public abstract void copy(Matrix2fc var1, Matrix3f var2);

    public abstract void copy(Matrix3fc var1, Matrix2f var2);

    public abstract void copy(Matrix2fc var1, Matrix3x2f var2);

    public abstract void copy(Matrix3x2fc var1, Matrix2f var2);

    public abstract void copy(Matrix2dc var1, Matrix3d var2);

    public abstract void copy(Matrix3dc var1, Matrix2d var2);

    public abstract void copy(Matrix2dc var1, Matrix3x2d var2);

    public abstract void copy(Matrix3x2dc var1, Matrix2d var2);

    public abstract void copy3x3(Matrix4fc var1, Matrix4f var2);

    public abstract void copy3x3(Matrix4x3fc var1, Matrix4x3f var2);

    public abstract void copy3x3(Matrix3fc var1, Matrix4x3f var2);

    public abstract void copy3x3(Matrix3fc var1, Matrix4f var2);

    public abstract void copy4x3(Matrix4fc var1, Matrix4f var2);

    public abstract void copy4x3(Matrix4x3fc var1, Matrix4f var2);

    public abstract void copy(float[] var1, int var2, Matrix4f var3);

    public abstract void copyTransposed(float[] var1, int var2, Matrix4f var3);

    public abstract void copy(float[] var1, int var2, Matrix3f var3);

    public abstract void copy(float[] var1, int var2, Matrix4x3f var3);

    public abstract void copy(float[] var1, int var2, Matrix3x2f var3);

    public abstract void copy(double[] var1, int var2, Matrix3x2d var3);

    public abstract void copy(float[] var1, int var2, Matrix3x2d var3);

    public abstract void copy(float[] var1, int var2, Matrix2f var3);

    public abstract void copy(double[] var1, int var2, Matrix2d var3);

    public abstract void copy(Matrix4fc var1, float[] var2, int var3);

    public abstract void copy(Matrix3fc var1, float[] var2, int var3);

    public abstract void copy(Matrix4x3fc var1, float[] var2, int var3);

    public abstract void copy(Matrix3x2fc var1, float[] var2, int var3);

    public abstract void copy(Matrix3x2dc var1, double[] var2, int var3);

    public abstract void copy(Matrix2fc var1, float[] var2, int var3);

    public abstract void copy(Matrix2dc var1, double[] var2, int var3);

    public abstract void copy4x4(Matrix4x3fc var1, float[] var2, int var3);

    public abstract void copy4x4(Matrix4x3dc var1, float[] var2, int var3);

    public abstract void copy4x4(Matrix4x3dc var1, double[] var2, int var3);

    public abstract void copy4x4(Matrix3x2fc var1, float[] var2, int var3);

    public abstract void copy4x4(Matrix3x2dc var1, double[] var2, int var3);

    public abstract void copy3x3(Matrix3x2fc var1, float[] var2, int var3);

    public abstract void copy3x3(Matrix3x2dc var1, double[] var2, int var3);

    public abstract void identity(Matrix4f var1);

    public abstract void identity(Matrix4x3f var1);

    public abstract void identity(Matrix3f var1);

    public abstract void identity(Matrix3x2f var1);

    public abstract void identity(Matrix3x2d var1);

    public abstract void identity(Matrix2f var1);

    public abstract void swap(Matrix4f var1, Matrix4f var2);

    public abstract void swap(Matrix4x3f var1, Matrix4x3f var2);

    public abstract void swap(Matrix3f var1, Matrix3f var2);

    public abstract void swap(Matrix2f var1, Matrix2f var2);

    public abstract void swap(Matrix2d var1, Matrix2d var2);

    public abstract void zero(Matrix4f var1);

    public abstract void zero(Matrix4x3f var1);

    public abstract void zero(Matrix3f var1);

    public abstract void zero(Matrix3x2f var1);

    public abstract void zero(Matrix3x2d var1);

    public abstract void zero(Matrix2f var1);

    public abstract void zero(Matrix2d var1);

    public static class MemUtilUnsafe
    extends MemUtilNIO {
        public static final Unsafe UNSAFE = MemUtilUnsafe.getUnsafeInstance();
        public static final long ADDRESS;
        public static final long Matrix2f_m00;
        public static final long Matrix3f_m00;
        public static final long Matrix3d_m00;
        public static final long Matrix4f_m00;
        public static final long Matrix4d_m00;
        public static final long Matrix4x3f_m00;
        public static final long Matrix3x2f_m00;
        public static final long Vector4f_x;
        public static final long Vector4i_x;
        public static final long Vector3f_x;
        public static final long Vector3i_x;
        public static final long Vector2f_x;
        public static final long Vector2i_x;
        public static final long floatArrayOffset;

        private static long findBufferAddress() {
            try {
                return UNSAFE.objectFieldOffset(MemUtilUnsafe.getDeclaredField(Buffer.class, "address"));
            }
            catch (Exception e) {
                throw new UnsupportedOperationException(e);
            }
        }

        private static long checkMatrix4f() throws NoSuchFieldException, SecurityException {
            Field f = Matrix4f.class.getDeclaredField("m00");
            long Matrix4f_m00 = UNSAFE.objectFieldOffset(f);
            for (int i = 1; i < 16; ++i) {
                int c = i >>> 2;
                int r = i & 3;
                f = (class$1 == null ? MemUtilUnsafe.class$("org.joml.Matrix4f") : class$1).getDeclaredField("m" + c + r);
                long offset = UNSAFE.objectFieldOffset(f);
                if (offset == Matrix4f_m00 + (long)(i << 2)) continue;
                throw new UnsupportedOperationException("Unexpected Matrix4f element offset");
            }
            return Matrix4f_m00;
        }

        private static long checkMatrix4d() throws NoSuchFieldException, SecurityException {
            Field f = Matrix4d.class.getDeclaredField("m00");
            long Matrix4d_m00 = UNSAFE.objectFieldOffset(f);
            for (int i = 1; i < 16; ++i) {
                int c = i >>> 2;
                int r = i & 3;
                f = (class$2 == null ? MemUtilUnsafe.class$("org.joml.Matrix4d") : class$2).getDeclaredField("m" + c + r);
                long offset = UNSAFE.objectFieldOffset(f);
                if (offset == Matrix4d_m00 + (long)(i << 3)) continue;
                throw new UnsupportedOperationException("Unexpected Matrix4d element offset");
            }
            return Matrix4d_m00;
        }

        private static long checkMatrix4x3f() throws NoSuchFieldException, SecurityException {
            Field f = Matrix4x3f.class.getDeclaredField("m00");
            long Matrix4x3f_m00 = UNSAFE.objectFieldOffset(f);
            for (int i = 1; i < 12; ++i) {
                int c = i / 3;
                int r = i % 3;
                f = (class$3 == null ? MemUtilUnsafe.class$("org.joml.Matrix4x3f") : class$3).getDeclaredField("m" + c + r);
                long offset = UNSAFE.objectFieldOffset(f);
                if (offset == Matrix4x3f_m00 + (long)(i << 2)) continue;
                throw new UnsupportedOperationException("Unexpected Matrix4x3f element offset");
            }
            return Matrix4x3f_m00;
        }

        private static long checkMatrix3f() throws NoSuchFieldException, SecurityException {
            Field f = Matrix3f.class.getDeclaredField("m00");
            long Matrix3f_m00 = UNSAFE.objectFieldOffset(f);
            for (int i = 1; i < 9; ++i) {
                int c = i / 3;
                int r = i % 3;
                f = (class$4 == null ? MemUtilUnsafe.class$("org.joml.Matrix3f") : class$4).getDeclaredField("m" + c + r);
                long offset = UNSAFE.objectFieldOffset(f);
                if (offset == Matrix3f_m00 + (long)(i << 2)) continue;
                throw new UnsupportedOperationException("Unexpected Matrix3f element offset");
            }
            return Matrix3f_m00;
        }

        private static long checkMatrix3d() throws NoSuchFieldException, SecurityException {
            Field f = Matrix3d.class.getDeclaredField("m00");
            long Matrix3d_m00 = UNSAFE.objectFieldOffset(f);
            for (int i = 1; i < 9; ++i) {
                int c = i / 3;
                int r = i % 3;
                f = (class$5 == null ? MemUtilUnsafe.class$("org.joml.Matrix3d") : class$5).getDeclaredField("m" + c + r);
                long offset = UNSAFE.objectFieldOffset(f);
                if (offset == Matrix3d_m00 + (long)(i << 3)) continue;
                throw new UnsupportedOperationException("Unexpected Matrix3d element offset");
            }
            return Matrix3d_m00;
        }

        private static long checkMatrix3x2f() throws NoSuchFieldException, SecurityException {
            Field f = Matrix3x2f.class.getDeclaredField("m00");
            long Matrix3x2f_m00 = UNSAFE.objectFieldOffset(f);
            for (int i = 1; i < 6; ++i) {
                int c = i / 2;
                int r = i % 2;
                f = (class$6 == null ? MemUtilUnsafe.class$("org.joml.Matrix3x2f") : class$6).getDeclaredField("m" + c + r);
                long offset = UNSAFE.objectFieldOffset(f);
                if (offset == Matrix3x2f_m00 + (long)(i << 2)) continue;
                throw new UnsupportedOperationException("Unexpected Matrix3x2f element offset");
            }
            return Matrix3x2f_m00;
        }

        private static long checkMatrix2f() throws NoSuchFieldException, SecurityException {
            Field f = Matrix2f.class.getDeclaredField("m00");
            long Matrix2f_m00 = UNSAFE.objectFieldOffset(f);
            for (int i = 1; i < 4; ++i) {
                int c = i / 2;
                int r = i % 2;
                f = (class$7 == null ? MemUtilUnsafe.class$("org.joml.Matrix2f") : class$7).getDeclaredField("m" + c + r);
                long offset = UNSAFE.objectFieldOffset(f);
                if (offset == Matrix2f_m00 + (long)(i << 2)) continue;
                throw new UnsupportedOperationException("Unexpected Matrix2f element offset");
            }
            return Matrix2f_m00;
        }

        private static long checkVector4f() throws NoSuchFieldException, SecurityException {
            Field f = Vector4f.class.getDeclaredField("x");
            long Vector4f_x = UNSAFE.objectFieldOffset(f);
            String[] names = new String[]{"y", "z", "w"};
            for (int i = 1; i < 4; ++i) {
                f = (class$8 == null ? MemUtilUnsafe.class$("org.joml.Vector4f") : class$8).getDeclaredField(names[i - 1]);
                long offset = UNSAFE.objectFieldOffset(f);
                if (offset == Vector4f_x + (long)(i << 2)) continue;
                throw new UnsupportedOperationException("Unexpected Vector4f element offset");
            }
            return Vector4f_x;
        }

        private static long checkVector4i() throws NoSuchFieldException, SecurityException {
            Field f = Vector4i.class.getDeclaredField("x");
            long Vector4i_x = UNSAFE.objectFieldOffset(f);
            String[] names = new String[]{"y", "z", "w"};
            for (int i = 1; i < 4; ++i) {
                f = (class$9 == null ? MemUtilUnsafe.class$("org.joml.Vector4i") : class$9).getDeclaredField(names[i - 1]);
                long offset = UNSAFE.objectFieldOffset(f);
                if (offset == Vector4i_x + (long)(i << 2)) continue;
                throw new UnsupportedOperationException("Unexpected Vector4i element offset");
            }
            return Vector4i_x;
        }

        private static long checkVector3f() throws NoSuchFieldException, SecurityException {
            Field f = Vector3f.class.getDeclaredField("x");
            long Vector3f_x = UNSAFE.objectFieldOffset(f);
            String[] names = new String[]{"y", "z"};
            for (int i = 1; i < 3; ++i) {
                f = (class$10 == null ? MemUtilUnsafe.class$("org.joml.Vector3f") : class$10).getDeclaredField(names[i - 1]);
                long offset = UNSAFE.objectFieldOffset(f);
                if (offset == Vector3f_x + (long)(i << 2)) continue;
                throw new UnsupportedOperationException("Unexpected Vector3f element offset");
            }
            return Vector3f_x;
        }

        private static long checkVector3i() throws NoSuchFieldException, SecurityException {
            Field f = Vector3i.class.getDeclaredField("x");
            long Vector3i_x = UNSAFE.objectFieldOffset(f);
            String[] names = new String[]{"y", "z"};
            for (int i = 1; i < 3; ++i) {
                f = (class$11 == null ? MemUtilUnsafe.class$("org.joml.Vector3i") : class$11).getDeclaredField(names[i - 1]);
                long offset = UNSAFE.objectFieldOffset(f);
                if (offset == Vector3i_x + (long)(i << 2)) continue;
                throw new UnsupportedOperationException("Unexpected Vector3i element offset");
            }
            return Vector3i_x;
        }

        private static long checkVector2f() throws NoSuchFieldException, SecurityException {
            Field f = Vector2f.class.getDeclaredField("x");
            long Vector2f_x = UNSAFE.objectFieldOffset(f);
            f = Vector2f.class.getDeclaredField("y");
            long offset = UNSAFE.objectFieldOffset(f);
            if (offset != Vector2f_x + 4L) {
                throw new UnsupportedOperationException("Unexpected Vector2f element offset");
            }
            return Vector2f_x;
        }

        private static long checkVector2i() throws NoSuchFieldException, SecurityException {
            Field f = Vector2i.class.getDeclaredField("x");
            long Vector2i_x = UNSAFE.objectFieldOffset(f);
            f = Vector2i.class.getDeclaredField("y");
            long offset = UNSAFE.objectFieldOffset(f);
            if (offset != Vector2i_x + 4L) {
                throw new UnsupportedOperationException("Unexpected Vector2i element offset");
            }
            return Vector2i_x;
        }

        private static Field getDeclaredField(Class root, String fieldName) throws NoSuchFieldException {
            Class type = root;
            do {
                try {
                    Field field = type.getDeclaredField(fieldName);
                    return field;
                }
                catch (NoSuchFieldException e) {
                    type = type.getSuperclass();
                }
                catch (SecurityException e) {
                    type = type.getSuperclass();
                }
            } while (type != null);
            throw new NoSuchFieldException(fieldName + " does not exist in " + root.getName() + " or any of its superclasses.");
        }

        public static Unsafe getUnsafeInstance() throws SecurityException {
            Field[] fields = Unsafe.class.getDeclaredFields();
            for (int i = 0; i < fields.length; ++i) {
                int modifiers;
                Field field = fields[i];
                if (!field.getType().equals(class$14 == null ? MemUtilUnsafe.class$("sun.misc.Unsafe") : class$14) || !Modifier.isStatic(modifiers = field.getModifiers()) || !Modifier.isFinal(modifiers)) continue;
                field.setAccessible(true);
                try {
                    return (Unsafe)field.get(null);
                }
                catch (IllegalAccessException illegalAccessException) {
                    break;
                }
            }
            throw new UnsupportedOperationException();
        }

        public static void put(Matrix4f m, long destAddr) {
            for (int i = 0; i < 8; ++i) {
                UNSAFE.putLong(null, destAddr + (long)(i << 3), UNSAFE.getLong(m, Matrix4f_m00 + (long)(i << 3)));
            }
        }

        public static void put4x3(Matrix4f m, long destAddr) {
            Unsafe u = UNSAFE;
            for (int i = 0; i < 4; ++i) {
                u.putLong(null, destAddr + (long)(12 * i), u.getLong(m, Matrix4f_m00 + (long)(i << 4)));
            }
            u.putFloat(null, destAddr + 8L, m.m02());
            u.putFloat(null, destAddr + 20L, m.m12());
            u.putFloat(null, destAddr + 32L, m.m22());
            u.putFloat(null, destAddr + 44L, m.m32());
        }

        public static void put3x4(Matrix4f m, long destAddr) {
            for (int i = 0; i < 6; ++i) {
                UNSAFE.putLong(null, destAddr + (long)(i << 3), UNSAFE.getLong(m, Matrix4f_m00 + (long)(i << 3)));
            }
        }

        public static void put(Matrix4x3f m, long destAddr) {
            for (int i = 0; i < 6; ++i) {
                UNSAFE.putLong(null, destAddr + (long)(i << 3), UNSAFE.getLong(m, Matrix4x3f_m00 + (long)(i << 3)));
            }
        }

        public static void put4x4(Matrix4x3f m, long destAddr) {
            for (int i = 0; i < 4; ++i) {
                UNSAFE.putLong(null, destAddr + (long)(i << 4), UNSAFE.getLong(m, Matrix4x3f_m00 + (long)(12 * i)));
                long lng = (long)UNSAFE.getInt(m, Matrix4x3f_m00 + 8L + (long)(12 * i)) & 0xFFFFFFFFL;
                UNSAFE.putLong(null, destAddr + 8L + (long)(i << 4), lng);
            }
            UNSAFE.putFloat(null, destAddr + 60L, 1.0f);
        }

        public static void put3x4(Matrix4x3f m, long destAddr) {
            for (int i = 0; i < 3; ++i) {
                UNSAFE.putLong(null, destAddr + (long)(i << 4), UNSAFE.getLong(m, Matrix4x3f_m00 + (long)(12 * i)));
                UNSAFE.putFloat(null, destAddr + (long)(i << 4) + 8L, UNSAFE.getFloat(m, Matrix4x3f_m00 + 8L + (long)(12 * i)));
                UNSAFE.putFloat(null, destAddr + (long)(i << 4) + 12L, 0.0f);
            }
        }

        public static void put4x4(Matrix4x3d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putDouble(null, destAddr, m.m00());
            u.putDouble(null, destAddr + 8L, m.m01());
            u.putDouble(null, destAddr + 16L, m.m02());
            u.putDouble(null, destAddr + 24L, 0.0);
            u.putDouble(null, destAddr + 32L, m.m10());
            u.putDouble(null, destAddr + 40L, m.m11());
            u.putDouble(null, destAddr + 48L, m.m12());
            u.putDouble(null, destAddr + 56L, 0.0);
            u.putDouble(null, destAddr + 64L, m.m20());
            u.putDouble(null, destAddr + 72L, m.m21());
            u.putDouble(null, destAddr + 80L, m.m22());
            u.putDouble(null, destAddr + 88L, 0.0);
            u.putDouble(null, destAddr + 96L, m.m30());
            u.putDouble(null, destAddr + 104L, m.m31());
            u.putDouble(null, destAddr + 112L, m.m32());
            u.putDouble(null, destAddr + 120L, 1.0);
        }

        public static void put4x4(Matrix3x2f m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putLong(null, destAddr, u.getLong(m, Matrix3x2f_m00));
            u.putLong(null, destAddr + 8L, 0L);
            u.putLong(null, destAddr + 16L, u.getLong(m, Matrix3x2f_m00 + 8L));
            u.putLong(null, destAddr + 24L, 0L);
            u.putLong(null, destAddr + 32L, 0L);
            u.putLong(null, destAddr + 40L, 1065353216L);
            u.putLong(null, destAddr + 48L, u.getLong(m, Matrix3x2f_m00 + 16L));
            u.putLong(null, destAddr + 56L, 4575657221408423936L);
        }

        public static void put4x4(Matrix3x2d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putDouble(null, destAddr, m.m00());
            u.putDouble(null, destAddr + 8L, m.m01());
            u.putDouble(null, destAddr + 16L, 0.0);
            u.putDouble(null, destAddr + 24L, 0.0);
            u.putDouble(null, destAddr + 32L, m.m10());
            u.putDouble(null, destAddr + 40L, m.m11());
            u.putDouble(null, destAddr + 48L, 0.0);
            u.putDouble(null, destAddr + 56L, 0.0);
            u.putDouble(null, destAddr + 64L, 0.0);
            u.putDouble(null, destAddr + 72L, 0.0);
            u.putDouble(null, destAddr + 80L, 1.0);
            u.putDouble(null, destAddr + 88L, 0.0);
            u.putDouble(null, destAddr + 96L, m.m20());
            u.putDouble(null, destAddr + 104L, m.m21());
            u.putDouble(null, destAddr + 112L, 0.0);
            u.putDouble(null, destAddr + 120L, 1.0);
        }

        public static void put3x3(Matrix3x2f m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putLong(null, destAddr, u.getLong(m, Matrix3x2f_m00));
            u.putInt(null, destAddr + 8L, 0);
            u.putLong(null, destAddr + 12L, u.getLong(m, Matrix3x2f_m00 + 8L));
            u.putInt(null, destAddr + 20L, 0);
            u.putLong(null, destAddr + 24L, u.getLong(m, Matrix3x2f_m00 + 16L));
            u.putFloat(null, destAddr + 32L, 1.0f);
        }

        public static void put3x3(Matrix3x2d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putDouble(null, destAddr, m.m00());
            u.putDouble(null, destAddr + 8L, m.m01());
            u.putDouble(null, destAddr + 16L, 0.0);
            u.putDouble(null, destAddr + 24L, m.m10());
            u.putDouble(null, destAddr + 32L, m.m11());
            u.putDouble(null, destAddr + 40L, 0.0);
            u.putDouble(null, destAddr + 48L, m.m20());
            u.putDouble(null, destAddr + 56L, m.m21());
            u.putDouble(null, destAddr + 64L, 1.0);
        }

        public static void putTransposed(Matrix4f m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putFloat(null, destAddr, m.m00());
            u.putFloat(null, destAddr + 4L, m.m10());
            u.putFloat(null, destAddr + 8L, m.m20());
            u.putFloat(null, destAddr + 12L, m.m30());
            u.putFloat(null, destAddr + 16L, m.m01());
            u.putFloat(null, destAddr + 20L, m.m11());
            u.putFloat(null, destAddr + 24L, m.m21());
            u.putFloat(null, destAddr + 28L, m.m31());
            u.putFloat(null, destAddr + 32L, m.m02());
            u.putFloat(null, destAddr + 36L, m.m12());
            u.putFloat(null, destAddr + 40L, m.m22());
            u.putFloat(null, destAddr + 44L, m.m32());
            u.putFloat(null, destAddr + 48L, m.m03());
            u.putFloat(null, destAddr + 52L, m.m13());
            u.putFloat(null, destAddr + 56L, m.m23());
            u.putFloat(null, destAddr + 60L, m.m33());
        }

        public static void put4x3Transposed(Matrix4f m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putFloat(null, destAddr, m.m00());
            u.putFloat(null, destAddr + 4L, m.m10());
            u.putFloat(null, destAddr + 8L, m.m20());
            u.putFloat(null, destAddr + 12L, m.m30());
            u.putFloat(null, destAddr + 16L, m.m01());
            u.putFloat(null, destAddr + 20L, m.m11());
            u.putFloat(null, destAddr + 24L, m.m21());
            u.putFloat(null, destAddr + 28L, m.m31());
            u.putFloat(null, destAddr + 32L, m.m02());
            u.putFloat(null, destAddr + 36L, m.m12());
            u.putFloat(null, destAddr + 40L, m.m22());
            u.putFloat(null, destAddr + 44L, m.m32());
        }

        public static void putTransposed(Matrix4x3f m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putFloat(null, destAddr, m.m00());
            u.putFloat(null, destAddr + 4L, m.m10());
            u.putFloat(null, destAddr + 8L, m.m20());
            u.putFloat(null, destAddr + 12L, m.m30());
            u.putFloat(null, destAddr + 16L, m.m01());
            u.putFloat(null, destAddr + 20L, m.m11());
            u.putFloat(null, destAddr + 24L, m.m21());
            u.putFloat(null, destAddr + 28L, m.m31());
            u.putFloat(null, destAddr + 32L, m.m02());
            u.putFloat(null, destAddr + 36L, m.m12());
            u.putFloat(null, destAddr + 40L, m.m22());
            u.putFloat(null, destAddr + 44L, m.m32());
        }

        public static void putTransposed(Matrix3f m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putFloat(null, destAddr, m.m00());
            u.putFloat(null, destAddr + 4L, m.m10());
            u.putFloat(null, destAddr + 8L, m.m20());
            u.putFloat(null, destAddr + 12L, m.m01());
            u.putFloat(null, destAddr + 16L, m.m11());
            u.putFloat(null, destAddr + 20L, m.m21());
            u.putFloat(null, destAddr + 24L, m.m02());
            u.putFloat(null, destAddr + 28L, m.m12());
            u.putFloat(null, destAddr + 32L, m.m22());
        }

        public static void putTransposed(Matrix3x2f m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putFloat(null, destAddr, m.m00());
            u.putFloat(null, destAddr + 4L, m.m10());
            u.putFloat(null, destAddr + 8L, m.m20());
            u.putFloat(null, destAddr + 12L, m.m01());
            u.putFloat(null, destAddr + 16L, m.m11());
            u.putFloat(null, destAddr + 20L, m.m21());
        }

        public static void putTransposed(Matrix2f m, long destAddr) {
            UNSAFE.putFloat(null, destAddr, m.m00());
            UNSAFE.putFloat(null, destAddr + 4L, m.m10());
            UNSAFE.putFloat(null, destAddr + 8L, m.m01());
            UNSAFE.putFloat(null, destAddr + 12L, m.m11());
        }

        public static void put(Matrix4d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putDouble(null, destAddr, m.m00());
            u.putDouble(null, destAddr + 8L, m.m01());
            u.putDouble(null, destAddr + 16L, m.m02());
            u.putDouble(null, destAddr + 24L, m.m03());
            u.putDouble(null, destAddr + 32L, m.m10());
            u.putDouble(null, destAddr + 40L, m.m11());
            u.putDouble(null, destAddr + 48L, m.m12());
            u.putDouble(null, destAddr + 56L, m.m13());
            u.putDouble(null, destAddr + 64L, m.m20());
            u.putDouble(null, destAddr + 72L, m.m21());
            u.putDouble(null, destAddr + 80L, m.m22());
            u.putDouble(null, destAddr + 88L, m.m23());
            u.putDouble(null, destAddr + 96L, m.m30());
            u.putDouble(null, destAddr + 104L, m.m31());
            u.putDouble(null, destAddr + 112L, m.m32());
            u.putDouble(null, destAddr + 120L, m.m33());
        }

        public static void put(Matrix4x3d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putDouble(null, destAddr, m.m00());
            u.putDouble(null, destAddr + 8L, m.m01());
            u.putDouble(null, destAddr + 16L, m.m02());
            u.putDouble(null, destAddr + 24L, m.m10());
            u.putDouble(null, destAddr + 32L, m.m11());
            u.putDouble(null, destAddr + 40L, m.m12());
            u.putDouble(null, destAddr + 48L, m.m20());
            u.putDouble(null, destAddr + 56L, m.m21());
            u.putDouble(null, destAddr + 64L, m.m22());
            u.putDouble(null, destAddr + 72L, m.m30());
            u.putDouble(null, destAddr + 80L, m.m31());
            u.putDouble(null, destAddr + 88L, m.m32());
        }

        public static void putTransposed(Matrix4d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putDouble(null, destAddr, m.m00());
            u.putDouble(null, destAddr + 8L, m.m10());
            u.putDouble(null, destAddr + 16L, m.m20());
            u.putDouble(null, destAddr + 24L, m.m30());
            u.putDouble(null, destAddr + 32L, m.m01());
            u.putDouble(null, destAddr + 40L, m.m11());
            u.putDouble(null, destAddr + 48L, m.m21());
            u.putDouble(null, destAddr + 56L, m.m31());
            u.putDouble(null, destAddr + 64L, m.m02());
            u.putDouble(null, destAddr + 72L, m.m12());
            u.putDouble(null, destAddr + 80L, m.m22());
            u.putDouble(null, destAddr + 88L, m.m32());
            u.putDouble(null, destAddr + 96L, m.m03());
            u.putDouble(null, destAddr + 104L, m.m13());
            u.putDouble(null, destAddr + 112L, m.m23());
            u.putDouble(null, destAddr + 120L, m.m33());
        }

        public static void putfTransposed(Matrix4d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putFloat(null, destAddr, (float)m.m00());
            u.putFloat(null, destAddr + 4L, (float)m.m10());
            u.putFloat(null, destAddr + 8L, (float)m.m20());
            u.putFloat(null, destAddr + 12L, (float)m.m30());
            u.putFloat(null, destAddr + 16L, (float)m.m01());
            u.putFloat(null, destAddr + 20L, (float)m.m11());
            u.putFloat(null, destAddr + 24L, (float)m.m21());
            u.putFloat(null, destAddr + 28L, (float)m.m31());
            u.putFloat(null, destAddr + 32L, (float)m.m02());
            u.putFloat(null, destAddr + 36L, (float)m.m12());
            u.putFloat(null, destAddr + 40L, (float)m.m22());
            u.putFloat(null, destAddr + 44L, (float)m.m32());
            u.putFloat(null, destAddr + 48L, (float)m.m03());
            u.putFloat(null, destAddr + 52L, (float)m.m13());
            u.putFloat(null, destAddr + 56L, (float)m.m23());
            u.putFloat(null, destAddr + 60L, (float)m.m33());
        }

        public static void put4x3Transposed(Matrix4d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putDouble(null, destAddr, m.m00());
            u.putDouble(null, destAddr + 8L, m.m10());
            u.putDouble(null, destAddr + 16L, m.m20());
            u.putDouble(null, destAddr + 24L, m.m30());
            u.putDouble(null, destAddr + 32L, m.m01());
            u.putDouble(null, destAddr + 40L, m.m11());
            u.putDouble(null, destAddr + 48L, m.m21());
            u.putDouble(null, destAddr + 56L, m.m31());
            u.putDouble(null, destAddr + 64L, m.m02());
            u.putDouble(null, destAddr + 72L, m.m12());
            u.putDouble(null, destAddr + 80L, m.m22());
            u.putDouble(null, destAddr + 88L, m.m32());
        }

        public static void putTransposed(Matrix4x3d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putDouble(null, destAddr, m.m00());
            u.putDouble(null, destAddr + 8L, m.m10());
            u.putDouble(null, destAddr + 16L, m.m20());
            u.putDouble(null, destAddr + 24L, m.m30());
            u.putDouble(null, destAddr + 32L, m.m01());
            u.putDouble(null, destAddr + 40L, m.m11());
            u.putDouble(null, destAddr + 48L, m.m21());
            u.putDouble(null, destAddr + 56L, m.m31());
            u.putDouble(null, destAddr + 64L, m.m02());
            u.putDouble(null, destAddr + 72L, m.m12());
            u.putDouble(null, destAddr + 80L, m.m22());
            u.putDouble(null, destAddr + 88L, m.m32());
        }

        public static void putTransposed(Matrix3d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putDouble(null, destAddr, m.m00());
            u.putDouble(null, destAddr + 8L, m.m10());
            u.putDouble(null, destAddr + 16L, m.m20());
            u.putDouble(null, destAddr + 24L, m.m01());
            u.putDouble(null, destAddr + 32L, m.m11());
            u.putDouble(null, destAddr + 40L, m.m21());
            u.putDouble(null, destAddr + 48L, m.m02());
            u.putDouble(null, destAddr + 56L, m.m12());
            u.putDouble(null, destAddr + 64L, m.m22());
        }

        public static void putTransposed(Matrix3x2d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putDouble(null, destAddr, m.m00());
            u.putDouble(null, destAddr + 8L, m.m10());
            u.putDouble(null, destAddr + 16L, m.m20());
            u.putDouble(null, destAddr + 24L, m.m01());
            u.putDouble(null, destAddr + 32L, m.m11());
            u.putDouble(null, destAddr + 40L, m.m21());
        }

        public static void putTransposed(Matrix2d m, long destAddr) {
            UNSAFE.putDouble(null, destAddr, m.m00());
            UNSAFE.putDouble(null, destAddr + 8L, m.m10());
            UNSAFE.putDouble(null, destAddr + 16L, m.m10());
            UNSAFE.putDouble(null, destAddr + 24L, m.m10());
        }

        public static void putfTransposed(Matrix4x3d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putFloat(null, destAddr, (float)m.m00());
            u.putFloat(null, destAddr + 4L, (float)m.m10());
            u.putFloat(null, destAddr + 8L, (float)m.m20());
            u.putFloat(null, destAddr + 12L, (float)m.m30());
            u.putFloat(null, destAddr + 16L, (float)m.m01());
            u.putFloat(null, destAddr + 20L, (float)m.m11());
            u.putFloat(null, destAddr + 24L, (float)m.m21());
            u.putFloat(null, destAddr + 28L, (float)m.m31());
            u.putFloat(null, destAddr + 32L, (float)m.m02());
            u.putFloat(null, destAddr + 36L, (float)m.m12());
            u.putFloat(null, destAddr + 40L, (float)m.m22());
            u.putFloat(null, destAddr + 44L, (float)m.m32());
        }

        public static void putfTransposed(Matrix3d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putFloat(null, destAddr, (float)m.m00());
            u.putFloat(null, destAddr + 4L, (float)m.m10());
            u.putFloat(null, destAddr + 8L, (float)m.m20());
            u.putFloat(null, destAddr + 12L, (float)m.m01());
            u.putFloat(null, destAddr + 16L, (float)m.m11());
            u.putFloat(null, destAddr + 20L, (float)m.m21());
            u.putFloat(null, destAddr + 24L, (float)m.m02());
            u.putFloat(null, destAddr + 28L, (float)m.m12());
            u.putFloat(null, destAddr + 32L, (float)m.m22());
        }

        public static void putfTransposed(Matrix3x2d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putFloat(null, destAddr, (float)m.m00());
            u.putFloat(null, destAddr + 4L, (float)m.m10());
            u.putFloat(null, destAddr + 8L, (float)m.m20());
            u.putFloat(null, destAddr + 12L, (float)m.m01());
            u.putFloat(null, destAddr + 16L, (float)m.m11());
            u.putFloat(null, destAddr + 20L, (float)m.m21());
        }

        public static void putfTransposed(Matrix2d m, long destAddr) {
            UNSAFE.putFloat(null, destAddr, (float)m.m00());
            UNSAFE.putFloat(null, destAddr + 4L, (float)m.m00());
            UNSAFE.putFloat(null, destAddr + 8L, (float)m.m00());
            UNSAFE.putFloat(null, destAddr + 12L, (float)m.m00());
        }

        public static void putf(Matrix4d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putFloat(null, destAddr, (float)m.m00());
            u.putFloat(null, destAddr + 4L, (float)m.m01());
            u.putFloat(null, destAddr + 8L, (float)m.m02());
            u.putFloat(null, destAddr + 12L, (float)m.m03());
            u.putFloat(null, destAddr + 16L, (float)m.m10());
            u.putFloat(null, destAddr + 20L, (float)m.m11());
            u.putFloat(null, destAddr + 24L, (float)m.m12());
            u.putFloat(null, destAddr + 28L, (float)m.m13());
            u.putFloat(null, destAddr + 32L, (float)m.m20());
            u.putFloat(null, destAddr + 36L, (float)m.m21());
            u.putFloat(null, destAddr + 40L, (float)m.m22());
            u.putFloat(null, destAddr + 44L, (float)m.m23());
            u.putFloat(null, destAddr + 48L, (float)m.m30());
            u.putFloat(null, destAddr + 52L, (float)m.m31());
            u.putFloat(null, destAddr + 56L, (float)m.m32());
            u.putFloat(null, destAddr + 60L, (float)m.m33());
        }

        public static void putf(Matrix4x3d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putFloat(null, destAddr, (float)m.m00());
            u.putFloat(null, destAddr + 4L, (float)m.m01());
            u.putFloat(null, destAddr + 8L, (float)m.m02());
            u.putFloat(null, destAddr + 12L, (float)m.m10());
            u.putFloat(null, destAddr + 16L, (float)m.m11());
            u.putFloat(null, destAddr + 20L, (float)m.m12());
            u.putFloat(null, destAddr + 24L, (float)m.m20());
            u.putFloat(null, destAddr + 28L, (float)m.m21());
            u.putFloat(null, destAddr + 32L, (float)m.m22());
            u.putFloat(null, destAddr + 36L, (float)m.m30());
            u.putFloat(null, destAddr + 40L, (float)m.m31());
            u.putFloat(null, destAddr + 44L, (float)m.m32());
        }

        public static void put(Matrix3f m, long destAddr) {
            for (int i = 0; i < 4; ++i) {
                UNSAFE.putLong(null, destAddr + (long)(i << 3), UNSAFE.getLong(m, Matrix3f_m00 + (long)(i << 3)));
            }
            UNSAFE.putFloat(null, destAddr + 32L, m.m22());
        }

        public static void put3x4(Matrix3f m, long destAddr) {
            for (int i = 0; i < 3; ++i) {
                UNSAFE.putLong(null, destAddr + (long)(i << 4), UNSAFE.getLong(m, Matrix3f_m00 + (long)(12 * i)));
                UNSAFE.putFloat(null, destAddr + (long)(i << 4) + 8L, UNSAFE.getFloat(m, Matrix3f_m00 + 8L + (long)(12 * i)));
                UNSAFE.putFloat(null, destAddr + (long)(i << 4) + 12L, 0.0f);
            }
        }

        public static void put(Matrix3d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putDouble(null, destAddr, m.m00());
            u.putDouble(null, destAddr + 8L, m.m01());
            u.putDouble(null, destAddr + 16L, m.m02());
            u.putDouble(null, destAddr + 24L, m.m10());
            u.putDouble(null, destAddr + 32L, m.m11());
            u.putDouble(null, destAddr + 40L, m.m12());
            u.putDouble(null, destAddr + 48L, m.m20());
            u.putDouble(null, destAddr + 56L, m.m21());
            u.putDouble(null, destAddr + 64L, m.m22());
        }

        public static void put(Matrix3x2f m, long destAddr) {
            for (int i = 0; i < 3; ++i) {
                UNSAFE.putLong(null, destAddr + (long)(i << 3), UNSAFE.getLong(m, Matrix3x2f_m00 + (long)(i << 3)));
            }
        }

        public static void put(Matrix3x2d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putDouble(null, destAddr, m.m00());
            u.putDouble(null, destAddr + 8L, m.m01());
            u.putDouble(null, destAddr + 16L, m.m10());
            u.putDouble(null, destAddr + 24L, m.m11());
            u.putDouble(null, destAddr + 32L, m.m20());
            u.putDouble(null, destAddr + 40L, m.m21());
        }

        public static void putf(Matrix3d m, long destAddr) {
            Unsafe u = UNSAFE;
            u.putFloat(null, destAddr, (float)m.m00());
            u.putFloat(null, destAddr + 4L, (float)m.m01());
            u.putFloat(null, destAddr + 8L, (float)m.m02());
            u.putFloat(null, destAddr + 12L, (float)m.m10());
            u.putFloat(null, destAddr + 16L, (float)m.m11());
            u.putFloat(null, destAddr + 20L, (float)m.m12());
            u.putFloat(null, destAddr + 24L, (float)m.m20());
            u.putFloat(null, destAddr + 28L, (float)m.m21());
            u.putFloat(null, destAddr + 32L, (float)m.m22());
        }

        public static void put(Matrix2f m, long destAddr) {
            UNSAFE.putLong(null, destAddr, UNSAFE.getLong(m, Matrix2f_m00));
            UNSAFE.putLong(null, destAddr + 8L, UNSAFE.getLong(m, Matrix2f_m00 + 8L));
        }

        public static void put(Matrix2d m, long destAddr) {
            UNSAFE.putDouble(null, destAddr, m.m00());
            UNSAFE.putDouble(null, destAddr + 8L, m.m01());
            UNSAFE.putDouble(null, destAddr + 16L, m.m10());
            UNSAFE.putDouble(null, destAddr + 24L, m.m11());
        }

        public static void putf(Matrix2d m, long destAddr) {
            UNSAFE.putFloat(null, destAddr, (float)m.m00());
            UNSAFE.putFloat(null, destAddr + 4L, (float)m.m01());
            UNSAFE.putFloat(null, destAddr + 8L, (float)m.m10());
            UNSAFE.putFloat(null, destAddr + 12L, (float)m.m11());
        }

        public static void put(Vector4d src, long destAddr) {
            UNSAFE.putDouble(null, destAddr, src.x);
            UNSAFE.putDouble(null, destAddr + 8L, src.y);
            UNSAFE.putDouble(null, destAddr + 16L, src.z);
            UNSAFE.putDouble(null, destAddr + 24L, src.w);
        }

        public static void putf(Vector4d src, long destAddr) {
            UNSAFE.putFloat(null, destAddr, (float)src.x);
            UNSAFE.putFloat(null, destAddr + 4L, (float)src.y);
            UNSAFE.putFloat(null, destAddr + 8L, (float)src.z);
            UNSAFE.putFloat(null, destAddr + 12L, (float)src.w);
        }

        public static void put(Vector4f src, long destAddr) {
            UNSAFE.putLong(null, destAddr, UNSAFE.getLong(src, Vector4f_x));
            UNSAFE.putLong(null, destAddr + 8L, UNSAFE.getLong(src, Vector4f_x + 8L));
        }

        public static void put(Vector4i src, long destAddr) {
            UNSAFE.putLong(null, destAddr, UNSAFE.getLong(src, Vector4i_x));
            UNSAFE.putLong(null, destAddr + 8L, UNSAFE.getLong(src, Vector4i_x + 8L));
        }

        public static void put(Vector4L src, long destAddr) {
            UNSAFE.putLong(null, destAddr, src.x);
            UNSAFE.putLong(null, destAddr + 8L, src.y);
            UNSAFE.putLong(null, destAddr + 16L, src.z);
            UNSAFE.putLong(null, destAddr + 24L, src.w);
        }

        public static void put(Vector3f src, long destAddr) {
            UNSAFE.putLong(null, destAddr, UNSAFE.getLong(src, Vector3f_x));
            UNSAFE.putFloat(null, destAddr + 8L, src.z);
        }

        public static void put(Vector3d src, long destAddr) {
            UNSAFE.putDouble(null, destAddr, src.x);
            UNSAFE.putDouble(null, destAddr + 8L, src.y);
            UNSAFE.putDouble(null, destAddr + 16L, src.z);
        }

        public static void putf(Vector3d src, long destAddr) {
            UNSAFE.putFloat(null, destAddr, (float)src.x);
            UNSAFE.putFloat(null, destAddr + 4L, (float)src.y);
            UNSAFE.putFloat(null, destAddr + 8L, (float)src.z);
        }

        public static void put(Vector3i src, long destAddr) {
            UNSAFE.putLong(null, destAddr, UNSAFE.getLong(src, Vector3i_x));
            UNSAFE.putInt(null, destAddr + 8L, src.z);
        }

        public static void put(Vector3L src, long destAddr) {
            UNSAFE.putLong(null, destAddr, src.x);
            UNSAFE.putLong(null, destAddr + 8L, src.y);
            UNSAFE.putLong(null, destAddr + 16L, src.z);
        }

        public static void put(Vector2f src, long destAddr) {
            UNSAFE.putLong(null, destAddr, UNSAFE.getLong(src, Vector2f_x));
        }

        public static void put(Vector2d src, long destAddr) {
            UNSAFE.putDouble(null, destAddr, src.x);
            UNSAFE.putDouble(null, destAddr + 8L, src.y);
        }

        public static void put(Vector2i src, long destAddr) {
            UNSAFE.putLong(null, destAddr, UNSAFE.getLong(src, Vector2i_x));
        }

        public static void put(Vector2L src, long destAddr) {
            UNSAFE.putLong(null, destAddr, src.x);
            UNSAFE.putLong(null, destAddr + 8L, src.y);
        }

        public static void get(Matrix4f m, long srcAddr) {
            for (int i = 0; i < 8; ++i) {
                UNSAFE.putLong(m, Matrix4f_m00 + (long)(i << 3), UNSAFE.getLong(srcAddr + (long)(i << 3)));
            }
        }

        public static void getTransposed(Matrix4f m, long srcAddr) {
            Unsafe u = UNSAFE;
            m._m00(u.getFloat(srcAddr))._m10(u.getFloat(srcAddr + 4L))._m20(u.getFloat(srcAddr + 8L))._m30(u.getFloat(srcAddr + 12L))._m01(u.getFloat(srcAddr + 16L))._m11(u.getFloat(srcAddr + 20L))._m21(u.getFloat(srcAddr + 24L))._m31(u.getFloat(srcAddr + 28L))._m02(u.getFloat(srcAddr + 32L))._m12(u.getFloat(srcAddr + 36L))._m22(u.getFloat(srcAddr + 40L))._m32(u.getFloat(srcAddr + 44L))._m03(u.getFloat(srcAddr + 48L))._m13(u.getFloat(srcAddr + 52L))._m23(u.getFloat(srcAddr + 56L))._m33(u.getFloat(srcAddr + 60L));
        }

        public static void getTransposed(Matrix3f m, long srcAddr) {
            Unsafe u = UNSAFE;
            m._m00(u.getFloat(srcAddr))._m10(u.getFloat(srcAddr + 4L))._m20(u.getFloat(srcAddr + 8L))._m01(u.getFloat(srcAddr + 12L))._m11(u.getFloat(srcAddr + 16L))._m21(u.getFloat(srcAddr + 20L))._m02(u.getFloat(srcAddr + 24L))._m12(u.getFloat(srcAddr + 28L))._m22(u.getFloat(srcAddr + 32L));
        }

        public static void getTransposed(Matrix4x3f m, long srcAddr) {
            Unsafe u = UNSAFE;
            m._m00(u.getFloat(srcAddr))._m10(u.getFloat(srcAddr + 4L))._m20(u.getFloat(srcAddr + 8L))._m30(u.getFloat(srcAddr + 12L))._m01(u.getFloat(srcAddr + 16L))._m11(u.getFloat(srcAddr + 20L))._m21(u.getFloat(srcAddr + 24L))._m31(u.getFloat(srcAddr + 28L))._m02(u.getFloat(srcAddr + 32L))._m12(u.getFloat(srcAddr + 36L))._m22(u.getFloat(srcAddr + 40L))._m32(u.getFloat(srcAddr + 44L));
        }

        public static void getTransposed(Matrix3x2f m, long srcAddr) {
            Unsafe u = UNSAFE;
            m._m00(u.getFloat(srcAddr))._m10(u.getFloat(srcAddr + 4L))._m20(u.getFloat(srcAddr + 8L))._m01(u.getFloat(srcAddr + 12L))._m11(u.getFloat(srcAddr + 16L))._m21(u.getFloat(srcAddr + 20L));
        }

        public static void getTransposed(Matrix2f m, long srcAddr) {
            Unsafe u = UNSAFE;
            m._m00(u.getFloat(srcAddr))._m10(u.getFloat(srcAddr + 4L))._m01(u.getFloat(srcAddr + 8L))._m11(u.getFloat(srcAddr + 12L));
        }

        public static void getTransposed(Matrix2d m, long srcAddr) {
            Unsafe u = UNSAFE;
            m._m00(u.getDouble(srcAddr))._m10(u.getDouble(srcAddr + 8L))._m01(u.getDouble(srcAddr + 16L))._m11(u.getDouble(srcAddr + 24L));
        }

        public static void getTransposed(Matrix4x3d m, long srcAddr) {
            Unsafe u = UNSAFE;
            m._m00(u.getDouble(srcAddr))._m10(u.getDouble(srcAddr + 8L))._m20(u.getDouble(srcAddr + 16L))._m30(u.getDouble(srcAddr + 24L))._m01(u.getDouble(srcAddr + 32L))._m11(u.getDouble(srcAddr + 40L))._m21(u.getDouble(srcAddr + 48L))._m31(u.getDouble(srcAddr + 56L))._m02(u.getDouble(srcAddr + 64L))._m12(u.getDouble(srcAddr + 72L))._m22(u.getDouble(srcAddr + 80L))._m32(u.getDouble(srcAddr + 88L));
        }

        public static void getTransposed(Matrix3x2d m, long srcAddr) {
            Unsafe u = UNSAFE;
            m._m00(u.getDouble(srcAddr))._m10(u.getDouble(srcAddr + 8L))._m20(u.getDouble(srcAddr + 16L))._m01(u.getDouble(srcAddr + 24L))._m11(u.getDouble(srcAddr + 32L))._m21(u.getDouble(srcAddr + 40L));
        }

        public static void getTransposed(Matrix3d m, long srcAddr) {
            Unsafe u = UNSAFE;
            m._m00(u.getDouble(srcAddr))._m10(u.getDouble(srcAddr + 8L))._m20(u.getDouble(srcAddr + 16L))._m01(u.getDouble(srcAddr + 24L))._m11(u.getDouble(srcAddr + 32L))._m21(u.getDouble(srcAddr + 40L))._m02(u.getDouble(srcAddr + 48L))._m12(u.getDouble(srcAddr + 56L))._m22(u.getDouble(srcAddr + 64L));
        }

        public static void getTransposed(Matrix4d m, long srcAddr) {
            Unsafe u = UNSAFE;
            m._m00(u.getDouble(srcAddr))._m10(u.getDouble(srcAddr + 8L))._m20(u.getDouble(srcAddr + 16L))._m30(u.getDouble(srcAddr + 24L))._m01(u.getDouble(srcAddr + 32L))._m11(u.getDouble(srcAddr + 40L))._m21(u.getDouble(srcAddr + 48L))._m31(u.getDouble(srcAddr + 56L))._m02(u.getDouble(srcAddr + 64L))._m12(u.getDouble(srcAddr + 72L))._m22(u.getDouble(srcAddr + 80L))._m32(u.getDouble(srcAddr + 88L))._m03(u.getDouble(srcAddr + 96L))._m13(u.getDouble(srcAddr + 104L))._m23(u.getDouble(srcAddr + 112L))._m33(u.getDouble(srcAddr + 120L));
        }

        public static void get(Matrix4x3f m, long srcAddr) {
            for (int i = 0; i < 6; ++i) {
                UNSAFE.putLong(m, Matrix4x3f_m00 + (long)(i << 3), UNSAFE.getLong(srcAddr + (long)(i << 3)));
            }
        }

        public static void get(Matrix4d m, long srcAddr) {
            Unsafe u = UNSAFE;
            m._m00(u.getDouble(null, srcAddr))._m01(u.getDouble(null, srcAddr + 8L))._m02(u.getDouble(null, srcAddr + 16L))._m03(u.getDouble(null, srcAddr + 24L))._m10(u.getDouble(null, srcAddr + 32L))._m11(u.getDouble(null, srcAddr + 40L))._m12(u.getDouble(null, srcAddr + 48L))._m13(u.getDouble(null, srcAddr + 56L))._m20(u.getDouble(null, srcAddr + 64L))._m21(u.getDouble(null, srcAddr + 72L))._m22(u.getDouble(null, srcAddr + 80L))._m23(u.getDouble(null, srcAddr + 88L))._m30(u.getDouble(null, srcAddr + 96L))._m31(u.getDouble(null, srcAddr + 104L))._m32(u.getDouble(null, srcAddr + 112L))._m33(u.getDouble(null, srcAddr + 120L));
        }

        public static void get(Matrix4x3d m, long srcAddr) {
            Unsafe u = UNSAFE;
            m._m00(u.getDouble(null, srcAddr))._m01(u.getDouble(null, srcAddr + 8L))._m02(u.getDouble(null, srcAddr + 16L))._m10(u.getDouble(null, srcAddr + 24L))._m11(u.getDouble(null, srcAddr + 32L))._m12(u.getDouble(null, srcAddr + 40L))._m20(u.getDouble(null, srcAddr + 48L))._m21(u.getDouble(null, srcAddr + 56L))._m22(u.getDouble(null, srcAddr + 64L))._m30(u.getDouble(null, srcAddr + 72L))._m31(u.getDouble(null, srcAddr + 80L))._m32(u.getDouble(null, srcAddr + 88L));
        }

        public static void getf(Matrix4d m, long srcAddr) {
            Unsafe u = UNSAFE;
            m._m00(u.getFloat(null, srcAddr))._m01(u.getFloat(null, srcAddr + 4L))._m02(u.getFloat(null, srcAddr + 8L))._m03(u.getFloat(null, srcAddr + 12L))._m10(u.getFloat(null, srcAddr + 16L))._m11(u.getFloat(null, srcAddr + 20L))._m12(u.getFloat(null, srcAddr + 24L))._m13(u.getFloat(null, srcAddr + 28L))._m20(u.getFloat(null, srcAddr + 32L))._m21(u.getFloat(null, srcAddr + 36L))._m22(u.getFloat(null, srcAddr + 40L))._m23(u.getFloat(null, srcAddr + 44L))._m30(u.getFloat(null, srcAddr + 48L))._m31(u.getFloat(null, srcAddr + 52L))._m32(u.getFloat(null, srcAddr + 56L))._m33(u.getFloat(null, srcAddr + 60L));
        }

        public static void getf(Matrix4x3d m, long srcAddr) {
            Unsafe u = UNSAFE;
            m._m00(u.getFloat(null, srcAddr))._m01(u.getFloat(null, srcAddr + 4L))._m02(u.getFloat(null, srcAddr + 8L))._m10(u.getFloat(null, srcAddr + 12L))._m11(u.getFloat(null, srcAddr + 16L))._m12(u.getFloat(null, srcAddr + 20L))._m20(u.getFloat(null, srcAddr + 24L))._m21(u.getFloat(null, srcAddr + 28L))._m22(u.getFloat(null, srcAddr + 32L))._m30(u.getFloat(null, srcAddr + 36L))._m31(u.getFloat(null, srcAddr + 40L))._m32(u.getFloat(null, srcAddr + 44L));
        }

        public static void get(Matrix3f m, long srcAddr) {
            for (int i = 0; i < 4; ++i) {
                UNSAFE.putLong(m, Matrix3f_m00 + (long)(i << 3), UNSAFE.getLong(null, srcAddr + (long)(i << 3)));
            }
            m._m22(UNSAFE.getFloat(null, srcAddr + 32L));
        }

        public static void get(Matrix3d m, long srcAddr) {
            Unsafe u = UNSAFE;
            m._m00(u.getDouble(null, srcAddr))._m01(u.getDouble(null, srcAddr + 8L))._m02(u.getDouble(null, srcAddr + 16L))._m10(u.getDouble(null, srcAddr + 24L))._m11(u.getDouble(null, srcAddr + 32L))._m12(u.getDouble(null, srcAddr + 40L))._m20(u.getDouble(null, srcAddr + 48L))._m21(u.getDouble(null, srcAddr + 56L))._m22(u.getDouble(null, srcAddr + 64L));
        }

        public static void get(Matrix3x2f m, long srcAddr) {
            for (int i = 0; i < 3; ++i) {
                UNSAFE.putLong(m, Matrix3x2f_m00 + (long)(i << 3), UNSAFE.getLong(null, srcAddr + (long)(i << 3)));
            }
        }

        public static void get(Matrix3x2d m, long srcAddr) {
            Unsafe u = UNSAFE;
            m._m00(u.getDouble(null, srcAddr))._m01(u.getDouble(null, srcAddr + 8L))._m10(u.getDouble(null, srcAddr + 16L))._m11(u.getDouble(null, srcAddr + 24L))._m20(u.getDouble(null, srcAddr + 32L))._m21(u.getDouble(null, srcAddr + 40L));
        }

        public static void getf(Matrix3d m, long srcAddr) {
            Unsafe u = UNSAFE;
            m._m00(u.getFloat(null, srcAddr))._m01(u.getFloat(null, srcAddr + 4L))._m02(u.getFloat(null, srcAddr + 8L))._m10(u.getFloat(null, srcAddr + 12L))._m11(u.getFloat(null, srcAddr + 16L))._m12(u.getFloat(null, srcAddr + 20L))._m20(u.getFloat(null, srcAddr + 24L))._m21(u.getFloat(null, srcAddr + 28L))._m22(u.getFloat(null, srcAddr + 32L));
        }

        public static void get(Matrix2f m, long srcAddr) {
            UNSAFE.putLong(m, Matrix2f_m00, UNSAFE.getLong(null, srcAddr));
            UNSAFE.putLong(m, Matrix2f_m00 + 8L, UNSAFE.getLong(null, srcAddr + 8L));
        }

        public static void get(Matrix2d m, long srcAddr) {
            m._m00(UNSAFE.getDouble(null, srcAddr))._m01(UNSAFE.getDouble(null, srcAddr + 8L))._m10(UNSAFE.getDouble(null, srcAddr + 16L))._m11(UNSAFE.getDouble(null, srcAddr + 24L));
        }

        public static void getf(Matrix2d m, long srcAddr) {
            m._m00(UNSAFE.getFloat(null, srcAddr))._m01(UNSAFE.getFloat(null, srcAddr + 4L))._m10(UNSAFE.getFloat(null, srcAddr + 8L))._m11(UNSAFE.getFloat(null, srcAddr + 12L));
        }

        public static void get(Vector4d dst, long srcAddr) {
            dst.x = UNSAFE.getDouble(null, srcAddr);
            dst.y = UNSAFE.getDouble(null, srcAddr + 8L);
            dst.z = UNSAFE.getDouble(null, srcAddr + 16L);
            dst.w = UNSAFE.getDouble(null, srcAddr + 24L);
        }

        public static void get(Vector4f dst, long srcAddr) {
            dst.x = UNSAFE.getFloat(null, srcAddr);
            dst.y = UNSAFE.getFloat(null, srcAddr + 4L);
            dst.z = UNSAFE.getFloat(null, srcAddr + 8L);
            dst.w = UNSAFE.getFloat(null, srcAddr + 12L);
        }

        public static void get(Vector4i dst, long srcAddr) {
            dst.x = UNSAFE.getInt(null, srcAddr);
            dst.y = UNSAFE.getInt(null, srcAddr + 4L);
            dst.z = UNSAFE.getInt(null, srcAddr + 8L);
            dst.w = UNSAFE.getInt(null, srcAddr + 12L);
        }

        public static void get(Vector4L dst, long srcAddr) {
            dst.x = UNSAFE.getLong(null, srcAddr);
            dst.y = UNSAFE.getLong(null, srcAddr + 8L);
            dst.z = UNSAFE.getLong(null, srcAddr + 16L);
            dst.w = UNSAFE.getLong(null, srcAddr + 24L);
        }

        public static void get(Vector3f dst, long srcAddr) {
            dst.x = UNSAFE.getFloat(null, srcAddr);
            dst.y = UNSAFE.getFloat(null, srcAddr + 4L);
            dst.z = UNSAFE.getFloat(null, srcAddr + 8L);
        }

        public static void get(Vector3d dst, long srcAddr) {
            dst.x = UNSAFE.getDouble(null, srcAddr);
            dst.y = UNSAFE.getDouble(null, srcAddr + 8L);
            dst.z = UNSAFE.getDouble(null, srcAddr + 16L);
        }

        public static void get(Vector3i dst, long srcAddr) {
            dst.x = UNSAFE.getInt(null, srcAddr);
            dst.y = UNSAFE.getInt(null, srcAddr + 4L);
            dst.z = UNSAFE.getInt(null, srcAddr + 8L);
        }

        public static void get(Vector3L dst, long srcAddr) {
            dst.x = UNSAFE.getLong(null, srcAddr);
            dst.y = UNSAFE.getLong(null, srcAddr + 8L);
            dst.z = UNSAFE.getLong(null, srcAddr + 16L);
        }

        public static void get(Vector2f dst, long srcAddr) {
            dst.x = UNSAFE.getFloat(null, srcAddr);
            dst.y = UNSAFE.getFloat(null, srcAddr + 4L);
        }

        public static void get(Vector2d dst, long srcAddr) {
            dst.x = UNSAFE.getDouble(null, srcAddr);
            dst.y = UNSAFE.getDouble(null, srcAddr + 8L);
        }

        public static void get(Vector2i dst, long srcAddr) {
            dst.x = UNSAFE.getInt(null, srcAddr);
            dst.y = UNSAFE.getInt(null, srcAddr + 4L);
        }

        public static void get(Vector2L dst, long srcAddr) {
            dst.x = UNSAFE.getLong(null, srcAddr);
            dst.y = UNSAFE.getLong(null, srcAddr + 8L);
        }

        public static void putMatrix3f(Quaternionf q, long addr) {
            float dx = q.x + q.x;
            float dy = q.y + q.y;
            float dz = q.z + q.z;
            float q00 = dx * q.x;
            float q11 = dy * q.y;
            float q22 = dz * q.z;
            float q01 = dx * q.y;
            float q02 = dx * q.z;
            float q03 = dx * q.w;
            float q12 = dy * q.z;
            float q13 = dy * q.w;
            float q23 = dz * q.w;
            Unsafe u = UNSAFE;
            u.putFloat(null, addr, 1.0f - q11 - q22);
            u.putFloat(null, addr + 4L, q01 + q23);
            u.putFloat(null, addr + 8L, q02 - q13);
            u.putFloat(null, addr + 12L, q01 - q23);
            u.putFloat(null, addr + 16L, 1.0f - q22 - q00);
            u.putFloat(null, addr + 20L, q12 + q03);
            u.putFloat(null, addr + 24L, q02 + q13);
            u.putFloat(null, addr + 28L, q12 - q03);
            u.putFloat(null, addr + 32L, 1.0f - q11 - q00);
        }

        public static void putMatrix4f(Quaternionf q, long addr) {
            float dx = q.x + q.x;
            float dy = q.y + q.y;
            float dz = q.z + q.z;
            float q00 = dx * q.x;
            float q11 = dy * q.y;
            float q22 = dz * q.z;
            float q01 = dx * q.y;
            float q02 = dx * q.z;
            float q03 = dx * q.w;
            float q12 = dy * q.z;
            float q13 = dy * q.w;
            float q23 = dz * q.w;
            Unsafe u = UNSAFE;
            u.putFloat(null, addr, 1.0f - q11 - q22);
            u.putFloat(null, addr + 4L, q01 + q23);
            u.putLong(null, addr + 8L, (long)Float.floatToRawIntBits(q02 - q13) & 0xFFFFFFFFL);
            u.putFloat(null, addr + 16L, q01 - q23);
            u.putFloat(null, addr + 20L, 1.0f - q22 - q00);
            u.putLong(null, addr + 24L, (long)Float.floatToRawIntBits(q12 + q03) & 0xFFFFFFFFL);
            u.putFloat(null, addr + 32L, q02 + q13);
            u.putFloat(null, addr + 36L, q12 - q03);
            u.putLong(null, addr + 40L, (long)Float.floatToRawIntBits(1.0f - q11 - q00) & 0xFFFFFFFFL);
            u.putLong(null, addr + 48L, 0L);
            u.putLong(null, addr + 56L, 4575657221408423936L);
        }

        public static void putMatrix4x3f(Quaternionf q, long addr) {
            float dx = q.x + q.x;
            float dy = q.y + q.y;
            float dz = q.z + q.z;
            float q00 = dx * q.x;
            float q11 = dy * q.y;
            float q22 = dz * q.z;
            float q01 = dx * q.y;
            float q02 = dx * q.z;
            float q03 = dx * q.w;
            float q12 = dy * q.z;
            float q13 = dy * q.w;
            float q23 = dz * q.w;
            Unsafe u = UNSAFE;
            u.putFloat(null, addr, 1.0f - q11 - q22);
            u.putFloat(null, addr + 4L, q01 + q23);
            u.putFloat(null, addr + 8L, q02 - q13);
            u.putFloat(null, addr + 12L, q01 - q23);
            u.putFloat(null, addr + 16L, 1.0f - q22 - q00);
            u.putFloat(null, addr + 20L, q12 + q03);
            u.putFloat(null, addr + 24L, q02 + q13);
            u.putFloat(null, addr + 28L, q12 - q03);
            u.putFloat(null, addr + 32L, 1.0f - q11 - q00);
            u.putLong(null, addr + 36L, 0L);
            u.putFloat(null, addr + 44L, 0.0f);
        }

        private static void throwNoDirectBufferException() {
            throw new IllegalArgumentException("Must use a direct buffer");
        }

        public void putMatrix3f(Quaternionf q, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 36);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putMatrix3f(q, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putMatrix3f(q, offset, dest);
            }
        }

        public void putMatrix3f(Quaternionf q, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 9);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putMatrix3f(q, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.putMatrix3f(q, offset, dest);
            }
        }

        private static void checkPut(int offset, boolean direct, int capacity, int i) {
            if (!direct) {
                MemUtilUnsafe.throwNoDirectBufferException();
            }
            if (capacity - offset < i) {
                throw new BufferOverflowException();
            }
        }

        public void putMatrix4f(Quaternionf q, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 64);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putMatrix4f(q, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putMatrix4f(q, offset, dest);
            }
        }

        public void putMatrix4f(Quaternionf q, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putMatrix4f(q, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.putMatrix4f(q, offset, dest);
            }
        }

        public void putMatrix4x3f(Quaternionf q, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 48);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putMatrix4x3f(q, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putMatrix4x3f(q, offset, dest);
            }
        }

        public void putMatrix4x3f(Quaternionf q, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 12);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putMatrix4x3f(q, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.putMatrix4x3f(q, offset, dest);
            }
        }

        public void put(Matrix4f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put(m, offset, dest);
            }
        }

        public void put(Matrix4f m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 64);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(m, offset, dest);
            }
        }

        public void put4x3(Matrix4f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 12);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put4x3(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put4x3(m, offset, dest);
            }
        }

        public void put4x3(Matrix4f m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 48);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put4x3(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put4x3(m, offset, dest);
            }
        }

        public void put3x4(Matrix4f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 12);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put3x4(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put3x4(m, offset, dest);
            }
        }

        public void put3x4(Matrix4f m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 48);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put3x4(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put3x4(m, offset, dest);
            }
        }

        public void put(Matrix4x3f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 12);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put(m, offset, dest);
            }
        }

        public void put(Matrix4x3f m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 48);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(m, offset, dest);
            }
        }

        public void put4x4(Matrix4x3f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put4x4(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put4x4(m, offset, dest);
            }
        }

        public void put4x4(Matrix4x3f m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 64);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put4x4(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put4x4(m, offset, dest);
            }
        }

        public void put3x4(Matrix4x3f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 12);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put3x4(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put3x4(m, offset, dest);
            }
        }

        public void put3x4(Matrix4x3f m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 48);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put3x4(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put3x4(m, offset, dest);
            }
        }

        public void put4x4(Matrix4x3d m, int offset, DoubleBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put4x4(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.put4x4(m, offset, dest);
            }
        }

        public void put4x4(Matrix4x3d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 128);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put4x4(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put4x4(m, offset, dest);
            }
        }

        public void put4x4(Matrix3x2f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put4x4(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put4x4(m, offset, dest);
            }
        }

        public void put4x4(Matrix3x2f m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 64);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put4x4(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put4x4(m, offset, dest);
            }
        }

        public void put4x4(Matrix3x2d m, int offset, DoubleBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put4x4(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.put4x4(m, offset, dest);
            }
        }

        public void put4x4(Matrix3x2d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 128);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put4x4(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put4x4(m, offset, dest);
            }
        }

        public void put3x3(Matrix3x2f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 9);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put3x3(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put3x3(m, offset, dest);
            }
        }

        public void put3x3(Matrix3x2f m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 36);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put3x3(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put3x3(m, offset, dest);
            }
        }

        public void put3x3(Matrix3x2d m, int offset, DoubleBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 9);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put3x3(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.put3x3(m, offset, dest);
            }
        }

        public void put3x3(Matrix3x2d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 72);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put3x3(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put3x3(m, offset, dest);
            }
        }

        public void putTransposed(Matrix4f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void putTransposed(Matrix4f m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 64);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void put4x3Transposed(Matrix4f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 12);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put4x3Transposed(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put4x3Transposed(m, offset, dest);
            }
        }

        public void put4x3Transposed(Matrix4f m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 48);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put4x3Transposed(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put4x3Transposed(m, offset, dest);
            }
        }

        public void putTransposed(Matrix4x3f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 12);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void putTransposed(Matrix4x3f m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 48);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void putTransposed(Matrix3f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 9);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void putTransposed(Matrix3f m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 36);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void putTransposed(Matrix3x2f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 6);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void putTransposed(Matrix2f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 4);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void putTransposed(Matrix2f m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void put(Matrix4d m, int offset, DoubleBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.put(m, offset, dest);
            }
        }

        public void put(Matrix4d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 128);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(m, offset, dest);
            }
        }

        public void put(Matrix4x3d m, int offset, DoubleBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 12);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.put(m, offset, dest);
            }
        }

        public void put(Matrix4x3d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 96);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(m, offset, dest);
            }
        }

        public void putf(Matrix4d m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putf(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.putf(m, offset, dest);
            }
        }

        public void putf(Matrix4d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 64);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putf(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putf(m, offset, dest);
            }
        }

        public void putf(Matrix4x3d m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 12);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putf(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.putf(m, offset, dest);
            }
        }

        public void putf(Matrix4x3d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 48);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putf(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putf(m, offset, dest);
            }
        }

        public void putTransposed(Matrix4d m, int offset, DoubleBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void putTransposed(Matrix4d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 128);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void put4x3Transposed(Matrix4d m, int offset, DoubleBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 12);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put4x3Transposed(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.put4x3Transposed(m, offset, dest);
            }
        }

        public void put4x3Transposed(Matrix4d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 96);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put4x3Transposed(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put4x3Transposed(m, offset, dest);
            }
        }

        public void putTransposed(Matrix4x3d m, int offset, DoubleBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 12);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void putTransposed(Matrix4x3d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 96);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void putTransposed(Matrix3d m, int offset, DoubleBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 9);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void putTransposed(Matrix3d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 72);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void putTransposed(Matrix3x2d m, int offset, DoubleBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 6);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void putTransposed(Matrix3x2d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 48);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void putTransposed(Matrix2d m, int offset, DoubleBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 4);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void putTransposed(Matrix2d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 32);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putTransposed(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putTransposed(m, offset, dest);
            }
        }

        public void putfTransposed(Matrix4d m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putfTransposed(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.putfTransposed(m, offset, dest);
            }
        }

        public void putfTransposed(Matrix4d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 64);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putfTransposed(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putfTransposed(m, offset, dest);
            }
        }

        public void putfTransposed(Matrix4x3d m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 12);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putfTransposed(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.putfTransposed(m, offset, dest);
            }
        }

        public void putfTransposed(Matrix4x3d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 48);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putfTransposed(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putfTransposed(m, offset, dest);
            }
        }

        public void putfTransposed(Matrix3d m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 9);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putfTransposed(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.putfTransposed(m, offset, dest);
            }
        }

        public void putfTransposed(Matrix3d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 36);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putfTransposed(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putfTransposed(m, offset, dest);
            }
        }

        public void putfTransposed(Matrix3x2d m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 6);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putfTransposed(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.putfTransposed(m, offset, dest);
            }
        }

        public void putfTransposed(Matrix3x2d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 24);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putfTransposed(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putfTransposed(m, offset, dest);
            }
        }

        public void putfTransposed(Matrix2d m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 4);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putfTransposed(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.putfTransposed(m, offset, dest);
            }
        }

        public void putfTransposed(Matrix2d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putfTransposed(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putfTransposed(m, offset, dest);
            }
        }

        public void put(Matrix3f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 9);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put(m, offset, dest);
            }
        }

        public void put(Matrix3f m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 36);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(m, offset, dest);
            }
        }

        public void put3x4(Matrix3f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 12);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put3x4(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put3x4(m, offset, dest);
            }
        }

        public void put3x4(Matrix3f m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 48);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put3x4(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put3x4(m, offset, dest);
            }
        }

        public void put(Matrix3d m, int offset, DoubleBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 9);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.put(m, offset, dest);
            }
        }

        public void put(Matrix3d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 72);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(m, offset, dest);
            }
        }

        public void put(Matrix3x2f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 6);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put(m, offset, dest);
            }
        }

        public void put(Matrix3x2f m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 24);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(m, offset, dest);
            }
        }

        public void put(Matrix3x2d m, int offset, DoubleBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 6);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.put(m, offset, dest);
            }
        }

        public void put(Matrix3x2d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 48);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(m, offset, dest);
            }
        }

        public void putf(Matrix3d m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 9);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putf(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.putf(m, offset, dest);
            }
        }

        public void putf(Matrix3d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 36);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putf(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putf(m, offset, dest);
            }
        }

        public void put(Matrix2f m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 4);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put(m, offset, dest);
            }
        }

        public void put(Matrix2f m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(m, offset, dest);
            }
        }

        public void put(Matrix2d m, int offset, DoubleBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 4);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.put(m, offset, dest);
            }
        }

        public void put(Matrix2d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 32);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(m, offset, dest);
            }
        }

        public void putf(Matrix2d m, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 4);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putf(m, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.putf(m, offset, dest);
            }
        }

        public void putf(Matrix2d m, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putf(m, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putf(m, offset, dest);
            }
        }

        public void put(Vector4d src, int offset, DoubleBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 4);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector4d src, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 4);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putf(src, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector4d src, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 32);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(src, offset, dest);
            }
        }

        public void putf(Vector4d src, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putf(src, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putf(src, offset, dest);
            }
        }

        public void put(Vector4f src, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 4);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector4f src, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector4i src, int offset, IntBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 4);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector4i src, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector3f src, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 3);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector3f src, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 12);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector3d src, int offset, DoubleBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 3);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector3d src, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 3);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putf(src, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector3d src, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 24);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(src, offset, dest);
            }
        }

        public void putf(Vector3d src, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 12);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.putf(src, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.putf(src, offset, dest);
            }
        }

        public void put(Vector3i src, int offset, IntBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 3);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector3i src, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 12);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector2f src, int offset, FloatBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 2);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector2f src, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 8);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector2d src, int offset, DoubleBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 2);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector2d src, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector2i src, int offset, IntBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 2);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 2));
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector2i src, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 8);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector2L src, int offset, LongBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 2);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + ((long)offset << 3));
            } else {
                super.put(src, offset, dest);
            }
        }

        public void put(Vector2L src, int offset, ByteBuffer dest) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkPut(offset, dest.isDirect(), dest.capacity(), 16);
            }
            if (dest.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.put(src, UNSAFE.getLong(dest, ADDRESS) + (long)offset);
            } else {
                super.put(src, offset, dest);
            }
        }

        public void get(Matrix4f m, int offset, FloatBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 16);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 2));
            } else {
                super.get(m, offset, src);
            }
        }

        public void get(Matrix4f m, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 64);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(m, offset, src);
            }
        }

        public float get(Matrix4f m, int column, int row) {
            return UNSAFE.getFloat(m, Matrix4f_m00 + ((long)column << 4) + ((long)row << 2));
        }

        public Matrix4f set(Matrix4f m, int column, int row, float value) {
            UNSAFE.putFloat(m, Matrix4f_m00 + ((long)column << 4) + ((long)row << 2), value);
            return m;
        }

        public double get(Matrix4d m, int column, int row) {
            return UNSAFE.getDouble(m, Matrix4d_m00 + ((long)column << 5) + ((long)row << 3));
        }

        public Matrix4d set(Matrix4d m, int column, int row, double value) {
            UNSAFE.putDouble(m, Matrix4d_m00 + ((long)column << 5) + ((long)row << 3), value);
            return m;
        }

        public float get(Matrix3f m, int column, int row) {
            return UNSAFE.getFloat(m, Matrix3f_m00 + (long)column * 12L + ((long)row << 2));
        }

        public Matrix3f set(Matrix3f m, int column, int row, float value) {
            UNSAFE.putFloat(m, Matrix3f_m00 + (long)column * 12L + ((long)row << 2), value);
            return m;
        }

        public double get(Matrix3d m, int column, int row) {
            return UNSAFE.getDouble(m, Matrix3d_m00 + (long)column * 24L + ((long)row << 3));
        }

        public Matrix3d set(Matrix3d m, int column, int row, double value) {
            UNSAFE.putDouble(m, Matrix3d_m00 + (long)column * 24L + ((long)row << 3), value);
            return m;
        }

        public void get(Matrix4x3f m, int offset, FloatBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 12);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 2));
            } else {
                super.get(m, offset, src);
            }
        }

        public void get(Matrix4x3f m, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 48);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(m, offset, src);
            }
        }

        public void get(Matrix4d m, int offset, DoubleBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 16);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 3));
            } else {
                super.get(m, offset, src);
            }
        }

        public void get(Matrix4d m, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 128);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(m, offset, src);
            }
        }

        public void get(Matrix4x3d m, int offset, DoubleBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 12);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 3));
            } else {
                super.get(m, offset, src);
            }
        }

        public void get(Matrix4x3d m, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 96);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(m, offset, src);
            }
        }

        public void getf(Matrix4d m, int offset, FloatBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 16);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.getf(m, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 2));
            } else {
                super.getf(m, offset, src);
            }
        }

        public void getf(Matrix4d m, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 64);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.getf(m, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.getf(m, offset, src);
            }
        }

        public void getf(Matrix4x3d m, int offset, FloatBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 12);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.getf(m, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 2));
            } else {
                super.getf(m, offset, src);
            }
        }

        private static void checkGet(int offset, boolean direct, int capacity, int i) {
            if (!direct) {
                MemUtilUnsafe.throwNoDirectBufferException();
            }
            if (capacity - offset < i) {
                throw new BufferUnderflowException();
            }
        }

        public void getf(Matrix4x3d m, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 48);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.getf(m, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.getf(m, offset, src);
            }
        }

        public void get(Matrix3f m, int offset, FloatBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 9);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 2));
            } else {
                super.get(m, offset, src);
            }
        }

        public void get(Matrix3f m, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 36);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(m, offset, src);
            }
        }

        public void get(Matrix3d m, int offset, DoubleBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 9);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 3));
            } else {
                super.get(m, offset, src);
            }
        }

        public void get(Matrix3d m, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 72);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(m, offset, src);
            }
        }

        public void get(Matrix3x2f m, int offset, FloatBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 6);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 2));
            } else {
                super.get(m, offset, src);
            }
        }

        public void get(Matrix3x2f m, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 24);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(m, offset, src);
            }
        }

        public void get(Matrix3x2d m, int offset, DoubleBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 6);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 3));
            } else {
                super.get(m, offset, src);
            }
        }

        public void get(Matrix3x2d m, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 48);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(m, offset, src);
            }
        }

        public void getf(Matrix3d m, int offset, FloatBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 9);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.getf(m, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 2));
            } else {
                super.getf(m, offset, src);
            }
        }

        public void getf(Matrix3d m, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 36);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.getf(m, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.getf(m, offset, src);
            }
        }

        public void get(Matrix2f m, int offset, FloatBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 4);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 2));
            } else {
                super.get(m, offset, src);
            }
        }

        public void get(Matrix2f m, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 16);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(m, offset, src);
            }
        }

        public void get(Matrix2d m, int offset, DoubleBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 4);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 3));
            } else {
                super.get(m, offset, src);
            }
        }

        public void get(Matrix2d m, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 32);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(m, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(m, offset, src);
            }
        }

        public void getf(Matrix2d m, int offset, FloatBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 4);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.getf(m, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 2));
            } else {
                super.getf(m, offset, src);
            }
        }

        public void getf(Matrix2d m, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 16);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.getf(m, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.getf(m, offset, src);
            }
        }

        public void get(Vector4d dst, int offset, DoubleBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 4);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 3));
            } else {
                super.get(dst, offset, src);
            }
        }

        public void get(Vector4d dst, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 32);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(dst, offset, src);
            }
        }

        public void get(Vector4f dst, int offset, FloatBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 4);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 2));
            } else {
                super.get(dst, offset, src);
            }
        }

        public void get(Vector4f dst, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 16);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(dst, offset, src);
            }
        }

        public void get(Vector4i dst, int offset, IntBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 4);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 2));
            } else {
                super.get(dst, offset, src);
            }
        }

        public void get(Vector4i dst, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 16);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(dst, offset, src);
            }
        }

        public void get(Vector3f dst, int offset, FloatBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 3);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 2));
            } else {
                super.get(dst, offset, src);
            }
        }

        public void get(Vector3f dst, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 12);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(dst, offset, src);
            }
        }

        public void get(Vector3d dst, int offset, DoubleBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 3);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 3));
            } else {
                super.get(dst, offset, src);
            }
        }

        public void get(Vector3d dst, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 24);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(dst, offset, src);
            }
        }

        public void get(Vector3i dst, int offset, IntBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 3);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 2));
            } else {
                super.get(dst, offset, src);
            }
        }

        public void get(Vector3i dst, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 12);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(dst, offset, src);
            }
        }

        public void get(Vector2f dst, int offset, FloatBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 2);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 2));
            } else {
                super.get(dst, offset, src);
            }
        }

        public void get(Vector2f dst, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 8);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(dst, offset, src);
            }
        }

        public void get(Vector2d dst, int offset, DoubleBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 2);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 3));
            } else {
                super.get(dst, offset, src);
            }
        }

        public void get(Vector2d dst, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 16);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(dst, offset, src);
            }
        }

        public void get(Vector2i dst, int offset, IntBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 2);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + ((long)offset << 2));
            } else {
                super.get(dst, offset, src);
            }
        }

        public void get(Vector2i dst, int offset, ByteBuffer src) {
            if (Options.DEBUG) {
                MemUtilUnsafe.checkGet(offset, src.isDirect(), src.capacity(), 8);
            }
            if (src.order() == ByteOrder.nativeOrder()) {
                MemUtilUnsafe.get(dst, UNSAFE.getLong(src, ADDRESS) + (long)offset);
            } else {
                super.get(dst, offset, src);
            }
        }

        static {
            try {
                ADDRESS = MemUtilUnsafe.findBufferAddress();
                Matrix4f_m00 = MemUtilUnsafe.checkMatrix4f();
                Matrix4d_m00 = MemUtilUnsafe.checkMatrix4d();
                Matrix4x3f_m00 = MemUtilUnsafe.checkMatrix4x3f();
                Matrix3f_m00 = MemUtilUnsafe.checkMatrix3f();
                Matrix3d_m00 = MemUtilUnsafe.checkMatrix3d();
                Matrix3x2f_m00 = MemUtilUnsafe.checkMatrix3x2f();
                Matrix2f_m00 = MemUtilUnsafe.checkMatrix2f();
                Vector4f_x = MemUtilUnsafe.checkVector4f();
                Vector4i_x = MemUtilUnsafe.checkVector4i();
                Vector3f_x = MemUtilUnsafe.checkVector3f();
                Vector3i_x = MemUtilUnsafe.checkVector3i();
                Vector2f_x = MemUtilUnsafe.checkVector2f();
                Vector2i_x = MemUtilUnsafe.checkVector2i();
                floatArrayOffset = UNSAFE.arrayBaseOffset([F.class);
                Unsafe.class.getDeclaredMethod("getLong", Object.class, Long.TYPE);
                Unsafe.class.getDeclaredMethod("putLong", Object.class, Long.TYPE, Long.TYPE);
            }
            catch (NoSuchFieldException e) {
                throw new UnsupportedOperationException(e);
            }
            catch (NoSuchMethodException e) {
                throw new UnsupportedOperationException(e);
            }
        }
    }

    public static class MemUtilNIO
    extends MemUtil {
        public void put0(Matrix4f m, FloatBuffer dest) {
            dest.put(0, m.m00()).put(1, m.m01()).put(2, m.m02()).put(3, m.m03()).put(4, m.m10()).put(5, m.m11()).put(6, m.m12()).put(7, m.m13()).put(8, m.m20()).put(9, m.m21()).put(10, m.m22()).put(11, m.m23()).put(12, m.m30()).put(13, m.m31()).put(14, m.m32()).put(15, m.m33());
        }

        public void putN(Matrix4f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, m.m02()).put(offset + 3, m.m03()).put(offset + 4, m.m10()).put(offset + 5, m.m11()).put(offset + 6, m.m12()).put(offset + 7, m.m13()).put(offset + 8, m.m20()).put(offset + 9, m.m21()).put(offset + 10, m.m22()).put(offset + 11, m.m23()).put(offset + 12, m.m30()).put(offset + 13, m.m31()).put(offset + 14, m.m32()).put(offset + 15, m.m33());
        }

        public void put(Matrix4f m, int offset, FloatBuffer dest) {
            if (offset == 0) {
                this.put0(m, dest);
            } else {
                this.putN(m, offset, dest);
            }
        }

        public void put0(Matrix4f m, ByteBuffer dest) {
            dest.putFloat(0, m.m00()).putFloat(4, m.m01()).putFloat(8, m.m02()).putFloat(12, m.m03()).putFloat(16, m.m10()).putFloat(20, m.m11()).putFloat(24, m.m12()).putFloat(28, m.m13()).putFloat(32, m.m20()).putFloat(36, m.m21()).putFloat(40, m.m22()).putFloat(44, m.m23()).putFloat(48, m.m30()).putFloat(52, m.m31()).putFloat(56, m.m32()).putFloat(60, m.m33());
        }

        private void putN(Matrix4f m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, m.m00()).putFloat(offset + 4, m.m01()).putFloat(offset + 8, m.m02()).putFloat(offset + 12, m.m03()).putFloat(offset + 16, m.m10()).putFloat(offset + 20, m.m11()).putFloat(offset + 24, m.m12()).putFloat(offset + 28, m.m13()).putFloat(offset + 32, m.m20()).putFloat(offset + 36, m.m21()).putFloat(offset + 40, m.m22()).putFloat(offset + 44, m.m23()).putFloat(offset + 48, m.m30()).putFloat(offset + 52, m.m31()).putFloat(offset + 56, m.m32()).putFloat(offset + 60, m.m33());
        }

        public void put(Matrix4f m, int offset, ByteBuffer dest) {
            if (offset == 0) {
                this.put0(m, dest);
            } else {
                this.putN(m, offset, dest);
            }
        }

        public void put4x3_0(Matrix4f m, FloatBuffer dest) {
            dest.put(0, m.m00()).put(1, m.m01()).put(2, m.m02()).put(3, m.m10()).put(4, m.m11()).put(5, m.m12()).put(6, m.m20()).put(7, m.m21()).put(8, m.m22()).put(9, m.m30()).put(10, m.m31()).put(11, m.m32());
        }

        public void put4x3_N(Matrix4f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, m.m02()).put(offset + 3, m.m10()).put(offset + 4, m.m11()).put(offset + 5, m.m12()).put(offset + 6, m.m20()).put(offset + 7, m.m21()).put(offset + 8, m.m22()).put(offset + 9, m.m30()).put(offset + 10, m.m31()).put(offset + 11, m.m32());
        }

        public void put4x3(Matrix4f m, int offset, FloatBuffer dest) {
            if (offset == 0) {
                this.put4x3_0(m, dest);
            } else {
                this.put4x3_N(m, offset, dest);
            }
        }

        public void put4x3_0(Matrix4f m, ByteBuffer dest) {
            dest.putFloat(0, m.m00()).putFloat(4, m.m01()).putFloat(8, m.m02()).putFloat(12, m.m10()).putFloat(16, m.m11()).putFloat(20, m.m12()).putFloat(24, m.m20()).putFloat(28, m.m21()).putFloat(32, m.m22()).putFloat(36, m.m30()).putFloat(40, m.m31()).putFloat(44, m.m32());
        }

        private void put4x3_N(Matrix4f m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, m.m00()).putFloat(offset + 4, m.m01()).putFloat(offset + 8, m.m02()).putFloat(offset + 12, m.m10()).putFloat(offset + 16, m.m11()).putFloat(offset + 20, m.m12()).putFloat(offset + 24, m.m20()).putFloat(offset + 28, m.m21()).putFloat(offset + 32, m.m22()).putFloat(offset + 36, m.m30()).putFloat(offset + 40, m.m31()).putFloat(offset + 44, m.m32());
        }

        public void put4x3(Matrix4f m, int offset, ByteBuffer dest) {
            if (offset == 0) {
                this.put4x3_0(m, dest);
            } else {
                this.put4x3_N(m, offset, dest);
            }
        }

        public void put3x4_0(Matrix4f m, ByteBuffer dest) {
            dest.putFloat(0, m.m00()).putFloat(4, m.m01()).putFloat(8, m.m02()).putFloat(12, m.m03()).putFloat(16, m.m10()).putFloat(20, m.m11()).putFloat(24, m.m12()).putFloat(28, m.m13()).putFloat(32, m.m20()).putFloat(36, m.m21()).putFloat(40, m.m22()).putFloat(44, m.m23());
        }

        private void put3x4_N(Matrix4f m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, m.m00()).putFloat(offset + 4, m.m01()).putFloat(offset + 8, m.m02()).putFloat(offset + 12, m.m03()).putFloat(offset + 16, m.m10()).putFloat(offset + 20, m.m11()).putFloat(offset + 24, m.m12()).putFloat(offset + 28, m.m13()).putFloat(offset + 32, m.m20()).putFloat(offset + 36, m.m21()).putFloat(offset + 40, m.m22()).putFloat(offset + 44, m.m23());
        }

        public void put3x4(Matrix4f m, int offset, ByteBuffer dest) {
            if (offset == 0) {
                this.put3x4_0(m, dest);
            } else {
                this.put3x4_N(m, offset, dest);
            }
        }

        public void put3x4_0(Matrix4f m, FloatBuffer dest) {
            dest.put(0, m.m00()).put(1, m.m01()).put(2, m.m02()).put(3, m.m03()).put(4, m.m10()).put(5, m.m11()).put(6, m.m12()).put(7, m.m13()).put(8, m.m20()).put(9, m.m21()).put(10, m.m22()).put(11, m.m23());
        }

        public void put3x4_N(Matrix4f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, m.m02()).put(offset + 3, m.m03()).put(offset + 4, m.m10()).put(offset + 5, m.m11()).put(offset + 6, m.m12()).put(offset + 7, m.m13()).put(offset + 8, m.m20()).put(offset + 9, m.m21()).put(offset + 10, m.m22()).put(offset + 11, m.m23());
        }

        public void put3x4(Matrix4f m, int offset, FloatBuffer dest) {
            if (offset == 0) {
                this.put3x4_0(m, dest);
            } else {
                this.put3x4_N(m, offset, dest);
            }
        }

        public void put3x4_0(Matrix4x3f m, ByteBuffer dest) {
            dest.putFloat(0, m.m00()).putFloat(4, m.m01()).putFloat(8, m.m02()).putFloat(12, 0.0f).putFloat(16, m.m10()).putFloat(20, m.m11()).putFloat(24, m.m12()).putFloat(28, 0.0f).putFloat(32, m.m20()).putFloat(36, m.m21()).putFloat(40, m.m22()).putFloat(44, 0.0f);
        }

        private void put3x4_N(Matrix4x3f m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, m.m00()).putFloat(offset + 4, m.m01()).putFloat(offset + 8, m.m02()).putFloat(offset + 12, 0.0f).putFloat(offset + 16, m.m10()).putFloat(offset + 20, m.m11()).putFloat(offset + 24, m.m12()).putFloat(offset + 28, 0.0f).putFloat(offset + 32, m.m20()).putFloat(offset + 36, m.m21()).putFloat(offset + 40, m.m22()).putFloat(offset + 44, 0.0f);
        }

        public void put3x4(Matrix4x3f m, int offset, ByteBuffer dest) {
            if (offset == 0) {
                this.put3x4_0(m, dest);
            } else {
                this.put3x4_N(m, offset, dest);
            }
        }

        public void put3x4_0(Matrix4x3f m, FloatBuffer dest) {
            dest.put(0, m.m00()).put(1, m.m01()).put(2, m.m02()).put(3, 0.0f).put(4, m.m10()).put(5, m.m11()).put(6, m.m12()).put(7, 0.0f).put(8, m.m20()).put(9, m.m21()).put(10, m.m22()).put(11, 0.0f);
        }

        public void put3x4_N(Matrix4x3f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, m.m02()).put(offset + 3, 0.0f).put(offset + 4, m.m10()).put(offset + 5, m.m11()).put(offset + 6, m.m12()).put(offset + 7, 0.0f).put(offset + 8, m.m20()).put(offset + 9, m.m21()).put(offset + 10, m.m22()).put(offset + 11, 0.0f);
        }

        public void put3x4(Matrix4x3f m, int offset, FloatBuffer dest) {
            if (offset == 0) {
                this.put3x4_0(m, dest);
            } else {
                this.put3x4_N(m, offset, dest);
            }
        }

        public void put0(Matrix4x3f m, FloatBuffer dest) {
            dest.put(0, m.m00()).put(1, m.m01()).put(2, m.m02()).put(3, m.m10()).put(4, m.m11()).put(5, m.m12()).put(6, m.m20()).put(7, m.m21()).put(8, m.m22()).put(9, m.m30()).put(10, m.m31()).put(11, m.m32());
        }

        public void putN(Matrix4x3f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, m.m02()).put(offset + 3, m.m10()).put(offset + 4, m.m11()).put(offset + 5, m.m12()).put(offset + 6, m.m20()).put(offset + 7, m.m21()).put(offset + 8, m.m22()).put(offset + 9, m.m30()).put(offset + 10, m.m31()).put(offset + 11, m.m32());
        }

        public void put(Matrix4x3f m, int offset, FloatBuffer dest) {
            if (offset == 0) {
                this.put0(m, dest);
            } else {
                this.putN(m, offset, dest);
            }
        }

        public void put0(Matrix4x3f m, ByteBuffer dest) {
            dest.putFloat(0, m.m00()).putFloat(4, m.m01()).putFloat(8, m.m02()).putFloat(12, m.m10()).putFloat(16, m.m11()).putFloat(20, m.m12()).putFloat(24, m.m20()).putFloat(28, m.m21()).putFloat(32, m.m22()).putFloat(36, m.m30()).putFloat(40, m.m31()).putFloat(44, m.m32());
        }

        public void putN(Matrix4x3f m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, m.m00()).putFloat(offset + 4, m.m01()).putFloat(offset + 8, m.m02()).putFloat(offset + 12, m.m10()).putFloat(offset + 16, m.m11()).putFloat(offset + 20, m.m12()).putFloat(offset + 24, m.m20()).putFloat(offset + 28, m.m21()).putFloat(offset + 32, m.m22()).putFloat(offset + 36, m.m30()).putFloat(offset + 40, m.m31()).putFloat(offset + 44, m.m32());
        }

        public void put(Matrix4x3f m, int offset, ByteBuffer dest) {
            if (offset == 0) {
                this.put0(m, dest);
            } else {
                this.putN(m, offset, dest);
            }
        }

        public void put4x4(Matrix4x3f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, m.m02()).put(offset + 3, 0.0f).put(offset + 4, m.m10()).put(offset + 5, m.m11()).put(offset + 6, m.m12()).put(offset + 7, 0.0f).put(offset + 8, m.m20()).put(offset + 9, m.m21()).put(offset + 10, m.m22()).put(offset + 11, 0.0f).put(offset + 12, m.m30()).put(offset + 13, m.m31()).put(offset + 14, m.m32()).put(offset + 15, 1.0f);
        }

        public void put4x4(Matrix4x3f m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, m.m00()).putFloat(offset + 4, m.m01()).putFloat(offset + 8, m.m02()).putFloat(offset + 12, 0.0f).putFloat(offset + 16, m.m10()).putFloat(offset + 20, m.m11()).putFloat(offset + 24, m.m12()).putFloat(offset + 28, 0.0f).putFloat(offset + 32, m.m20()).putFloat(offset + 36, m.m21()).putFloat(offset + 40, m.m22()).putFloat(offset + 44, 0.0f).putFloat(offset + 48, m.m30()).putFloat(offset + 52, m.m31()).putFloat(offset + 56, m.m32()).putFloat(offset + 60, 1.0f);
        }

        public void put4x4(Matrix4x3d m, int offset, DoubleBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, m.m02()).put(offset + 3, 0.0).put(offset + 4, m.m10()).put(offset + 5, m.m11()).put(offset + 6, m.m12()).put(offset + 7, 0.0).put(offset + 8, m.m20()).put(offset + 9, m.m21()).put(offset + 10, m.m22()).put(offset + 11, 0.0).put(offset + 12, m.m30()).put(offset + 13, m.m31()).put(offset + 14, m.m32()).put(offset + 15, 1.0);
        }

        public void put4x4(Matrix4x3d m, int offset, ByteBuffer dest) {
            dest.putDouble(offset, m.m00()).putDouble(offset + 8, m.m01()).putDouble(offset + 16, m.m02()).putDouble(offset + 24, 0.0).putDouble(offset + 32, m.m10()).putDouble(offset + 40, m.m11()).putDouble(offset + 48, m.m12()).putDouble(offset + 56, 0.0).putDouble(offset + 64, m.m20()).putDouble(offset + 72, m.m21()).putDouble(offset + 80, m.m22()).putDouble(offset + 88, 0.0).putDouble(offset + 96, m.m30()).putDouble(offset + 104, m.m31()).putDouble(offset + 112, m.m32()).putDouble(offset + 120, 1.0);
        }

        public void put4x4(Matrix3x2f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, 0.0f).put(offset + 3, 0.0f).put(offset + 4, m.m10()).put(offset + 5, m.m11()).put(offset + 6, 0.0f).put(offset + 7, 0.0f).put(offset + 8, 0.0f).put(offset + 9, 0.0f).put(offset + 10, 1.0f).put(offset + 11, 0.0f).put(offset + 12, m.m20()).put(offset + 13, m.m21()).put(offset + 14, 0.0f).put(offset + 15, 1.0f);
        }

        public void put4x4(Matrix3x2f m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, m.m00()).putFloat(offset + 4, m.m01()).putFloat(offset + 8, 0.0f).putFloat(offset + 12, 0.0f).putFloat(offset + 16, m.m10()).putFloat(offset + 20, m.m11()).putFloat(offset + 24, 0.0f).putFloat(offset + 28, 0.0f).putFloat(offset + 32, 0.0f).putFloat(offset + 36, 0.0f).putFloat(offset + 40, 1.0f).putFloat(offset + 44, 0.0f).putFloat(offset + 48, m.m20()).putFloat(offset + 52, m.m21()).putFloat(offset + 56, 0.0f).putFloat(offset + 60, 1.0f);
        }

        public void put4x4(Matrix3x2d m, int offset, DoubleBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, 0.0).put(offset + 3, 0.0).put(offset + 4, m.m10()).put(offset + 5, m.m11()).put(offset + 6, 0.0).put(offset + 7, 0.0).put(offset + 8, 0.0).put(offset + 9, 0.0).put(offset + 10, 1.0).put(offset + 11, 0.0).put(offset + 12, m.m20()).put(offset + 13, m.m21()).put(offset + 14, 0.0).put(offset + 15, 1.0);
        }

        public void put4x4(Matrix3x2d m, int offset, ByteBuffer dest) {
            dest.putDouble(offset, m.m00()).putDouble(offset + 8, m.m01()).putDouble(offset + 16, 0.0).putDouble(offset + 24, 0.0).putDouble(offset + 32, m.m10()).putDouble(offset + 40, m.m11()).putDouble(offset + 48, 0.0).putDouble(offset + 56, 0.0).putDouble(offset + 64, 0.0).putDouble(offset + 72, 0.0).putDouble(offset + 80, 1.0).putDouble(offset + 88, 0.0).putDouble(offset + 96, m.m20()).putDouble(offset + 104, m.m21()).putDouble(offset + 112, 0.0).putDouble(offset + 120, 1.0);
        }

        public void put3x3(Matrix3x2f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, 0.0f).put(offset + 3, m.m10()).put(offset + 4, m.m11()).put(offset + 5, 0.0f).put(offset + 6, m.m20()).put(offset + 7, m.m21()).put(offset + 8, 1.0f);
        }

        public void put3x3(Matrix3x2f m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, m.m00()).putFloat(offset + 4, m.m01()).putFloat(offset + 8, 0.0f).putFloat(offset + 12, m.m10()).putFloat(offset + 16, m.m11()).putFloat(offset + 20, 0.0f).putFloat(offset + 24, m.m20()).putFloat(offset + 28, m.m21()).putFloat(offset + 32, 1.0f);
        }

        public void put3x3(Matrix3x2d m, int offset, DoubleBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, 0.0).put(offset + 3, m.m10()).put(offset + 4, m.m11()).put(offset + 5, 0.0).put(offset + 6, m.m20()).put(offset + 7, m.m21()).put(offset + 8, 1.0);
        }

        public void put3x3(Matrix3x2d m, int offset, ByteBuffer dest) {
            dest.putDouble(offset, m.m00()).putDouble(offset + 8, m.m01()).putDouble(offset + 16, 0.0).putDouble(offset + 24, m.m10()).putDouble(offset + 32, m.m11()).putDouble(offset + 40, 0.0).putDouble(offset + 48, m.m20()).putDouble(offset + 56, m.m21()).putDouble(offset + 64, 1.0);
        }

        private void putTransposedN(Matrix4f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m10()).put(offset + 2, m.m20()).put(offset + 3, m.m30()).put(offset + 4, m.m01()).put(offset + 5, m.m11()).put(offset + 6, m.m21()).put(offset + 7, m.m31()).put(offset + 8, m.m02()).put(offset + 9, m.m12()).put(offset + 10, m.m22()).put(offset + 11, m.m32()).put(offset + 12, m.m03()).put(offset + 13, m.m13()).put(offset + 14, m.m23()).put(offset + 15, m.m33());
        }

        private void putTransposed0(Matrix4f m, FloatBuffer dest) {
            dest.put(0, m.m00()).put(1, m.m10()).put(2, m.m20()).put(3, m.m30()).put(4, m.m01()).put(5, m.m11()).put(6, m.m21()).put(7, m.m31()).put(8, m.m02()).put(9, m.m12()).put(10, m.m22()).put(11, m.m32()).put(12, m.m03()).put(13, m.m13()).put(14, m.m23()).put(15, m.m33());
        }

        public void putTransposed(Matrix4f m, int offset, FloatBuffer dest) {
            if (offset == 0) {
                this.putTransposed0(m, dest);
            } else {
                this.putTransposedN(m, offset, dest);
            }
        }

        private void putTransposedN(Matrix4f m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, m.m00()).putFloat(offset + 4, m.m10()).putFloat(offset + 8, m.m20()).putFloat(offset + 12, m.m30()).putFloat(offset + 16, m.m01()).putFloat(offset + 20, m.m11()).putFloat(offset + 24, m.m21()).putFloat(offset + 28, m.m31()).putFloat(offset + 32, m.m02()).putFloat(offset + 36, m.m12()).putFloat(offset + 40, m.m22()).putFloat(offset + 44, m.m32()).putFloat(offset + 48, m.m03()).putFloat(offset + 52, m.m13()).putFloat(offset + 56, m.m23()).putFloat(offset + 60, m.m33());
        }

        private void putTransposed0(Matrix4f m, ByteBuffer dest) {
            dest.putFloat(0, m.m00()).putFloat(4, m.m10()).putFloat(8, m.m20()).putFloat(12, m.m30()).putFloat(16, m.m01()).putFloat(20, m.m11()).putFloat(24, m.m21()).putFloat(28, m.m31()).putFloat(32, m.m02()).putFloat(36, m.m12()).putFloat(40, m.m22()).putFloat(44, m.m32()).putFloat(48, m.m03()).putFloat(52, m.m13()).putFloat(56, m.m23()).putFloat(60, m.m33());
        }

        public void putTransposed(Matrix4f m, int offset, ByteBuffer dest) {
            if (offset == 0) {
                this.putTransposed0(m, dest);
            } else {
                this.putTransposedN(m, offset, dest);
            }
        }

        public void put4x3Transposed(Matrix4f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m10()).put(offset + 2, m.m20()).put(offset + 3, m.m30()).put(offset + 4, m.m01()).put(offset + 5, m.m11()).put(offset + 6, m.m21()).put(offset + 7, m.m31()).put(offset + 8, m.m02()).put(offset + 9, m.m12()).put(offset + 10, m.m22()).put(offset + 11, m.m32());
        }

        public void put4x3Transposed(Matrix4f m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, m.m00()).putFloat(offset + 4, m.m10()).putFloat(offset + 8, m.m20()).putFloat(offset + 12, m.m30()).putFloat(offset + 16, m.m01()).putFloat(offset + 20, m.m11()).putFloat(offset + 24, m.m21()).putFloat(offset + 28, m.m31()).putFloat(offset + 32, m.m02()).putFloat(offset + 36, m.m12()).putFloat(offset + 40, m.m22()).putFloat(offset + 44, m.m32());
        }

        public void putTransposed(Matrix4x3f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m10()).put(offset + 2, m.m20()).put(offset + 3, m.m30()).put(offset + 4, m.m01()).put(offset + 5, m.m11()).put(offset + 6, m.m21()).put(offset + 7, m.m31()).put(offset + 8, m.m02()).put(offset + 9, m.m12()).put(offset + 10, m.m22()).put(offset + 11, m.m32());
        }

        public void putTransposed(Matrix4x3f m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, m.m00()).putFloat(offset + 4, m.m10()).putFloat(offset + 8, m.m20()).putFloat(offset + 12, m.m30()).putFloat(offset + 16, m.m01()).putFloat(offset + 20, m.m11()).putFloat(offset + 24, m.m21()).putFloat(offset + 28, m.m31()).putFloat(offset + 32, m.m02()).putFloat(offset + 36, m.m12()).putFloat(offset + 40, m.m22()).putFloat(offset + 44, m.m32());
        }

        public void putTransposed(Matrix3f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m10()).put(offset + 2, m.m20()).put(offset + 3, m.m01()).put(offset + 4, m.m11()).put(offset + 5, m.m21()).put(offset + 6, m.m02()).put(offset + 7, m.m12()).put(offset + 8, m.m22());
        }

        public void putTransposed(Matrix3f m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, m.m00()).putFloat(offset + 4, m.m10()).putFloat(offset + 8, m.m20()).putFloat(offset + 12, m.m01()).putFloat(offset + 16, m.m11()).putFloat(offset + 20, m.m21()).putFloat(offset + 24, m.m02()).putFloat(offset + 28, m.m12()).putFloat(offset + 32, m.m22());
        }

        public void putTransposed(Matrix2f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m10()).put(offset + 2, m.m01()).put(offset + 3, m.m11());
        }

        public void putTransposed(Matrix3x2f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m10()).put(offset + 2, m.m20()).put(offset + 3, m.m01()).put(offset + 4, m.m11()).put(offset + 5, m.m21());
        }

        public void putTransposed(Matrix2f m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, m.m00()).putFloat(offset + 4, m.m10()).putFloat(offset + 8, m.m01()).putFloat(offset + 12, m.m11());
        }

        public void put(Matrix4d m, int offset, DoubleBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, m.m02()).put(offset + 3, m.m03()).put(offset + 4, m.m10()).put(offset + 5, m.m11()).put(offset + 6, m.m12()).put(offset + 7, m.m13()).put(offset + 8, m.m20()).put(offset + 9, m.m21()).put(offset + 10, m.m22()).put(offset + 11, m.m23()).put(offset + 12, m.m30()).put(offset + 13, m.m31()).put(offset + 14, m.m32()).put(offset + 15, m.m33());
        }

        public void put(Matrix4d m, int offset, ByteBuffer dest) {
            dest.putDouble(offset, m.m00()).putDouble(offset + 8, m.m01()).putDouble(offset + 16, m.m02()).putDouble(offset + 24, m.m03()).putDouble(offset + 32, m.m10()).putDouble(offset + 40, m.m11()).putDouble(offset + 48, m.m12()).putDouble(offset + 56, m.m13()).putDouble(offset + 64, m.m20()).putDouble(offset + 72, m.m21()).putDouble(offset + 80, m.m22()).putDouble(offset + 88, m.m23()).putDouble(offset + 96, m.m30()).putDouble(offset + 104, m.m31()).putDouble(offset + 112, m.m32()).putDouble(offset + 120, m.m33());
        }

        public void put(Matrix4x3d m, int offset, DoubleBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, m.m02()).put(offset + 3, m.m10()).put(offset + 4, m.m11()).put(offset + 5, m.m12()).put(offset + 6, m.m20()).put(offset + 7, m.m21()).put(offset + 8, m.m22()).put(offset + 9, m.m30()).put(offset + 10, m.m31()).put(offset + 11, m.m32());
        }

        public void put(Matrix4x3d m, int offset, ByteBuffer dest) {
            dest.putDouble(offset, m.m00()).putDouble(offset + 8, m.m01()).putDouble(offset + 16, m.m02()).putDouble(offset + 24, m.m10()).putDouble(offset + 32, m.m11()).putDouble(offset + 40, m.m12()).putDouble(offset + 48, m.m20()).putDouble(offset + 56, m.m21()).putDouble(offset + 64, m.m22()).putDouble(offset + 72, m.m30()).putDouble(offset + 80, m.m31()).putDouble(offset + 88, m.m32());
        }

        public void putf(Matrix4d m, int offset, FloatBuffer dest) {
            dest.put(offset, (float)m.m00()).put(offset + 1, (float)m.m01()).put(offset + 2, (float)m.m02()).put(offset + 3, (float)m.m03()).put(offset + 4, (float)m.m10()).put(offset + 5, (float)m.m11()).put(offset + 6, (float)m.m12()).put(offset + 7, (float)m.m13()).put(offset + 8, (float)m.m20()).put(offset + 9, (float)m.m21()).put(offset + 10, (float)m.m22()).put(offset + 11, (float)m.m23()).put(offset + 12, (float)m.m30()).put(offset + 13, (float)m.m31()).put(offset + 14, (float)m.m32()).put(offset + 15, (float)m.m33());
        }

        public void putf(Matrix4d m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, (float)m.m00()).putFloat(offset + 4, (float)m.m01()).putFloat(offset + 8, (float)m.m02()).putFloat(offset + 12, (float)m.m03()).putFloat(offset + 16, (float)m.m10()).putFloat(offset + 20, (float)m.m11()).putFloat(offset + 24, (float)m.m12()).putFloat(offset + 28, (float)m.m13()).putFloat(offset + 32, (float)m.m20()).putFloat(offset + 36, (float)m.m21()).putFloat(offset + 40, (float)m.m22()).putFloat(offset + 44, (float)m.m23()).putFloat(offset + 48, (float)m.m30()).putFloat(offset + 52, (float)m.m31()).putFloat(offset + 56, (float)m.m32()).putFloat(offset + 60, (float)m.m33());
        }

        public void putf(Matrix4x3d m, int offset, FloatBuffer dest) {
            dest.put(offset, (float)m.m00()).put(offset + 1, (float)m.m01()).put(offset + 2, (float)m.m02()).put(offset + 3, (float)m.m10()).put(offset + 4, (float)m.m11()).put(offset + 5, (float)m.m12()).put(offset + 6, (float)m.m20()).put(offset + 7, (float)m.m21()).put(offset + 8, (float)m.m22()).put(offset + 9, (float)m.m30()).put(offset + 10, (float)m.m31()).put(offset + 11, (float)m.m32());
        }

        public void putf(Matrix4x3d m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, (float)m.m00()).putFloat(offset + 4, (float)m.m01()).putFloat(offset + 8, (float)m.m02()).putFloat(offset + 12, (float)m.m10()).putFloat(offset + 16, (float)m.m11()).putFloat(offset + 20, (float)m.m12()).putFloat(offset + 24, (float)m.m20()).putFloat(offset + 28, (float)m.m21()).putFloat(offset + 32, (float)m.m22()).putFloat(offset + 36, (float)m.m30()).putFloat(offset + 40, (float)m.m31()).putFloat(offset + 44, (float)m.m32());
        }

        public void putTransposed(Matrix4d m, int offset, DoubleBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m10()).put(offset + 2, m.m20()).put(offset + 3, m.m30()).put(offset + 4, m.m01()).put(offset + 5, m.m11()).put(offset + 6, m.m21()).put(offset + 7, m.m31()).put(offset + 8, m.m02()).put(offset + 9, m.m12()).put(offset + 10, m.m22()).put(offset + 11, m.m32()).put(offset + 12, m.m03()).put(offset + 13, m.m13()).put(offset + 14, m.m23()).put(offset + 15, m.m33());
        }

        public void putTransposed(Matrix4d m, int offset, ByteBuffer dest) {
            dest.putDouble(offset, m.m00()).putDouble(offset + 8, m.m10()).putDouble(offset + 16, m.m20()).putDouble(offset + 24, m.m30()).putDouble(offset + 32, m.m01()).putDouble(offset + 40, m.m11()).putDouble(offset + 48, m.m21()).putDouble(offset + 56, m.m31()).putDouble(offset + 64, m.m02()).putDouble(offset + 72, m.m12()).putDouble(offset + 80, m.m22()).putDouble(offset + 88, m.m32()).putDouble(offset + 96, m.m03()).putDouble(offset + 104, m.m13()).putDouble(offset + 112, m.m23()).putDouble(offset + 120, m.m33());
        }

        public void put4x3Transposed(Matrix4d m, int offset, DoubleBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m10()).put(offset + 2, m.m20()).put(offset + 3, m.m30()).put(offset + 4, m.m01()).put(offset + 5, m.m11()).put(offset + 6, m.m21()).put(offset + 7, m.m31()).put(offset + 8, m.m02()).put(offset + 9, m.m12()).put(offset + 10, m.m22()).put(offset + 11, m.m32());
        }

        public void put4x3Transposed(Matrix4d m, int offset, ByteBuffer dest) {
            dest.putDouble(offset, m.m00()).putDouble(offset + 8, m.m10()).putDouble(offset + 16, m.m20()).putDouble(offset + 24, m.m30()).putDouble(offset + 32, m.m01()).putDouble(offset + 40, m.m11()).putDouble(offset + 48, m.m21()).putDouble(offset + 56, m.m31()).putDouble(offset + 64, m.m02()).putDouble(offset + 72, m.m12()).putDouble(offset + 80, m.m22()).putDouble(offset + 88, m.m32());
        }

        public void putTransposed(Matrix4x3d m, int offset, DoubleBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m10()).put(offset + 2, m.m20()).put(offset + 3, m.m30()).put(offset + 4, m.m01()).put(offset + 5, m.m11()).put(offset + 6, m.m21()).put(offset + 7, m.m31()).put(offset + 8, m.m02()).put(offset + 9, m.m12()).put(offset + 10, m.m22()).put(offset + 11, m.m32());
        }

        public void putTransposed(Matrix4x3d m, int offset, ByteBuffer dest) {
            dest.putDouble(offset, m.m00()).putDouble(offset + 8, m.m10()).putDouble(offset + 16, m.m20()).putDouble(offset + 24, m.m30()).putDouble(offset + 32, m.m01()).putDouble(offset + 40, m.m11()).putDouble(offset + 48, m.m21()).putDouble(offset + 56, m.m31()).putDouble(offset + 64, m.m02()).putDouble(offset + 72, m.m12()).putDouble(offset + 80, m.m22()).putDouble(offset + 88, m.m32());
        }

        public void putTransposed(Matrix3d m, int offset, DoubleBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m10()).put(offset + 2, m.m20()).put(offset + 3, m.m01()).put(offset + 4, m.m11()).put(offset + 5, m.m21()).put(offset + 6, m.m02()).put(offset + 7, m.m12()).put(offset + 8, m.m22());
        }

        public void putTransposed(Matrix3d m, int offset, ByteBuffer dest) {
            dest.putDouble(offset, m.m00()).putDouble(offset + 8, m.m10()).putDouble(offset + 16, m.m20()).putDouble(offset + 24, m.m01()).putDouble(offset + 32, m.m11()).putDouble(offset + 40, m.m21()).putDouble(offset + 48, m.m02()).putDouble(offset + 56, m.m12()).putDouble(offset + 64, m.m22());
        }

        public void putTransposed(Matrix3x2d m, int offset, ByteBuffer dest) {
            dest.putDouble(offset, m.m00()).putDouble(offset + 8, m.m10()).putDouble(offset + 16, m.m20()).putDouble(offset + 24, m.m01()).putDouble(offset + 32, m.m11()).putDouble(offset + 40, m.m21());
        }

        public void putTransposed(Matrix3x2d m, int offset, DoubleBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m10()).put(offset + 2, m.m20()).put(offset + 3, m.m01()).put(offset + 4, m.m11()).put(offset + 5, m.m21());
        }

        public void putTransposed(Matrix2d m, int offset, DoubleBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m10()).put(offset + 2, m.m01()).put(offset + 3, m.m11());
        }

        public void putTransposed(Matrix2d m, int offset, ByteBuffer dest) {
            dest.putDouble(offset, m.m00()).putDouble(offset + 8, m.m10()).putDouble(offset + 16, m.m01()).putDouble(offset + 24, m.m11());
        }

        public void putfTransposed(Matrix4x3d m, int offset, FloatBuffer dest) {
            dest.put(offset, (float)m.m00()).put(offset + 1, (float)m.m10()).put(offset + 2, (float)m.m20()).put(offset + 3, (float)m.m30()).put(offset + 4, (float)m.m01()).put(offset + 5, (float)m.m11()).put(offset + 6, (float)m.m21()).put(offset + 7, (float)m.m31()).put(offset + 8, (float)m.m02()).put(offset + 9, (float)m.m12()).put(offset + 10, (float)m.m22()).put(offset + 11, (float)m.m32());
        }

        public void putfTransposed(Matrix4x3d m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, (float)m.m00()).putFloat(offset + 4, (float)m.m10()).putFloat(offset + 8, (float)m.m20()).putFloat(offset + 12, (float)m.m30()).putFloat(offset + 16, (float)m.m01()).putFloat(offset + 20, (float)m.m11()).putFloat(offset + 24, (float)m.m21()).putFloat(offset + 28, (float)m.m31()).putFloat(offset + 32, (float)m.m02()).putFloat(offset + 36, (float)m.m12()).putFloat(offset + 40, (float)m.m22()).putFloat(offset + 44, (float)m.m32());
        }

        public void putfTransposed(Matrix3d m, int offset, FloatBuffer dest) {
            dest.put(offset, (float)m.m00()).put(offset + 1, (float)m.m10()).put(offset + 2, (float)m.m20()).put(offset + 3, (float)m.m01()).put(offset + 4, (float)m.m11()).put(offset + 5, (float)m.m21()).put(offset + 6, (float)m.m02()).put(offset + 7, (float)m.m12()).put(offset + 8, (float)m.m22());
        }

        public void putfTransposed(Matrix3d m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, (float)m.m00()).putFloat(offset + 4, (float)m.m10()).putFloat(offset + 8, (float)m.m20()).putFloat(offset + 12, (float)m.m01()).putFloat(offset + 16, (float)m.m11()).putFloat(offset + 20, (float)m.m21()).putFloat(offset + 24, (float)m.m02()).putFloat(offset + 28, (float)m.m12()).putFloat(offset + 32, (float)m.m22());
        }

        public void putfTransposed(Matrix3x2d m, int offset, FloatBuffer dest) {
            dest.put(offset, (float)m.m00()).put(offset + 1, (float)m.m10()).put(offset + 2, (float)m.m20()).put(offset + 3, (float)m.m01()).put(offset + 4, (float)m.m11()).put(offset + 5, (float)m.m21());
        }

        public void putfTransposed(Matrix3x2d m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, (float)m.m00()).putFloat(offset + 4, (float)m.m10()).putFloat(offset + 8, (float)m.m20()).putFloat(offset + 12, (float)m.m01()).putFloat(offset + 16, (float)m.m11()).putFloat(offset + 20, (float)m.m21());
        }

        public void putfTransposed(Matrix2d m, int offset, FloatBuffer dest) {
            dest.put(offset, (float)m.m00()).put(offset + 1, (float)m.m10()).put(offset + 2, (float)m.m01()).put(offset + 3, (float)m.m11());
        }

        public void putfTransposed(Matrix2d m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, (float)m.m00()).putFloat(offset + 4, (float)m.m10()).putFloat(offset + 8, (float)m.m01()).putFloat(offset + 12, (float)m.m11());
        }

        public void putfTransposed(Matrix4d m, int offset, FloatBuffer dest) {
            dest.put(offset, (float)m.m00()).put(offset + 1, (float)m.m10()).put(offset + 2, (float)m.m20()).put(offset + 3, (float)m.m30()).put(offset + 4, (float)m.m01()).put(offset + 5, (float)m.m11()).put(offset + 6, (float)m.m21()).put(offset + 7, (float)m.m31()).put(offset + 8, (float)m.m02()).put(offset + 9, (float)m.m12()).put(offset + 10, (float)m.m22()).put(offset + 11, (float)m.m32()).put(offset + 12, (float)m.m03()).put(offset + 13, (float)m.m13()).put(offset + 14, (float)m.m23()).put(offset + 15, (float)m.m33());
        }

        public void putfTransposed(Matrix4d m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, (float)m.m00()).putFloat(offset + 4, (float)m.m10()).putFloat(offset + 8, (float)m.m20()).putFloat(offset + 12, (float)m.m30()).putFloat(offset + 16, (float)m.m01()).putFloat(offset + 20, (float)m.m11()).putFloat(offset + 24, (float)m.m21()).putFloat(offset + 28, (float)m.m31()).putFloat(offset + 32, (float)m.m02()).putFloat(offset + 36, (float)m.m12()).putFloat(offset + 40, (float)m.m22()).putFloat(offset + 44, (float)m.m32()).putFloat(offset + 48, (float)m.m03()).putFloat(offset + 52, (float)m.m13()).putFloat(offset + 56, (float)m.m23()).putFloat(offset + 60, (float)m.m33());
        }

        public void put0(Matrix3f m, FloatBuffer dest) {
            dest.put(0, m.m00()).put(1, m.m01()).put(2, m.m02()).put(3, m.m10()).put(4, m.m11()).put(5, m.m12()).put(6, m.m20()).put(7, m.m21()).put(8, m.m22());
        }

        public void putN(Matrix3f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, m.m02()).put(offset + 3, m.m10()).put(offset + 4, m.m11()).put(offset + 5, m.m12()).put(offset + 6, m.m20()).put(offset + 7, m.m21()).put(offset + 8, m.m22());
        }

        public void put(Matrix3f m, int offset, FloatBuffer dest) {
            if (offset == 0) {
                this.put0(m, dest);
            } else {
                this.putN(m, offset, dest);
            }
        }

        public void put0(Matrix3f m, ByteBuffer dest) {
            dest.putFloat(0, m.m00()).putFloat(4, m.m01()).putFloat(8, m.m02()).putFloat(12, m.m10()).putFloat(16, m.m11()).putFloat(20, m.m12()).putFloat(24, m.m20()).putFloat(28, m.m21()).putFloat(32, m.m22());
        }

        public void putN(Matrix3f m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, m.m00()).putFloat(offset + 4, m.m01()).putFloat(offset + 8, m.m02()).putFloat(offset + 12, m.m10()).putFloat(offset + 16, m.m11()).putFloat(offset + 20, m.m12()).putFloat(offset + 24, m.m20()).putFloat(offset + 28, m.m21()).putFloat(offset + 32, m.m22());
        }

        public void put(Matrix3f m, int offset, ByteBuffer dest) {
            if (offset == 0) {
                this.put0(m, dest);
            } else {
                this.putN(m, offset, dest);
            }
        }

        public void put3x4_0(Matrix3f m, ByteBuffer dest) {
            dest.putFloat(0, m.m00()).putFloat(4, m.m01()).putFloat(8, m.m02()).putFloat(12, 0.0f).putFloat(16, m.m10()).putFloat(20, m.m11()).putFloat(24, m.m12()).putFloat(28, 0.0f).putFloat(32, m.m20()).putFloat(36, m.m21()).putFloat(40, m.m22()).putFloat(44, 0.0f);
        }

        private void put3x4_N(Matrix3f m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, m.m00()).putFloat(offset + 4, m.m01()).putFloat(offset + 8, m.m02()).putFloat(offset + 12, 0.0f).putFloat(offset + 16, m.m10()).putFloat(offset + 20, m.m11()).putFloat(offset + 24, m.m12()).putFloat(offset + 28, 0.0f).putFloat(offset + 32, m.m20()).putFloat(offset + 36, m.m21()).putFloat(offset + 40, m.m22()).putFloat(offset + 44, 0.0f);
        }

        public void put3x4(Matrix3f m, int offset, ByteBuffer dest) {
            if (offset == 0) {
                this.put3x4_0(m, dest);
            } else {
                this.put3x4_N(m, offset, dest);
            }
        }

        public void put3x4_0(Matrix3f m, FloatBuffer dest) {
            dest.put(0, m.m00()).put(1, m.m01()).put(2, m.m02()).put(3, 0.0f).put(4, m.m10()).put(5, m.m11()).put(6, m.m12()).put(7, 0.0f).put(8, m.m20()).put(9, m.m21()).put(10, m.m22()).put(11, 0.0f);
        }

        public void put3x4_N(Matrix3f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, m.m02()).put(offset + 3, 0.0f).put(offset + 4, m.m10()).put(offset + 5, m.m11()).put(offset + 6, m.m12()).put(offset + 7, 0.0f).put(offset + 8, m.m20()).put(offset + 9, m.m21()).put(offset + 10, m.m22()).put(offset + 11, 0.0f);
        }

        public void put3x4(Matrix3f m, int offset, FloatBuffer dest) {
            if (offset == 0) {
                this.put3x4_0(m, dest);
            } else {
                this.put3x4_N(m, offset, dest);
            }
        }

        public void put(Matrix3d m, int offset, DoubleBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, m.m02()).put(offset + 3, m.m10()).put(offset + 4, m.m11()).put(offset + 5, m.m12()).put(offset + 6, m.m20()).put(offset + 7, m.m21()).put(offset + 8, m.m22());
        }

        public void put(Matrix3d m, int offset, ByteBuffer dest) {
            dest.putDouble(offset, m.m00()).putDouble(offset + 8, m.m01()).putDouble(offset + 16, m.m02()).putDouble(offset + 24, m.m10()).putDouble(offset + 32, m.m11()).putDouble(offset + 40, m.m12()).putDouble(offset + 48, m.m20()).putDouble(offset + 56, m.m21()).putDouble(offset + 64, m.m22());
        }

        public void put(Matrix3x2f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, m.m10()).put(offset + 3, m.m11()).put(offset + 4, m.m20()).put(offset + 5, m.m21());
        }

        public void put(Matrix3x2f m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, m.m00()).putFloat(offset + 4, m.m01()).putFloat(offset + 8, m.m10()).putFloat(offset + 12, m.m11()).putFloat(offset + 16, m.m20()).putFloat(offset + 20, m.m21());
        }

        public void put(Matrix3x2d m, int offset, DoubleBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, m.m10()).put(offset + 3, m.m11()).put(offset + 4, m.m20()).put(offset + 5, m.m21());
        }

        public void put(Matrix3x2d m, int offset, ByteBuffer dest) {
            dest.putDouble(offset, m.m00()).putDouble(offset + 8, m.m01()).putDouble(offset + 16, m.m10()).putDouble(offset + 24, m.m11()).putDouble(offset + 32, m.m20()).putDouble(offset + 40, m.m21());
        }

        public void putf(Matrix3d m, int offset, FloatBuffer dest) {
            dest.put(offset, (float)m.m00()).put(offset + 1, (float)m.m01()).put(offset + 2, (float)m.m02()).put(offset + 3, (float)m.m10()).put(offset + 4, (float)m.m11()).put(offset + 5, (float)m.m12()).put(offset + 6, (float)m.m20()).put(offset + 7, (float)m.m21()).put(offset + 8, (float)m.m22());
        }

        public void put(Matrix2f m, int offset, FloatBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, m.m10()).put(offset + 3, m.m11());
        }

        public void put(Matrix2f m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, m.m00()).putFloat(offset + 4, m.m01()).putFloat(offset + 8, m.m10()).putFloat(offset + 12, m.m11());
        }

        public void put(Matrix2d m, int offset, DoubleBuffer dest) {
            dest.put(offset, m.m00()).put(offset + 1, m.m01()).put(offset + 2, m.m10()).put(offset + 3, m.m11());
        }

        public void put(Matrix2d m, int offset, ByteBuffer dest) {
            dest.putDouble(offset, m.m00()).putDouble(offset + 8, m.m01()).putDouble(offset + 16, m.m10()).putDouble(offset + 24, m.m11());
        }

        public void putf(Matrix2d m, int offset, FloatBuffer dest) {
            dest.put(offset, (float)m.m00()).put(offset + 1, (float)m.m01()).put(offset + 2, (float)m.m10()).put(offset + 3, (float)m.m11());
        }

        public void putf(Matrix2d m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, (float)m.m00()).putFloat(offset + 4, (float)m.m01()).putFloat(offset + 8, (float)m.m10()).putFloat(offset + 12, (float)m.m11());
        }

        public void putf(Matrix3d m, int offset, ByteBuffer dest) {
            dest.putFloat(offset, (float)m.m00()).putFloat(offset + 4, (float)m.m01()).putFloat(offset + 8, (float)m.m02()).putFloat(offset + 12, (float)m.m10()).putFloat(offset + 16, (float)m.m11()).putFloat(offset + 20, (float)m.m12()).putFloat(offset + 24, (float)m.m20()).putFloat(offset + 28, (float)m.m21()).putFloat(offset + 32, (float)m.m22());
        }

        public void put(Vector4d src, int offset, DoubleBuffer dest) {
            dest.put(offset, src.x).put(offset + 1, src.y).put(offset + 2, src.z).put(offset + 3, src.w);
        }

        public void put(Vector4d src, int offset, FloatBuffer dest) {
            dest.put(offset, (float)src.x).put(offset + 1, (float)src.y).put(offset + 2, (float)src.z).put(offset + 3, (float)src.w);
        }

        public void put(Vector4d src, int offset, ByteBuffer dest) {
            dest.putDouble(offset, src.x).putDouble(offset + 8, src.y).putDouble(offset + 16, src.z).putDouble(offset + 24, src.w);
        }

        public void putf(Vector4d src, int offset, ByteBuffer dest) {
            dest.putFloat(offset, (float)src.x).putFloat(offset + 4, (float)src.y).putFloat(offset + 8, (float)src.z).putFloat(offset + 12, (float)src.w);
        }

        public void put(Vector4f src, int offset, FloatBuffer dest) {
            dest.put(offset, src.x).put(offset + 1, src.y).put(offset + 2, src.z).put(offset + 3, src.w);
        }

        public void put(Vector4f src, int offset, ByteBuffer dest) {
            dest.putFloat(offset, src.x).putFloat(offset + 4, src.y).putFloat(offset + 8, src.z).putFloat(offset + 12, src.w);
        }

        public void put(Vector4L src, int offset, LongBuffer dest) {
            dest.put(offset, src.x).put(offset + 1, src.y).put(offset + 2, src.z).put(offset + 3, src.w);
        }

        public void put(Vector4L src, int offset, ByteBuffer dest) {
            dest.putLong(offset, src.x).putLong(offset + 8, src.y).putLong(offset + 16, src.z).putLong(offset + 24, src.w);
        }

        public void put(Vector4i src, int offset, IntBuffer dest) {
            dest.put(offset, src.x).put(offset + 1, src.y).put(offset + 2, src.z).put(offset + 3, src.w);
        }

        public void put(Vector4i src, int offset, ByteBuffer dest) {
            dest.putInt(offset, src.x).putInt(offset + 4, src.y).putInt(offset + 8, src.z).putInt(offset + 12, src.w);
        }

        public void put(Vector3f src, int offset, FloatBuffer dest) {
            dest.put(offset, src.x).put(offset + 1, src.y).put(offset + 2, src.z);
        }

        public void put(Vector3f src, int offset, ByteBuffer dest) {
            dest.putFloat(offset, src.x).putFloat(offset + 4, src.y).putFloat(offset + 8, src.z);
        }

        public void put(Vector3d src, int offset, DoubleBuffer dest) {
            dest.put(offset, src.x).put(offset + 1, src.y).put(offset + 2, src.z);
        }

        public void put(Vector3d src, int offset, FloatBuffer dest) {
            dest.put(offset, (float)src.x).put(offset + 1, (float)src.y).put(offset + 2, (float)src.z);
        }

        public void put(Vector3d src, int offset, ByteBuffer dest) {
            dest.putDouble(offset, src.x).putDouble(offset + 8, src.y).putDouble(offset + 16, src.z);
        }

        public void putf(Vector3d src, int offset, ByteBuffer dest) {
            dest.putFloat(offset, (float)src.x).putFloat(offset + 4, (float)src.y).putFloat(offset + 8, (float)src.z);
        }

        public void put(Vector3i src, int offset, IntBuffer dest) {
            dest.put(offset, src.x).put(offset + 1, src.y).put(offset + 2, src.z);
        }

        public void put(Vector3i src, int offset, ByteBuffer dest) {
            dest.putInt(offset, src.x).putInt(offset + 4, src.y).putInt(offset + 8, src.z);
        }

        public void put(Vector3L src, int offset, LongBuffer dest) {
            dest.put(offset, src.x).put(offset + 1, src.y).put(offset + 2, src.z);
        }

        public void put(Vector3L src, int offset, ByteBuffer dest) {
            dest.putLong(offset, src.x).putLong(offset + 8, src.y).putLong(offset + 16, src.z);
        }

        public void put(Vector2f src, int offset, FloatBuffer dest) {
            dest.put(offset, src.x).put(offset + 1, src.y);
        }

        public void put(Vector2f src, int offset, ByteBuffer dest) {
            dest.putFloat(offset, src.x).putFloat(offset + 4, src.y);
        }

        public void put(Vector2d src, int offset, DoubleBuffer dest) {
            dest.put(offset, src.x).put(offset + 1, src.y);
        }

        public void put(Vector2d src, int offset, ByteBuffer dest) {
            dest.putDouble(offset, src.x).putDouble(offset + 8, src.y);
        }

        public void put(Vector2i src, int offset, IntBuffer dest) {
            dest.put(offset, src.x).put(offset + 1, src.y);
        }

        public void put(Vector2i src, int offset, ByteBuffer dest) {
            dest.putInt(offset, src.x).putInt(offset + 4, src.y);
        }

        public void put(Vector2i src, int offset, LongBuffer dest) {
            dest.put(offset, src.x).put(offset + 1, src.y);
        }

        public void put(Vector2L src, int offset, ByteBuffer dest) {
            dest.putLong(offset, src.x).putLong(offset + 8, src.y);
        }

        public void put(Vector2L src, int offset, LongBuffer dest) {
            dest.put(offset, src.x).put(offset + 1, src.y);
        }

        public void get(Matrix4f m, int offset, FloatBuffer src) {
            m._m00(src.get(offset))._m01(src.get(offset + 1))._m02(src.get(offset + 2))._m03(src.get(offset + 3))._m10(src.get(offset + 4))._m11(src.get(offset + 5))._m12(src.get(offset + 6))._m13(src.get(offset + 7))._m20(src.get(offset + 8))._m21(src.get(offset + 9))._m22(src.get(offset + 10))._m23(src.get(offset + 11))._m30(src.get(offset + 12))._m31(src.get(offset + 13))._m32(src.get(offset + 14))._m33(src.get(offset + 15));
        }

        public void get(Matrix4f m, int offset, ByteBuffer src) {
            m._m00(src.getFloat(offset))._m01(src.getFloat(offset + 4))._m02(src.getFloat(offset + 8))._m03(src.getFloat(offset + 12))._m10(src.getFloat(offset + 16))._m11(src.getFloat(offset + 20))._m12(src.getFloat(offset + 24))._m13(src.getFloat(offset + 28))._m20(src.getFloat(offset + 32))._m21(src.getFloat(offset + 36))._m22(src.getFloat(offset + 40))._m23(src.getFloat(offset + 44))._m30(src.getFloat(offset + 48))._m31(src.getFloat(offset + 52))._m32(src.getFloat(offset + 56))._m33(src.getFloat(offset + 60));
        }

        public void getTransposed(Matrix4f m, int offset, FloatBuffer src) {
            m._m00(src.get(offset))._m10(src.get(offset + 1))._m20(src.get(offset + 2))._m30(src.get(offset + 3))._m01(src.get(offset + 4))._m11(src.get(offset + 5))._m21(src.get(offset + 6))._m31(src.get(offset + 7))._m02(src.get(offset + 8))._m12(src.get(offset + 9))._m22(src.get(offset + 10))._m32(src.get(offset + 11))._m03(src.get(offset + 12))._m13(src.get(offset + 13))._m23(src.get(offset + 14))._m33(src.get(offset + 15));
        }

        public void getTransposed(Matrix4f m, int offset, ByteBuffer src) {
            m._m00(src.getFloat(offset))._m10(src.getFloat(offset + 4))._m20(src.getFloat(offset + 8))._m30(src.getFloat(offset + 12))._m01(src.getFloat(offset + 16))._m11(src.getFloat(offset + 20))._m21(src.getFloat(offset + 24))._m31(src.getFloat(offset + 28))._m02(src.getFloat(offset + 32))._m12(src.getFloat(offset + 36))._m22(src.getFloat(offset + 40))._m32(src.getFloat(offset + 44))._m03(src.getFloat(offset + 48))._m13(src.getFloat(offset + 52))._m23(src.getFloat(offset + 56))._m33(src.getFloat(offset + 60));
        }

        public void get(Matrix4x3f m, int offset, FloatBuffer src) {
            m._m00(src.get(offset))._m01(src.get(offset + 1))._m02(src.get(offset + 2))._m10(src.get(offset + 3))._m11(src.get(offset + 4))._m12(src.get(offset + 5))._m20(src.get(offset + 6))._m21(src.get(offset + 7))._m22(src.get(offset + 8))._m30(src.get(offset + 9))._m31(src.get(offset + 10))._m32(src.get(offset + 11));
        }

        public void get(Matrix4x3f m, int offset, ByteBuffer src) {
            m._m00(src.getFloat(offset))._m01(src.getFloat(offset + 4))._m02(src.getFloat(offset + 8))._m10(src.getFloat(offset + 12))._m11(src.getFloat(offset + 16))._m12(src.getFloat(offset + 20))._m20(src.getFloat(offset + 24))._m21(src.getFloat(offset + 28))._m22(src.getFloat(offset + 32))._m30(src.getFloat(offset + 36))._m31(src.getFloat(offset + 40))._m32(src.getFloat(offset + 44));
        }

        public void get(Matrix4d m, int offset, DoubleBuffer src) {
            m._m00(src.get(offset))._m01(src.get(offset + 1))._m02(src.get(offset + 2))._m03(src.get(offset + 3))._m10(src.get(offset + 4))._m11(src.get(offset + 5))._m12(src.get(offset + 6))._m13(src.get(offset + 7))._m20(src.get(offset + 8))._m21(src.get(offset + 9))._m22(src.get(offset + 10))._m23(src.get(offset + 11))._m30(src.get(offset + 12))._m31(src.get(offset + 13))._m32(src.get(offset + 14))._m33(src.get(offset + 15));
        }

        public void get(Matrix4d m, int offset, ByteBuffer src) {
            m._m00(src.getDouble(offset))._m01(src.getDouble(offset + 8))._m02(src.getDouble(offset + 16))._m03(src.getDouble(offset + 24))._m10(src.getDouble(offset + 32))._m11(src.getDouble(offset + 40))._m12(src.getDouble(offset + 48))._m13(src.getDouble(offset + 56))._m20(src.getDouble(offset + 64))._m21(src.getDouble(offset + 72))._m22(src.getDouble(offset + 80))._m23(src.getDouble(offset + 88))._m30(src.getDouble(offset + 96))._m31(src.getDouble(offset + 104))._m32(src.getDouble(offset + 112))._m33(src.getDouble(offset + 120));
        }

        public void get(Matrix4x3d m, int offset, DoubleBuffer src) {
            m._m00(src.get(offset))._m01(src.get(offset + 1))._m02(src.get(offset + 2))._m10(src.get(offset + 3))._m11(src.get(offset + 4))._m12(src.get(offset + 5))._m20(src.get(offset + 6))._m21(src.get(offset + 7))._m22(src.get(offset + 8))._m30(src.get(offset + 9))._m31(src.get(offset + 10))._m32(src.get(offset + 11));
        }

        public void get(Matrix4x3d m, int offset, ByteBuffer src) {
            m._m00(src.getDouble(offset))._m01(src.getDouble(offset + 8))._m02(src.getDouble(offset + 16))._m10(src.getDouble(offset + 24))._m11(src.getDouble(offset + 32))._m12(src.getDouble(offset + 40))._m20(src.getDouble(offset + 48))._m21(src.getDouble(offset + 56))._m22(src.getDouble(offset + 64))._m30(src.getDouble(offset + 72))._m31(src.getDouble(offset + 80))._m32(src.getDouble(offset + 88));
        }

        public void getf(Matrix4d m, int offset, FloatBuffer src) {
            m._m00(src.get(offset))._m01(src.get(offset + 1))._m02(src.get(offset + 2))._m03(src.get(offset + 3))._m10(src.get(offset + 4))._m11(src.get(offset + 5))._m12(src.get(offset + 6))._m13(src.get(offset + 7))._m20(src.get(offset + 8))._m21(src.get(offset + 9))._m22(src.get(offset + 10))._m23(src.get(offset + 11))._m30(src.get(offset + 12))._m31(src.get(offset + 13))._m32(src.get(offset + 14))._m33(src.get(offset + 15));
        }

        public void getf(Matrix4d m, int offset, ByteBuffer src) {
            m._m00(src.getFloat(offset))._m01(src.getFloat(offset + 4))._m02(src.getFloat(offset + 8))._m03(src.getFloat(offset + 12))._m10(src.getFloat(offset + 16))._m11(src.getFloat(offset + 20))._m12(src.getFloat(offset + 24))._m13(src.getFloat(offset + 28))._m20(src.getFloat(offset + 32))._m21(src.getFloat(offset + 36))._m22(src.getFloat(offset + 40))._m23(src.getFloat(offset + 44))._m30(src.getFloat(offset + 48))._m31(src.getFloat(offset + 52))._m32(src.getFloat(offset + 56))._m33(src.getFloat(offset + 60));
        }

        public void getf(Matrix4x3d m, int offset, FloatBuffer src) {
            m._m00(src.get(offset))._m01(src.get(offset + 1))._m02(src.get(offset + 2))._m10(src.get(offset + 3))._m11(src.get(offset + 4))._m12(src.get(offset + 5))._m20(src.get(offset + 6))._m21(src.get(offset + 7))._m22(src.get(offset + 8))._m30(src.get(offset + 9))._m31(src.get(offset + 10))._m32(src.get(offset + 11));
        }

        public void getf(Matrix4x3d m, int offset, ByteBuffer src) {
            m._m00(src.getFloat(offset))._m01(src.getFloat(offset + 4))._m02(src.getFloat(offset + 8))._m10(src.getFloat(offset + 12))._m11(src.getFloat(offset + 16))._m12(src.getFloat(offset + 20))._m20(src.getFloat(offset + 24))._m21(src.getFloat(offset + 28))._m22(src.getFloat(offset + 32))._m30(src.getFloat(offset + 36))._m31(src.getFloat(offset + 40))._m32(src.getFloat(offset + 44));
        }

        public void get(Matrix3f m, int offset, FloatBuffer src) {
            m._m00(src.get(offset))._m01(src.get(offset + 1))._m02(src.get(offset + 2))._m10(src.get(offset + 3))._m11(src.get(offset + 4))._m12(src.get(offset + 5))._m20(src.get(offset + 6))._m21(src.get(offset + 7))._m22(src.get(offset + 8));
        }

        public void get(Matrix3f m, int offset, ByteBuffer src) {
            m._m00(src.getFloat(offset))._m01(src.getFloat(offset + 4))._m02(src.getFloat(offset + 8))._m10(src.getFloat(offset + 12))._m11(src.getFloat(offset + 16))._m12(src.getFloat(offset + 20))._m20(src.getFloat(offset + 24))._m21(src.getFloat(offset + 28))._m22(src.getFloat(offset + 32));
        }

        public void get(Matrix3d m, int offset, DoubleBuffer src) {
            m._m00(src.get(offset))._m01(src.get(offset + 1))._m02(src.get(offset + 2))._m10(src.get(offset + 3))._m11(src.get(offset + 4))._m12(src.get(offset + 5))._m20(src.get(offset + 6))._m21(src.get(offset + 7))._m22(src.get(offset + 8));
        }

        public void get(Matrix3d m, int offset, ByteBuffer src) {
            m._m00(src.getDouble(offset))._m01(src.getDouble(offset + 8))._m02(src.getDouble(offset + 16))._m10(src.getDouble(offset + 24))._m11(src.getDouble(offset + 32))._m12(src.getDouble(offset + 40))._m20(src.getDouble(offset + 48))._m21(src.getDouble(offset + 56))._m22(src.getDouble(offset + 64));
        }

        public void get(Matrix3x2f m, int offset, FloatBuffer src) {
            m._m00(src.get(offset))._m01(src.get(offset + 1))._m10(src.get(offset + 2))._m11(src.get(offset + 3))._m20(src.get(offset + 4))._m21(src.get(offset + 5));
        }

        public void get(Matrix3x2f m, int offset, ByteBuffer src) {
            m._m00(src.getFloat(offset))._m01(src.getFloat(offset + 4))._m10(src.getFloat(offset + 8))._m11(src.getFloat(offset + 12))._m20(src.getFloat(offset + 16))._m21(src.getFloat(offset + 20));
        }

        public void get(Matrix3x2d m, int offset, DoubleBuffer src) {
            m._m00(src.get(offset))._m01(src.get(offset + 1))._m10(src.get(offset + 2))._m11(src.get(offset + 3))._m20(src.get(offset + 4))._m21(src.get(offset + 5));
        }

        public void get(Matrix3x2d m, int offset, ByteBuffer src) {
            m._m00(src.getDouble(offset))._m01(src.getDouble(offset + 8))._m10(src.getDouble(offset + 16))._m11(src.getDouble(offset + 24))._m20(src.getDouble(offset + 32))._m21(src.getDouble(offset + 40));
        }

        public void getf(Matrix3d m, int offset, FloatBuffer src) {
            m._m00(src.get(offset))._m01(src.get(offset + 1))._m02(src.get(offset + 2))._m10(src.get(offset + 3))._m11(src.get(offset + 4))._m12(src.get(offset + 5))._m20(src.get(offset + 6))._m21(src.get(offset + 7))._m22(src.get(offset + 8));
        }

        public void getf(Matrix3d m, int offset, ByteBuffer src) {
            m._m00(src.getFloat(offset))._m01(src.getFloat(offset + 4))._m02(src.getFloat(offset + 8))._m10(src.getFloat(offset + 12))._m11(src.getFloat(offset + 16))._m12(src.getFloat(offset + 20))._m20(src.getFloat(offset + 24))._m21(src.getFloat(offset + 28))._m22(src.getFloat(offset + 32));
        }

        public void get(Matrix2f m, int offset, FloatBuffer src) {
            m._m00(src.get(offset))._m01(src.get(offset + 1))._m10(src.get(offset + 2))._m11(src.get(offset + 3));
        }

        public void get(Matrix2f m, int offset, ByteBuffer src) {
            m._m00(src.getFloat(offset))._m01(src.getFloat(offset + 4))._m10(src.getFloat(offset + 8))._m11(src.getFloat(offset + 12));
        }

        public void get(Matrix2d m, int offset, DoubleBuffer src) {
            m._m00(src.get(offset))._m01(src.get(offset + 1))._m10(src.get(offset + 2))._m11(src.get(offset + 3));
        }

        public void get(Matrix2d m, int offset, ByteBuffer src) {
            m._m00(src.getDouble(offset))._m01(src.getDouble(offset + 8))._m10(src.getDouble(offset + 16))._m11(src.getDouble(offset + 24));
        }

        public void getf(Matrix2d m, int offset, FloatBuffer src) {
            m._m00(src.get(offset))._m01(src.get(offset + 1))._m10(src.get(offset + 2))._m11(src.get(offset + 3));
        }

        public void getf(Matrix2d m, int offset, ByteBuffer src) {
            m._m00(src.getFloat(offset))._m01(src.getFloat(offset + 4))._m10(src.getFloat(offset + 8))._m11(src.getFloat(offset + 12));
        }

        public void get(Vector4d dst, int offset, DoubleBuffer src) {
            dst.x = src.get(offset);
            dst.y = src.get(offset + 1);
            dst.z = src.get(offset + 2);
            dst.w = src.get(offset + 3);
        }

        public void get(Vector4d dst, int offset, ByteBuffer src) {
            dst.x = src.getDouble(offset);
            dst.y = src.getDouble(offset + 8);
            dst.z = src.getDouble(offset + 16);
            dst.w = src.getDouble(offset + 24);
        }

        public void get(Vector4f dst, int offset, FloatBuffer src) {
            dst.x = src.get(offset);
            dst.y = src.get(offset + 1);
            dst.z = src.get(offset + 2);
            dst.w = src.get(offset + 3);
        }

        public void get(Vector4f dst, int offset, ByteBuffer src) {
            dst.x = src.getFloat(offset);
            dst.y = src.getFloat(offset + 4);
            dst.z = src.getFloat(offset + 8);
            dst.w = src.getFloat(offset + 12);
        }

        public void get(Vector4L dst, int offset, LongBuffer src) {
            dst.x = src.get(offset);
            dst.y = src.get(offset + 1);
            dst.z = src.get(offset + 2);
            dst.w = src.get(offset + 3);
        }

        public void get(Vector4L dst, int offset, ByteBuffer src) {
            dst.x = src.getLong(offset);
            dst.y = src.getLong(offset + 8);
            dst.z = src.getLong(offset + 16);
            dst.w = src.getLong(offset + 24);
        }

        public void get(Vector4i dst, int offset, IntBuffer src) {
            dst.x = src.get(offset);
            dst.y = src.get(offset + 1);
            dst.z = src.get(offset + 2);
            dst.w = src.get(offset + 3);
        }

        public void get(Vector4i dst, int offset, ByteBuffer src) {
            dst.x = src.getInt(offset);
            dst.y = src.getInt(offset + 4);
            dst.z = src.getInt(offset + 8);
            dst.w = src.getInt(offset + 12);
        }

        public void get(Vector3f dst, int offset, FloatBuffer src) {
            dst.x = src.get(offset);
            dst.y = src.get(offset + 1);
            dst.z = src.get(offset + 2);
        }

        public void get(Vector3f dst, int offset, ByteBuffer src) {
            dst.x = src.getFloat(offset);
            dst.y = src.getFloat(offset + 4);
            dst.z = src.getFloat(offset + 8);
        }

        public void get(Vector3d dst, int offset, DoubleBuffer src) {
            dst.x = src.get(offset);
            dst.y = src.get(offset + 1);
            dst.z = src.get(offset + 2);
        }

        public void get(Vector3d dst, int offset, ByteBuffer src) {
            dst.x = src.getDouble(offset);
            dst.y = src.getDouble(offset + 8);
            dst.z = src.getDouble(offset + 16);
        }

        public void get(Vector3i dst, int offset, IntBuffer src) {
            dst.x = src.get(offset);
            dst.y = src.get(offset + 1);
            dst.z = src.get(offset + 2);
        }

        public void get(Vector3i dst, int offset, ByteBuffer src) {
            dst.x = src.getInt(offset);
            dst.y = src.getInt(offset + 4);
            dst.z = src.getInt(offset + 8);
        }

        public void get(Vector3L dst, int offset, LongBuffer src) {
            dst.x = src.get(offset);
            dst.y = src.get(offset + 1);
            dst.z = src.get(offset + 2);
        }

        public void get(Vector3L dst, int offset, ByteBuffer src) {
            dst.x = src.getLong(offset);
            dst.y = src.getLong(offset + 8);
            dst.z = src.getLong(offset + 16);
        }

        public void get(Vector2f dst, int offset, FloatBuffer src) {
            dst.x = src.get(offset);
            dst.y = src.get(offset + 1);
        }

        public void get(Vector2f dst, int offset, ByteBuffer src) {
            dst.x = src.getFloat(offset);
            dst.y = src.getFloat(offset + 4);
        }

        public void get(Vector2d dst, int offset, DoubleBuffer src) {
            dst.x = src.get(offset);
            dst.y = src.get(offset + 1);
        }

        public void get(Vector2d dst, int offset, ByteBuffer src) {
            dst.x = src.getDouble(offset);
            dst.y = src.getDouble(offset + 8);
        }

        public void get(Vector2i dst, int offset, IntBuffer src) {
            dst.x = src.get(offset);
            dst.y = src.get(offset + 1);
        }

        public void get(Vector2L dst, int offset, LongBuffer src) {
            dst.x = src.get(offset);
            dst.y = src.get(offset + 1);
        }

        public void get(Vector2i dst, int offset, ByteBuffer src) {
            dst.x = src.getInt(offset);
            dst.y = src.getInt(offset + 4);
        }

        public void get(Vector2L dst, int offset, ByteBuffer src) {
            dst.x = src.getLong(offset);
            dst.y = src.getLong(offset + 8);
        }

        public float get(Matrix4f m, int column, int row) {
            switch (column) {
                case 0: {
                    switch (row) {
                        case 0: {
                            return m.m00();
                        }
                        case 1: {
                            return m.m01();
                        }
                        case 2: {
                            return m.m02();
                        }
                        case 3: {
                            return m.m03();
                        }
                    }
                    break;
                }
                case 1: {
                    switch (row) {
                        case 0: {
                            return m.m10();
                        }
                        case 1: {
                            return m.m11();
                        }
                        case 2: {
                            return m.m12();
                        }
                        case 3: {
                            return m.m13();
                        }
                    }
                    break;
                }
                case 2: {
                    switch (row) {
                        case 0: {
                            return m.m20();
                        }
                        case 1: {
                            return m.m21();
                        }
                        case 2: {
                            return m.m22();
                        }
                        case 3: {
                            return m.m23();
                        }
                    }
                    break;
                }
                case 3: {
                    switch (row) {
                        case 0: {
                            return m.m30();
                        }
                        case 1: {
                            return m.m31();
                        }
                        case 2: {
                            return m.m32();
                        }
                        case 3: {
                            return m.m33();
                        }
                    }
                    break;
                }
            }
            throw new IllegalArgumentException();
        }

        public Matrix4f set(Matrix4f m, int column, int row, float value) {
            switch (column) {
                case 0: {
                    switch (row) {
                        case 0: {
                            return m.m00(value);
                        }
                        case 1: {
                            return m.m01(value);
                        }
                        case 2: {
                            return m.m02(value);
                        }
                        case 3: {
                            return m.m03(value);
                        }
                    }
                    break;
                }
                case 1: {
                    switch (row) {
                        case 0: {
                            return m.m10(value);
                        }
                        case 1: {
                            return m.m11(value);
                        }
                        case 2: {
                            return m.m12(value);
                        }
                        case 3: {
                            return m.m13(value);
                        }
                    }
                    break;
                }
                case 2: {
                    switch (row) {
                        case 0: {
                            return m.m20(value);
                        }
                        case 1: {
                            return m.m21(value);
                        }
                        case 2: {
                            return m.m22(value);
                        }
                        case 3: {
                            return m.m23(value);
                        }
                    }
                    break;
                }
                case 3: {
                    switch (row) {
                        case 0: {
                            return m.m30(value);
                        }
                        case 1: {
                            return m.m31(value);
                        }
                        case 2: {
                            return m.m32(value);
                        }
                        case 3: {
                            return m.m33(value);
                        }
                    }
                    break;
                }
            }
            throw new IllegalArgumentException();
        }

        public double get(Matrix4d m, int column, int row) {
            switch (column) {
                case 0: {
                    switch (row) {
                        case 0: {
                            return m.m00;
                        }
                        case 1: {
                            return m.m01;
                        }
                        case 2: {
                            return m.m02;
                        }
                        case 3: {
                            return m.m03;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (row) {
                        case 0: {
                            return m.m10;
                        }
                        case 1: {
                            return m.m11;
                        }
                        case 2: {
                            return m.m12;
                        }
                        case 3: {
                            return m.m13;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (row) {
                        case 0: {
                            return m.m20;
                        }
                        case 1: {
                            return m.m21;
                        }
                        case 2: {
                            return m.m22;
                        }
                        case 3: {
                            return m.m23;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (row) {
                        case 0: {
                            return m.m30;
                        }
                        case 1: {
                            return m.m31;
                        }
                        case 2: {
                            return m.m32;
                        }
                        case 3: {
                            return m.m33;
                        }
                    }
                    break;
                }
            }
            throw new IllegalArgumentException();
        }

        public Matrix4d set(Matrix4d m, int column, int row, double value) {
            switch (column) {
                case 0: {
                    switch (row) {
                        case 0: {
                            return m.m00(value);
                        }
                        case 1: {
                            return m.m01(value);
                        }
                        case 2: {
                            return m.m02(value);
                        }
                        case 3: {
                            return m.m03(value);
                        }
                    }
                    break;
                }
                case 1: {
                    switch (row) {
                        case 0: {
                            return m.m10(value);
                        }
                        case 1: {
                            return m.m11(value);
                        }
                        case 2: {
                            return m.m12(value);
                        }
                        case 3: {
                            return m.m13(value);
                        }
                    }
                    break;
                }
                case 2: {
                    switch (row) {
                        case 0: {
                            return m.m20(value);
                        }
                        case 1: {
                            return m.m21(value);
                        }
                        case 2: {
                            return m.m22(value);
                        }
                        case 3: {
                            return m.m23(value);
                        }
                    }
                    break;
                }
                case 3: {
                    switch (row) {
                        case 0: {
                            return m.m30(value);
                        }
                        case 1: {
                            return m.m31(value);
                        }
                        case 2: {
                            return m.m32(value);
                        }
                        case 3: {
                            return m.m33(value);
                        }
                    }
                    break;
                }
            }
            throw new IllegalArgumentException();
        }

        public float get(Matrix3f m, int column, int row) {
            switch (column) {
                case 0: {
                    switch (row) {
                        case 0: {
                            return m.m00;
                        }
                        case 1: {
                            return m.m01;
                        }
                        case 2: {
                            return m.m02;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (row) {
                        case 0: {
                            return m.m10;
                        }
                        case 1: {
                            return m.m11;
                        }
                        case 2: {
                            return m.m12;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (row) {
                        case 0: {
                            return m.m20;
                        }
                        case 1: {
                            return m.m21;
                        }
                        case 2: {
                            return m.m22;
                        }
                    }
                    break;
                }
            }
            throw new IllegalArgumentException();
        }

        public Matrix3f set(Matrix3f m, int column, int row, float value) {
            switch (column) {
                case 0: {
                    switch (row) {
                        case 0: {
                            return m.m00(value);
                        }
                        case 1: {
                            return m.m01(value);
                        }
                        case 2: {
                            return m.m02(value);
                        }
                    }
                    break;
                }
                case 1: {
                    switch (row) {
                        case 0: {
                            return m.m10(value);
                        }
                        case 1: {
                            return m.m11(value);
                        }
                        case 2: {
                            return m.m12(value);
                        }
                    }
                    break;
                }
                case 2: {
                    switch (row) {
                        case 0: {
                            return m.m20(value);
                        }
                        case 1: {
                            return m.m21(value);
                        }
                        case 2: {
                            return m.m22(value);
                        }
                    }
                    break;
                }
            }
            throw new IllegalArgumentException();
        }

        public double get(Matrix3d m, int column, int row) {
            switch (column) {
                case 0: {
                    switch (row) {
                        case 0: {
                            return m.m00;
                        }
                        case 1: {
                            return m.m01;
                        }
                        case 2: {
                            return m.m02;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (row) {
                        case 0: {
                            return m.m10;
                        }
                        case 1: {
                            return m.m11;
                        }
                        case 2: {
                            return m.m12;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (row) {
                        case 0: {
                            return m.m20;
                        }
                        case 1: {
                            return m.m21;
                        }
                        case 2: {
                            return m.m22;
                        }
                    }
                    break;
                }
            }
            throw new IllegalArgumentException();
        }

        public Matrix3d set(Matrix3d m, int column, int row, double value) {
            switch (column) {
                case 0: {
                    switch (row) {
                        case 0: {
                            return m.m00(value);
                        }
                        case 1: {
                            return m.m01(value);
                        }
                        case 2: {
                            return m.m02(value);
                        }
                    }
                    break;
                }
                case 1: {
                    switch (row) {
                        case 0: {
                            return m.m10(value);
                        }
                        case 1: {
                            return m.m11(value);
                        }
                        case 2: {
                            return m.m12(value);
                        }
                    }
                    break;
                }
                case 2: {
                    switch (row) {
                        case 0: {
                            return m.m20(value);
                        }
                        case 1: {
                            return m.m21(value);
                        }
                        case 2: {
                            return m.m22(value);
                        }
                    }
                    break;
                }
            }
            throw new IllegalArgumentException();
        }

        public Vector4f getColumn(Matrix4f m, int column, Vector4f dest) {
            switch (column) {
                case 0: {
                    return dest.set(m.m00(), m.m01(), m.m02(), m.m03());
                }
                case 1: {
                    return dest.set(m.m10(), m.m11(), m.m12(), m.m13());
                }
                case 2: {
                    return dest.set(m.m20(), m.m21(), m.m22(), m.m23());
                }
                case 3: {
                    return dest.set(m.m30(), m.m31(), m.m32(), m.m33());
                }
            }
            throw new IndexOutOfBoundsException();
        }

        public Matrix4f setColumn(Vector4f v, int column, Matrix4f dest) {
            switch (column) {
                case 0: {
                    return dest._m00(v.x)._m01(v.y)._m02(v.z)._m03(v.w);
                }
                case 1: {
                    return dest._m10(v.x)._m11(v.y)._m12(v.z)._m13(v.w);
                }
                case 2: {
                    return dest._m20(v.x)._m21(v.y)._m22(v.z)._m23(v.w);
                }
                case 3: {
                    return dest._m30(v.x)._m31(v.y)._m32(v.z)._m33(v.w);
                }
            }
            throw new IndexOutOfBoundsException();
        }

        public Matrix4f setColumn(Vector4fc v, int column, Matrix4f dest) {
            switch (column) {
                case 0: {
                    return dest._m00(v.x())._m01(v.y())._m02(v.z())._m03(v.w());
                }
                case 1: {
                    return dest._m10(v.x())._m11(v.y())._m12(v.z())._m13(v.w());
                }
                case 2: {
                    return dest._m20(v.x())._m21(v.y())._m22(v.z())._m23(v.w());
                }
                case 3: {
                    return dest._m30(v.x())._m31(v.y())._m32(v.z())._m33(v.w());
                }
            }
            throw new IndexOutOfBoundsException();
        }

        public void copy(Matrix4fc src, Matrix4f dest) {
            dest._m00(src.m00())._m01(src.m01())._m02(src.m02())._m03(src.m03())._m10(src.m10())._m11(src.m11())._m12(src.m12())._m13(src.m13())._m20(src.m20())._m21(src.m21())._m22(src.m22())._m23(src.m23())._m30(src.m30())._m31(src.m31())._m32(src.m32())._m33(src.m33());
        }

        public void copy(Matrix3fc src, Matrix4f dest) {
            dest._m00(src.m00())._m01(src.m01())._m02(src.m02())._m03(0.0f)._m10(src.m10())._m11(src.m11())._m12(src.m12())._m13(0.0f)._m20(src.m20())._m21(src.m21())._m22(src.m22())._m23(0.0f)._m30(0.0f)._m31(0.0f)._m32(0.0f)._m33(1.0f);
        }

        public void copy(Matrix4fc src, Matrix3f dest) {
            dest._m00(src.m00())._m01(src.m01())._m02(src.m02())._m10(src.m10())._m11(src.m11())._m12(src.m12())._m20(src.m20())._m21(src.m21())._m22(src.m22());
        }

        public void copy(Matrix3fc src, Matrix4x3f dest) {
            dest._m00(src.m00())._m01(src.m01())._m02(src.m02())._m10(src.m10())._m11(src.m11())._m12(src.m12())._m20(src.m20())._m21(src.m21())._m22(src.m22())._m30(0.0f)._m31(0.0f)._m32(0.0f);
        }

        public void copy(Matrix3x2fc src, Matrix3x2f dest) {
            dest._m00(src.m00())._m01(src.m01())._m10(src.m10())._m11(src.m11())._m20(src.m20())._m21(src.m21());
        }

        public void copy(Matrix3x2dc src, Matrix3x2d dest) {
            dest._m00(src.m00())._m01(src.m01())._m10(src.m10())._m11(src.m11())._m20(src.m20())._m21(src.m21());
        }

        public void copy(Matrix2fc src, Matrix2f dest) {
            dest._m00(src.m00())._m01(src.m01())._m10(src.m10())._m11(src.m11());
        }

        public void copy(Matrix2dc src, Matrix2d dest) {
            dest._m00(src.m00())._m01(src.m01())._m10(src.m10())._m11(src.m11());
        }

        public void copy(Matrix2fc src, Matrix3f dest) {
            dest._m00(src.m00())._m01(src.m01())._m02(0.0f)._m10(src.m10())._m11(src.m11())._m12(0.0f)._m20(0.0f)._m21(0.0f)._m22(1.0f);
        }

        public void copy(Matrix3fc src, Matrix2f dest) {
            dest._m00(src.m00())._m01(src.m01())._m10(src.m10())._m11(src.m11());
        }

        public void copy(Matrix2fc src, Matrix3x2f dest) {
            dest._m00(src.m00())._m01(src.m01())._m10(src.m10())._m11(src.m11())._m20(0.0f)._m21(0.0f);
        }

        public void copy(Matrix3x2fc src, Matrix2f dest) {
            dest._m00(src.m00())._m01(src.m01())._m10(src.m10())._m11(src.m11());
        }

        public void copy(Matrix2dc src, Matrix3d dest) {
            dest._m00(src.m00())._m01(src.m01())._m02(0.0)._m10(src.m10())._m11(src.m11())._m12(0.0)._m20(0.0)._m21(0.0)._m22(1.0);
        }

        public void copy(Matrix3dc src, Matrix2d dest) {
            dest._m00(src.m00())._m01(src.m01())._m10(src.m10())._m11(src.m11());
        }

        public void copy(Matrix2dc src, Matrix3x2d dest) {
            dest._m00(src.m00())._m01(src.m01())._m10(src.m10())._m11(src.m11())._m20(0.0)._m21(0.0);
        }

        public void copy(Matrix3x2dc src, Matrix2d dest) {
            dest._m00(src.m00())._m01(src.m01())._m10(src.m10())._m11(src.m11());
        }

        public void copy3x3(Matrix4fc src, Matrix4f dest) {
            dest._m00(src.m00())._m01(src.m01())._m02(src.m02())._m10(src.m10())._m11(src.m11())._m12(src.m12())._m20(src.m20())._m21(src.m21())._m22(src.m22());
        }

        public void copy3x3(Matrix4x3fc src, Matrix4x3f dest) {
            dest._m00(src.m00())._m01(src.m01())._m02(src.m02())._m10(src.m10())._m11(src.m11())._m12(src.m12())._m20(src.m20())._m21(src.m21())._m22(src.m22());
        }

        public void copy3x3(Matrix3fc src, Matrix4x3f dest) {
            dest._m00(src.m00())._m01(src.m01())._m02(src.m02())._m10(src.m10())._m11(src.m11())._m12(src.m12())._m20(src.m20())._m21(src.m21())._m22(src.m22());
        }

        public void copy3x3(Matrix3fc src, Matrix4f dest) {
            dest._m00(src.m00())._m01(src.m01())._m02(src.m02())._m10(src.m10())._m11(src.m11())._m12(src.m12())._m20(src.m20())._m21(src.m21())._m22(src.m22());
        }

        public void copy4x3(Matrix4x3fc src, Matrix4f dest) {
            dest._m00(src.m00())._m01(src.m01())._m02(src.m02())._m10(src.m10())._m11(src.m11())._m12(src.m12())._m20(src.m20())._m21(src.m21())._m22(src.m22())._m30(src.m30())._m31(src.m31())._m32(src.m32());
        }

        public void copy4x3(Matrix4fc src, Matrix4f dest) {
            dest._m00(src.m00())._m01(src.m01())._m02(src.m02())._m10(src.m10())._m11(src.m11())._m12(src.m12())._m20(src.m20())._m21(src.m21())._m22(src.m22())._m30(src.m30())._m31(src.m31())._m32(src.m32());
        }

        public void copy(Matrix4fc src, Matrix4x3f dest) {
            dest._m00(src.m00())._m01(src.m01())._m02(src.m02())._m10(src.m10())._m11(src.m11())._m12(src.m12())._m20(src.m20())._m21(src.m21())._m22(src.m22())._m30(src.m30())._m31(src.m31())._m32(src.m32());
        }

        public void copy(Matrix4x3fc src, Matrix4f dest) {
            dest._m00(src.m00())._m01(src.m01())._m02(src.m02())._m03(0.0f)._m10(src.m10())._m11(src.m11())._m12(src.m12())._m13(0.0f)._m20(src.m20())._m21(src.m21())._m22(src.m22())._m23(0.0f)._m30(src.m30())._m31(src.m31())._m32(src.m32())._m33(1.0f);
        }

        public void copy(Matrix4x3fc src, Matrix4x3f dest) {
            dest._m00(src.m00())._m01(src.m01())._m02(src.m02())._m10(src.m10())._m11(src.m11())._m12(src.m12())._m20(src.m20())._m21(src.m21())._m22(src.m22())._m30(src.m30())._m31(src.m31())._m32(src.m32());
        }

        public void copy(Matrix3fc src, Matrix3f dest) {
            dest._m00(src.m00())._m01(src.m01())._m02(src.m02())._m10(src.m10())._m11(src.m11())._m12(src.m12())._m20(src.m20())._m21(src.m21())._m22(src.m22());
        }

        public void copy(float[] arr, int off, Matrix4f dest) {
            dest._m00(arr[off + 0])._m01(arr[off + 1])._m02(arr[off + 2])._m03(arr[off + 3])._m10(arr[off + 4])._m11(arr[off + 5])._m12(arr[off + 6])._m13(arr[off + 7])._m20(arr[off + 8])._m21(arr[off + 9])._m22(arr[off + 10])._m23(arr[off + 11])._m30(arr[off + 12])._m31(arr[off + 13])._m32(arr[off + 14])._m33(arr[off + 15]);
        }

        public void copyTransposed(float[] arr, int off, Matrix4f dest) {
            dest._m00(arr[off + 0])._m10(arr[off + 1])._m20(arr[off + 2])._m30(arr[off + 3])._m01(arr[off + 4])._m11(arr[off + 5])._m21(arr[off + 6])._m31(arr[off + 7])._m02(arr[off + 8])._m12(arr[off + 9])._m22(arr[off + 10])._m32(arr[off + 11])._m03(arr[off + 12])._m13(arr[off + 13])._m23(arr[off + 14])._m33(arr[off + 15]);
        }

        public void copy(float[] arr, int off, Matrix3f dest) {
            dest._m00(arr[off + 0])._m01(arr[off + 1])._m02(arr[off + 2])._m10(arr[off + 3])._m11(arr[off + 4])._m12(arr[off + 5])._m20(arr[off + 6])._m21(arr[off + 7])._m22(arr[off + 8]);
        }

        public void copy(float[] arr, int off, Matrix4x3f dest) {
            dest._m00(arr[off + 0])._m01(arr[off + 1])._m02(arr[off + 2])._m10(arr[off + 3])._m11(arr[off + 4])._m12(arr[off + 5])._m20(arr[off + 6])._m21(arr[off + 7])._m22(arr[off + 8])._m30(arr[off + 9])._m31(arr[off + 10])._m32(arr[off + 11]);
        }

        public void copy(float[] arr, int off, Matrix3x2f dest) {
            dest._m00(arr[off + 0])._m01(arr[off + 1])._m10(arr[off + 2])._m11(arr[off + 3])._m20(arr[off + 4])._m21(arr[off + 5]);
        }

        public void copy(double[] arr, int off, Matrix3x2d dest) {
            dest._m00(arr[off + 0])._m01(arr[off + 1])._m10(arr[off + 2])._m11(arr[off + 3])._m20(arr[off + 4])._m21(arr[off + 5]);
        }

        public void copy(float[] arr, int off, Matrix3x2d dest) {
            dest._m00(arr[off + 0])._m01(arr[off + 1])._m10(arr[off + 2])._m11(arr[off + 3])._m20(arr[off + 4])._m21(arr[off + 5]);
        }

        public void copy(float[] arr, int off, Matrix2f dest) {
            dest._m00(arr[off + 0])._m01(arr[off + 1])._m10(arr[off + 2])._m11(arr[off + 3]);
        }

        public void copy(double[] arr, int off, Matrix2d dest) {
            dest._m00(arr[off + 0])._m01(arr[off + 1])._m10(arr[off + 2])._m11(arr[off + 3]);
        }

        public void copy(Matrix4fc src, float[] dest, int off) {
            dest[off + 0] = src.m00();
            dest[off + 1] = src.m01();
            dest[off + 2] = src.m02();
            dest[off + 3] = src.m03();
            dest[off + 4] = src.m10();
            dest[off + 5] = src.m11();
            dest[off + 6] = src.m12();
            dest[off + 7] = src.m13();
            dest[off + 8] = src.m20();
            dest[off + 9] = src.m21();
            dest[off + 10] = src.m22();
            dest[off + 11] = src.m23();
            dest[off + 12] = src.m30();
            dest[off + 13] = src.m31();
            dest[off + 14] = src.m32();
            dest[off + 15] = src.m33();
        }

        public void copy(Matrix3fc src, float[] dest, int off) {
            dest[off + 0] = src.m00();
            dest[off + 1] = src.m01();
            dest[off + 2] = src.m02();
            dest[off + 3] = src.m10();
            dest[off + 4] = src.m11();
            dest[off + 5] = src.m12();
            dest[off + 6] = src.m20();
            dest[off + 7] = src.m21();
            dest[off + 8] = src.m22();
        }

        public void copy(Matrix4x3fc src, float[] dest, int off) {
            dest[off + 0] = src.m00();
            dest[off + 1] = src.m01();
            dest[off + 2] = src.m02();
            dest[off + 3] = src.m10();
            dest[off + 4] = src.m11();
            dest[off + 5] = src.m12();
            dest[off + 6] = src.m20();
            dest[off + 7] = src.m21();
            dest[off + 8] = src.m22();
            dest[off + 9] = src.m30();
            dest[off + 10] = src.m31();
            dest[off + 11] = src.m32();
        }

        public void copy(Matrix3x2fc src, float[] dest, int off) {
            dest[off + 0] = src.m00();
            dest[off + 1] = src.m01();
            dest[off + 2] = src.m10();
            dest[off + 3] = src.m11();
            dest[off + 4] = src.m20();
            dest[off + 5] = src.m21();
        }

        public void copy(Matrix3x2dc src, double[] dest, int off) {
            dest[off + 0] = src.m00();
            dest[off + 1] = src.m01();
            dest[off + 2] = src.m10();
            dest[off + 3] = src.m11();
            dest[off + 4] = src.m20();
            dest[off + 5] = src.m21();
        }

        public void copy(Matrix2fc src, float[] dest, int off) {
            dest[off + 0] = src.m00();
            dest[off + 1] = src.m01();
            dest[off + 2] = src.m10();
            dest[off + 3] = src.m11();
        }

        public void copy(Matrix2dc src, double[] dest, int off) {
            dest[off + 0] = src.m00();
            dest[off + 1] = src.m01();
            dest[off + 2] = src.m10();
            dest[off + 3] = src.m11();
        }

        public void copy4x4(Matrix4x3fc src, float[] dest, int off) {
            dest[off + 0] = src.m00();
            dest[off + 1] = src.m01();
            dest[off + 2] = src.m02();
            dest[off + 3] = 0.0f;
            dest[off + 4] = src.m10();
            dest[off + 5] = src.m11();
            dest[off + 6] = src.m12();
            dest[off + 7] = 0.0f;
            dest[off + 8] = src.m20();
            dest[off + 9] = src.m21();
            dest[off + 10] = src.m22();
            dest[off + 11] = 0.0f;
            dest[off + 12] = src.m30();
            dest[off + 13] = src.m31();
            dest[off + 14] = src.m32();
            dest[off + 15] = 1.0f;
        }

        public void copy4x4(Matrix4x3dc src, float[] dest, int off) {
            dest[off + 0] = (float)src.m00();
            dest[off + 1] = (float)src.m01();
            dest[off + 2] = (float)src.m02();
            dest[off + 3] = 0.0f;
            dest[off + 4] = (float)src.m10();
            dest[off + 5] = (float)src.m11();
            dest[off + 6] = (float)src.m12();
            dest[off + 7] = 0.0f;
            dest[off + 8] = (float)src.m20();
            dest[off + 9] = (float)src.m21();
            dest[off + 10] = (float)src.m22();
            dest[off + 11] = 0.0f;
            dest[off + 12] = (float)src.m30();
            dest[off + 13] = (float)src.m31();
            dest[off + 14] = (float)src.m32();
            dest[off + 15] = 1.0f;
        }

        public void copy4x4(Matrix4x3dc src, double[] dest, int off) {
            dest[off + 0] = src.m00();
            dest[off + 1] = src.m01();
            dest[off + 2] = src.m02();
            dest[off + 3] = 0.0;
            dest[off + 4] = src.m10();
            dest[off + 5] = src.m11();
            dest[off + 6] = src.m12();
            dest[off + 7] = 0.0;
            dest[off + 8] = src.m20();
            dest[off + 9] = src.m21();
            dest[off + 10] = src.m22();
            dest[off + 11] = 0.0;
            dest[off + 12] = src.m30();
            dest[off + 13] = src.m31();
            dest[off + 14] = src.m32();
            dest[off + 15] = 1.0;
        }

        public void copy3x3(Matrix3x2fc src, float[] dest, int off) {
            dest[off + 0] = src.m00();
            dest[off + 1] = src.m01();
            dest[off + 2] = 0.0f;
            dest[off + 3] = src.m10();
            dest[off + 4] = src.m11();
            dest[off + 5] = 0.0f;
            dest[off + 6] = src.m20();
            dest[off + 7] = src.m21();
            dest[off + 8] = 1.0f;
        }

        public void copy3x3(Matrix3x2dc src, double[] dest, int off) {
            dest[off + 0] = src.m00();
            dest[off + 1] = src.m01();
            dest[off + 2] = 0.0;
            dest[off + 3] = src.m10();
            dest[off + 4] = src.m11();
            dest[off + 5] = 0.0;
            dest[off + 6] = src.m20();
            dest[off + 7] = src.m21();
            dest[off + 8] = 1.0;
        }

        public void copy4x4(Matrix3x2fc src, float[] dest, int off) {
            dest[off + 0] = src.m00();
            dest[off + 1] = src.m01();
            dest[off + 2] = 0.0f;
            dest[off + 3] = 0.0f;
            dest[off + 4] = src.m10();
            dest[off + 5] = src.m11();
            dest[off + 6] = 0.0f;
            dest[off + 7] = 0.0f;
            dest[off + 8] = 0.0f;
            dest[off + 9] = 0.0f;
            dest[off + 10] = 1.0f;
            dest[off + 11] = 0.0f;
            dest[off + 12] = src.m20();
            dest[off + 13] = src.m21();
            dest[off + 14] = 0.0f;
            dest[off + 15] = 1.0f;
        }

        public void copy4x4(Matrix3x2dc src, double[] dest, int off) {
            dest[off + 0] = src.m00();
            dest[off + 1] = src.m01();
            dest[off + 2] = 0.0;
            dest[off + 3] = 0.0;
            dest[off + 4] = src.m10();
            dest[off + 5] = src.m11();
            dest[off + 6] = 0.0;
            dest[off + 7] = 0.0;
            dest[off + 8] = 0.0;
            dest[off + 9] = 0.0;
            dest[off + 10] = 1.0;
            dest[off + 11] = 0.0;
            dest[off + 12] = src.m20();
            dest[off + 13] = src.m21();
            dest[off + 14] = 0.0;
            dest[off + 15] = 1.0;
        }

        public void identity(Matrix4f dest) {
            dest._m00(1.0f)._m01(0.0f)._m02(0.0f)._m03(0.0f)._m10(0.0f)._m11(1.0f)._m12(0.0f)._m13(0.0f)._m20(0.0f)._m21(0.0f)._m22(1.0f)._m23(0.0f)._m30(0.0f)._m31(0.0f)._m32(0.0f)._m33(1.0f);
        }

        public void identity(Matrix4x3f dest) {
            dest._m00(1.0f)._m01(0.0f)._m02(0.0f)._m10(0.0f)._m11(1.0f)._m12(0.0f)._m20(0.0f)._m21(0.0f)._m22(1.0f)._m30(0.0f)._m31(0.0f)._m32(0.0f);
        }

        public void identity(Matrix3f dest) {
            dest._m00(1.0f)._m01(0.0f)._m02(0.0f)._m10(0.0f)._m11(1.0f)._m12(0.0f)._m20(0.0f)._m21(0.0f)._m22(1.0f);
        }

        public void identity(Matrix3x2f dest) {
            dest._m00(1.0f)._m01(0.0f)._m10(0.0f)._m11(1.0f)._m20(0.0f)._m21(0.0f);
        }

        public void identity(Matrix3x2d dest) {
            dest._m00(1.0)._m01(0.0)._m10(0.0)._m11(1.0)._m20(0.0)._m21(0.0);
        }

        public void identity(Matrix2f dest) {
            dest._m00(1.0f)._m01(0.0f)._m10(0.0f)._m11(1.0f);
        }

        public void swap(Matrix4f m1, Matrix4f m2) {
            float tmp = m1.m00();
            m1._m00(m2.m00());
            m2._m00(tmp);
            tmp = m1.m01();
            m1._m01(m2.m01());
            m2._m01(tmp);
            tmp = m1.m02();
            m1._m02(m2.m02());
            m2._m02(tmp);
            tmp = m1.m03();
            m1._m03(m2.m03());
            m2._m03(tmp);
            tmp = m1.m10();
            m1._m10(m2.m10());
            m2._m10(tmp);
            tmp = m1.m11();
            m1._m11(m2.m11());
            m2._m11(tmp);
            tmp = m1.m12();
            m1._m12(m2.m12());
            m2._m12(tmp);
            tmp = m1.m13();
            m1._m13(m2.m13());
            m2._m13(tmp);
            tmp = m1.m20();
            m1._m20(m2.m20());
            m2._m20(tmp);
            tmp = m1.m21();
            m1._m21(m2.m21());
            m2._m21(tmp);
            tmp = m1.m22();
            m1._m22(m2.m22());
            m2._m22(tmp);
            tmp = m1.m23();
            m1._m23(m2.m23());
            m2._m23(tmp);
            tmp = m1.m30();
            m1._m30(m2.m30());
            m2._m30(tmp);
            tmp = m1.m31();
            m1._m31(m2.m31());
            m2._m31(tmp);
            tmp = m1.m32();
            m1._m32(m2.m32());
            m2._m32(tmp);
            tmp = m1.m33();
            m1._m33(m2.m33());
            m2._m33(tmp);
        }

        public void swap(Matrix4x3f m1, Matrix4x3f m2) {
            float tmp = m1.m00();
            m1._m00(m2.m00());
            m2._m00(tmp);
            tmp = m1.m01();
            m1._m01(m2.m01());
            m2._m01(tmp);
            tmp = m1.m02();
            m1._m02(m2.m02());
            m2._m02(tmp);
            tmp = m1.m10();
            m1._m10(m2.m10());
            m2._m10(tmp);
            tmp = m1.m11();
            m1._m11(m2.m11());
            m2._m11(tmp);
            tmp = m1.m12();
            m1._m12(m2.m12());
            m2._m12(tmp);
            tmp = m1.m20();
            m1._m20(m2.m20());
            m2._m20(tmp);
            tmp = m1.m21();
            m1._m21(m2.m21());
            m2._m21(tmp);
            tmp = m1.m22();
            m1._m22(m2.m22());
            m2._m22(tmp);
            tmp = m1.m30();
            m1._m30(m2.m30());
            m2._m30(tmp);
            tmp = m1.m31();
            m1._m31(m2.m31());
            m2._m31(tmp);
            tmp = m1.m32();
            m1._m32(m2.m32());
            m2._m32(tmp);
        }

        public void swap(Matrix3f m1, Matrix3f m2) {
            float tmp = m1.m00();
            m1._m00(m2.m00());
            m2._m00(tmp);
            tmp = m1.m01();
            m1._m01(m2.m01());
            m2._m01(tmp);
            tmp = m1.m02();
            m1._m02(m2.m02());
            m2._m02(tmp);
            tmp = m1.m10();
            m1._m10(m2.m10());
            m2._m10(tmp);
            tmp = m1.m11();
            m1._m11(m2.m11());
            m2._m11(tmp);
            tmp = m1.m12();
            m1._m12(m2.m12());
            m2._m12(tmp);
            tmp = m1.m20();
            m1._m20(m2.m20());
            m2._m20(tmp);
            tmp = m1.m21();
            m1._m21(m2.m21());
            m2._m21(tmp);
            tmp = m1.m22();
            m1._m22(m2.m22());
            m2._m22(tmp);
        }

        public void swap(Matrix2f m1, Matrix2f m2) {
            float tmp = m1.m00();
            m1._m00(m2.m00());
            m2._m00(tmp);
            tmp = m1.m01();
            m1._m00(m2.m01());
            m2._m01(tmp);
            tmp = m1.m10();
            m1._m00(m2.m10());
            m2._m10(tmp);
            tmp = m1.m11();
            m1._m00(m2.m11());
            m2._m11(tmp);
        }

        public void swap(Matrix2d m1, Matrix2d m2) {
            double tmp = m1.m00();
            m1._m00(m2.m00());
            m2._m00(tmp);
            tmp = m1.m01();
            m1._m00(m2.m01());
            m2._m01(tmp);
            tmp = m1.m10();
            m1._m00(m2.m10());
            m2._m10(tmp);
            tmp = m1.m11();
            m1._m00(m2.m11());
            m2._m11(tmp);
        }

        public void zero(Matrix4f dest) {
            dest._m00(0.0f)._m01(0.0f)._m02(0.0f)._m03(0.0f)._m10(0.0f)._m11(0.0f)._m12(0.0f)._m13(0.0f)._m20(0.0f)._m21(0.0f)._m22(0.0f)._m23(0.0f)._m30(0.0f)._m31(0.0f)._m32(0.0f)._m33(0.0f);
        }

        public void zero(Matrix4x3f dest) {
            dest._m00(0.0f)._m01(0.0f)._m02(0.0f)._m10(0.0f)._m11(0.0f)._m12(0.0f)._m20(0.0f)._m21(0.0f)._m22(0.0f)._m30(0.0f)._m31(0.0f)._m32(0.0f);
        }

        public void zero(Matrix3f dest) {
            dest._m00(0.0f)._m01(0.0f)._m02(0.0f)._m10(0.0f)._m11(0.0f)._m12(0.0f)._m20(0.0f)._m21(0.0f)._m22(0.0f);
        }

        public void zero(Matrix3x2f dest) {
            dest._m00(0.0f)._m01(0.0f)._m10(0.0f)._m11(0.0f)._m20(0.0f)._m21(0.0f);
        }

        public void zero(Matrix3x2d dest) {
            dest._m00(0.0)._m01(0.0)._m10(0.0)._m11(0.0)._m20(0.0)._m21(0.0);
        }

        public void zero(Matrix2f dest) {
            dest._m00(0.0f)._m01(0.0f)._m10(0.0f)._m11(0.0f);
        }

        public void zero(Matrix2d dest) {
            dest._m00(0.0)._m01(0.0)._m10(0.0)._m11(0.0);
        }

        public void putMatrix3f(Quaternionf q, int position, ByteBuffer dest) {
            float w2 = q.w * q.w;
            float x2 = q.x * q.x;
            float y2 = q.y * q.y;
            float z2 = q.z * q.z;
            float zw = q.z * q.w;
            float xy = q.x * q.y;
            float xz = q.x * q.z;
            float yw = q.y * q.w;
            float yz = q.y * q.z;
            float xw = q.x * q.w;
            dest.putFloat(position, w2 + x2 - z2 - y2).putFloat(position + 4, xy + zw + zw + xy).putFloat(position + 8, xz - yw + xz - yw).putFloat(position + 12, -zw + xy - zw + xy).putFloat(position + 16, y2 - z2 + w2 - x2).putFloat(position + 20, yz + yz + xw + xw).putFloat(position + 24, yw + xz + xz + yw).putFloat(position + 28, yz + yz - xw - xw).putFloat(position + 32, z2 - y2 - x2 + w2);
        }

        public void putMatrix3f(Quaternionf q, int position, FloatBuffer dest) {
            float w2 = q.w * q.w;
            float x2 = q.x * q.x;
            float y2 = q.y * q.y;
            float z2 = q.z * q.z;
            float zw = q.z * q.w;
            float xy = q.x * q.y;
            float xz = q.x * q.z;
            float yw = q.y * q.w;
            float yz = q.y * q.z;
            float xw = q.x * q.w;
            dest.put(position, w2 + x2 - z2 - y2).put(position + 1, xy + zw + zw + xy).put(position + 2, xz - yw + xz - yw).put(position + 3, -zw + xy - zw + xy).put(position + 4, y2 - z2 + w2 - x2).put(position + 5, yz + yz + xw + xw).put(position + 6, yw + xz + xz + yw).put(position + 7, yz + yz - xw - xw).put(position + 8, z2 - y2 - x2 + w2);
        }

        public void putMatrix4f(Quaternionf q, int position, ByteBuffer dest) {
            float w2 = q.w * q.w;
            float x2 = q.x * q.x;
            float y2 = q.y * q.y;
            float z2 = q.z * q.z;
            float zw = q.z * q.w;
            float xy = q.x * q.y;
            float xz = q.x * q.z;
            float yw = q.y * q.w;
            float yz = q.y * q.z;
            float xw = q.x * q.w;
            dest.putFloat(position, w2 + x2 - z2 - y2).putFloat(position + 4, xy + zw + zw + xy).putFloat(position + 8, xz - yw + xz - yw).putFloat(position + 12, 0.0f).putFloat(position + 16, -zw + xy - zw + xy).putFloat(position + 20, y2 - z2 + w2 - x2).putFloat(position + 24, yz + yz + xw + xw).putFloat(position + 28, 0.0f).putFloat(position + 32, yw + xz + xz + yw).putFloat(position + 36, yz + yz - xw - xw).putFloat(position + 40, z2 - y2 - x2 + w2).putFloat(position + 44, 0.0f).putLong(position + 48, 0L).putLong(position + 56, 4575657221408423936L);
        }

        public void putMatrix4f(Quaternionf q, int position, FloatBuffer dest) {
            float w2 = q.w * q.w;
            float x2 = q.x * q.x;
            float y2 = q.y * q.y;
            float z2 = q.z * q.z;
            float zw = q.z * q.w;
            float xy = q.x * q.y;
            float xz = q.x * q.z;
            float yw = q.y * q.w;
            float yz = q.y * q.z;
            float xw = q.x * q.w;
            dest.put(position, w2 + x2 - z2 - y2).put(position + 1, xy + zw + zw + xy).put(position + 2, xz - yw + xz - yw).put(position + 3, 0.0f).put(position + 4, -zw + xy - zw + xy).put(position + 5, y2 - z2 + w2 - x2).put(position + 6, yz + yz + xw + xw).put(position + 7, 0.0f).put(position + 8, yw + xz + xz + yw).put(position + 9, yz + yz - xw - xw).put(position + 10, z2 - y2 - x2 + w2).put(position + 11, 0.0f).put(position + 12, 0.0f).put(position + 13, 0.0f).put(position + 14, 0.0f).put(position + 15, 1.0f);
        }

        public void putMatrix4x3f(Quaternionf q, int position, ByteBuffer dest) {
            float w2 = q.w * q.w;
            float x2 = q.x * q.x;
            float y2 = q.y * q.y;
            float z2 = q.z * q.z;
            float zw = q.z * q.w;
            float xy = q.x * q.y;
            float xz = q.x * q.z;
            float yw = q.y * q.w;
            float yz = q.y * q.z;
            float xw = q.x * q.w;
            dest.putFloat(position, w2 + x2 - z2 - y2).putFloat(position + 4, xy + zw + zw + xy).putFloat(position + 8, xz - yw + xz - yw).putFloat(position + 12, -zw + xy - zw + xy).putFloat(position + 16, y2 - z2 + w2 - x2).putFloat(position + 20, yz + yz + xw + xw).putFloat(position + 24, yw + xz + xz + yw).putFloat(position + 28, yz + yz - xw - xw).putFloat(position + 32, z2 - y2 - x2 + w2).putLong(position + 36, 0L).putFloat(position + 44, 0.0f);
        }

        public void putMatrix4x3f(Quaternionf q, int position, FloatBuffer dest) {
            float w2 = q.w * q.w;
            float x2 = q.x * q.x;
            float y2 = q.y * q.y;
            float z2 = q.z * q.z;
            float zw = q.z * q.w;
            float xy = q.x * q.y;
            float xz = q.x * q.z;
            float yw = q.y * q.w;
            float yz = q.y * q.z;
            float xw = q.x * q.w;
            dest.put(position, w2 + x2 - z2 - y2).put(position + 1, xy + zw + zw + xy).put(position + 2, xz - yw + xz - yw).put(position + 3, -zw + xy - zw + xy).put(position + 4, y2 - z2 + w2 - x2).put(position + 5, yz + yz + xw + xw).put(position + 6, yw + xz + xz + yw).put(position + 7, yz + yz - xw - xw).put(position + 8, z2 - y2 - x2 + w2).put(position + 9, 0.0f).put(position + 10, 0.0f).put(position + 11, 0.0f);
        }
    }
}

