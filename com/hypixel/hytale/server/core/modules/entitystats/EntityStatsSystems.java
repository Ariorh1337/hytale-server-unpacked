/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entitystats;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.dependency.SystemTypeDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.ISystem;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.ComponentUpdate;
import com.hypixel.hytale.protocol.ComponentUpdateType;
import com.hypixel.hytale.protocol.EntityStatUpdate;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entity.AllLegacyLivingEntityTypesQuery;
import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.RegeneratingValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityStatsSystems {

    public static class ClearChanges
    extends EntityTickingSystem<EntityStore> {
        private final ComponentType<EntityStore, EntityStatMap> componentType;
        private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemDependency(Order.AFTER, EntityTrackerUpdate.class));

        public ClearChanges(ComponentType<EntityStore, EntityStatMap> componentType) {
            this.componentType = componentType;
        }

        @Override
        public Query<EntityStore> getQuery() {
            return this.componentType;
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return this.dependencies;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            EntityStatMap statMap = archetypeChunk.getComponent(index, this.componentType);
            statMap.clearUpdates();
        }
    }

    public static class EntityTrackerRemove
    extends RefChangeSystem<EntityStore, EntityStatMap> {
        private final ComponentType<EntityStore, EntityStatMap> componentType;
        private final ComponentType<EntityStore, EntityTrackerSystems.Visible> visibleComponentType = EntityTrackerSystems.Visible.getComponentType();
        @Nonnull
        private final Query<EntityStore> query;

        public EntityTrackerRemove(ComponentType<EntityStore, EntityStatMap> componentType) {
            this.componentType = componentType;
            this.query = Query.and(this.visibleComponentType, componentType);
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.query;
        }

        @Override
        @Nonnull
        public ComponentType<EntityStore, EntityStatMap> componentType() {
            return this.componentType;
        }

        @Override
        public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull EntityStatMap component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        }

        @Override
        public void onComponentSet(@Nonnull Ref<EntityStore> ref, EntityStatMap oldComponent, @Nonnull EntityStatMap newComponent, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        }

        @Override
        public void onComponentRemoved(@Nonnull Ref<EntityStore> ref, @Nonnull EntityStatMap component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            for (EntityTrackerSystems.EntityViewer viewer : store.getComponent(ref, this.visibleComponentType).visibleTo.values()) {
                viewer.queueRemove(ref, ComponentUpdateType.EntityStats);
            }
        }
    }

    public static class EntityTrackerUpdate
    extends EntityTickingSystem<EntityStore> {
        private final ComponentType<EntityStore, EntityStatMap> componentType;
        private final ComponentType<EntityStore, EntityTrackerSystems.Visible> visibleComponentType = EntityTrackerSystems.Visible.getComponentType();
        @Nonnull
        private final Query<EntityStore> query;
        private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemDependency(Order.BEFORE, EntityTrackerSystems.EffectControllerSystem.class), new SystemTypeDependency<EntityStore, StatModifyingSystem>(Order.AFTER, EntityStatsModule.get().getStatModifyingSystemType()));

        public EntityTrackerUpdate(ComponentType<EntityStore, EntityStatMap> componentType) {
            this.componentType = componentType;
            this.query = Query.and(this.visibleComponentType, componentType);
        }

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return EntityTrackerSystems.QUEUE_UPDATE_GROUP;
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.query;
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return this.dependencies;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            EntityTrackerSystems.EntityViewer selfEntityViewer;
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            EntityTrackerSystems.Visible visible = archetypeChunk.getComponent(index, this.visibleComponentType);
            EntityStatMap statMap = archetypeChunk.getComponent(index, this.componentType);
            if (!visible.newlyVisibleTo.isEmpty()) {
                EntityTrackerUpdate.queueUpdatesForNewlyVisible(ref, statMap, visible.newlyVisibleTo);
            }
            if (statMap.consumeSelfNetworkOutdated() && (selfEntityViewer = visible.visibleTo.get(ref)) != null && !visible.newlyVisibleTo.containsKey(ref)) {
                ComponentUpdate update = new ComponentUpdate();
                update.type = ComponentUpdateType.EntityStats;
                update.entityStatUpdates = statMap.consumeSelfUpdates();
                selfEntityViewer.queueUpdate(ref, update);
            }
            if (statMap.consumeNetworkOutdated()) {
                ComponentUpdate update = new ComponentUpdate();
                update.type = ComponentUpdateType.EntityStats;
                update.entityStatUpdates = statMap.consumeOtherUpdates();
                for (Map.Entry<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> entry : visible.visibleTo.entrySet()) {
                    Ref<EntityStore> viewerRef = entry.getKey();
                    if (visible.newlyVisibleTo.containsKey(viewerRef) || ref.equals(viewerRef)) continue;
                    entry.getValue().queueUpdate(ref, update);
                }
            }
        }

        private static void queueUpdatesForNewlyVisible(@Nonnull Ref<EntityStore> ref, @Nonnull EntityStatMap statMap, @Nonnull Map<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> newlyVisibleTo) {
            ComponentUpdate update = new ComponentUpdate();
            update.type = ComponentUpdateType.EntityStats;
            update.entityStatUpdates = statMap.createInitUpdate(false);
            for (Map.Entry<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> entry : newlyVisibleTo.entrySet()) {
                if (ref.equals(entry.getKey())) {
                    EntityTrackerUpdate.queueUpdateForNewlyVisibleSelf(ref, statMap, entry.getValue());
                    continue;
                }
                entry.getValue().queueUpdate(ref, update);
            }
        }

        private static void queueUpdateForNewlyVisibleSelf(Ref<EntityStore> ref, @Nonnull EntityStatMap statMap, @Nonnull EntityTrackerSystems.EntityViewer viewer) {
            ComponentUpdate update = new ComponentUpdate();
            update.type = ComponentUpdateType.EntityStats;
            update.entityStatUpdates = statMap.createInitUpdate(true);
            viewer.queueUpdate(ref, update);
        }
    }

    public static class Recalculate
    extends EntityTickingSystem<EntityStore> {
        @Nonnull
        private final ComponentType<EntityStore, EntityStatMap> entityStatMapComponentType;
        @Nonnull
        private final Query<EntityStore> query;

        public Recalculate(@Nonnull ComponentType<EntityStore, EntityStatMap> entityStatMapComponentType) {
            this.entityStatMapComponentType = entityStatMapComponentType;
            this.query = Query.and(AllLegacyLivingEntityTypesQuery.INSTANCE, entityStatMapComponentType);
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.query;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            LivingEntity livingEntity = (LivingEntity)EntityUtils.getEntity(index, archetypeChunk);
            assert (livingEntity != null);
            EntityStatMap entityStatMapComponent = archetypeChunk.getComponent(index, this.entityStatMapComponentType);
            assert (entityStatMapComponent != null);
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            livingEntity.getStatModifiersManager().recalculateEntityStatModifiers(ref, entityStatMapComponent, commandBuffer);
        }
    }

    public static class Changes
    extends EntityTickingSystem<EntityStore> {
        private final ComponentType<EntityStore, EntityStatMap> componentType;
        @Nonnull
        private final Query<EntityStore> query;
        private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemDependency(Order.BEFORE, EntityTrackerUpdate.class), new SystemTypeDependency<EntityStore, StatModifyingSystem>(Order.AFTER, EntityStatsModule.get().getStatModifyingSystemType()));

        public Changes(ComponentType<EntityStore, EntityStatMap> componentType) {
            this.componentType = componentType;
            this.query = Query.and(componentType, InteractionModule.get().getInteractionManagerComponent(), AllLegacyLivingEntityTypesQuery.INSTANCE);
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return this.dependencies;
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.query;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return false;
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            EntityStatMap entityStatMapComponent = archetypeChunk.getComponent(index, this.componentType);
            assert (entityStatMapComponent != null);
            InteractionManager interactionManagerComponent = archetypeChunk.getComponent(index, InteractionModule.get().getInteractionManagerComponent());
            assert (interactionManagerComponent != null);
            boolean isDead = archetypeChunk.getArchetype().contains(DeathComponent.getComponentType());
            Int2ObjectMap<List<EntityStatUpdate>> statChanges = entityStatMapComponent.getSelfUpdates();
            Int2ObjectMap<FloatList> statValues = entityStatMapComponent.getSelfStatValues();
            for (int statIndex = 0; statIndex < entityStatMapComponent.size(); ++statIndex) {
                List updates = (List)statChanges.get(statIndex);
                if (updates == null || updates.isEmpty()) continue;
                FloatList statChangeList = (FloatList)statValues.get(statIndex);
                EntityStatValue entityStatValue = entityStatMapComponent.get(statIndex);
                if (entityStatValue == null) continue;
                EntityStatType entityStatType = EntityStatType.getAssetMap().getAsset(statIndex);
                for (int i = 0; i < updates.size(); ++i) {
                    EntityStatUpdate update = (EntityStatUpdate)updates.get(i);
                    float statPrevious = statChangeList.getFloat(i * 2);
                    float statValue = statChangeList.getFloat(i * 2 + 1);
                    if (Changes.testMaxValue(statValue, statPrevious, entityStatValue, entityStatType.getMaxValueEffects())) {
                        Changes.runInteractions(ref, interactionManagerComponent, entityStatType.getMaxValueEffects(), commandBuffer);
                    }
                    if (Changes.testMinValue(statValue, statPrevious, entityStatValue, entityStatType.getMinValueEffects())) {
                        Changes.runInteractions(ref, interactionManagerComponent, entityStatType.getMinValueEffects(), commandBuffer);
                    }
                    if (isDead || statIndex != DefaultEntityStatTypes.getHealth() || update.value > 0.0f || !(statValue <= entityStatValue.getMin())) continue;
                    DeathComponent.tryAddComponent(commandBuffer, archetypeChunk.getReferenceTo(index), new Damage(Damage.NULL_SOURCE, DamageCause.COMMAND, 0.0f));
                    isDead = true;
                }
            }
        }

        private static boolean testMaxValue(float value, float previousValue, @Nonnull EntityStatValue stat, @Nullable EntityStatType.EntityStatEffects valueEffects) {
            if (valueEffects == null) {
                return false;
            }
            if (valueEffects.triggerAtZero() && stat.getMax() > 0.0f) {
                return previousValue < 0.0f && value >= 0.0f;
            }
            return previousValue != stat.getMax() && value == stat.getMax();
        }

        private static boolean testMinValue(float value, float previousValue, @Nonnull EntityStatValue stat, @Nullable EntityStatType.EntityStatEffects valueEffects) {
            if (valueEffects == null) {
                return false;
            }
            if (valueEffects.triggerAtZero() && stat.getMin() < 0.0f) {
                return previousValue > 0.0f && value < 0.0f;
            }
            return previousValue != stat.getMin() && value == stat.getMin();
        }

        private static void runInteractions(@Nonnull Ref<EntityStore> ref, @Nonnull InteractionManager interactionManager, @Nullable EntityStatType.EntityStatEffects valueEffects, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (valueEffects == null) {
                return;
            }
            String interactions = valueEffects.getInteractions();
            if (interactions == null) {
                return;
            }
            InteractionContext context = InteractionContext.forInteraction(interactionManager, ref, InteractionType.EntityStatEffect, componentAccessor);
            InteractionChain chain = interactionManager.initChain(InteractionType.EntityStatEffect, context, RootInteraction.getRootInteractionOrUnknown(interactions), true);
            interactionManager.queueExecuteChain(chain);
        }
    }

    public static class Regenerate<EntityType extends LivingEntity>
    extends EntityTickingSystem<EntityStore>
    implements StatModifyingSystem {
        private final ComponentType<EntityStore, EntityStatMap> componentType;
        private final ComponentType<EntityStore, EntityType> entityTypeComponent;
        private final Query<EntityStore> query;

        public Regenerate(ComponentType<EntityStore, EntityStatMap> componentType, ComponentType<EntityStore, EntityType> entityTypeComponent) {
            this.componentType = componentType;
            this.entityTypeComponent = entityTypeComponent;
            this.query = Query.and(componentType, entityTypeComponent);
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.query;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return false;
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            EntityStatMap map = archetypeChunk.getComponent(index, this.componentType);
            assert (map != null);
            Instant now = store.getResource(TimeResource.getResourceType()).getNow();
            int size = map.size();
            if (map.tempRegenerationValues.length < size) {
                map.tempRegenerationValues = new float[size];
            }
            for (int statIndex = 1; statIndex < size; ++statIndex) {
                EntityStatValue value = map.get(statIndex);
                if (value == null) continue;
                map.tempRegenerationValues[statIndex] = 0.0f;
                RegeneratingValue[] regenerating = value.getRegeneratingValues();
                if (regenerating == null) continue;
                for (RegeneratingValue regeneratingValue : regenerating) {
                    if (regeneratingValue.getRegenerating().getAmount() > 0.0f ? value.get() >= value.getMax() : value.get() <= value.getMin()) continue;
                    int n = statIndex;
                    map.tempRegenerationValues[n] = map.tempRegenerationValues[n] + regeneratingValue.regenerate(commandBuffer, ref, now, dt, value, map.tempRegenerationValues[statIndex]);
                }
            }
            LivingEntity entity = (LivingEntity)archetypeChunk.getComponent(index, this.entityTypeComponent);
            assert (entity != null);
            ItemContainer armorContainer = entity.getInventory().getArmor();
            short armorContainerCapacity = armorContainer.getCapacity();
            for (short i = 0; i < armorContainerCapacity; i = (short)(i + 1)) {
                Item item;
                ItemStack itemStack = armorContainer.getItemStack(i);
                if (ItemStack.isEmpty(itemStack) || (item = itemStack.getItem()).getArmor() == null || item.getArmor().getRegeneratingValues() == null || item.getArmor().getRegeneratingValues().isEmpty()) continue;
                for (int statIndex = 1; statIndex < size; ++statIndex) {
                    List regenValues;
                    EntityStatValue value = map.get(statIndex);
                    if (value == null || (regenValues = (List)item.getArmor().getRegeneratingValues().get(statIndex)) == null || regenValues.isEmpty()) continue;
                    for (RegeneratingValue regeneratingValue : regenValues) {
                        if (regeneratingValue.getRegenerating().getAmount() > 0.0f ? value.get() >= value.getMax() : value.get() <= value.getMin()) continue;
                        int n = statIndex;
                        map.tempRegenerationValues[n] = map.tempRegenerationValues[n] + regeneratingValue.regenerate(commandBuffer, ref, now, dt, value, map.tempRegenerationValues[statIndex]);
                    }
                }
            }
            for (int statIndex = 1; statIndex < size; ++statIndex) {
                EntityStatValue value = map.get(statIndex);
                if (value == null) continue;
                float amount = map.tempRegenerationValues[statIndex];
                boolean invulnerable = commandBuffer.getArchetype(ref).contains(Invulnerable.getComponentType());
                if (amount < 0.0f && !value.getIgnoreInvulnerability() && invulnerable) {
                    return;
                }
                if (amount == 0.0f) continue;
                map.addStatValue(statIndex, amount);
            }
        }
    }

    public static class Setup
    extends HolderSystem<EntityStore> {
        private final ComponentType<EntityStore, EntityStatMap> componentType;

        public Setup(ComponentType<EntityStore, EntityStatMap> componentType) {
            this.componentType = componentType;
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return AllLegacyLivingEntityTypesQuery.INSTANCE;
        }

        @Override
        public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
            EntityStatMap stats = holder.getComponent(this.componentType);
            if (stats == null) {
                stats = holder.ensureAndGetComponent(this.componentType);
                stats.update();
            }
        }

        @Override
        public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
        }
    }

    public static interface StatModifyingSystem
    extends ISystem<EntityStore> {
    }
}

