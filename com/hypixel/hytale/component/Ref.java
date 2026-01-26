/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component;

import com.hypixel.hytale.component.Store;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Ref<ECS_TYPE> {
    public static final Ref<?>[] EMPTY_ARRAY = new Ref[0];
    @Nonnull
    private final Store<ECS_TYPE> store;
    private volatile int index;
    private volatile transient int hashCode;
    private volatile Throwable invalidatedBy;

    public Ref(@Nonnull Store<ECS_TYPE> store) {
        this(store, Integer.MIN_VALUE);
    }

    public Ref(@Nonnull Store<ECS_TYPE> store, int index) {
        this.store = store;
        this.index = index;
        this.hashCode = this.hashCode0();
    }

    @Nonnull
    public Store<ECS_TYPE> getStore() {
        return this.store;
    }

    public int getIndex() {
        return this.index;
    }

    void setIndex(int index) {
        this.index = index;
    }

    void invalidate() {
        this.index = Integer.MIN_VALUE;
        this.hashCode = this.hashCode0();
        this.invalidatedBy = new Throwable();
    }

    void invalidate(@Nullable Throwable invalidatedBy) {
        this.index = Integer.MIN_VALUE;
        this.hashCode = this.hashCode0();
        this.invalidatedBy = invalidatedBy != null ? invalidatedBy : new Throwable();
    }

    public void validate() {
        if (this.index == Integer.MIN_VALUE) {
            throw new IllegalStateException("Invalid entity reference!", this.invalidatedBy);
        }
    }

    public boolean isValid() {
        return this.index != Integer.MIN_VALUE;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Ref ref = (Ref)o;
        if (this.index != ref.index) {
            return false;
        }
        return this.store.equals(ref.store);
    }

    public boolean equals(@Nullable Ref<ECS_TYPE> o) {
        return this == o || o != null && this.index == o.index && this.store.equals(o.store);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public int hashCode0() {
        int result = this.store.hashCode();
        result = 31 * result + this.index;
        return result;
    }

    @Nonnull
    public String toString() {
        return "Ref{store=" + String.valueOf(this.store.getClass()) + "@" + this.store.hashCode() + ", index=" + this.index + "}";
    }
}

