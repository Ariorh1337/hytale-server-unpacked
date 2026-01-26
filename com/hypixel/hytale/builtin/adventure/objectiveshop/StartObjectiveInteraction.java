/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectiveshop;

import com.hypixel.hytale.builtin.adventure.objectives.ObjectivePlugin;
import com.hypixel.hytale.builtin.adventure.objectives.config.ObjectiveAsset;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.player.pages.choices.ChoiceInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.HashSet;
import java.util.UUID;
import javax.annotation.Nonnull;

public class StartObjectiveInteraction
extends ChoiceInteraction {
    public static final BuilderCodec<StartObjectiveInteraction> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(StartObjectiveInteraction.class, StartObjectiveInteraction::new, ChoiceInteraction.BASE_CODEC).append(new KeyedCodec<String>("ObjectiveId", Codec.STRING), (startObjectiveInteraction, s) -> {
        startObjectiveInteraction.objectiveId = s;
    }, startObjectiveInteraction -> startObjectiveInteraction.objectiveId).addValidator(ObjectiveAsset.VALIDATOR_CACHE.getValidator()).add()).build();
    protected String objectiveId;

    public StartObjectiveInteraction(String objectiveId) {
        this.objectiveId = objectiveId;
    }

    protected StartObjectiveInteraction() {
    }

    public String getObjectiveId() {
        return this.objectiveId;
    }

    @Override
    public void run(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef) {
        HashSet<UUID> playerSet = new HashSet<UUID>();
        playerSet.add(playerRef.getUuid());
        World world = store.getExternalData().getWorld();
        ObjectivePlugin.get().startObjective(this.objectiveId, playerSet, world.getWorldConfig().getUuid(), null, store);
    }

    @Override
    @Nonnull
    public String toString() {
        return "StartObjectiveInteraction{objectiveId='" + this.objectiveId + "'} " + super.toString();
    }
}

