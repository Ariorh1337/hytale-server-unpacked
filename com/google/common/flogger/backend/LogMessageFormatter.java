/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger.backend;

import com.google.common.flogger.backend.LogData;
import com.google.common.flogger.backend.MetadataProcessor;

public abstract class LogMessageFormatter {
    public String format(LogData logData, MetadataProcessor metadata) {
        return this.append(logData, metadata, new StringBuilder()).toString();
    }

    public abstract StringBuilder append(LogData var1, MetadataProcessor var2, StringBuilder var3);
}

