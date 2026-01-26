/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.camera.interaction;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.camera.CameraEffect;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CameraShakeInteraction
extends SimpleInstantInteraction {
    @Nonnull
    public static final BuilderCodec<CameraShakeInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CameraShakeInteraction.class, CameraShakeInteraction::new, SimpleInstantInteraction.CODEC).documentation("Triggers a camera shake effect on use.")).appendInherited(new KeyedCodec<String>("CameraEffect", CameraEffect.CHILD_ASSET_CODEC), (interaction, effect) -> {
        interaction.effectId = effect;
    }, interaction -> interaction.effectId, (interaction, parent) -> {
        interaction.effectId = parent.effectId;
    }).addValidator(Validators.nonNull()).addValidator(CameraEffect.VALIDATOR_CACHE.getValidator()).add()).afterDecode(cameraShakeInteraction -> {
        if (cameraShakeInteraction.effectId != null) {
            cameraShakeInteraction.effectIndex = CameraEffect.getAssetMap().getIndex(cameraShakeInteraction.effectId);
        }
    })).build();
    @Nullable
    protected String effectId;
    protected int effectIndex = Integer.MIN_VALUE;

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        if (this.effectIndex == Integer.MIN_VALUE) {
            return;
        }
        Ref<EntityStore> ref = context.getEntity();
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        PlayerRef playerRefComponent = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
        if (playerRefComponent == null) {
            return;
        }
        CameraEffect cameraShakeEffect = CameraEffect.getAssetMap().getAsset(this.effectIndex);
        if (cameraShakeEffect == null) {
            return;
        }
        playerRefComponent.getPacketHandler().writeNoCache(cameraShakeEffect.createCameraShakePacket());
    }

    @Override
    @Nonnull
    public String toString() {
        return "CameraShakeInteraction{effectId='" + this.effectId + "', effectIndex=" + this.effectIndex + "}";
    }
}

