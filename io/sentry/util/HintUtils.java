/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import io.sentry.Hint;
import io.sentry.ILogger;
import io.sentry.hints.ApplyScopeData;
import io.sentry.hints.Backfillable;
import io.sentry.hints.Cached;
import io.sentry.hints.EventDropReason;
import io.sentry.util.LogUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class HintUtils {
    private HintUtils() {
    }

    public static void setIsFromHybridSdk(@NotNull Hint hint, @NotNull String sdkName) {
        if (sdkName.startsWith("sentry.javascript") || sdkName.startsWith("sentry.dart") || sdkName.startsWith("sentry.dotnet")) {
            hint.set("sentry:isFromHybridSdk", true);
        }
    }

    public static boolean isFromHybridSdk(@NotNull Hint hint) {
        return Boolean.TRUE.equals(hint.getAs("sentry:isFromHybridSdk", Boolean.class));
    }

    public static void setEventDropReason(@NotNull Hint hint, @NotNull EventDropReason eventDropReason) {
        hint.set("sentry:eventDropReason", (Object)eventDropReason);
    }

    @Nullable
    public static EventDropReason getEventDropReason(@NotNull Hint hint) {
        return hint.getAs("sentry:eventDropReason", EventDropReason.class);
    }

    public static Hint createWithTypeCheckHint(Object typeCheckHint) {
        Hint hint = new Hint();
        HintUtils.setTypeCheckHint(hint, typeCheckHint);
        return hint;
    }

    public static void setTypeCheckHint(@NotNull Hint hint, Object typeCheckHint) {
        hint.set("sentry:typeCheckHint", typeCheckHint);
    }

    @Nullable
    public static Object getSentrySdkHint(@NotNull Hint hint) {
        return hint.get("sentry:typeCheckHint");
    }

    public static boolean hasType(@NotNull Hint hint, @NotNull Class<?> clazz) {
        Object sentrySdkHint = HintUtils.getSentrySdkHint(hint);
        return clazz.isInstance(sentrySdkHint);
    }

    public static <T> void runIfDoesNotHaveType(@NotNull Hint hint, @NotNull Class<T> clazz, SentryNullableConsumer<Object> lambda) {
        HintUtils.runIfHasType(hint, clazz, ignored -> {}, (value, clazz2) -> lambda.accept(value));
    }

    public static <T> void runIfHasType(@NotNull Hint hint, @NotNull Class<T> clazz, SentryConsumer<T> lambda) {
        HintUtils.runIfHasType(hint, clazz, lambda, (value, clazz2) -> {});
    }

    public static <T> void runIfHasTypeLogIfNot(@NotNull Hint hint, @NotNull Class<T> clazz, ILogger logger, SentryConsumer<T> lambda) {
        HintUtils.runIfHasType(hint, clazz, lambda, (sentrySdkHint, expectedClass) -> LogUtils.logNotInstanceOf(expectedClass, sentrySdkHint, logger));
    }

    public static <T> void runIfHasType(@NotNull Hint hint, @NotNull Class<T> clazz, SentryConsumer<T> lambda, SentryHintFallback fallbackLambda) {
        Object sentrySdkHint = HintUtils.getSentrySdkHint(hint);
        if (HintUtils.hasType(hint, clazz) && sentrySdkHint != null) {
            lambda.accept(sentrySdkHint);
        } else {
            fallbackLambda.accept(sentrySdkHint, clazz);
        }
    }

    public static boolean shouldApplyScopeData(@NotNull Hint hint) {
        return !HintUtils.hasType(hint, Cached.class) && !HintUtils.hasType(hint, Backfillable.class) || HintUtils.hasType(hint, ApplyScopeData.class);
    }

    @FunctionalInterface
    public static interface SentryConsumer<T> {
        public void accept(@NotNull T var1);
    }

    @FunctionalInterface
    public static interface SentryNullableConsumer<T> {
        public void accept(@Nullable T var1);
    }

    @FunctionalInterface
    public static interface SentryHintFallback {
        public void accept(@Nullable Object var1, @NotNull Class<?> var2);
    }
}

