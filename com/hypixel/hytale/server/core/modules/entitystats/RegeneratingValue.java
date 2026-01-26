/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entitystats;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.Condition;
import com.hypixel.hytale.server.core.modules.entitystats.asset.modifier.RegeneratingModifier;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.time.Instant;
import javax.annotation.Nonnull;

public class RegeneratingValue {
    @Nonnull
    private final EntityStatType.Regenerating regenerating;
    private float remainingUntilRegen;

    public RegeneratingValue(@Nonnull EntityStatType.Regenerating regenerating) {
        this.regenerating = regenerating;
    }

    public boolean shouldRegenerate(@Nonnull ComponentAccessor<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull Instant currentTime, float dt, @Nonnull EntityStatType.Regenerating regenerating) {
        this.remainingUntilRegen -= dt;
        if (this.remainingUntilRegen < 0.0f) {
            this.remainingUntilRegen += regenerating.getInterval();
            return Condition.allConditionsMet(store, ref, currentTime, regenerating);
        }
        return false;
    }

    public float regenerate(@Nonnull ComponentAccessor<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull Instant currentTime, float dt, @Nonnull EntityStatValue value, float currentAmount) {
        float toAdd;
        if (!this.shouldRegenerate(store, ref, currentTime, dt, this.regenerating)) {
            return 0.0f;
        }
        switch (this.regenerating.getRegenType()) {
            default: {
                throw new MatchException(null, null);
            }
            case ADDITIVE: {
                float f = this.regenerating.getAmount();
                break;
            }
            case PERCENTAGE: {
                float f = toAdd = this.regenerating.getAmount() * (value.getMax() - value.getMin());
            }
        }
        if (this.regenerating.getModifiers() != null) {
            for (RegeneratingModifier modifier : this.regenerating.getModifiers()) {
                toAdd *= modifier.getModifier(store, ref, currentTime);
            }
        }
        return this.regenerating.clampAmount(toAdd, currentAmount, value);
    }

    public EntityStatType.Regenerating getRegenerating() {
        return this.regenerating;
    }

    @Nonnull
    public String toString() {
        return "RegeneratingValue{regenerating=" + String.valueOf(this.regenerating) + ", remainingUntilRegen=" + this.remainingUntilRegen + "}";
    }
}

