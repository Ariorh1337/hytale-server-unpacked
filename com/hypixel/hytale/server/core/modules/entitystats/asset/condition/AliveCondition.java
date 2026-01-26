/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entitystats.asset.condition;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.Condition;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.time.Instant;
import javax.annotation.Nonnull;

public class AliveCondition
extends Condition {
    @Nonnull
    public static final BuilderCodec<AliveCondition> CODEC = BuilderCodec.builder(AliveCondition.class, AliveCondition::new, Condition.BASE_CODEC).build();

    protected AliveCondition() {
    }

    public AliveCondition(boolean inverse) {
        super(inverse);
    }

    @Override
    public boolean eval0(@Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull Ref<EntityStore> ref, @Nonnull Instant currentTime) {
        return !componentAccessor.getArchetype(ref).contains(DeathComponent.getComponentType());
    }

    @Override
    @Nonnull
    public String toString() {
        return "AliveCondition{} " + super.toString();
    }
}

