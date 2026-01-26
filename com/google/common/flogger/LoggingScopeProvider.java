/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger;

import com.google.common.flogger.LoggingScope;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public interface LoggingScopeProvider {
    @NullableDecl
    public LoggingScope getCurrentScope();
}

