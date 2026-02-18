/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.InfoLogLevel;
import org.rocksdb.LoggerType;

public interface LoggerInterface {
    public void setInfoLogLevel(InfoLogLevel var1);

    public InfoLogLevel infoLogLevel();

    public long getNativeHandle();

    public LoggerType getLoggerType();
}

