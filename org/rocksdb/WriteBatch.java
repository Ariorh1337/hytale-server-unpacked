/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.ByteBuffer;
import org.rocksdb.AbstractWriteBatch;
import org.rocksdb.RocksCallbackObject;
import org.rocksdb.RocksDBException;

public class WriteBatch
extends AbstractWriteBatch {
    public WriteBatch() {
        this(0);
    }

    public WriteBatch(int n) {
        super(WriteBatch.newWriteBatch(n));
    }

    public WriteBatch(byte[] byArray) {
        super(WriteBatch.newWriteBatch(byArray, byArray.length));
    }

    public void iterate(Handler handler) throws RocksDBException {
        WriteBatch.iterate(this.nativeHandle_, handler.nativeHandle_);
    }

    public byte[] data() throws RocksDBException {
        return WriteBatch.data(this.nativeHandle_);
    }

    public long getDataSize() {
        return WriteBatch.getDataSize(this.nativeHandle_);
    }

    public boolean hasPut() {
        return WriteBatch.hasPut(this.nativeHandle_);
    }

    public boolean hasDelete() {
        return WriteBatch.hasDelete(this.nativeHandle_);
    }

    public boolean hasSingleDelete() {
        return WriteBatch.hasSingleDelete(this.nativeHandle_);
    }

    public boolean hasDeleteRange() {
        return WriteBatch.hasDeleteRange(this.nativeHandle_);
    }

    public boolean hasMerge() {
        return WriteBatch.hasMerge(this.nativeHandle_);
    }

    public boolean hasBeginPrepare() {
        return WriteBatch.hasBeginPrepare(this.nativeHandle_);
    }

    public boolean hasEndPrepare() {
        return WriteBatch.hasEndPrepare(this.nativeHandle_);
    }

    public boolean hasCommit() {
        return WriteBatch.hasCommit(this.nativeHandle_);
    }

    public boolean hasRollback() {
        return WriteBatch.hasRollback(this.nativeHandle_);
    }

    @Override
    public WriteBatch getWriteBatch() {
        return this;
    }

    public void markWalTerminationPoint() {
        WriteBatch.markWalTerminationPoint(this.nativeHandle_);
    }

    public SavePoint getWalTerminationPoint() {
        return WriteBatch.getWalTerminationPoint(this.nativeHandle_);
    }

    @Override
    WriteBatch getWriteBatch(long l) {
        return this;
    }

    WriteBatch(long l) {
        this(l, false);
    }

    WriteBatch(long l, boolean bl) {
        super(l);
        if (!bl) {
            this.disOwnNativeHandle();
        }
    }

    @Override
    protected final void disposeInternal(long l) {
        WriteBatch.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    @Override
    final int count0(long l) {
        return WriteBatch.count0Jni(l);
    }

    private static native int count0Jni(long var0);

    @Override
    final void put(long l, byte[] byArray, int n, byte[] byArray2, int n2) {
        WriteBatch.putJni(l, byArray, n, byArray2, n2);
    }

    private static native void putJni(long var0, byte[] var2, int var3, byte[] var4, int var5);

    @Override
    final void put(long l, byte[] byArray, int n, byte[] byArray2, int n2, long l2) {
        WriteBatch.putJni(l, byArray, n, byArray2, n2, l2);
    }

    private static native void putJni(long var0, byte[] var2, int var3, byte[] var4, int var5, long var6);

    @Override
    final void putDirect(long l, ByteBuffer byteBuffer, int n, int n2, ByteBuffer byteBuffer2, int n3, int n4, long l2) {
        WriteBatch.putDirectJni(l, byteBuffer, n, n2, byteBuffer2, n3, n4, l2);
    }

    private static native void putDirectJni(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7, long var8);

    @Override
    final void merge(long l, byte[] byArray, int n, byte[] byArray2, int n2) {
        WriteBatch.mergeJni(l, byArray, n, byArray2, n2);
    }

    private static native void mergeJni(long var0, byte[] var2, int var3, byte[] var4, int var5);

    @Override
    final void merge(long l, byte[] byArray, int n, byte[] byArray2, int n2, long l2) {
        WriteBatch.mergeJni(l, byArray, n, byArray2, n2, l2);
    }

    private static native void mergeJni(long var0, byte[] var2, int var3, byte[] var4, int var5, long var6);

    @Override
    final void delete(long l, byte[] byArray, int n) throws RocksDBException {
        WriteBatch.deleteJni(l, byArray, n);
    }

    private static native void deleteJni(long var0, byte[] var2, int var3) throws RocksDBException;

    @Override
    final void delete(long l, byte[] byArray, int n, long l2) throws RocksDBException {
        WriteBatch.deleteJni(l, byArray, n, l2);
    }

    private static native void deleteJni(long var0, byte[] var2, int var3, long var4) throws RocksDBException;

    @Override
    final void singleDelete(long l, byte[] byArray, int n) throws RocksDBException {
        WriteBatch.singleDeleteJni(l, byArray, n);
    }

    private static native void singleDeleteJni(long var0, byte[] var2, int var3) throws RocksDBException;

    @Override
    final void singleDelete(long l, byte[] byArray, int n, long l2) throws RocksDBException {
        WriteBatch.singleDeleteJni(l, byArray, n, l2);
    }

    private static native void singleDeleteJni(long var0, byte[] var2, int var3, long var4) throws RocksDBException;

    @Override
    final void deleteDirect(long l, ByteBuffer byteBuffer, int n, int n2, long l2) throws RocksDBException {
        WriteBatch.deleteDirectJni(l, byteBuffer, n, n2, l2);
    }

    private static native void deleteDirectJni(long var0, ByteBuffer var2, int var3, int var4, long var5) throws RocksDBException;

    @Override
    final void deleteRange(long l, byte[] byArray, int n, byte[] byArray2, int n2) {
        WriteBatch.deleteRangeJni(l, byArray, n, byArray2, n2);
    }

    private static native void deleteRangeJni(long var0, byte[] var2, int var3, byte[] var4, int var5);

    @Override
    final void deleteRange(long l, byte[] byArray, int n, byte[] byArray2, int n2, long l2) {
        WriteBatch.deleteRangeJni(l, byArray, n, byArray2, n2, l2);
    }

    private static native void deleteRangeJni(long var0, byte[] var2, int var3, byte[] var4, int var5, long var6);

    @Override
    final void putLogData(long l, byte[] byArray, int n) throws RocksDBException {
        WriteBatch.putLogDataJni(l, byArray, n);
    }

    private static native void putLogDataJni(long var0, byte[] var2, int var3) throws RocksDBException;

    @Override
    final void clear0(long l) {
        WriteBatch.clear0Jni(l);
    }

    private static native void clear0Jni(long var0);

    @Override
    final void setSavePoint0(long l) {
        WriteBatch.setSavePoint0Jni(l);
    }

    private static native void setSavePoint0Jni(long var0);

    @Override
    final void rollbackToSavePoint0(long l) {
        WriteBatch.rollbackToSavePoint0Jni(l);
    }

    private static native void rollbackToSavePoint0Jni(long var0);

    @Override
    final void popSavePoint(long l) throws RocksDBException {
        WriteBatch.popSavePointJni(l);
    }

    private static native void popSavePointJni(long var0) throws RocksDBException;

    @Override
    final void setMaxBytes(long l, long l2) {
        WriteBatch.setMaxBytesJni(l, l2);
    }

    private static native void setMaxBytesJni(long var0, long var2);

    private static native long newWriteBatch(int var0);

    private static native long newWriteBatch(byte[] var0, int var1);

    private static native void iterate(long var0, long var2) throws RocksDBException;

    private static native byte[] data(long var0) throws RocksDBException;

    private static native long getDataSize(long var0);

    private static native boolean hasPut(long var0);

    private static native boolean hasDelete(long var0);

    private static native boolean hasSingleDelete(long var0);

    private static native boolean hasDeleteRange(long var0);

    private static native boolean hasMerge(long var0);

    private static native boolean hasBeginPrepare(long var0);

    private static native boolean hasEndPrepare(long var0);

    private static native boolean hasCommit(long var0);

    private static native boolean hasRollback(long var0);

    private static native void markWalTerminationPoint(long var0);

    private static native SavePoint getWalTerminationPoint(long var0);

    public static class SavePoint {
        private long size;
        private long count;
        private long contentFlags;

        public SavePoint(long l, long l2, long l3) {
            this.size = l;
            this.count = l2;
            this.contentFlags = l3;
        }

        public void clear() {
            this.size = 0L;
            this.count = 0L;
            this.contentFlags = 0L;
        }

        public long getSize() {
            return this.size;
        }

        public long getCount() {
            return this.count;
        }

        public long getContentFlags() {
            return this.contentFlags;
        }

        public boolean isCleared() {
            return (this.size | this.count | this.contentFlags) == 0L;
        }
    }

    public static abstract class Handler
    extends RocksCallbackObject {
        public Handler() {
            super(0L);
        }

        @Override
        protected long initializeNative(long ... lArray) {
            return this.createNewHandler0();
        }

        public abstract void put(int var1, byte[] var2, byte[] var3) throws RocksDBException;

        public abstract void put(byte[] var1, byte[] var2);

        public abstract void merge(int var1, byte[] var2, byte[] var3) throws RocksDBException;

        public abstract void merge(byte[] var1, byte[] var2);

        public abstract void delete(int var1, byte[] var2) throws RocksDBException;

        public abstract void delete(byte[] var1);

        public abstract void singleDelete(int var1, byte[] var2) throws RocksDBException;

        public abstract void singleDelete(byte[] var1);

        public abstract void deleteRange(int var1, byte[] var2, byte[] var3) throws RocksDBException;

        public abstract void deleteRange(byte[] var1, byte[] var2);

        public abstract void logData(byte[] var1);

        public abstract void putBlobIndex(int var1, byte[] var2, byte[] var3) throws RocksDBException;

        public abstract void markBeginPrepare() throws RocksDBException;

        public abstract void markEndPrepare(byte[] var1) throws RocksDBException;

        public abstract void markNoop(boolean var1) throws RocksDBException;

        public abstract void markRollback(byte[] var1) throws RocksDBException;

        public abstract void markCommit(byte[] var1) throws RocksDBException;

        public abstract void markCommitWithTimestamp(byte[] var1, byte[] var2) throws RocksDBException;

        public boolean shouldContinue() {
            return true;
        }

        private native long createNewHandler0();
    }
}

