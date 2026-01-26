/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.farming.states;

import com.hypixel.hytale.builtin.adventure.farming.FarmingPlugin;
import com.hypixel.hytale.builtin.adventure.farming.FarmingUtil;
import com.hypixel.hytale.builtin.adventure.farming.component.CoopResidentComponent;
import com.hypixel.hytale.builtin.adventure.farming.config.FarmingCoopAsset;
import com.hypixel.hytale.builtin.tagset.TagSetPlugin;
import com.hypixel.hytale.builtin.tagset.config.NPCGroup;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.range.IntRange;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemDrop;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemDropList;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.reference.PersistentRef;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.EmptyItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.metadata.CapturedNPCMetadata;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.spawning.ISpawnableWithModel;
import com.hypixel.hytale.server.spawning.SpawnTestResult;
import com.hypixel.hytale.server.spawning.SpawningContext;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nullable;

public class CoopBlock
implements Component<ChunkStore> {
    public static final String STATE_PRODUCE = "Produce_Ready";
    public static final BuilderCodec<CoopBlock> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CoopBlock.class, CoopBlock::new).append(new KeyedCodec<String>("FarmingCoopId", Codec.STRING, true), (coop, s) -> {
        coop.coopAssetId = s;
    }, coop -> coop.coopAssetId).add()).append(new KeyedCodec<T[]>("Residents", new ArrayCodec<CoopResident>(CoopResident.CODEC, CoopResident[]::new)), (coop, residents) -> {
        coop.residents = new ArrayList<CoopResident>(Arrays.asList(residents));
    }, coop -> (CoopResident[])coop.residents.toArray(CoopResident[]::new)).add()).append(new KeyedCodec<ItemContainer>("Storage", ItemContainer.CODEC), (coop, storage) -> {
        coop.itemContainer = storage;
    }, coop -> coop.itemContainer).add()).build();
    protected String coopAssetId;
    protected List<CoopResident> residents = new ArrayList<CoopResident>();
    protected ItemContainer itemContainer = EmptyItemContainer.INSTANCE;

    public static ComponentType<ChunkStore, CoopBlock> getComponentType() {
        return FarmingPlugin.get().getCoopBlockStateComponentType();
    }

    public CoopBlock() {
        ArrayList<ItemStack> remainder = new ArrayList<ItemStack>();
        this.itemContainer = ItemContainer.ensureContainerCapacity(this.itemContainer, (short)5, SimpleItemContainer::new, remainder);
    }

    @Nullable
    public FarmingCoopAsset getCoopAsset() {
        return FarmingCoopAsset.getAssetMap().getAsset(this.coopAssetId);
    }

    public CoopBlock(String farmingCoopId, List<CoopResident> residents, ItemContainer itemContainer) {
        this.coopAssetId = farmingCoopId;
        this.residents.addAll(residents);
        this.itemContainer = itemContainer.clone();
        ObjectArrayList<ItemStack> remainder = new ObjectArrayList<ItemStack>();
        this.itemContainer = ItemContainer.ensureContainerCapacity(this.itemContainer, (short)5, SimpleItemContainer::new, remainder);
    }

    public boolean tryPutResident(CapturedNPCMetadata metadata, WorldTimeResource worldTimeResource) {
        FarmingCoopAsset coopAsset = this.getCoopAsset();
        if (coopAsset == null) {
            return false;
        }
        if (this.residents.size() >= coopAsset.getMaxResidents()) {
            return false;
        }
        if (!this.getCoopAcceptsNPCGroup(metadata.getRoleIndex())) {
            return false;
        }
        this.residents.add(new CoopResident(metadata, null, worldTimeResource.getGameTime()));
        return true;
    }

    public boolean tryPutWildResidentFromWild(Store<EntityStore> store, Ref<EntityStore> entityRef, WorldTimeResource worldTimeResource, Vector3i coopLocation) {
        FarmingCoopAsset coopAsset = this.getCoopAsset();
        if (coopAsset == null) {
            return false;
        }
        NPCEntity npcComponent = store.getComponent(entityRef, NPCEntity.getComponentType());
        if (npcComponent == null) {
            return false;
        }
        CoopResidentComponent coopResidentComponent = store.getComponent(entityRef, CoopResidentComponent.getComponentType());
        if (coopResidentComponent != null) {
            return false;
        }
        if (!this.getCoopAcceptsNPCGroup(npcComponent.getRoleIndex())) {
            return false;
        }
        if (this.residents.size() >= coopAsset.getMaxResidents()) {
            return false;
        }
        coopResidentComponent = store.ensureAndGetComponent(entityRef, CoopResidentComponent.getComponentType());
        coopResidentComponent.setCoopLocation(coopLocation);
        UUIDComponent uuidComponent = store.getComponent(entityRef, UUIDComponent.getComponentType());
        if (uuidComponent == null) {
            return false;
        }
        PersistentRef persistentRef = new PersistentRef();
        persistentRef.setEntity(entityRef, uuidComponent.getUuid());
        CapturedNPCMetadata metadata = FarmingUtil.generateCapturedNPCMetadata(store, entityRef, npcComponent.getRoleIndex());
        CoopResident residentRecord = new CoopResident(metadata, persistentRef, worldTimeResource.getGameTime());
        residentRecord.deployedToWorld = true;
        this.residents.add(residentRecord);
        return true;
    }

    public boolean getCoopAcceptsNPCGroup(int npcRoleIndex) {
        TagSetPlugin.TagSetLookup tagSetPlugin = TagSetPlugin.get(NPCGroup.class);
        FarmingCoopAsset coopAsset = this.getCoopAsset();
        if (coopAsset == null) {
            return false;
        }
        int[] acceptedNpcGroupIndexes = coopAsset.getAcceptedNpcGroupIndexes();
        if (acceptedNpcGroupIndexes == null) {
            return true;
        }
        for (int group : acceptedNpcGroupIndexes) {
            if (!tagSetPlugin.tagInSet(group, npcRoleIndex)) continue;
            return true;
        }
        return false;
    }

    public void generateProduceToInventory(WorldTimeResource worldTimeResource) {
        Instant currentTime = worldTimeResource.getGameTime();
        FarmingCoopAsset coopAsset = this.getCoopAsset();
        if (coopAsset == null) {
            return;
        }
        Map<String, String> produceDropsMap = coopAsset.getProduceDrops();
        if (produceDropsMap.isEmpty()) {
            return;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        ArrayList<ItemStack> generatedItemDrops = new ArrayList<ItemStack>();
        for (CoopResident resident : this.residents) {
            ItemDropList dropListAsset;
            Instant lastProduced = resident.getLastProduced();
            if (lastProduced == null) {
                resident.setLastProduced(currentTime);
                continue;
            }
            CapturedNPCMetadata residentMeta = resident.getMetadata();
            int npcRoleIndex = residentMeta.getRoleIndex();
            String npcName = NPCPlugin.get().getName(npcRoleIndex);
            String npcDropListName = produceDropsMap.get(npcName);
            if (npcDropListName == null || (dropListAsset = ItemDropList.getAssetMap().getAsset(npcDropListName)) == null) continue;
            Duration harvestDiff = Duration.between(lastProduced, currentTime);
            long hoursSinceLastHarvest = harvestDiff.toHours();
            int produceCount = MathUtil.ceil((float)hoursSinceLastHarvest / (float)WorldTimeResource.HOURS_PER_DAY);
            ArrayList<ItemDrop> configuredItemDrops = new ArrayList<ItemDrop>();
            for (int i = 0; i < produceCount; ++i) {
                dropListAsset.getContainer().populateDrops(configuredItemDrops, random::nextDouble, npcDropListName);
                for (ItemDrop drop : configuredItemDrops) {
                    if (drop == null || drop.getItemId() == null) {
                        ((HytaleLogger.Api)HytaleLogger.forEnclosingClass().atWarning()).log("Tried to create ItemDrop for non-existent item in drop list id '%s'", npcDropListName);
                        continue;
                    }
                    int amount = drop.getRandomQuantity(random);
                    if (amount <= 0) continue;
                    generatedItemDrops.add(new ItemStack(drop.getItemId(), amount, drop.getMetadata()));
                }
                configuredItemDrops.clear();
            }
            resident.setLastProduced(currentTime);
        }
        this.itemContainer.addItemStacks(generatedItemDrops);
    }

    public void gatherProduceFromInventory(ItemContainer playerInventory) {
        for (ItemStack item : this.itemContainer.removeAllItemStacks()) {
            playerInventory.addItemStack(item);
        }
    }

    public void ensureSpawnResidentsInWorld(World world, Store<EntityStore> store, Vector3d coopLocation, Vector3d spawnOffset) {
        NPCPlugin npcModule = NPCPlugin.get();
        FarmingCoopAsset coopAsset = this.getCoopAsset();
        if (coopAsset == null) {
            return;
        }
        float radiansPerSpawn = (float)Math.PI * 2 / (float)coopAsset.getMaxResidents();
        Vector3d spawnOffsetIteration = spawnOffset;
        SpawningContext spawningContext = new SpawningContext();
        for (CoopResident resident : this.residents) {
            CapturedNPCMetadata residentMeta = resident.getMetadata();
            int npcRoleIndex = residentMeta.getRoleIndex();
            boolean residentDeployed = resident.getDeployedToWorld();
            PersistentRef residentEntityId = resident.getPersistentRef();
            if (residentDeployed || residentEntityId != null) continue;
            Vector3d residentSpawnLocation = new Vector3d().assign(coopLocation).add(spawnOffsetIteration);
            Builder<Role> roleBuilder = NPCPlugin.get().tryGetCachedValidRole(npcRoleIndex);
            if (roleBuilder == null) continue;
            spawningContext.setSpawnable((ISpawnableWithModel)((Object)roleBuilder));
            if (!spawningContext.set(world, residentSpawnLocation.x, residentSpawnLocation.y, residentSpawnLocation.z) || spawningContext.canSpawn() != SpawnTestResult.TEST_OK) continue;
            Pair<Ref<EntityStore>, NPCEntity> npcPair = npcModule.spawnEntity(store, npcRoleIndex, spawningContext.newPosition(), Vector3f.ZERO, null, null);
            if (npcPair == null) {
                resident.setPersistentRef(null);
                resident.setDeployedToWorld(false);
                continue;
            }
            Ref<EntityStore> npcRef = npcPair.first();
            NPCEntity npcComponent = npcPair.second();
            npcComponent.getLeashPoint().assign(coopLocation);
            if (npcRef == null || !npcRef.isValid()) {
                resident.setPersistentRef(null);
                resident.setDeployedToWorld(false);
                continue;
            }
            UUIDComponent uuidComponent = store.getComponent(npcRef, UUIDComponent.getComponentType());
            if (uuidComponent == null) {
                resident.setPersistentRef(null);
                resident.setDeployedToWorld(false);
                continue;
            }
            CoopResidentComponent coopResidentComponent = new CoopResidentComponent();
            coopResidentComponent.setCoopLocation(coopLocation.toVector3i());
            store.addComponent(npcRef, CoopResidentComponent.getComponentType(), coopResidentComponent);
            PersistentRef persistentRef = new PersistentRef();
            persistentRef.setEntity(npcRef, uuidComponent.getUuid());
            resident.setPersistentRef(persistentRef);
            resident.setDeployedToWorld(true);
            spawnOffsetIteration = spawnOffsetIteration.rotateY(radiansPerSpawn);
        }
    }

    public void ensureNoResidentsInWorld(Store<EntityStore> store) {
        ArrayList<CoopResident> residentsToRemove = new ArrayList<CoopResident>();
        for (CoopResident resident : this.residents) {
            boolean deployed = resident.getDeployedToWorld();
            PersistentRef entityUuid = resident.getPersistentRef();
            if (!deployed && entityUuid == null) continue;
            Ref<EntityStore> entityRef = entityUuid.getEntity(store);
            if (entityRef == null) {
                residentsToRemove.add(resident);
                continue;
            }
            CoopResidentComponent coopResidentComponent = store.getComponent(entityRef, CoopResidentComponent.getComponentType());
            if (coopResidentComponent == null) {
                residentsToRemove.add(resident);
                continue;
            }
            DeathComponent deathComponent = store.getComponent(entityRef, DeathComponent.getComponentType());
            if (deathComponent != null) {
                residentsToRemove.add(resident);
                continue;
            }
            coopResidentComponent.setMarkedForDespawn(true);
            resident.setPersistentRef(null);
            resident.setDeployedToWorld(false);
        }
        for (CoopResident resident : residentsToRemove) {
            this.residents.remove(resident);
        }
    }

    public boolean shouldResidentsBeInCoop(WorldTimeResource worldTimeResource) {
        FarmingCoopAsset coopAsset = this.getCoopAsset();
        if (coopAsset == null) {
            return true;
        }
        IntRange roamTimeRange = coopAsset.getResidentRoamTime();
        if (roamTimeRange == null) {
            return true;
        }
        int gameHour = worldTimeResource.getCurrentHour();
        return !roamTimeRange.includes(gameHour);
    }

    @Nullable
    public Instant getNextScheduledTick(WorldTimeResource worldTimeResource) {
        Instant gameTime = worldTimeResource.getGameTime();
        LocalDateTime gameDateTime = worldTimeResource.getGameDateTime();
        int gameHour = worldTimeResource.getCurrentHour();
        int minutes = gameDateTime.getMinute();
        FarmingCoopAsset coopAsset = this.getCoopAsset();
        if (coopAsset == null) {
            return null;
        }
        IntRange roamTimeRange = coopAsset.getResidentRoamTime();
        if (roamTimeRange == null) {
            return null;
        }
        int nextScheduledHour = 0;
        int minTime = roamTimeRange.getInclusiveMin();
        int maxTime = roamTimeRange.getInclusiveMax();
        nextScheduledHour = coopAsset.getResidentRoamTime().includes(gameHour) ? coopAsset.getResidentRoamTime().getInclusiveMax() + 1 - gameHour : (gameHour > maxTime ? WorldTimeResource.HOURS_PER_DAY - gameHour + minTime : minTime - gameHour);
        return gameTime.plus((long)nextScheduledHour * 60L - (long)minutes, ChronoUnit.MINUTES);
    }

    public void handleResidentDespawn(UUID entityUuid) {
        CoopResident removedResident = null;
        for (CoopResident resident : this.residents) {
            if (resident.persistentRef == null || resident.persistentRef.getUuid() != entityUuid) continue;
            removedResident = resident;
            break;
        }
        if (removedResident == null) {
            return;
        }
        this.residents.remove(removedResident);
    }

    public void handleBlockBroken(World world, WorldTimeResource worldTimeResource, Store<EntityStore> store, int blockX, int blockY, int blockZ) {
        Vector3i location = new Vector3i(blockX, blockY, blockZ);
        world.execute(() -> this.ensureSpawnResidentsInWorld(world, store, location.toVector3d(), new Vector3d().assign(Vector3d.FORWARD)));
        this.generateProduceToInventory(worldTimeResource);
        Vector3d dropPosition = new Vector3d((float)blockX + 0.5f, blockY, (float)blockZ + 0.5f);
        Holder[] itemEntityHolders = ItemComponent.generateItemDrops(store, this.itemContainer.removeAllItemStacks(), dropPosition, Vector3f.ZERO);
        if (itemEntityHolders.length > 0) {
            world.execute(() -> store.addEntities(itemEntityHolders, AddReason.SPAWN));
        }
        world.execute(() -> {
            for (CoopResident resident : this.residents) {
                PersistentRef persistentRef = resident.getPersistentRef();
                if (persistentRef == null) continue;
                Ref<EntityStore> ref = persistentRef.getEntity(store);
                if (ref == null) {
                    return;
                }
                store.tryRemoveComponent(ref, CoopResidentComponent.getComponentType());
            }
        });
    }

    public boolean hasProduce() {
        return !this.itemContainer.isEmpty();
    }

    @Override
    public Component<ChunkStore> clone() {
        return new CoopBlock(this.coopAssetId, this.residents, this.itemContainer);
    }

    public static class CoopResident {
        public static final BuilderCodec<CoopResident> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CoopResident.class, CoopResident::new).append(new KeyedCodec<CapturedNPCMetadata>("Metadata", CapturedNPCMetadata.CODEC), (coop, meta) -> {
            coop.metadata = meta;
        }, coop -> coop.metadata).add()).append(new KeyedCodec<PersistentRef>("PersistentRef", PersistentRef.CODEC), (coop, persistentRef) -> {
            coop.persistentRef = persistentRef;
        }, coop -> coop.persistentRef).add()).append(new KeyedCodec<Boolean>("DeployedToWorld", Codec.BOOLEAN), (coop, deployedToWorld) -> {
            coop.deployedToWorld = deployedToWorld;
        }, coop -> coop.deployedToWorld).add()).append(new KeyedCodec("LastHarvested", Codec.INSTANT), (coop, instant) -> {
            coop.lastProduced = instant;
        }, coop -> coop.lastProduced).add()).build();
        protected CapturedNPCMetadata metadata;
        @Nullable
        protected PersistentRef persistentRef;
        protected boolean deployedToWorld;
        protected Instant lastProduced;

        public CoopResident() {
        }

        public CoopResident(CapturedNPCMetadata metadata, PersistentRef persistentRef, Instant lastProduced) {
            this.metadata = metadata;
            this.persistentRef = persistentRef;
            this.lastProduced = lastProduced;
        }

        public CapturedNPCMetadata getMetadata() {
            return this.metadata;
        }

        @Nullable
        public PersistentRef getPersistentRef() {
            return this.persistentRef;
        }

        public void setPersistentRef(@Nullable PersistentRef persistentRef) {
            this.persistentRef = persistentRef;
        }

        public boolean getDeployedToWorld() {
            return this.deployedToWorld;
        }

        public void setDeployedToWorld(boolean deployedToWorld) {
            this.deployedToWorld = deployedToWorld;
        }

        public Instant getLastProduced() {
            return this.lastProduced;
        }

        public void setLastProduced(Instant lastProduced) {
            this.lastProduced = lastProduced;
        }
    }
}

