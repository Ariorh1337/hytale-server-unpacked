/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import io.sentry.ISentryLifecycleToken;
import io.sentry.util.AutoClosableReentrantLock;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class LazyEvaluator<T> {
    @Nullable
    private volatile T value = null;
    @NotNull
    private final Evaluator<T> evaluator;
    @NotNull
    private final AutoClosableReentrantLock lock = new AutoClosableReentrantLock();

    public LazyEvaluator(@NotNull Evaluator<T> evaluator) {
        this.evaluator = evaluator;
    }

    @NotNull
    public T getValue() {
        if (this.value == null) {
            try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
                if (this.value == null) {
                    this.value = this.evaluator.evaluate();
                }
            }
        }
        return this.value;
    }

    public void setValue(@Nullable T value) {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            this.value = value;
        }
    }

    public void resetValue() {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            this.value = null;
        }
    }

    public static interface Evaluator<T> {
        @NotNull
        public T evaluate();
    }
}

