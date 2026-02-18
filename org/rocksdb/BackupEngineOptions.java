/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.io.File;
import org.rocksdb.Env;
import org.rocksdb.Logger;
import org.rocksdb.RateLimiter;
import org.rocksdb.RocksObject;

public class BackupEngineOptions
extends RocksObject {
    private Env backupEnv = null;
    private Logger infoLog = null;
    private RateLimiter backupRateLimiter = null;
    private RateLimiter restoreRateLimiter = null;

    public BackupEngineOptions(String string) {
        super(BackupEngineOptions.newBackupEngineOptions(BackupEngineOptions.ensureWritableFile(string)));
    }

    private static String ensureWritableFile(String string) {
        File file;
        File file2 = file = string == null ? null : new File(string);
        if (file == null || !file.isDirectory() || !file.canWrite()) {
            throw new IllegalArgumentException("Illegal path provided.");
        }
        return string;
    }

    public String backupDir() {
        assert (this.isOwningHandle());
        return BackupEngineOptions.backupDir(this.nativeHandle_);
    }

    public BackupEngineOptions setBackupEnv(Env env) {
        assert (this.isOwningHandle());
        BackupEngineOptions.setBackupEnv(this.nativeHandle_, env.nativeHandle_);
        this.backupEnv = env;
        return this;
    }

    public Env backupEnv() {
        return this.backupEnv;
    }

    public BackupEngineOptions setShareTableFiles(boolean bl) {
        assert (this.isOwningHandle());
        BackupEngineOptions.setShareTableFiles(this.nativeHandle_, bl);
        return this;
    }

    public boolean shareTableFiles() {
        assert (this.isOwningHandle());
        return BackupEngineOptions.shareTableFiles(this.nativeHandle_);
    }

    public BackupEngineOptions setInfoLog(Logger logger) {
        assert (this.isOwningHandle());
        BackupEngineOptions.setInfoLog(this.nativeHandle_, logger.nativeHandle_);
        this.infoLog = logger;
        return this;
    }

    public Logger infoLog() {
        return this.infoLog;
    }

    public BackupEngineOptions setSync(boolean bl) {
        assert (this.isOwningHandle());
        BackupEngineOptions.setSync(this.nativeHandle_, bl);
        return this;
    }

    public boolean sync() {
        assert (this.isOwningHandle());
        return BackupEngineOptions.sync(this.nativeHandle_);
    }

    public BackupEngineOptions setDestroyOldData(boolean bl) {
        assert (this.isOwningHandle());
        BackupEngineOptions.setDestroyOldData(this.nativeHandle_, bl);
        return this;
    }

    public boolean destroyOldData() {
        assert (this.isOwningHandle());
        return BackupEngineOptions.destroyOldData(this.nativeHandle_);
    }

    public BackupEngineOptions setBackupLogFiles(boolean bl) {
        assert (this.isOwningHandle());
        BackupEngineOptions.setBackupLogFiles(this.nativeHandle_, bl);
        return this;
    }

    public boolean backupLogFiles() {
        assert (this.isOwningHandle());
        return BackupEngineOptions.backupLogFiles(this.nativeHandle_);
    }

    public BackupEngineOptions setBackupRateLimit(long l) {
        assert (this.isOwningHandle());
        BackupEngineOptions.setBackupRateLimit(this.nativeHandle_, l <= 0L ? 0L : l);
        return this;
    }

    public long backupRateLimit() {
        assert (this.isOwningHandle());
        return BackupEngineOptions.backupRateLimit(this.nativeHandle_);
    }

    public BackupEngineOptions setBackupRateLimiter(RateLimiter rateLimiter) {
        assert (this.isOwningHandle());
        BackupEngineOptions.setBackupRateLimiter(this.nativeHandle_, rateLimiter.nativeHandle_);
        this.backupRateLimiter = rateLimiter;
        return this;
    }

    public RateLimiter backupRateLimiter() {
        assert (this.isOwningHandle());
        return this.backupRateLimiter;
    }

    public BackupEngineOptions setRestoreRateLimit(long l) {
        assert (this.isOwningHandle());
        BackupEngineOptions.setRestoreRateLimit(this.nativeHandle_, l <= 0L ? 0L : l);
        return this;
    }

    public long restoreRateLimit() {
        assert (this.isOwningHandle());
        return BackupEngineOptions.restoreRateLimit(this.nativeHandle_);
    }

    public BackupEngineOptions setRestoreRateLimiter(RateLimiter rateLimiter) {
        assert (this.isOwningHandle());
        BackupEngineOptions.setRestoreRateLimiter(this.nativeHandle_, rateLimiter.nativeHandle_);
        this.restoreRateLimiter = rateLimiter;
        return this;
    }

    public RateLimiter restoreRateLimiter() {
        assert (this.isOwningHandle());
        return this.restoreRateLimiter;
    }

    public BackupEngineOptions setShareFilesWithChecksum(boolean bl) {
        assert (this.isOwningHandle());
        BackupEngineOptions.setShareFilesWithChecksum(this.nativeHandle_, bl);
        return this;
    }

    public boolean shareFilesWithChecksum() {
        assert (this.isOwningHandle());
        return BackupEngineOptions.shareFilesWithChecksum(this.nativeHandle_);
    }

    public BackupEngineOptions setMaxBackgroundOperations(int n) {
        assert (this.isOwningHandle());
        BackupEngineOptions.setMaxBackgroundOperations(this.nativeHandle_, n);
        return this;
    }

    public int maxBackgroundOperations() {
        assert (this.isOwningHandle());
        return BackupEngineOptions.maxBackgroundOperations(this.nativeHandle_);
    }

    public BackupEngineOptions setCallbackTriggerIntervalSize(long l) {
        assert (this.isOwningHandle());
        BackupEngineOptions.setCallbackTriggerIntervalSize(this.nativeHandle_, l);
        return this;
    }

    public long callbackTriggerIntervalSize() {
        assert (this.isOwningHandle());
        return BackupEngineOptions.callbackTriggerIntervalSize(this.nativeHandle_);
    }

    private static native long newBackupEngineOptions(String var0);

    private static native String backupDir(long var0);

    private static native void setBackupEnv(long var0, long var2);

    private static native void setShareTableFiles(long var0, boolean var2);

    private static native boolean shareTableFiles(long var0);

    private static native void setInfoLog(long var0, long var2);

    private static native void setSync(long var0, boolean var2);

    private static native boolean sync(long var0);

    private static native void setDestroyOldData(long var0, boolean var2);

    private static native boolean destroyOldData(long var0);

    private static native void setBackupLogFiles(long var0, boolean var2);

    private static native boolean backupLogFiles(long var0);

    private static native void setBackupRateLimit(long var0, long var2);

    private static native long backupRateLimit(long var0);

    private static native void setBackupRateLimiter(long var0, long var2);

    private static native void setRestoreRateLimit(long var0, long var2);

    private static native long restoreRateLimit(long var0);

    private static native void setRestoreRateLimiter(long var0, long var2);

    private static native void setShareFilesWithChecksum(long var0, boolean var2);

    private static native boolean shareFilesWithChecksum(long var0);

    private static native void setMaxBackgroundOperations(long var0, int var2);

    private static native int maxBackgroundOperations(long var0);

    private static native void setCallbackTriggerIntervalSize(long var0, long var2);

    private static native long callbackTriggerIntervalSize(long var0);

    @Override
    protected final void disposeInternal(long l) {
        BackupEngineOptions.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

