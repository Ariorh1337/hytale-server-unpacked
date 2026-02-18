/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Map;
import org.rocksdb.Env;
import org.rocksdb.Logger;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksObject;

public final class SstFileManager
extends RocksObject {
    public static final long RATE_BYTES_PER_SEC_DEFAULT = 0L;
    public static final boolean DELETE_EXISTING_TRASH_DEFAULT = true;
    public static final double MAX_TRASH_DB_RATION_DEFAULT = 0.25;
    public static final long BYTES_MAX_DELETE_CHUNK_DEFAULT = 0x4000000L;

    public SstFileManager(Env env) throws RocksDBException {
        this(env, null);
    }

    public SstFileManager(Env env, Logger logger) throws RocksDBException {
        this(env, logger, 0L);
    }

    public SstFileManager(Env env, Logger logger, long l) throws RocksDBException {
        this(env, logger, l, 0.25);
    }

    public SstFileManager(Env env, Logger logger, long l, double d) throws RocksDBException {
        this(env, logger, l, d, 0x4000000L);
    }

    public SstFileManager(Env env, Logger logger, long l, double d, long l2) throws RocksDBException {
        super(SstFileManager.newSstFileManager(env.nativeHandle_, logger != null ? logger.nativeHandle_ : 0L, l, d, l2));
    }

    public void setMaxAllowedSpaceUsage(long l) {
        SstFileManager.setMaxAllowedSpaceUsage(this.nativeHandle_, l);
    }

    public void setCompactionBufferSize(long l) {
        SstFileManager.setCompactionBufferSize(this.nativeHandle_, l);
    }

    public boolean isMaxAllowedSpaceReached() {
        return SstFileManager.isMaxAllowedSpaceReached(this.nativeHandle_);
    }

    public boolean isMaxAllowedSpaceReachedIncludingCompactions() {
        return SstFileManager.isMaxAllowedSpaceReachedIncludingCompactions(this.nativeHandle_);
    }

    public long getTotalSize() {
        return SstFileManager.getTotalSize(this.nativeHandle_);
    }

    public Map<String, Long> getTrackedFiles() {
        return SstFileManager.getTrackedFiles(this.nativeHandle_);
    }

    public long getDeleteRateBytesPerSecond() {
        return SstFileManager.getDeleteRateBytesPerSecond(this.nativeHandle_);
    }

    public void setDeleteRateBytesPerSecond(long l) {
        SstFileManager.setDeleteRateBytesPerSecond(this.nativeHandle_, l);
    }

    public double getMaxTrashDBRatio() {
        return SstFileManager.getMaxTrashDBRatio(this.nativeHandle_);
    }

    public void setMaxTrashDBRatio(double d) {
        SstFileManager.setMaxTrashDBRatio(this.nativeHandle_, d);
    }

    private static native long newSstFileManager(long var0, long var2, long var4, double var6, long var8) throws RocksDBException;

    private static native void setMaxAllowedSpaceUsage(long var0, long var2);

    private static native void setCompactionBufferSize(long var0, long var2);

    private static native boolean isMaxAllowedSpaceReached(long var0);

    private static native boolean isMaxAllowedSpaceReachedIncludingCompactions(long var0);

    private static native long getTotalSize(long var0);

    private static native Map<String, Long> getTrackedFiles(long var0);

    private static native long getDeleteRateBytesPerSecond(long var0);

    private static native void setDeleteRateBytesPerSecond(long var0, long var2);

    private static native double getMaxTrashDBRatio(long var0);

    private static native void setMaxTrashDBRatio(long var0, double var2);

    @Override
    protected void disposeInternal(long l) {
        SstFileManager.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

