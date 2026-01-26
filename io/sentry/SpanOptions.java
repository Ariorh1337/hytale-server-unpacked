/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.ScopeBindingMode;
import io.sentry.SentryDate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpanOptions {
    @Nullable
    private SentryDate startTimestamp = null;
    @NotNull
    private ScopeBindingMode scopeBindingMode = ScopeBindingMode.AUTO;
    private boolean trimStart = false;
    private boolean trimEnd = false;
    private boolean isIdle = false;
    @Nullable
    protected String origin = "manual";

    @Nullable
    public SentryDate getStartTimestamp() {
        return this.startTimestamp;
    }

    public void setStartTimestamp(@Nullable SentryDate startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public boolean isTrimStart() {
        return this.trimStart;
    }

    public boolean isTrimEnd() {
        return this.trimEnd;
    }

    public boolean isIdle() {
        return this.isIdle;
    }

    public void setTrimStart(boolean trimStart) {
        this.trimStart = trimStart;
    }

    public void setTrimEnd(boolean trimEnd) {
        this.trimEnd = trimEnd;
    }

    public void setIdle(boolean idle) {
        this.isIdle = idle;
    }

    @Nullable
    public String getOrigin() {
        return this.origin;
    }

    public void setOrigin(@Nullable String origin) {
        this.origin = origin;
    }

    @NotNull
    public ScopeBindingMode getScopeBindingMode() {
        return this.scopeBindingMode;
    }

    public void setScopeBindingMode(@NotNull ScopeBindingMode scopeBindingMode) {
        this.scopeBindingMode = scopeBindingMode;
    }
}

