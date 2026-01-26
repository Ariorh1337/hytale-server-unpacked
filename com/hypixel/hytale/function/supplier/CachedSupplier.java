/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.function.supplier;

import java.util.function.Supplier;
import javax.annotation.Nullable;

public class CachedSupplier<T>
implements Supplier<T> {
    private final Supplier<T> delegate;
    private volatile transient boolean initialized;
    @Nullable
    private transient T value;

    public CachedSupplier(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public T get() {
        if (!this.initialized) {
            CachedSupplier cachedSupplier = this;
            synchronized (cachedSupplier) {
                if (!this.initialized) {
                    T t = this.delegate.get();
                    this.value = t;
                    this.initialized = true;
                    return t;
                }
            }
        }
        return this.value;
    }

    @Nullable
    public T getValue() {
        return this.value;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void invalidate() {
        if (this.initialized) {
            CachedSupplier cachedSupplier = this;
            synchronized (cachedSupplier) {
                if (this.initialized) {
                    this.value = null;
                    this.initialized = false;
                }
            }
        }
    }
}

