/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.camera.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.asset.type.camera.CameraEffect;
import com.hypixel.hytale.server.core.asset.type.gameplay.CameraEffectsConfig;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CameraEffectSystem
extends DamageEventSystem {
    @Nonnull
    private static final ComponentType<EntityStore, PlayerRef> PLAYER_REF_COMPONENT_TYPE = PlayerRef.getComponentType();
    private static final ComponentType<EntityStore, EntityStatMap> ENTITY_STAT_MAP_COMPONENT_TYPE = EntityStatMap.getComponentType();
    @Nonnull
    private static final Query<EntityStore> QUERY = Query.and(PLAYER_REF_COMPONENT_TYPE, ENTITY_STAT_MAP_COMPONENT_TYPE);

    @Override
    @Nullable
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getInspectDamageGroup();
    }

    @Override
    @Nonnull
    public Query<EntityStore> getQuery() {
        return QUERY;
    }

    @Override
    public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
        int effectIndex;
        EntityStatMap entityStatMapComponent = archetypeChunk.getComponent(index, ENTITY_STAT_MAP_COMPONENT_TYPE);
        assert (entityStatMapComponent != null);
        EntityStatValue healthStat = entityStatMapComponent.get(DefaultEntityStatTypes.getHealth());
        if (healthStat == null) {
            return;
        }
        float health = healthStat.getMax() - healthStat.getMin();
        if (health <= 0.0f) {
            return;
        }
        PlayerRef playerRefComponent = archetypeChunk.getComponent(index, PLAYER_REF_COMPONENT_TYPE);
        assert (playerRefComponent != null);
        World world = commandBuffer.getExternalData().getWorld();
        CameraEffectsConfig cameraEffectsConfig = world.getGameplayConfig().getCameraEffectsConfig();
        Damage.CameraEffect effect = damage.getIfPresentMetaObject(Damage.CAMERA_EFFECT);
        int n = effectIndex = effect != null ? effect.getEffectIndex() : cameraEffectsConfig.getCameraEffectIndex(damage.getDamageCauseIndex());
        if (effectIndex == Integer.MIN_VALUE) {
            return;
        }
        CameraEffect cameraEffect = CameraEffect.getAssetMap().getAsset(effectIndex);
        if (cameraEffect == null) {
            return;
        }
        float intensity = MathUtil.clamp(damage.getAmount() / health, 0.0f, 1.0f);
        playerRefComponent.getPacketHandler().writeNoCache(cameraEffect.createCameraShakePacket(intensity));
    }
}

