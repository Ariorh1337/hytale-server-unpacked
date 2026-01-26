/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entity.damage;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.interface_.KillFeedMessage;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.gameplay.DeathConfig;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.asset.type.gameplay.WorldMapConfig;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.entity.AnimationUtils;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.entity.entities.player.pages.RespawnPage;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.modules.entity.AllLegacyLivingEntityTypesQuery;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeferredCorpseRemoval;
import com.hypixel.hytale.server.core.modules.entity.damage.event.KillFeedEvent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsSystems;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.modules.interaction.interaction.UnarmedInteractions;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeathSystems {
    private static void playDeathAnimation(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent deathComponent, @Nullable ModelComponent modelComponent, @Nonnull MovementStatesComponent movementStatesComponent, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (modelComponent == null) {
            return;
        }
        DamageCause deathCause = deathComponent.getDeathCause();
        if (deathCause == null) {
            return;
        }
        Model model = modelComponent.getModel();
        String[] animationIds = Entity.DefaultAnimations.getDeathAnimationIds(movementStatesComponent.getMovementStates(), deathCause);
        String selectedAnimationId = model.getFirstBoundAnimationId(animationIds);
        AnimationUtils.playAnimation(ref, AnimationSlot.Status, selectedAnimationId, true, componentAccessor);
    }

    public static class CorpseRemoval
    extends EntityTickingSystem<EntityStore> {
        @Nonnull
        private static final ComponentType<EntityStore, DeferredCorpseRemoval> DEFERRED_CORPSE_REMOVAL_COMPONENT_TYPE = DeferredCorpseRemoval.getComponentType();
        @Nonnull
        private static final Query<EntityStore> QUERY = Query.and(DeathComponent.getComponentType(), Query.not(Player.getComponentType()));

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            DeathComponent deathComponent = archetypeChunk.getComponent(index, DeathComponent.getComponentType());
            assert (deathComponent != null);
            InteractionChain deathInteractionChain = deathComponent.getInteractionChain();
            if (deathInteractionChain != null && deathInteractionChain.getServerState() == InteractionState.NotFinished) {
                return;
            }
            DeferredCorpseRemoval corpseRemoval = archetypeChunk.getComponent(index, DEFERRED_CORPSE_REMOVAL_COMPONENT_TYPE);
            if (corpseRemoval == null || corpseRemoval.tick(dt)) {
                commandBuffer.removeEntity(archetypeChunk.getReferenceTo(index), RemoveReason.REMOVE);
            }
        }
    }

    public static class SpawnedDeathAnimation
    extends RefSystem<EntityStore> {
        private static final Query<EntityStore> QUERY = Query.and(AllLegacyLivingEntityTypesQuery.INSTANCE, DeathComponent.getComponentType(), MovementStatesComponent.getComponentType());

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            DeathComponent deathComponent = commandBuffer.getComponent(ref, DeathComponent.getComponentType());
            assert (deathComponent != null);
            ModelComponent modelComponent = commandBuffer.getComponent(ref, ModelComponent.getComponentType());
            MovementStatesComponent movementStatesComponent = commandBuffer.getComponent(ref, MovementStatesComponent.getComponentType());
            assert (movementStatesComponent != null);
            DeathSystems.playDeathAnimation(ref, deathComponent, modelComponent, movementStatesComponent, commandBuffer);
        }

        @Override
        public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        }
    }

    public static class DeathAnimation
    extends OnDeathSystem {
        @Nonnull
        private final Query<EntityStore> query = Query.and(MovementStatesComponent.getComponentType(), AllLegacyLivingEntityTypesQuery.INSTANCE);
        @Nonnull
        private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemDependency(Order.BEFORE, EntityStatsSystems.EntityTrackerUpdate.class), new SystemDependency(Order.AFTER, ClearEntityEffects.class));

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
        public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            ModelComponent modelComponent = commandBuffer.getComponent(ref, ModelComponent.getComponentType());
            MovementStatesComponent movementStatesComponent = commandBuffer.getComponent(ref, MovementStatesComponent.getComponentType());
            assert (movementStatesComponent != null);
            DeathSystems.playDeathAnimation(ref, component, modelComponent, movementStatesComponent, commandBuffer);
        }
    }

    public static class PlayerDeathMarker
    extends OnDeathSystem {
        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return Player.getComponentType();
        }

        @Override
        public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            World world = commandBuffer.getExternalData().getWorld();
            GameplayConfig gameplayConfig = world.getGameplayConfig();
            WorldMapConfig worldMapConfigGameplayConfig = gameplayConfig.getWorldMapConfig();
            if (!worldMapConfigGameplayConfig.isDisplayDeathMarker()) {
                return;
            }
            Player playerComponent = commandBuffer.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            TransformComponent transformComponent = commandBuffer.getComponent(ref, TransformComponent.getComponentType());
            assert (transformComponent != null);
            Vector3d position = transformComponent.getPosition();
            Transform transform = new Transform(position.getX(), position.getY(), position.getZ(), 0.0f, 0.0f, 0.0f);
            WorldTimeResource worldTimeResource = commandBuffer.getResource(WorldTimeResource.getResourceType());
            Instant gameTime = worldTimeResource.getGameTime();
            int daysSinceWorldStart = (int)WorldTimeResource.ZERO_YEAR.until(gameTime, ChronoUnit.DAYS);
            String deathMarkerId = "death-marker-" + String.valueOf(UUID.randomUUID());
            PlayerWorldData perWorldData = playerComponent.getPlayerConfigData().getPerWorldData(world.getName());
            perWorldData.addLastDeath(deathMarkerId, transform, daysSinceWorldStart);
        }
    }

    public static class PlayerDeathScreen
    extends OnDeathSystem {
        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return Query.and(Player.getComponentType(), TransformComponent.getComponentType());
        }

        @Override
        public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            if (!component.isShowDeathMenu()) {
                return;
            }
            Damage deathInfo = component.getDeathInfo();
            Message deathMessage = deathInfo != null ? deathInfo.getDeathMessage(ref, commandBuffer) : null;
            component.setDeathMessage(deathMessage);
            PlayerRef playerRefComponent = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            PageManager pageManager = playerComponent.getPageManager();
            pageManager.openCustomPage(ref, store, new RespawnPage(playerRefComponent, deathMessage, component.displayDataOnDeathScreen(), component.getDeathItemLoss()));
        }
    }

    public static class KillFeed
    extends OnDeathSystem {
        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return Archetype.empty();
        }

        @Override
        public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            Damage.EntitySource entitySource;
            Ref<EntityStore> sourceRef;
            Damage deathInfo = component.getDeathInfo();
            if (deathInfo == null) {
                return;
            }
            World world = commandBuffer.getExternalData().getWorld();
            ObjectArrayList<PlayerRef> broadcastTargets = new ObjectArrayList<PlayerRef>(world.getPlayerRefs());
            Message killerMessage = null;
            Damage.Source source = deathInfo.getSource();
            if (source instanceof Damage.EntitySource && (sourceRef = (entitySource = (Damage.EntitySource)source).getRef()).isValid()) {
                KillFeedEvent.KillerMessage killerMessageEvent = new KillFeedEvent.KillerMessage(deathInfo, ref);
                store.invoke(sourceRef, killerMessageEvent);
                if (killerMessageEvent.isCancelled()) {
                    return;
                }
                killerMessage = killerMessageEvent.getMessage();
            }
            KillFeedEvent.DecedentMessage decedentMessageEvent = new KillFeedEvent.DecedentMessage(deathInfo);
            store.invoke(ref, decedentMessageEvent);
            if (decedentMessageEvent.isCancelled()) {
                return;
            }
            Message decedentMessage = decedentMessageEvent.getMessage();
            if (killerMessage == null && decedentMessage == null) {
                return;
            }
            KillFeedEvent.Display killFeedEvent = new KillFeedEvent.Display(deathInfo, deathInfo.getIfPresentMetaObject(Damage.DEATH_ICON), broadcastTargets);
            store.invoke(ref, killFeedEvent);
            if (killFeedEvent.isCancelled()) {
                return;
            }
            KillFeedMessage killFeedMessage = new KillFeedMessage(killerMessage != null ? killerMessage.getFormattedMessage() : null, decedentMessage != null ? decedentMessage.getFormattedMessage() : null, killFeedEvent.getIcon());
            for (PlayerRef targetPlayerRef : killFeedEvent.getBroadcastTargets()) {
                targetPlayerRef.getPacketHandler().write((Packet)killFeedMessage);
            }
        }
    }

    public static class RunDeathInteractions
    extends OnDeathSystem {
        @Nonnull
        private static final ComponentType<EntityStore, Interactions> INTERACTIONS_COMPONENT_TYPE = Interactions.getComponentType();
        @Nonnull
        private static final ComponentType<EntityStore, InteractionManager> INTERACTION_MANAGER_COMPONENT_TYPE = InteractionModule.get().getInteractionManagerComponent();
        @Nonnull
        private static final Query<EntityStore> QUERY = Query.and(INTERACTIONS_COMPONENT_TYPE, INTERACTION_MANAGER_COMPONENT_TYPE);
        @Nonnull
        private static final Set<Dependency<EntityStore>> DEPENDENCIES = Set.of(new SystemDependency(Order.AFTER, ClearEntityEffects.class));

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return DEPENDENCIES;
        }

        @Override
        public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            RootInteraction rootInteraction;
            UnarmedInteractions unarmed;
            InteractionManager interactionManagerComponent = commandBuffer.getComponent(ref, INTERACTION_MANAGER_COMPONENT_TYPE);
            assert (interactionManagerComponent != null);
            Interactions interactionsComponent = commandBuffer.getComponent(ref, INTERACTIONS_COMPONENT_TYPE);
            assert (interactionsComponent != null);
            String rootId = interactionsComponent.getInteractionId(InteractionType.Death);
            if (rootId == null && (unarmed = UnarmedInteractions.getAssetMap().getAsset("Empty")) != null) {
                rootId = unarmed.getInteractions().get((Object)InteractionType.Death);
            }
            RootInteraction rootInteraction2 = rootInteraction = rootId != null ? (RootInteraction)RootInteraction.getAssetMap().getAsset(rootId) : null;
            if (rootInteraction == null) {
                return;
            }
            InteractionContext context = InteractionContext.forInteraction(interactionManagerComponent, ref, InteractionType.Death, commandBuffer);
            InteractionChain chain = interactionManagerComponent.initChain(InteractionType.Death, context, rootInteraction, false);
            interactionManagerComponent.queueExecuteChain(chain);
            component.setInteractionChain(chain);
        }
    }

    public static class PlayerDropItemsConfig
    extends OnDeathSystem {
        @Nonnull
        private static final Set<Dependency<EntityStore>> DEPENDENCIES = Set.of(new SystemDependency(Order.BEFORE, DropPlayerDeathItems.class));

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return Query.and(Player.getComponentType(), PlayerRef.getComponentType());
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return DEPENDENCIES;
        }

        @Override
        public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            DeathConfig deathConfig = store.getExternalData().getWorld().getDeathConfig();
            component.setItemsLossMode(deathConfig.getItemsLossMode());
            component.setItemsAmountLossPercentage(deathConfig.getItemsAmountLossPercentage());
            component.setItemsDurabilityLossPercentage(deathConfig.getItemsDurabilityLossPercentage());
        }
    }

    public static class DropPlayerDeathItems
    extends OnDeathSystem {
        @Nonnull
        private static final Query<EntityStore> QUERY = Archetype.of(Player.getComponentType(), TransformComponent.getComponentType(), HeadRotation.getComponentType());

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            if (playerComponent.getGameMode() == GameMode.Creative) {
                return;
            }
            component.setDisplayDataOnDeathScreen(true);
            CombinedItemContainer combinedItemContainer = playerComponent.getInventory().getCombinedEverything();
            if (component.getItemsDurabilityLossPercentage() > 0.0) {
                double durabilityLossRatio = component.getItemsDurabilityLossPercentage() / 100.0;
                boolean hasArmorBroken = false;
                for (short i = 0; i < combinedItemContainer.getCapacity(); i = (short)(i + 1)) {
                    double durabilityLoss;
                    ItemStack updatedItemStack;
                    ItemStackSlotTransaction transaction;
                    ItemStack itemStack = combinedItemContainer.getItemStack(i);
                    if (ItemStack.isEmpty(itemStack) || itemStack.isBroken() || !(transaction = combinedItemContainer.replaceItemStackInSlot(i, itemStack, updatedItemStack = itemStack.withIncreasedDurability(-(durabilityLoss = itemStack.getMaxDurability() * durabilityLossRatio)))).getSlotAfter().isBroken() || itemStack.getItem().getArmor() == null) continue;
                    hasArmorBroken = true;
                }
                if (hasArmorBroken) {
                    playerComponent.getStatModifiersManager().setRecalculate(true);
                }
            }
            List<ItemStack> itemsToDrop = null;
            switch (component.getItemsLossMode()) {
                case ALL: {
                    itemsToDrop = playerComponent.getInventory().dropAllItemStacks();
                    break;
                }
                case CONFIGURED: {
                    double itemsAmountLossPercentage = component.getItemsAmountLossPercentage();
                    if (!(itemsAmountLossPercentage > 0.0)) break;
                    double itemAmountLossRatio = itemsAmountLossPercentage / 100.0;
                    itemsToDrop = new ObjectArrayList<ItemStack>();
                    for (short i = 0; i < combinedItemContainer.getCapacity(); i = (short)(i + 1)) {
                        ItemStack itemStack = combinedItemContainer.getItemStack(i);
                        if (ItemStack.isEmpty(itemStack) || !itemStack.getItem().dropsOnDeath()) continue;
                        int quantityToLose = Math.max(1, MathUtil.floor((double)itemStack.getQuantity() * itemAmountLossRatio));
                        itemsToDrop.add(itemStack.withQuantity(quantityToLose));
                        int newQuantity = itemStack.getQuantity() - quantityToLose;
                        if (newQuantity > 0) {
                            ItemStack updatedItemStack = itemStack.withQuantity(newQuantity);
                            combinedItemContainer.replaceItemStackInSlot(i, itemStack, updatedItemStack);
                            continue;
                        }
                        combinedItemContainer.removeItemStackFromSlot(i);
                    }
                    break;
                }
            }
            if (itemsToDrop != null && !itemsToDrop.isEmpty()) {
                TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
                assert (transformComponent != null);
                Vector3d position = transformComponent.getPosition();
                HeadRotation headRotationComponent = store.getComponent(ref, HeadRotation.getComponentType());
                assert (headRotationComponent != null);
                Vector3f headRotation = headRotationComponent.getRotation();
                Holder<EntityStore>[] drops = ItemComponent.generateItemDrops(store, itemsToDrop, position.clone().add(0.0, 1.0, 0.0), headRotation);
                commandBuffer.addEntities(drops, AddReason.SPAWN);
                component.setItemsLostOnDeath(itemsToDrop);
            }
        }
    }

    public static class PlayerKilledPlayer
    extends OnDeathSystem {
        @Nonnull
        private static final Query<EntityStore> QUERY = Archetype.of(Player.getComponentType(), Nameplate.getComponentType());

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            Damage.Source source;
            Nameplate nameplateComponent = commandBuffer.getComponent(ref, Nameplate.getComponentType());
            Damage deathInfo = component.getDeathInfo();
            DamageCause deathCause = component.getDeathCause();
            if (deathCause != DamageCause.PHYSICAL && deathCause != DamageCause.PROJECTILE) {
                return;
            }
            if (deathInfo == null || !((source = deathInfo.getSource()) instanceof Damage.EntitySource)) {
                return;
            }
            Damage.EntitySource entitySource = (Damage.EntitySource)source;
            Ref<EntityStore> sourceRef = entitySource.getRef();
            if (!sourceRef.isValid()) {
                return;
            }
            Player attacker = store.getComponent(sourceRef, Player.getComponentType());
            if (attacker == null) {
                return;
            }
            attacker.sendMessage(Message.translation("server.general.killedEntity").param("entityName", nameplateComponent.getText()));
        }
    }

    public static class ClearEntityEffects
    extends OnDeathSystem {
        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return EffectControllerComponent.getComponentType();
        }

        @Override
        public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            EffectControllerComponent effectControllerComponent = commandBuffer.getComponent(ref, EffectControllerComponent.getComponentType());
            assert (effectControllerComponent != null);
            effectControllerComponent.clearEffects(ref, commandBuffer);
        }
    }

    public static class ClearInteractions
    extends OnDeathSystem {
        @Nonnull
        private static final ComponentType<EntityStore, InteractionManager> INTERACTION_MANAGER_COMPONENT_TYPE = InteractionModule.get().getInteractionManagerComponent();

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return RootDependency.firstSet();
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return INTERACTION_MANAGER_COMPONENT_TYPE;
        }

        @Override
        public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            InteractionManager interactionManagerComponent = commandBuffer.getComponent(ref, INTERACTION_MANAGER_COMPONENT_TYPE);
            assert (interactionManagerComponent != null);
            interactionManagerComponent.clear();
        }
    }

    public static class ClearHealth
    extends OnDeathSystem {
        @Nonnull
        private static final ComponentType<EntityStore, EntityStatMap> ENTITY_STAT_MAP_COMPONENT_TYPE = EntityStatMap.getComponentType();

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return RootDependency.firstSet();
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return ENTITY_STAT_MAP_COMPONENT_TYPE;
        }

        @Override
        public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            EntityStatMap entityStatMapComponent = store.getComponent(ref, ENTITY_STAT_MAP_COMPONENT_TYPE);
            assert (entityStatMapComponent != null);
            entityStatMapComponent.setStatValue(DefaultEntityStatTypes.getHealth(), 0.0f);
        }
    }

    public static abstract class OnDeathSystem
    extends RefChangeSystem<EntityStore, DeathComponent> {
        @Override
        @Nonnull
        public ComponentType<EntityStore, DeathComponent> componentType() {
            return DeathComponent.getComponentType();
        }

        @Override
        public void onComponentSet(@Nonnull Ref<EntityStore> ref, DeathComponent oldComponent, @Nonnull DeathComponent newComponent, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        }

        @Override
        public void onComponentRemoved(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        }
    }
}

