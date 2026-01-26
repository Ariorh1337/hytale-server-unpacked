/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.Position;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.world.PlaySoundEvent2D;
import com.hypixel.hytale.protocol.packets.world.PlaySoundEvent3D;
import com.hypixel.hytale.protocol.packets.world.PlaySoundEventEntity;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.PlayerUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SoundUtil {
    public static void playSoundEventEntity(int soundEventIndex, int networkId, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        SoundUtil.playSoundEventEntity(soundEventIndex, networkId, 1.0f, 1.0f, componentAccessor);
    }

    public static void playSoundEventEntity(int soundEventIndex, int networkId, float volumeModifier, float pitchModifier, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (soundEventIndex == 0) {
            return;
        }
        PlayerUtil.broadcastPacketToPlayers(componentAccessor, (Packet)new PlaySoundEventEntity(soundEventIndex, networkId, volumeModifier, pitchModifier));
    }

    public static void playSoundEvent2dToPlayer(@Nonnull PlayerRef playerRefComponent, int soundEventIndex, @Nonnull SoundCategory soundCategory) {
        SoundUtil.playSoundEvent2dToPlayer(playerRefComponent, soundEventIndex, soundCategory, 1.0f, 1.0f);
    }

    public static void playSoundEvent2dToPlayer(@Nonnull PlayerRef playerRefComponent, int soundEventIndex, @Nonnull SoundCategory soundCategory, float volumeModifier, float pitchModifier) {
        if (soundEventIndex == 0) {
            return;
        }
        playerRefComponent.getPacketHandler().write((Packet)new PlaySoundEvent2D(soundEventIndex, soundCategory, volumeModifier, pitchModifier));
    }

    public static void playSoundEvent2d(int soundEventIndex, @Nonnull SoundCategory soundCategory, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        SoundUtil.playSoundEvent2d(soundEventIndex, soundCategory, 1.0f, 1.0f, componentAccessor);
    }

    public static void playSoundEvent2d(int soundEventIndex, @Nonnull SoundCategory soundCategory, float volumeModifier, float pitchModifier, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (soundEventIndex == 0) {
            return;
        }
        PlayerUtil.broadcastPacketToPlayers(componentAccessor, (Packet)new PlaySoundEvent2D(soundEventIndex, soundCategory, volumeModifier, pitchModifier));
    }

    public static void playSoundEvent2d(@Nonnull Ref<EntityStore> ref, int soundEventIndex, @Nonnull SoundCategory soundCategory, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        SoundUtil.playSoundEvent2d(ref, soundEventIndex, soundCategory, 1.0f, 1.0f, componentAccessor);
    }

    public static void playSoundEvent2d(@Nonnull Ref<EntityStore> ref, int soundEventIndex, @Nonnull SoundCategory soundCategory, float volumeModifier, float pitchModifier, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (soundEventIndex == 0) {
            return;
        }
        PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
        if (playerRefComponent == null) {
            return;
        }
        playerRefComponent.getPacketHandler().write((Packet)new PlaySoundEvent2D(soundEventIndex, soundCategory, volumeModifier, pitchModifier));
    }

    public static void playSoundEvent3d(int soundEventIndex, @Nonnull SoundCategory soundCategory, double x, double y, double z, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        SoundUtil.playSoundEvent3d(soundEventIndex, soundCategory, x, y, z, 1.0f, 1.0f, componentAccessor);
    }

    public static void playSoundEvent3d(int soundEventIndex, @Nonnull SoundCategory soundCategory, double x, double y, double z, float volumeModifier, float pitchModifier, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (soundEventIndex == 0) {
            return;
        }
        SoundEvent soundEvent = SoundEvent.getAssetMap().getAsset(soundEventIndex);
        if (soundEvent == null) {
            return;
        }
        PlaySoundEvent3D packet = new PlaySoundEvent3D(soundEventIndex, soundCategory, new Position(x, y, z), volumeModifier, pitchModifier);
        Vector3d position = new Vector3d(x, y, z);
        SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = componentAccessor.getResource(EntityModule.get().getPlayerSpatialResourceType());
        ObjectList results = SpatialResource.getThreadLocalReferenceList();
        playerSpatialResource.getSpatialStructure().collect(position, soundEvent.getMaxDistance(), results);
        for (Ref ref : results) {
            if (ref == null || !ref.isValid()) continue;
            PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            playerRefComponent.getPacketHandler().write((Packet)packet);
        }
    }

    public static void playSoundEvent3d(int soundEventIndex, @Nonnull SoundCategory soundCategory, @Nonnull Vector3d position, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        SoundUtil.playSoundEvent3d(soundEventIndex, soundCategory, position.getX(), position.getY(), position.getZ(), componentAccessor);
    }

    public static void playSoundEvent3d(int soundEventIndex, @Nonnull SoundCategory soundCategory, double x, double y, double z, @Nonnull Predicate<Ref<EntityStore>> shouldHear, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        SoundUtil.playSoundEvent3d(soundEventIndex, soundCategory, x, y, z, 1.0f, 1.0f, shouldHear, componentAccessor);
    }

    public static void playSoundEvent3d(int soundEventIndex, @Nonnull SoundCategory soundCategory, double x, double y, double z, float volumeModifier, float pitchModifier, @Nonnull Predicate<Ref<EntityStore>> shouldHear, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (soundEventIndex == 0) {
            return;
        }
        SoundEvent soundEvent = SoundEvent.getAssetMap().getAsset(soundEventIndex);
        if (soundEvent == null) {
            return;
        }
        PlaySoundEvent3D packet = new PlaySoundEvent3D(soundEventIndex, soundCategory, new Position(x, y, z), volumeModifier, pitchModifier);
        SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = componentAccessor.getResource(EntityModule.get().getPlayerSpatialResourceType());
        ObjectList results = SpatialResource.getThreadLocalReferenceList();
        playerSpatialResource.getSpatialStructure().collect(new Vector3d(x, y, z), soundEvent.getMaxDistance(), results);
        for (Ref ref : results) {
            if (ref == null || !ref.isValid() || !shouldHear.test(ref)) continue;
            PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            playerRefComponent.getPacketHandler().write((Packet)packet);
        }
    }

    public static void playSoundEvent3d(@Nullable Ref<EntityStore> sourceRef, int soundEventIndex, @Nonnull Vector3d pos, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        SoundUtil.playSoundEvent3d(sourceRef, soundEventIndex, pos.getX(), pos.getY(), pos.getZ(), componentAccessor);
    }

    public static void playSoundEvent3d(@Nullable Ref<EntityStore> sourceRef, int soundEventIndex, double x, double y, double z, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        SoundUtil.playSoundEvent3d(sourceRef, soundEventIndex, x, y, z, false, componentAccessor);
    }

    public static void playSoundEvent3d(@Nullable Ref<EntityStore> sourceRef, int soundEventIndex, @Nonnull Vector3d position, boolean ignoreSource, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        SoundUtil.playSoundEvent3d(sourceRef, soundEventIndex, position.getX(), position.getY(), position.getZ(), ignoreSource, componentAccessor);
    }

    public static void playSoundEvent3d(@Nullable Ref<EntityStore> sourceRef, int soundEventIndex, double x, double y, double z, boolean ignoreSource, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        Entity sourceEntity = sourceRef != null ? EntityUtils.getEntity(sourceRef, componentAccessor) : null;
        SoundUtil.playSoundEvent3d(soundEventIndex, x, y, z, playerRef -> {
            if (sourceEntity == null) {
                return true;
            }
            if (ignoreSource && sourceRef.equals((Ref<EntityStore>)playerRef)) {
                return false;
            }
            return !sourceEntity.isHiddenFromLivingEntity(sourceRef, (Ref<EntityStore>)playerRef, componentAccessor);
        }, componentAccessor);
    }

    public static void playSoundEvent3d(int soundEventIndex, double x, double y, double z, @Nonnull Predicate<Ref<EntityStore>> shouldHear, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        SoundUtil.playSoundEvent3d(soundEventIndex, SoundCategory.SFX, x, y, z, shouldHear, componentAccessor);
    }

    public static void playSoundEvent3dToPlayer(@Nullable Ref<EntityStore> playerRef, int soundEventIndex, @Nonnull SoundCategory soundCategory, double x, double y, double z, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        SoundUtil.playSoundEvent3dToPlayer(playerRef, soundEventIndex, soundCategory, x, y, z, 1.0f, 1.0f, componentAccessor);
    }

    public static void playSoundEvent3dToPlayer(@Nullable Ref<EntityStore> playerRef, int soundEventIndex, @Nonnull SoundCategory soundCategory, double x, double y, double z, float volumeModifier, float pitchModifier, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (playerRef == null || soundEventIndex == 0) {
            return;
        }
        SoundEvent soundEventAsset = SoundEvent.getAssetMap().getAsset(soundEventIndex);
        if (soundEventAsset == null) {
            return;
        }
        float maxDistance = soundEventAsset.getMaxDistance();
        TransformComponent transformComponent = componentAccessor.getComponent(playerRef, TransformComponent.getComponentType());
        assert (transformComponent != null);
        if (transformComponent.getPosition().distanceSquaredTo(x, y, z) <= (double)(maxDistance * maxDistance)) {
            PlayerRef playerRefComponent = componentAccessor.getComponent(playerRef, PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            playerRefComponent.getPacketHandler().write((Packet)new PlaySoundEvent3D(soundEventIndex, soundCategory, new Position(x, y, z), volumeModifier, pitchModifier));
        }
    }

    public static void playSoundEvent3dToPlayer(@Nullable Ref<EntityStore> playerRef, int soundEventIndex, @Nonnull SoundCategory soundCategory, @Nonnull Vector3d position, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        SoundUtil.playSoundEvent3dToPlayer(playerRef, soundEventIndex, soundCategory, position.x, position.y, position.z, componentAccessor);
    }
}

