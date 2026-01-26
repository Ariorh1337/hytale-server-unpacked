/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.meta;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.server.core.meta.IMetaStore;
import com.hypixel.hytale.server.core.meta.MetaKey;
import com.hypixel.hytale.server.core.meta.PersistentMetaKey;
import java.util.function.Function;
import javax.annotation.Nullable;

public interface IMetaRegistry<K> {
    public <T> T newMetaObject(MetaKey<T> var1, K var2);

    public void forEachMetaEntry(IMetaStore<K> var1, MetaEntryConsumer var2);

    @Nullable
    public PersistentMetaKey<?> getMetaKeyForCodecKey(String var1);

    public <T> MetaKey<T> registerMetaObject(Function<K, T> var1, boolean var2, String var3, Codec<T> var4);

    default public <T> MetaKey<T> registerMetaObject(Function<K, T> supplier, String keyName, Codec<T> codec) {
        return this.registerMetaObject(supplier, true, keyName, codec);
    }

    default public <T> MetaKey<T> registerMetaObject(Function<K, T> supplier) {
        return this.registerMetaObject(supplier, false, null, null);
    }

    default public <T> MetaKey<T> registerMetaObject() {
        return this.registerMetaObject(parent -> null);
    }

    @FunctionalInterface
    public static interface MetaEntryConsumer {
        public <T> void accept(MetaKey<T> var1, T var2);
    }
}

