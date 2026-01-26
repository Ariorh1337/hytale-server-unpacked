/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.meta;

import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.server.core.meta.IMetaRegistry;
import com.hypixel.hytale.server.core.meta.IMetaStore;
import java.util.function.BiConsumer;
import org.bson.BsonDocument;
import org.bson.BsonValue;

public interface IMetaStoreImpl<K>
extends IMetaStore<K> {
    public IMetaRegistry<K> getRegistry();

    public void decode(BsonDocument var1, ExtraInfo var2);

    public BsonDocument encode(ExtraInfo var1);

    public void forEachUnknownEntry(BiConsumer<String, BsonValue> var1);
}

