/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.meta;

import com.hypixel.hytale.server.core.meta.IMetaStoreImpl;
import com.hypixel.hytale.server.core.meta.MetaKey;
import javax.annotation.Nullable;

public interface IMetaStore<K> {
    public IMetaStoreImpl<K> getMetaStore();

    default public <T> T getMetaObject(MetaKey<T> key) {
        return this.getMetaStore().getMetaObject(key);
    }

    @Nullable
    default public <T> T getIfPresentMetaObject(MetaKey<T> key) {
        return this.getMetaStore().getIfPresentMetaObject(key);
    }

    @Nullable
    default public <T> T putMetaObject(MetaKey<T> key, T obj) {
        return this.getMetaStore().putMetaObject(key, obj);
    }

    @Nullable
    default public <T> T removeMetaObject(MetaKey<T> key) {
        return this.getMetaStore().removeMetaObject(key);
    }

    @Nullable
    default public <T> T removeSerializedMetaObject(MetaKey<T> key) {
        return this.getMetaStore().removeSerializedMetaObject(key);
    }

    default public boolean hasMetaObject(MetaKey<?> key) {
        return this.getMetaStore().hasMetaObject(key);
    }

    default public void forEachMetaObject(MetaEntryConsumer consumer) {
        this.getMetaStore().forEachMetaObject(consumer);
    }

    default public void markMetaStoreDirty() {
        this.getMetaStore().markMetaStoreDirty();
    }

    default public boolean consumeMetaStoreDirty() {
        return this.getMetaStore().consumeMetaStoreDirty();
    }

    @FunctionalInterface
    public static interface MetaEntryConsumer {
        public <T> void accept(int var1, T var2);
    }
}

