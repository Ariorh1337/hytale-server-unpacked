/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb.util;

import org.rocksdb.InfoLogLevel;
import org.rocksdb.LoggerInterface;
import org.rocksdb.LoggerType;
import org.rocksdb.RocksObject;

public class StdErrLogger
extends RocksObject
implements LoggerInterface {
    public StdErrLogger(InfoLogLevel infoLogLevel) {
        this(infoLogLevel, null);
    }

    public StdErrLogger(InfoLogLevel infoLogLevel, String string) {
        super(StdErrLogger.newStdErrLogger(infoLogLevel.getValue(), string));
    }

    @Override
    public void setInfoLogLevel(InfoLogLevel infoLogLevel) {
        StdErrLogger.setInfoLogLevel(this.nativeHandle_, infoLogLevel.getValue());
    }

    @Override
    public InfoLogLevel infoLogLevel() {
        return InfoLogLevel.getInfoLogLevel(StdErrLogger.infoLogLevel(this.nativeHandle_));
    }

    @Override
    public LoggerType getLoggerType() {
        return LoggerType.STDERR_IMPLEMENTATION;
    }

    private static native long newStdErrLogger(byte var0, String var1);

    private static native void setInfoLogLevel(long var0, byte var2);

    private static native byte infoLogLevel(long var0);

    @Override
    protected native void disposeInternal(long var1);
}

