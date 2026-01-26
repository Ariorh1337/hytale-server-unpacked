/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger;

import com.google.common.flogger.StackSize;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public final class LogSiteStackTrace
extends Exception {
    LogSiteStackTrace(@NullableDecl Throwable cause, StackSize stackSize, StackTraceElement[] syntheticStackTrace) {
        super(stackSize.toString(), cause);
        this.setStackTrace(syntheticStackTrace);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}

