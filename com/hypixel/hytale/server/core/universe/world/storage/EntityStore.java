/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.storage;

import com.hypixel.hytale.codec.store.CodecKey;
import com.hypixel.hytale.codec.store.CodecStore;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.IResourceStorage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.metrics.MetricsRegistry;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldProvider;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityStore
implements WorldProvider {
    @Nonnull
    public static final MetricsRegistry<EntityStore> METRICS_REGISTRY = new MetricsRegistry<EntityStore>().register("Store", EntityStore::getStore, Store.METRICS_REGISTRY);
    @Nonnull
    public static final ComponentRegistry<EntityStore> REGISTRY = new ComponentRegistry();
    @Nonnull
    public static final CodecKey<Holder<EntityStore>> HOLDER_CODEC_KEY = new CodecKey("EntityHolder");
    @Nonnull
    public static final SystemGroup<EntityStore> SEND_PACKET_GROUP;
    @Nonnull
    private final AtomicInteger networkIdCounter = new AtomicInteger(1);
    @Nonnull
    private final World world;
    private Store<EntityStore> store;
    @Nonnull
    private final Map<UUID, Ref<EntityStore>> entitiesByUuid = new ConcurrentHashMap<UUID, Ref<EntityStore>>();
    @Nonnull
    private final Int2ObjectMap<Ref<EntityStore>> networkIdToRef = new Int2ObjectOpenHashMap<Ref<EntityStore>>();

    public EntityStore(@Nonnull World world) {
        this.world = world;
    }

    public void start(@Nonnull IResourceStorage resourceStorage) {
        this.store = REGISTRY.addStore(this, resourceStorage, store -> {
            this.store = store;
        });
    }

    public void shutdown() {
        this.store.shutdown();
        this.entitiesByUuid.clear();
    }

    public Store<EntityStore> getStore() {
        return this.store;
    }

    @Nullable
    public Ref<EntityStore> getRefFromUUID(@Nonnull UUID uuid) {
        return this.entitiesByUuid.get(uuid);
    }

    @Nullable
    public Ref<EntityStore> getRefFromNetworkId(int networkId) {
        return (Ref)this.networkIdToRef.get(networkId);
    }

    public int takeNextNetworkId() {
        return this.networkIdCounter.getAndIncrement();
    }

    @Override
    @Nonnull
    public World getWorld() {
        return this.world;
    }

    static {
        CodecStore.STATIC.putCodecSupplier(HOLDER_CODEC_KEY, REGISTRY::getEntityCodec);
        SEND_PACKET_GROUP = REGISTRY.registerSystemGroup();
    }

    public static class NetworkIdSystem
    extends RefSystem<EntityStore> {
        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return NetworkId.getComponentType();
        }

        @Override
        public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            EntityStore entityStore = store.getExternalData();
            NetworkId networkIdComponent = commandBuffer.getComponent(ref, NetworkId.getComponentType());
            assert (networkIdComponent != null);
            int networkId = networkIdComponent.getId();
            if (entityStore.networkIdToRef.putIfAbsent(networkId, ref) != null) {
                networkId = entityStore.takeNextNetworkId();
                commandBuffer.putComponent(ref, NetworkId.getComponentType(), new NetworkId(networkId));
                entityStore.networkIdToRef.put(networkId, ref);
            }
        }

        @Override
        public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            EntityStore entityStore = store.getExternalData();
            NetworkId networkIdComponent = commandBuffer.getComponent(ref, NetworkId.getComponentType());
            assert (networkIdComponent != null);
            entityStore.networkIdToRef.remove(networkIdComponent.getId(), (Object)ref);
        }
    }

    public static class UUIDSystem
    extends RefSystem<EntityStore> {
        @Nonnull
        private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return UUIDComponent.getComponentType();
        }

        @Override
        public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            UUIDComponent uuidComponent = commandBuffer.getComponent(ref, UUIDComponent.getComponentType());
            assert (uuidComponent != null);
            Ref<EntityStore> currentRef = store.getExternalData().entitiesByUuid.putIfAbsent(uuidComponent.getUuid(), ref);
            if (currentRef != null) {
                LOGGER.at(Level.WARNING).log("Removing duplicate entity with UUID: %s", uuidComponent.getUuid());
                commandBuffer.removeEntity(ref, RemoveReason.REMOVE);
            }
        }

        @Override
        public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            UUIDComponent uuidComponent = commandBuffer.getComponent(ref, UUIDComponent.getComponentType());
            assert (uuidComponent != null);
            store.getExternalData().entitiesByUuid.remove(uuidComponent.getUuid(), ref);
        }
    }
}

