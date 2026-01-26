/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.config.objectivesetup;

import com.hypixel.hytale.builtin.adventure.objectives.Objective;
import com.hypixel.hytale.builtin.adventure.objectives.ObjectivePlugin;
import com.hypixel.hytale.builtin.adventure.objectives.config.ObjectiveAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.objectivesetup.ObjectiveTypeSetup;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SetupObjective
extends ObjectiveTypeSetup {
    public static final BuilderCodec<SetupObjective> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(SetupObjective.class, SetupObjective::new).append(new KeyedCodec<String>("ObjectiveId", Codec.STRING), (setupObjective, s) -> {
        setupObjective.objectiveId = s;
    }, setupObjective -> setupObjective.objectiveId).addValidator(Validators.nonNull()).addValidatorLate(() -> ObjectiveAsset.VALIDATOR_CACHE.getValidator().late()).add()).build();
    protected String objectiveId;

    @Override
    public String getObjectiveIdToStart() {
        return this.objectiveId;
    }

    @Override
    @Nullable
    public Objective setup(@Nonnull Set<UUID> playerUUIDs, @Nonnull UUID worldUUID, @Nullable UUID markerUUID, @Nonnull Store<EntityStore> store) {
        return ObjectivePlugin.get().startObjective(this.objectiveId, playerUUIDs, worldUUID, markerUUID, store);
    }

    @Override
    @Nonnull
    public String toString() {
        return "SetupObjective{objectiveId='" + this.objectiveId + "'} " + super.toString();
    }
}

