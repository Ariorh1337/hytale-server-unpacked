/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.DBOptions;
import org.rocksdb.InfoLogLevel;
import org.rocksdb.LoggerInterface;
import org.rocksdb.LoggerType;
import org.rocksdb.Options;
import org.rocksdb.RocksCallbackObject;

public abstract class Logger
extends RocksCallbackObject
implements LoggerInterface {
    @Deprecated
    public Logger(Options options) {
        this(options.infoLogLevel());
    }

    @Deprecated
    public Logger(DBOptions dBOptions) {
        this(dBOptions.infoLogLevel());
    }

    public Logger(InfoLogLevel infoLogLevel) {
        super(infoLogLevel.getValue());
    }

    @Override
    protected long initializeNative(long ... lArray) {
        if (lArray.length == 1) {
            return this.newLogger(lArray[0]);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void setInfoLogLevel(InfoLogLevel infoLogLevel) {
        this.setInfoLogLevel(this.nativeHandle_, infoLogLevel.getValue());
    }

    @Override
    public InfoLogLevel infoLogLevel() {
        return InfoLogLevel.getInfoLogLevel(this.infoLogLevel(this.nativeHandle_));
    }

    @Override
    public long getNativeHandle() {
        return this.nativeHandle_;
    }

    @Override
    public final LoggerType getLoggerType() {
        return LoggerType.JAVA_IMPLEMENTATION;
    }

    protected abstract void log(InfoLogLevel var1, String var2);

    protected native long newLogger(long var1);

    protected native void setInfoLogLevel(long var1, byte var3);

    protected native byte infoLogLevel(long var1);

    @Override
    protected void disposeInternal() {
        this.disposeInternal(this.nativeHandle_);
    }

    private native void disposeInternal(long var1);
}

