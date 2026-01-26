/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.protocol.SdkVersion;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IOptionsObserver {
    public void setRelease(@Nullable String var1);

    public void setProguardUuid(@Nullable String var1);

    public void setSdkVersion(@Nullable SdkVersion var1);

    public void setEnvironment(@Nullable String var1);

    public void setDist(@Nullable String var1);

    public void setTags(@NotNull @NotNull Map<String, @NotNull String> var1);

    public void setReplayErrorSampleRate(@Nullable Double var1);
}

