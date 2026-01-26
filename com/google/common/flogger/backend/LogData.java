/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger.backend;

import com.google.common.flogger.LogSite;
import com.google.common.flogger.backend.Metadata;
import com.google.common.flogger.backend.TemplateContext;
import java.util.logging.Level;

public interface LogData {
    public Level getLevel();

    @Deprecated
    public long getTimestampMicros();

    public long getTimestampNanos();

    public String getLoggerName();

    public LogSite getLogSite();

    public Metadata getMetadata();

    public boolean wasForced();

    public TemplateContext getTemplateContext();

    public Object[] getArguments();

    public Object getLiteralArgument();
}

