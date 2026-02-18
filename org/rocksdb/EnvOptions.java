/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.DBOptions;
import org.rocksdb.RateLimiter;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksObject;

public class EnvOptions
extends RocksObject {
    private RateLimiter rateLimiter;

    public EnvOptions() {
        super(EnvOptions.newEnvOptionsInstance());
    }

    public EnvOptions(DBOptions dBOptions) {
        super(EnvOptions.newEnvOptions(dBOptions.nativeHandle_));
    }

    public EnvOptions setUseMmapReads(boolean bl) {
        EnvOptions.setUseMmapReads(this.nativeHandle_, bl);
        return this;
    }

    public boolean useMmapReads() {
        assert (this.isOwningHandle());
        return EnvOptions.useMmapReads(this.nativeHandle_);
    }

    public EnvOptions setUseMmapWrites(boolean bl) {
        EnvOptions.setUseMmapWrites(this.nativeHandle_, bl);
        return this;
    }

    public boolean useMmapWrites() {
        assert (this.isOwningHandle());
        return EnvOptions.useMmapWrites(this.nativeHandle_);
    }

    public EnvOptions setUseDirectReads(boolean bl) {
        EnvOptions.setUseDirectReads(this.nativeHandle_, bl);
        return this;
    }

    public boolean useDirectReads() {
        assert (this.isOwningHandle());
        return EnvOptions.useDirectReads(this.nativeHandle_);
    }

    public EnvOptions setUseDirectWrites(boolean bl) {
        EnvOptions.setUseDirectWrites(this.nativeHandle_, bl);
        return this;
    }

    public boolean useDirectWrites() {
        assert (this.isOwningHandle());
        return EnvOptions.useDirectWrites(this.nativeHandle_);
    }

    public EnvOptions setAllowFallocate(boolean bl) {
        EnvOptions.setAllowFallocate(this.nativeHandle_, bl);
        return this;
    }

    public boolean allowFallocate() {
        assert (this.isOwningHandle());
        return EnvOptions.allowFallocate(this.nativeHandle_);
    }

    public EnvOptions setSetFdCloexec(boolean bl) {
        EnvOptions.setSetFdCloexec(this.nativeHandle_, bl);
        return this;
    }

    public boolean setFdCloexec() {
        assert (this.isOwningHandle());
        return EnvOptions.setFdCloexec(this.nativeHandle_);
    }

    public EnvOptions setBytesPerSync(long l) {
        EnvOptions.setBytesPerSync(this.nativeHandle_, l);
        return this;
    }

    public long bytesPerSync() {
        assert (this.isOwningHandle());
        return EnvOptions.bytesPerSync(this.nativeHandle_);
    }

    public EnvOptions setFallocateWithKeepSize(boolean bl) {
        EnvOptions.setFallocateWithKeepSize(this.nativeHandle_, bl);
        return this;
    }

    public boolean fallocateWithKeepSize() {
        assert (this.isOwningHandle());
        return EnvOptions.fallocateWithKeepSize(this.nativeHandle_);
    }

    public EnvOptions setCompactionReadaheadSize(long l) {
        EnvOptions.setCompactionReadaheadSize(this.nativeHandle_, l);
        return this;
    }

    public long compactionReadaheadSize() {
        assert (this.isOwningHandle());
        return EnvOptions.compactionReadaheadSize(this.nativeHandle_);
    }

    public EnvOptions setWritableFileMaxBufferSize(long l) {
        EnvOptions.setWritableFileMaxBufferSize(this.nativeHandle_, l);
        return this;
    }

    public long writableFileMaxBufferSize() {
        assert (this.isOwningHandle());
        return EnvOptions.writableFileMaxBufferSize(this.nativeHandle_);
    }

    public EnvOptions setRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
        EnvOptions.setRateLimiter(this.nativeHandle_, rateLimiter.nativeHandle_);
        return this;
    }

    public RateLimiter rateLimiter() {
        assert (this.isOwningHandle());
        return this.rateLimiter;
    }

    private static long newEnvOptionsInstance() {
        RocksDB.loadLibrary();
        return EnvOptions.newEnvOptions();
    }

    private static native long newEnvOptions();

    private static native long newEnvOptions(long var0);

    @Override
    protected final void disposeInternal(long l) {
        EnvOptions.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native void setUseMmapReads(long var0, boolean var2);

    private static native boolean useMmapReads(long var0);

    private static native void setUseMmapWrites(long var0, boolean var2);

    private static native boolean useMmapWrites(long var0);

    private static native void setUseDirectReads(long var0, boolean var2);

    private static native boolean useDirectReads(long var0);

    private static native void setUseDirectWrites(long var0, boolean var2);

    private static native boolean useDirectWrites(long var0);

    private static native void setAllowFallocate(long var0, boolean var2);

    private static native boolean allowFallocate(long var0);

    private static native void setSetFdCloexec(long var0, boolean var2);

    private static native boolean setFdCloexec(long var0);

    private static native void setBytesPerSync(long var0, long var2);

    private static native long bytesPerSync(long var0);

    private static native void setFallocateWithKeepSize(long var0, boolean var2);

    private static native boolean fallocateWithKeepSize(long var0);

    private static native void setCompactionReadaheadSize(long var0, long var2);

    private static native long compactionReadaheadSize(long var0);

    private static native void setWritableFileMaxBufferSize(long var0, long var2);

    private static native long writableFileMaxBufferSize(long var0);

    private static native void setRateLimiter(long var0, long var2);
}

