/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface JsonUnknown {
    @Nullable
    public Map<String, Object> getUnknown();

    public void setUnknown(@Nullable Map<String, Object> var1);
}

