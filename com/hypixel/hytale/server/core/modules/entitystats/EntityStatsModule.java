/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entitystats;

import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.JsonAsset;
import com.hypixel.hytale.assetstore.codec.AssetCodec;
import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemType;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.particle.config.ParticleSystem;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.AllLegacyLivingEntityTypesQuery;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsSystems;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatTypePacketGenerator;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.AliveCondition;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.ChargingCondition;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.Condition;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.EnvironmentCondition;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.GlidingCondition;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.LogicCondition;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.NoDamageTakenCondition;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.OutOfCombatCondition;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.PlayerCondition;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.RegenHealthCondition;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.SprintingCondition;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.StatCondition;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.SuffocatingCondition;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.WieldingCondition;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bouncycastle.util.Arrays;

public class EntityStatsModule
extends JavaPlugin {
    public static final PluginManifest MANIFEST = PluginManifest.corePlugin(EntityStatsModule.class).depends(EntityModule.class).depends(InteractionModule.class).build();
    private static EntityStatsModule instance;
    private ComponentType<EntityStore, EntityStatMap> entityStatMapComponentType;
    private SystemType<EntityStore, EntityStatsSystems.StatModifyingSystem> statModifyingSystemType;

    public static EntityStatsModule get() {
        return instance;
    }

    public EntityStatsModule(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        Modifier.CODEC.register("Boost", (Class<Modifier>)StaticModifier.class, (Codec<Modifier>)StaticModifier.ENTITY_CODEC);
        Modifier.CODEC.register("Static", (Class<Modifier>)StaticModifier.class, (Codec<Modifier>)StaticModifier.ENTITY_CODEC);
        Condition.CODEC.register("LogicCondition", (Class<Condition>)LogicCondition.class, (Codec<Condition>)LogicCondition.CODEC);
        Condition.CODEC.register("RegenHealth", (Class<Condition>)RegenHealthCondition.class, (Codec<Condition>)RegenHealthCondition.CODEC);
        Condition.CODEC.register("NoDamageTaken", (Class<Condition>)NoDamageTakenCondition.class, (Codec<Condition>)NoDamageTakenCondition.CODEC);
        Condition.CODEC.register("Suffocating", (Class<Condition>)SuffocatingCondition.class, (Codec<Condition>)SuffocatingCondition.CODEC);
        Condition.CODEC.register("Charging", (Class<Condition>)ChargingCondition.class, (Codec<Condition>)ChargingCondition.CODEC);
        Condition.CODEC.register("Alive", (Class<Condition>)AliveCondition.class, (Codec<Condition>)AliveCondition.CODEC);
        Condition.CODEC.register("Environment", (Class<Condition>)EnvironmentCondition.class, (Codec<Condition>)EnvironmentCondition.CODEC);
        Condition.CODEC.register("Player", (Class<Condition>)PlayerCondition.class, (Codec<Condition>)PlayerCondition.CODEC);
        Condition.CODEC.register("OutOfCombat", (Class<Condition>)OutOfCombatCondition.class, (Codec<Condition>)OutOfCombatCondition.CODEC);
        Condition.CODEC.register("Wielding", (Class<Condition>)WieldingCondition.class, (Codec<Condition>)WieldingCondition.CODEC);
        Condition.CODEC.register("Sprinting", (Class<Condition>)SprintingCondition.class, (Codec<Condition>)SprintingCondition.CODEC);
        Condition.CODEC.register("Gliding", (Class<Condition>)GlidingCondition.class, (Codec<Condition>)GlidingCondition.CODEC);
        Condition.CODEC.register("Stat", (Class<Condition>)StatCondition.class, (Codec<Condition>)StatCondition.CODEC);
        AssetRegistry.register(((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)HytaleAssetStore.builder(EntityStatType.class, new IndexedLookupTableAssetMap(EntityStatType[]::new)).setPath("Entity/Stats")).setCodec((AssetCodec)EntityStatType.CODEC)).setKeyFunction(EntityStatType::getId)).setPacketGenerator(new EntityStatTypePacketGenerator()).setReplaceOnRemove(EntityStatType::getUnknownFor)).preLoadAssets(Collections.singletonList(EntityStatType.UNKNOWN))).loadsAfter(SoundEvent.class, ParticleSystem.class)).build());
        this.getEventRegistry().register(LoadedAssetsEvent.class, EntityStatType.class, this::onLoadedAssetsEvent);
        this.statModifyingSystemType = this.getEntityStoreRegistry().registerSystemType(EntityStatsSystems.StatModifyingSystem.class);
        this.entityStatMapComponentType = this.getEntityStoreRegistry().registerComponent(EntityStatMap.class, "EntityStats", EntityStatMap.CODEC);
        this.getEntityStoreRegistry().registerSystem(new EntityStatsSystems.Setup(this.entityStatMapComponentType));
        this.getEntityStoreRegistry().registerSystem(new PlayerRegenerateStatsSystem(this));
        this.getEntityStoreRegistry().registerSystem(new EntityStatsSystems.Recalculate(this.entityStatMapComponentType));
        this.getEntityStoreRegistry().registerSystem(new EntityStatsSystems.EntityTrackerUpdate(this.entityStatMapComponentType));
        this.getEntityStoreRegistry().registerSystem(new EntityStatsSystems.EntityTrackerRemove(this.entityStatMapComponentType));
        this.getEntityStoreRegistry().registerSystem(new EntityStatsSystems.Changes(this.entityStatMapComponentType));
        this.getEntityStoreRegistry().registerSystem(new EntityStatsSystems.ClearChanges(this.entityStatMapComponentType));
        this.getEventRegistry().register(LoadedAssetsEvent.class, Item.class, x$0 -> EntityStatsModule.onLoadedAssetsInvalidate(x$0));
        this.getEventRegistry().register(LoadedAssetsEvent.class, EntityEffect.class, x$0 -> EntityStatsModule.onLoadedAssetsInvalidate(x$0));
    }

    @Override
    protected void start() {
        DefaultEntityStatTypes.update();
        if (DefaultEntityStatTypes.getHealth() == Integer.MIN_VALUE || DefaultEntityStatTypes.getOxygen() == Integer.MIN_VALUE || DefaultEntityStatTypes.getMana() == Integer.MIN_VALUE || DefaultEntityStatTypes.getStamina() == Integer.MIN_VALUE || DefaultEntityStatTypes.getSignatureEnergy() == Integer.MIN_VALUE || DefaultEntityStatTypes.getAmmo() == Integer.MIN_VALUE) {
            throw new IllegalStateException("Missing default EntityStatType");
        }
    }

    @Nullable
    @Deprecated(forRemoval=true)
    public static EntityStatMap get(@Nonnull Entity entity) {
        Ref<EntityStore> ref = entity.getReference();
        if (ref == null || !ref.isValid()) {
            return null;
        }
        Store<EntityStore> store = ref.getStore();
        return store.getComponent(ref, EntityStatsModule.get().getEntityStatMapComponentType());
    }

    private void onLoadedAssetsEvent(LoadedAssetsEvent<String, EntityStatType, IndexedLookupTableAssetMap<String, EntityStatType>> event) {
        DefaultEntityStatTypes.update();
        Universe.get().getWorlds().forEach((s, world) -> world.execute(() -> {
            Store<EntityStore> store = world.getEntityStore().getStore();
            store.forEachEntityParallel(EntityStatMap.getComponentType(), (index, archetypeChunk, commandBuffer) -> archetypeChunk.getComponent(index, EntityStatMap.getComponentType()).update());
        }));
    }

    private static <K, T extends JsonAsset<K>, M extends AssetMap<K, T>> void onLoadedAssetsInvalidate(LoadedAssetsEvent<K, T, M> event) {
        Universe.get().getWorlds().forEach((s, world) -> world.execute(() -> {
            Store<EntityStore> store = world.getEntityStore().getStore();
            store.forEachEntityParallel(AllLegacyLivingEntityTypesQuery.INSTANCE, (index, archetypeChunk, commandBuffer) -> {
                LivingEntity livingEntity = (LivingEntity)EntityUtils.getEntity(index, archetypeChunk);
                assert (livingEntity != null);
                livingEntity.getStatModifiersManager().setRecalculate(true);
            });
        }));
    }

    @Nullable
    public static Int2FloatMap resolveEntityStats(@Nullable Object2FloatMap<String> raw) {
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        Int2FloatOpenHashMap out = null;
        for (Object2FloatMap.Entry entry : raw.object2FloatEntrySet()) {
            int index = EntityStatType.getAssetMap().getIndex((String)entry.getKey());
            if (index == Integer.MIN_VALUE) continue;
            if (out == null) {
                out = new Int2FloatOpenHashMap();
            }
            out.put(index, entry.getFloatValue());
        }
        return out;
    }

    @Nullable
    public static <T> Int2ObjectMap<T> resolveEntityStats(@Nullable Map<String, T> raw) {
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        Int2ObjectOpenHashMap<T> out = null;
        for (Map.Entry<String, T> entry : raw.entrySet()) {
            int index = EntityStatType.getAssetMap().getIndex(entry.getKey());
            if (index == Integer.MIN_VALUE) continue;
            if (out == null) {
                out = new Int2ObjectOpenHashMap<T>();
            }
            out.put(index, entry.getValue());
        }
        return out;
    }

    @Nullable
    public static int[] resolveEntityStats(@Nullable String[] raw) {
        if (Arrays.isNullOrEmpty(raw)) {
            return null;
        }
        int[] out = new int[raw.length];
        int size = 0;
        for (int i = 0; i < raw.length; ++i) {
            int index = EntityStatType.getAssetMap().getIndex(raw[i]);
            if (index == Integer.MIN_VALUE) continue;
            out[size++] = index;
        }
        if (size != raw.length) {
            out = Arrays.copyOf(out, size);
        }
        return out;
    }

    public ComponentType<EntityStore, EntityStatMap> getEntityStatMapComponentType() {
        return this.entityStatMapComponentType;
    }

    public SystemType<EntityStore, EntityStatsSystems.StatModifyingSystem> getStatModifyingSystemType() {
        return this.statModifyingSystemType;
    }

    public class PlayerRegenerateStatsSystem
    extends EntityStatsSystems.Regenerate<Player> {
        public PlayerRegenerateStatsSystem(EntityStatsModule this$0) {
            super(this$0.entityStatMapComponentType, Player.getComponentType());
        }
    }
}

