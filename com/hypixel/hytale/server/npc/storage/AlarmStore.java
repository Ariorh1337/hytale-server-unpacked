/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.storage;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.server.npc.storage.ParameterStore;
import com.hypixel.hytale.server.npc.util.Alarm;
import java.util.HashMap;
import javax.annotation.Nonnull;

public class AlarmStore
extends ParameterStore<Alarm> {
    public static final BuilderCodec<AlarmStore> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(AlarmStore.class, AlarmStore::new).append(new KeyedCodec("Parameters", new MapCodec<Alarm, HashMap>(Alarm.CODEC, HashMap::new, false)), (store, o) -> {
        store.parameters = o;
    }, store -> store.parameters).add()).build();

    @Override
    @Nonnull
    protected Alarm createParameter() {
        return new Alarm();
    }
}

