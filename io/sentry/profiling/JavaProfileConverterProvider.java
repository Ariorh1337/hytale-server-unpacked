/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.profiling;

import io.sentry.IProfileConverter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface JavaProfileConverterProvider {
    @NotNull
    public IProfileConverter getProfileConverter();
}

