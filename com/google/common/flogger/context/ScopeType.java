/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger.context;

import com.google.common.flogger.LoggingScope;
import com.google.common.flogger.LoggingScopeProvider;
import com.google.common.flogger.context.ContextDataProvider;
import com.google.common.flogger.util.Checks;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public final class ScopeType
implements LoggingScopeProvider {
    public static final ScopeType REQUEST = ScopeType.create("request");
    private final String name;

    public static ScopeType create(String name) {
        return new ScopeType(name);
    }

    private ScopeType(String name) {
        this.name = Checks.checkNotNull(name, "name");
    }

    LoggingScope newScope() {
        return LoggingScope.create(this.name);
    }

    @Override
    @NullableDecl
    public LoggingScope getCurrentScope() {
        return ContextDataProvider.getInstance().getScope(this);
    }
}

