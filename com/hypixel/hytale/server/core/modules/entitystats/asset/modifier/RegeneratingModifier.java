/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entitystats.asset.modifier;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.Condition;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.time.Instant;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class RegeneratingModifier {
    public static final BuilderCodec<RegeneratingModifier> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RegeneratingModifier.class, RegeneratingModifier::new).append(new KeyedCodec<T[]>("Conditions", new ArrayCodec<Condition>(Condition.CODEC, Condition[]::new)), (condition, value) -> {
        condition.conditions = value;
    }, condition -> condition.conditions).add()).append(new KeyedCodec<Float>("Amount", Codec.FLOAT), (condition, value) -> {
        condition.amount = value.floatValue();
    }, condition -> Float.valueOf(condition.amount)).add()).build();
    protected Condition[] conditions;
    protected float amount;

    protected RegeneratingModifier() {
    }

    public RegeneratingModifier(Condition[] conditions, float amount) {
        this.conditions = conditions;
        this.amount = amount;
    }

    public float getModifier(ComponentAccessor<EntityStore> store, Ref<EntityStore> ref, Instant currentTime) {
        if (Condition.allConditionsMet(store, ref, currentTime, this.conditions)) {
            return this.amount;
        }
        return 1.0f;
    }

    @Nonnull
    public String toString() {
        return "RegeneratingModifier{conditions=" + Arrays.toString(this.conditions) + ", amount=" + this.amount + "}";
    }
}

