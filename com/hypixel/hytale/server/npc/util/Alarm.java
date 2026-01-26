/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.util;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.npc.storage.PersistentParameter;
import java.time.Instant;
import javax.annotation.Nonnull;

public class Alarm
extends PersistentParameter<Instant> {
    public static final BuilderCodec<Alarm> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(Alarm.class, Alarm::new).append(new KeyedCodec("Instant", Codec.INSTANT), (alarm, o) -> {
        alarm.alarmInstant = o;
    }, alarm -> alarm.alarmInstant).add()).build();
    protected Instant alarmInstant;

    @Override
    protected void set0(Instant value) {
        this.alarmInstant = value;
    }

    public boolean isSet() {
        return this.alarmInstant != null;
    }

    public boolean hasPassed(@Nonnull Instant instant) {
        return this.alarmInstant != null && instant.isAfter(this.alarmInstant);
    }
}

