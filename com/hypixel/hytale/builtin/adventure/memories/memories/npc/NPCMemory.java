/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.memories.memories.npc;

import com.hypixel.hytale.builtin.adventure.memories.MemoriesGameplayConfig;
import com.hypixel.hytale.builtin.adventure.memories.MemoriesPlugin;
import com.hypixel.hytale.builtin.adventure.memories.component.PlayerMemories;
import com.hypixel.hytale.builtin.adventure.memories.memories.Memory;
import com.hypixel.hytale.builtin.instances.config.InstanceDiscoveryConfig;
import com.hypixel.hytale.builtin.instances.config.InstanceWorldConfig;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.spatial.SpatialStructure;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.packets.entities.SpawnModelParticles;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelParticle;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.entity.item.PickupItemComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGenerator;
import com.hypixel.hytale.server.worldgen.chunk.ZoneBiomeResult;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NPCMemory
extends Memory {
    @Nonnull
    public static final String ID = "NPC";
    @Nonnull
    public static final String ZONE_NAME_UNKNOWN = "???";
    @Nonnull
    public static final BuilderCodec<NPCMemory> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(NPCMemory.class, NPCMemory::new).append(new KeyedCodec<String>("NPCRole", Codec.STRING), (npcMemory, s) -> {
        npcMemory.npcRole = s;
    }, npcMemory -> npcMemory.npcRole).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<String>("TranslationKey", Codec.STRING), (npcMemory, s) -> {
        npcMemory.memoryTitleKey = s;
    }, npcMemory -> npcMemory.memoryTitleKey).add()).append(new KeyedCodec<Long>("CapturedTimestamp", Codec.LONG), (npcMemory, aDouble) -> {
        npcMemory.capturedTimestamp = aDouble;
    }, npcMemory -> npcMemory.capturedTimestamp).add()).append(new KeyedCodec<String>("FoundLocationZoneNameKey", Codec.STRING), (npcMemory, s) -> {
        npcMemory.foundLocationZoneNameKey = s;
    }, npcMemory -> npcMemory.foundLocationZoneNameKey).add()).append(new KeyedCodec<String>("FoundLocationNameKey", Codec.STRING), (npcMemory, s) -> {
        npcMemory.foundLocationGeneralNameKey = s;
    }, npcMemory -> npcMemory.foundLocationGeneralNameKey).add()).build();
    private String npcRole;
    private long capturedTimestamp;
    private String foundLocationZoneNameKey;
    private String foundLocationGeneralNameKey;
    private String memoryTitleKey;

    private NPCMemory() {
    }

    public NPCMemory(@Nonnull String npcRole, @Nonnull String nameTranslationKey) {
        this.npcRole = npcRole;
        this.memoryTitleKey = nameTranslationKey;
    }

    @Override
    public String getId() {
        return this.npcRole;
    }

    @Override
    @Nonnull
    public String getTitle() {
        return this.memoryTitleKey;
    }

    @Override
    @Nonnull
    public Message getTooltipText() {
        return Message.translation("server.memories.general.discovered.tooltipText");
    }

    @Override
    @Nonnull
    public String getIconPath() {
        return "UI/Custom/Pages/Memories/npcs/" + this.npcRole + ".png";
    }

    @Override
    @Nonnull
    public Message getUndiscoveredTooltipText() {
        return Message.translation("server.memories.general.undiscovered.tooltipText");
    }

    @Nonnull
    public String getNpcRole() {
        return this.npcRole;
    }

    public long getCapturedTimestamp() {
        return this.capturedTimestamp;
    }

    public String getFoundLocationZoneNameKey() {
        return this.foundLocationZoneNameKey;
    }

    @Nonnull
    public Message getLocationMessage() {
        if (this.foundLocationGeneralNameKey != null) {
            return Message.translation(this.foundLocationGeneralNameKey);
        }
        if (this.foundLocationZoneNameKey != null) {
            return Message.translation("server.map.region." + this.foundLocationZoneNameKey);
        }
        return Message.raw(ZONE_NAME_UNKNOWN);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        NPCMemory npcMemory = (NPCMemory)o;
        return Objects.equals(this.npcRole, npcMemory.npcRole);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(this.npcRole);
        return result;
    }

    @Override
    @Nonnull
    public String toString() {
        return "NPCMemory{npcRole='" + this.npcRole + "', capturedTimestamp=" + this.capturedTimestamp + "', foundLocationZoneNameKey='" + this.foundLocationZoneNameKey + "}";
    }

    public static class GatherMemoriesSystem
    extends EntityTickingSystem<EntityStore> {
        @Nonnull
        private final ComponentType<EntityStore, TransformComponent> transformComponentType;
        @Nonnull
        private final ComponentType<EntityStore, Player> playerComponentType;
        @Nonnull
        private final ComponentType<EntityStore, PlayerRef> playerRefComponentType;
        @Nonnull
        private final ComponentType<EntityStore, PlayerMemories> playerMemoriesComponentType;
        @Nonnull
        private final Query<EntityStore> query;
        private final double radius;

        public GatherMemoriesSystem(@Nonnull ComponentType<EntityStore, TransformComponent> transformComponentType, @Nonnull ComponentType<EntityStore, Player> playerComponentType, @Nonnull ComponentType<EntityStore, PlayerRef> playerRefComponentType, @Nonnull ComponentType<EntityStore, PlayerMemories> playerMemoriesComponentType, double radius) {
            this.transformComponentType = transformComponentType;
            this.playerComponentType = playerComponentType;
            this.playerRefComponentType = playerRefComponentType;
            this.playerMemoriesComponentType = playerMemoriesComponentType;
            this.query = Query.and(transformComponentType, playerComponentType, playerRefComponentType, playerMemoriesComponentType);
            this.radius = radius;
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            Player playerComponent = archetypeChunk.getComponent(index, this.playerComponentType);
            assert (playerComponent != null);
            if (playerComponent.getGameMode() != GameMode.Adventure) {
                return;
            }
            TransformComponent transformComponent = archetypeChunk.getComponent(index, this.transformComponentType);
            assert (transformComponent != null);
            Vector3d position = transformComponent.getPosition();
            SpatialResource<Ref<EntityStore>, EntityStore> npcSpatialResource = store.getResource(NPCPlugin.get().getNpcSpatialResource());
            ObjectList results = SpatialResource.getThreadLocalReferenceList();
            npcSpatialResource.getSpatialStructure().collect(position, this.radius, results);
            if (results.isEmpty()) {
                return;
            }
            PlayerRef playerRefComponent = archetypeChunk.getComponent(index, this.playerRefComponentType);
            assert (playerRefComponent != null);
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            MemoriesPlugin memoriesPlugin = MemoriesPlugin.get();
            PlayerMemories playerMemoriesComponent = archetypeChunk.getComponent(index, this.playerMemoriesComponentType);
            assert (playerMemoriesComponent != null);
            NPCMemory temp = new NPCMemory();
            World world = commandBuffer.getExternalData().getWorld();
            String foundLocationZoneNameKey = GatherMemoriesSystem.findLocationZoneName(world, position);
            for (Ref ref2 : results) {
                MemoriesGameplayConfig memoriesGameplayConfig;
                Role role;
                NPCEntity npcComponent = commandBuffer.getComponent(ref2, NPCEntity.getComponentType());
                if (npcComponent == null || (role = npcComponent.getRole()) == null || !role.isMemory()) continue;
                String memoriesNameOverride = role.getMemoriesNameOverride();
                temp.npcRole = memoriesNameOverride != null && !memoriesNameOverride.isEmpty() ? memoriesNameOverride : npcComponent.getRoleName();
                temp.memoryTitleKey = role.getNameTranslationKey();
                temp.capturedTimestamp = System.currentTimeMillis();
                temp.foundLocationGeneralNameKey = foundLocationZoneNameKey;
                if (memoriesPlugin.hasRecordedMemory(temp) || !playerMemoriesComponent.recordMemory(temp)) continue;
                NotificationUtil.sendNotification(playerRefComponent.getPacketHandler(), Message.translation("server.memories.general.collected").param("memoryTitle", Message.translation(temp.getTitle())), null, "NotificationIcons/MemoriesIcon.png");
                temp = new NPCMemory();
                TransformComponent npcTransformComponent = commandBuffer.getComponent(ref2, TransformComponent.getComponentType());
                if (npcTransformComponent == null || (memoriesGameplayConfig = MemoriesGameplayConfig.get(store.getExternalData().getWorld().getGameplayConfig())) == null) continue;
                ItemStack memoryItemStack = new ItemStack(memoriesGameplayConfig.getMemoriesCatchItemId());
                Vector3d memoryItemHolderPosition = npcTransformComponent.getPosition().clone();
                BoundingBox boundingBoxComponent = commandBuffer.getComponent(ref2, BoundingBox.getComponentType());
                if (boundingBoxComponent != null) {
                    memoryItemHolderPosition.y += boundingBoxComponent.getBoundingBox().middleY();
                }
                Holder<EntityStore> memoryItemHolder = ItemComponent.generatePickedUpItem(memoryItemStack, memoryItemHolderPosition, commandBuffer, ref);
                float memoryCatchItemLifetimeS = 0.62f;
                PickupItemComponent pickupItemComponent = memoryItemHolder.getComponent(PickupItemComponent.getComponentType());
                assert (pickupItemComponent != null);
                pickupItemComponent.setInitialLifeTime(0.62f);
                commandBuffer.addEntity(memoryItemHolder, AddReason.SPAWN);
                GatherMemoriesSystem.displayCatchEntityParticles(memoriesGameplayConfig, memoryItemHolderPosition, ref2, commandBuffer);
            }
        }

        private static String findLocationZoneName(@Nonnull World world, @Nonnull Vector3d position) {
            InstanceDiscoveryConfig discovery;
            IWorldGen worldGen = world.getChunkStore().getGenerator();
            if (worldGen instanceof ChunkGenerator) {
                ChunkGenerator generator = (ChunkGenerator)worldGen;
                int seed = (int)world.getWorldConfig().getSeed();
                ZoneBiomeResult result = generator.getZoneBiomeResultAt(seed, MathUtil.floor(position.x), MathUtil.floor(position.z));
                return "server.map.region." + result.getZoneResult().getZone().name();
            }
            InstanceWorldConfig instanceConfig = world.getWorldConfig().getPluginConfig().get(InstanceWorldConfig.class);
            if (instanceConfig != null && (discovery = instanceConfig.getDiscovery()) != null && discovery.getTitleKey() != null) {
                return discovery.getTitleKey();
            }
            return NPCMemory.ZONE_NAME_UNKNOWN;
        }

        private static void displayCatchEntityParticles(@Nonnull MemoriesGameplayConfig memoriesGameplayConfig, @Nonnull Vector3d targetPosition, @Nonnull Ref<EntityStore> targetRef, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            ModelParticle particle = memoriesGameplayConfig.getMemoriesCatchEntityParticle();
            if (particle == null) {
                return;
            }
            NetworkId networkIdComponent = commandBuffer.getComponent(targetRef, NetworkId.getComponentType());
            if (networkIdComponent == null) {
                return;
            }
            com.hypixel.hytale.protocol.ModelParticle[] modelParticlesProtocol = new com.hypixel.hytale.protocol.ModelParticle[]{particle.toPacket()};
            SpawnModelParticles packet = new SpawnModelParticles(networkIdComponent.getId(), modelParticlesProtocol);
            SpatialResource<Ref<EntityStore>, EntityStore> spatialResource = commandBuffer.getResource(EntityModule.get().getPlayerSpatialResourceType());
            SpatialStructure<Ref<EntityStore>> spatialStructure = spatialResource.getSpatialStructure();
            ObjectList results = SpatialResource.getThreadLocalReferenceList();
            spatialStructure.ordered(targetPosition, memoriesGameplayConfig.getMemoriesCatchParticleViewDistance(), results);
            for (Ref ref : results) {
                PlayerRef playerRefComponent = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
                if (playerRefComponent == null) continue;
                playerRefComponent.getPacketHandler().write((ToClientPacket)packet);
            }
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.query;
        }
    }
}

