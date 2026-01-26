/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.Breadcrumb;
import io.sentry.IScope;
import io.sentry.SentryLevel;
import io.sentry.SpanContext;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import java.util.Collection;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IScopeObserver {
    public void setUser(@Nullable User var1);

    public void addBreadcrumb(@NotNull Breadcrumb var1);

    public void setBreadcrumbs(@NotNull Collection<Breadcrumb> var1);

    public void setTag(@NotNull String var1, @NotNull String var2);

    public void removeTag(@NotNull String var1);

    public void setTags(@NotNull @NotNull Map<String, @NotNull String> var1);

    public void setExtra(@NotNull String var1, @NotNull String var2);

    public void removeExtra(@NotNull String var1);

    public void setExtras(@NotNull @NotNull Map<String, @NotNull Object> var1);

    public void setRequest(@Nullable Request var1);

    public void setFingerprint(@NotNull Collection<String> var1);

    public void setLevel(@Nullable SentryLevel var1);

    public void setContexts(@NotNull Contexts var1);

    public void setTransaction(@Nullable String var1);

    public void setTrace(@Nullable SpanContext var1, @NotNull IScope var2);

    public void setReplayId(@NotNull SentryId var1);
}

