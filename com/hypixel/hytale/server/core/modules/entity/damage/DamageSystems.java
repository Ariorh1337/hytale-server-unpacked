/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entity.damage;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.dependency.SystemGroupDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.spatial.SpatialStructure;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.random.RandomExtra;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.util.TrigMathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector4d;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.protocol.CombatTextUpdate;
import com.hypixel.hytale.protocol.ComponentUpdate;
import com.hypixel.hytale.protocol.ComponentUpdateType;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.entities.SpawnModelParticles;
import com.hypixel.hytale.protocol.packets.player.DamageInfo;
import com.hypixel.hytale.protocol.packets.player.ReticleEvent;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.gameplay.BrokenPenalties;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.asset.type.gameplay.PlayerConfig;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemArmor;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelParticle;
import com.hypixel.hytale.server.core.asset.type.particle.config.WorldParticle;
import com.hypixel.hytale.server.core.entity.AnimationUtils;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.damage.DamageDataComponent;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementConfig;
import com.hypixel.hytale.server.core.entity.knockback.KnockbackComponent;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.meta.DynamicMetaStore;
import com.hypixel.hytale.server.core.modules.entity.AllLegacyLivingEntityTypesQuery;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSystems;
import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsSystems;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.modules.entityui.EntityUIModule;
import com.hypixel.hytale.server.core.modules.entityui.UIComponentList;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.WieldingInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageEffects;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.SplitVelocity;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import java.lang.runtime.SwitchBootstraps;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bouncycastle.util.Arrays;

public class DamageSystems {
    public static final float DEFAULT_DAMAGE_DELAY = 1.0f;
    private static final Query<EntityStore> NPCS_QUERY = Query.and(AllLegacyLivingEntityTypesQuery.INSTANCE, EntityStatMap.getComponentType(), MovementStatesComponent.getComponentType(), Query.not(EntityModule.get().getPlayerComponentType()));

    public static void executeDamage(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull Damage damage) {
        componentAccessor.invoke(ref, damage);
    }

    public static void executeDamage(int index, @Nonnull ArchetypeChunk<EntityStore> chunk, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
        commandBuffer.invoke(chunk.getReferenceTo(index), damage);
    }

    public static void executeDamage(@Nonnull Ref<EntityStore> ref, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
        commandBuffer.invoke(ref, damage);
    }

    @Deprecated
    public static class HackKnockbackValues
    extends EntityTickingSystem<EntityStore> {
        public static float PLAYER_KNOCKBACK_SCALE = 25.0f;
        private static final Query<EntityStore> QUERY = Query.and(AllLegacyLivingEntityTypesQuery.INSTANCE, KnockbackComponent.getComponentType());

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getFilterDamageGroup();
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            KnockbackComponent knockbackComponent = archetypeChunk.getComponent(index, KnockbackComponent.getComponentType());
            assert (knockbackComponent != null);
            if (knockbackComponent.getVelocityConfig() == null || SplitVelocity.SHOULD_MODIFY_VELOCITY) {
                Vector3d vector = knockbackComponent.getVelocity();
                vector.x *= (double)PLAYER_KNOCKBACK_SCALE;
                vector.z *= (double)PLAYER_KNOCKBACK_SCALE;
                knockbackComponent.setVelocity(vector);
            }
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }
    }

    @Deprecated
    public static class ArmorDamageReduction
    extends DamageEventSystem {
        @Nonnull
        private static final Query<EntityStore> QUERY = AllLegacyLivingEntityTypesQuery.INSTANCE;

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getFilterDamageGroup();
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
            LivingEntity entity = (LivingEntity)EntityUtils.getEntity(index, archetypeChunk);
            assert (entity != null);
            World world = commandBuffer.getExternalData().getWorld();
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            Map<DamageCause, ArmorResistanceModifiers> resistances = ArmorDamageReduction.getResistanceModifiers(world, entity.getInventory().getArmor(), entity.canApplyItemStackPenalties(ref, commandBuffer), archetypeChunk.getComponent(index, EffectControllerComponent.getComponentType()));
            if (!damage.getCause().doesBypassResistances() && !resistances.isEmpty()) {
                ArmorResistanceModifiers damageModEntry = resistances.get(damage.getCause());
                if (damageModEntry == null) {
                    return;
                }
                float amount = Math.max(0.0f, damage.getAmount() - (float)damageModEntry.flatModifier);
                amount *= Math.max(0.0f, 1.0f - damageModEntry.multiplierModifier);
                while (damageModEntry.inheritedParentId != null && (damageModEntry = resistances.get(damageModEntry.inheritedParentId)) != null) {
                    amount = Math.max(0.0f, damage.getAmount() - (float)damageModEntry.flatModifier);
                    amount *= Math.max(0.0f, 1.0f - damageModEntry.multiplierModifier);
                }
                damage.setAmount(amount);
            }
        }

        @Nonnull
        public static Map<DamageCause, ArmorResistanceModifiers> getResistanceModifiers(@Nonnull World world, @Nonnull ItemContainer inventory, boolean canApplyItemStackPenalties, @Nullable EffectControllerComponent effectControllerComponent) {
            Object2ObjectOpenHashMap<DamageCause, ArmorResistanceModifiers> result = new Object2ObjectOpenHashMap<DamageCause, ArmorResistanceModifiers>();
            for (short index = 0; index < inventory.getCapacity(); index = (short)(index + 1)) {
                Item item;
                ItemArmor itemArmor;
                ItemStack itemStack = inventory.getItemStack(index);
                if (itemStack == null || itemStack.isEmpty() || (itemArmor = (item = itemStack.getItem()).getArmor()) == null) continue;
                Map<DamageCause, StaticModifier[]> resistances = itemArmor.getDamageResistanceValues();
                double flatResistance = itemArmor.getBaseDamageResistance();
                if (resistances == null) continue;
                for (Map.Entry<DamageCause, StaticModifier[]> entry : resistances.entrySet()) {
                    if (entry.getValue() == null) continue;
                    ArmorDamageReduction.calculateResistanceEntryModifications(entry, world, result, canApplyItemStackPenalties, itemStack.isBroken(), flatResistance);
                }
            }
            ArmorDamageReduction.addResistanceModifiersFromEntityEffects(result, effectControllerComponent);
            return result;
        }

        private static void calculateResistanceEntryModifications(@Nonnull Map.Entry<DamageCause, StaticModifier[]> entry, @Nonnull World world, @Nonnull Map<DamageCause, ArmorResistanceModifiers> result, boolean canApplyItemStackPenalties, boolean itemStackIsBroken, double flatResistance) {
            ArmorResistanceModifiers mods = result.computeIfAbsent(entry.getKey(), key -> new ArmorResistanceModifiers());
            StaticModifier[] valueArray = entry.getValue();
            for (int x = 0; x < valueArray.length; ++x) {
                StaticModifier entryValue = valueArray[x];
                if (entryValue.getCalculationType() == StaticModifier.CalculationType.ADDITIVE) {
                    mods.flatModifier = (int)((float)mods.flatModifier + entryValue.getAmount());
                    continue;
                }
                mods.multiplierModifier += entryValue.getAmount();
            }
            mods.flatModifier = (int)((double)mods.flatModifier + flatResistance);
            DamageCause damageCause = entry.getKey();
            if (damageCause != null && damageCause.getInherits() != null) {
                mods.inheritedParentId = (DamageCause)DamageCause.getAssetMap().getAsset(damageCause.getInherits());
            }
            if (canApplyItemStackPenalties && itemStackIsBroken) {
                BrokenPenalties brokenPenalties = world.getGameplayConfig().getItemDurabilityConfig().getBrokenPenalties();
                double penalty = brokenPenalties.getWeapon(0.0);
                mods.flatModifier = (int)((double)mods.flatModifier * (1.0 - penalty));
                mods.multiplierModifier = (float)((double)mods.multiplierModifier * (1.0 - penalty));
            }
        }

        private static void addResistanceModifiersFromEntityEffects(Map<DamageCause, ArmorResistanceModifiers> resistanceModifiers, EffectControllerComponent effectControllerComponent) {
            if (effectControllerComponent == null) {
                return;
            }
            IntIterator intIterator = effectControllerComponent.getActiveEffects().keySet().iterator();
            while (intIterator.hasNext()) {
                Map<DamageCause, StaticModifier[]> damageResistanceValues;
                int entityEffectIndex = (Integer)intIterator.next();
                EntityEffect entityEffectData = EntityEffect.getAssetMap().getAsset(entityEffectIndex);
                if (entityEffectData == null || (damageResistanceValues = entityEffectData.getDamageResistanceValues()) == null || damageResistanceValues.isEmpty()) continue;
                for (Map.Entry<DamageCause, StaticModifier[]> entry : damageResistanceValues.entrySet()) {
                    ArmorResistanceModifiers modifier = resistanceModifiers.computeIfAbsent(entry.getKey(), damageCause -> new ArmorResistanceModifiers());
                    for (StaticModifier staticModifier : entry.getValue()) {
                        if (staticModifier.getCalculationType() == StaticModifier.CalculationType.ADDITIVE) {
                            modifier.flatModifier = (int)((float)modifier.flatModifier + staticModifier.getAmount());
                            continue;
                        }
                        if (staticModifier.getCalculationType() != StaticModifier.CalculationType.MULTIPLICATIVE) continue;
                        modifier.multiplierModifier += staticModifier.getAmount();
                    }
                }
            }
        }

        public static class ArmorResistanceModifiers {
            public int flatModifier;
            public float multiplierModifier;
            @Nullable
            public DamageCause inheritedParentId;
        }
    }

    @Deprecated
    public static class ArmorKnockbackReduction
    extends DamageEventSystem {
        @Nonnull
        private static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();
        @Nonnull
        private static final Query<EntityStore> QUERY = Query.and(AllLegacyLivingEntityTypesQuery.INSTANCE, DamageDataComponent.getComponentType(), TRANSFORM_COMPONENT_TYPE);

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getFilterDamageGroup();
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage event) {
        }

        @Override
        public void handleInternal(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
            LivingEntity entity = (LivingEntity)EntityUtils.getEntity(index, archetypeChunk);
            assert (entity != null);
            Inventory inventory = entity.getInventory();
            if (inventory == null) {
                return;
            }
            ItemContainer armorContainer = inventory.getArmor();
            if (armorContainer == null) {
                return;
            }
            KnockbackComponent knockbackComponent = damage.getIfPresentMetaObject(Damage.KNOCKBACK_COMPONENT);
            if (knockbackComponent == null) {
                return;
            }
            float knockbackResistanceModifier = 0.0f;
            for (short i = 0; i < armorContainer.getCapacity(); i = (short)(i + 1)) {
                Map<DamageCause, Float> knockbackResistances;
                Item item;
                ItemArmor itemArmor;
                ItemStack itemStack = armorContainer.getItemStack(i);
                if (itemStack == null || itemStack.isEmpty() || (itemArmor = (item = itemStack.getItem()).getArmor()) == null || (knockbackResistances = itemArmor.getKnockbackResistances()) == null) continue;
                DamageCause damageCause = damage.getCause();
                knockbackResistanceModifier += knockbackResistances.get(damageCause).floatValue();
            }
            knockbackComponent.addModifier(Math.max(1.0f - knockbackResistanceModifier, 0.0f));
        }
    }

    @Deprecated
    public static class WieldingKnockbackReduction
    extends DamageEventSystem {
        @Nonnull
        private static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();
        @Nonnull
        private static final Query<EntityStore> QUERY = Query.and(AllLegacyLivingEntityTypesQuery.INSTANCE, DamageDataComponent.getComponentType(), TRANSFORM_COMPONENT_TYPE);

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getFilterDamageGroup();
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage event) {
        }

        @Override
        public void handleInternal(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
            Int2DoubleMap angledWieldingKnockbackModifiers;
            DamageDataComponent damageDataComponent = archetypeChunk.getComponent(index, DamageDataComponent.getComponentType());
            assert (damageDataComponent != null);
            TransformComponent transformComponent = archetypeChunk.getComponent(index, TRANSFORM_COMPONENT_TYPE);
            assert (transformComponent != null);
            WieldingInteraction wielding = damageDataComponent.getCurrentWielding();
            if (wielding == null) {
                return;
            }
            Int2DoubleMap knockbackModifiers = wielding.getKnockbackModifiers();
            WieldingInteraction.AngledWielding angledWielding = wielding.getAngledWielding();
            KnockbackComponent knockbackComponent = damage.getIfPresentMetaObject(Damage.KNOCKBACK_COMPONENT);
            if (knockbackComponent == null) {
                return;
            }
            Damage.Source source = damage.getSource();
            if (!(source instanceof Damage.EntitySource)) {
                return;
            }
            Damage.EntitySource entitySource = (Damage.EntitySource)source;
            Ref<EntityStore> sourceRef = entitySource.getRef();
            if (!sourceRef.isValid()) {
                return;
            }
            TransformComponent sourceTransformComponent = commandBuffer.getComponent(sourceRef, TRANSFORM_COMPONENT_TYPE);
            if (sourceTransformComponent == null) {
                return;
            }
            int damageCauseIndex = damage.getDamageCauseIndex();
            double angledWieldingModifier = 1.0;
            double wieldingModifier = knockbackModifiers.getOrDefault(damageCauseIndex, 1.0);
            if (angledWielding != null && (angledWieldingKnockbackModifiers = angledWielding.getKnockbackModifiers()).containsKey(damageCauseIndex)) {
                Vector3d targetPos = transformComponent.getPosition();
                Vector3d attackerPos = sourceTransformComponent.getPosition();
                float angleBetween = TrigMathUtil.atan2(attackerPos.x - targetPos.x, attackerPos.z - targetPos.z);
                if (Math.abs(MathUtil.compareAngle(angleBetween = MathUtil.wrapAngle(angleBetween + (float)Math.PI - transformComponent.getRotation().getYaw()), angledWielding.getAngleRad())) < angledWielding.getAngleDistanceRad()) {
                    angledWieldingModifier = angledWieldingKnockbackModifiers.getOrDefault(damageCauseIndex, 1.0);
                }
            }
            knockbackComponent.addModifier(wieldingModifier);
            knockbackComponent.addModifier(angledWieldingModifier);
        }
    }

    @Deprecated
    public static class WieldingDamageReduction
    extends DamageEventSystem {
        @Nonnull
        private static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();
        @Nonnull
        private static final Query<EntityStore> QUERY = Query.and(AllLegacyLivingEntityTypesQuery.INSTANCE, DamageDataComponent.getComponentType(), InteractionModule.get().getInteractionManagerComponent(), TRANSFORM_COMPONENT_TYPE);

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getFilterDamageGroup();
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
            NetworkId sourceNetworkIdComponent;
            Int2FloatMap angledWieldingDamageModifiers;
            DamageDataComponent damageDataComponent = archetypeChunk.getComponent(index, DamageDataComponent.getComponentType());
            assert (damageDataComponent != null);
            InteractionManager interactionManager = archetypeChunk.getComponent(index, InteractionModule.get().getInteractionManagerComponent());
            assert (interactionManager != null);
            WieldingInteraction wielding = damageDataComponent.getCurrentWielding();
            if (wielding == null) {
                return;
            }
            WieldingInteraction.AngledWielding angledWielding = wielding.getAngledWielding();
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            TransformComponent transformComponent = archetypeChunk.getComponent(index, TRANSFORM_COMPONENT_TYPE);
            assert (transformComponent != null);
            Vector3d targetPosition = transformComponent.getPosition();
            Vector3f targetRotation = transformComponent.getRotation();
            Damage.Source source = damage.getSource();
            if (!(source instanceof Damage.EntitySource)) {
                return;
            }
            Damage.EntitySource entitySource = (Damage.EntitySource)source;
            Ref<EntityStore> sourceRef = entitySource.getRef();
            if (!sourceRef.isValid()) {
                return;
            }
            TransformComponent sourceTransformComponent = commandBuffer.getComponent(sourceRef, TRANSFORM_COMPONENT_TYPE);
            if (sourceTransformComponent == null) {
                return;
            }
            int damageCauseIndex = damage.getDamageCauseIndex();
            float wieldingModifier = 1.0f;
            float angledWieldingModifier = 1.0f;
            String blockedInteractions = null;
            Int2FloatMap wieldingDamageModifiers = wielding.getDamageModifiers();
            if (!wieldingDamageModifiers.isEmpty()) {
                String wieldingBlockedInteractions;
                wieldingModifier = wieldingDamageModifiers.getOrDefault(damageCauseIndex, 1.0f);
                DamageEffects wieldingBlockedEffects = wielding.getBlockedEffects();
                if (wieldingBlockedEffects != null) {
                    wieldingBlockedEffects.addToDamage(damage);
                }
                if ((wieldingBlockedInteractions = wielding.getBlockedInteractions()) != null) {
                    blockedInteractions = wieldingBlockedInteractions;
                }
                damage.putMetaObject(Damage.BLOCKED, Boolean.TRUE);
            }
            if (angledWielding != null && (angledWieldingDamageModifiers = angledWielding.getDamageModifiers()).containsKey(damageCauseIndex)) {
                Vector3d sourcePosition = sourceTransformComponent.getPosition();
                float angleBetween = TrigMathUtil.atan2(sourcePosition.x - targetPosition.x, sourcePosition.z - targetPosition.z);
                if (Math.abs(MathUtil.compareAngle(angleBetween = MathUtil.wrapAngle(angleBetween + (float)Math.PI - targetRotation.getYaw()), angledWielding.getAngleRad())) < angledWielding.getAngleDistanceRad()) {
                    String wieldingBlockedInteractions;
                    angledWieldingModifier = angledWieldingDamageModifiers.getOrDefault(damageCauseIndex, 1.0f);
                    DamageEffects wieldingBlockedEffects = wielding.getBlockedEffects();
                    if (wieldingBlockedEffects != null) {
                        wieldingBlockedEffects.addToDamage(damage);
                    }
                    if ((wieldingBlockedInteractions = wielding.getBlockedInteractions()) != null) {
                        blockedInteractions = wieldingBlockedInteractions;
                    }
                    damage.putMetaObject(Damage.BLOCKED, Boolean.TRUE);
                }
            }
            damage.setAmount(damage.getAmount() * wieldingModifier * angledWieldingModifier);
            if (blockedInteractions != null && (sourceNetworkIdComponent = commandBuffer.getComponent(sourceRef, NetworkId.getComponentType())) != null) {
                InteractionContext context = InteractionContext.forInteraction(interactionManager, ref, InteractionType.Wielding, commandBuffer);
                DynamicMetaStore<InteractionContext> contextMetaStore = context.getMetaStore();
                contextMetaStore.putMetaObject(Interaction.TARGET_ENTITY, sourceRef);
                contextMetaStore.putMetaObject(Interaction.DAMAGE, damage);
                int networkId = sourceNetworkIdComponent.getId();
                InteractionChain chain = interactionManager.initChain(InteractionType.Wielding, context, RootInteraction.getRootInteractionOrUnknown(blockedInteractions), networkId, null, false);
                interactionManager.queueExecuteChain(chain);
            }
        }
    }

    public static class EntityUIEvents
    extends DamageEventSystem {
        @Nonnull
        private final ComponentType<EntityStore, EntityTrackerSystems.Visible> visibleComponentType = EntityModule.get().getVisibleComponentType();
        @Nonnull
        private final ComponentType<EntityStore, UIComponentList> uiComponentListComponentType = EntityUIModule.get().getUIComponentListType();
        @Nonnull
        private final Query<EntityStore> query = Query.and(this.visibleComponentType, this.uiComponentListComponentType);

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getInspectDamageGroup();
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.query;
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
            if (damage.getAmount() <= 0.0f) {
                return;
            }
            Damage.Source source = damage.getSource();
            if (!(source instanceof Damage.EntitySource)) {
                return;
            }
            Damage.EntitySource entitySource = (Damage.EntitySource)source;
            Ref<EntityStore> sourceRef = entitySource.getRef();
            if (!sourceRef.isValid()) {
                return;
            }
            PlayerRef sourcePlayerRef = commandBuffer.getComponent(sourceRef, PlayerRef.getComponentType());
            if (sourcePlayerRef == null || !sourcePlayerRef.isValid()) {
                return;
            }
            EntityTrackerSystems.EntityViewer sourceEntityViewerComponent = commandBuffer.getComponent(sourceRef, EntityTrackerSystems.EntityViewer.getComponentType());
            if (sourceEntityViewerComponent != null) {
                Float hitAngleDeg = damage.getIfPresentMetaObject(Damage.HIT_ANGLE);
                EntityUIEvents.queueUpdateFor(archetypeChunk.getReferenceTo(index), damage.getAmount(), hitAngleDeg, sourceEntityViewerComponent);
            }
        }

        private static void queueUpdateFor(@Nonnull Ref<EntityStore> ref, float damageAmount, @Nullable Float hitAngleDeg, @Nonnull EntityTrackerSystems.EntityViewer viewer) {
            ComponentUpdate update = new ComponentUpdate();
            update.type = ComponentUpdateType.CombatText;
            CombatTextUpdate combatTextUpdate = new CombatTextUpdate();
            combatTextUpdate.hitAngleDeg = hitAngleDeg == null ? 0.0f : hitAngleDeg.floatValue();
            combatTextUpdate.text = Integer.toString((int)Math.floor(damageAmount));
            update.combatTextUpdate = combatTextUpdate;
            viewer.queueUpdate(ref, update);
        }
    }

    public static class ReticleEvents
    extends DamageEventSystem {
        private static final int EVENT_ON_HIT_TAG_INDEX = AssetRegistry.getOrCreateTagIndex("OnHit");
        private static final int EVENT_ON_KILL_TAG_INDEX = AssetRegistry.getOrCreateTagIndex("OnKill");
        private static final ReticleEvent ON_HIT = new ReticleEvent(EVENT_ON_HIT_TAG_INDEX);
        private static final ReticleEvent ON_KILL = new ReticleEvent(EVENT_ON_KILL_TAG_INDEX);

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getInspectDamageGroup();
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return Archetype.empty();
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
            boolean isDead = archetypeChunk.getArchetype().contains(DeathComponent.getComponentType());
            if (damage.getAmount() <= 0.0f) {
                return;
            }
            Damage.Source source = damage.getSource();
            if (!(source instanceof Damage.EntitySource)) {
                return;
            }
            Damage.EntitySource entitySource = (Damage.EntitySource)source;
            Ref<EntityStore> sourceRef = entitySource.getRef();
            if (!sourceRef.isValid()) {
                return;
            }
            PlayerRef sourcePlayerRef = commandBuffer.getComponent(sourceRef, PlayerRef.getComponentType());
            if (sourcePlayerRef == null || !sourcePlayerRef.isValid()) {
                return;
            }
            sourcePlayerRef.getPacketHandler().writeNoCache(isDead ? ON_KILL : ON_HIT);
        }
    }

    public static class PlayerHitIndicators
    extends DamageEventSystem {
        @Nonnull
        private static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();
        @Nonnull
        private static final Query<EntityStore> QUERY = PlayerRef.getComponentType();

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getInspectDamageGroup();
        }

        @Override
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
            PlayerRef playerRefComponent = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            Damage.Source source = damage.getSource();
            if (!(source instanceof Damage.EntitySource)) {
                return;
            }
            Damage.EntitySource entitySource = (Damage.EntitySource)source;
            Ref<EntityStore> sourceRef = entitySource.getRef();
            if (!sourceRef.isValid()) {
                return;
            }
            DamageCause damageCause = damage.getCause();
            if (damageCause == null) {
                return;
            }
            TransformComponent sourceTransformComponent = commandBuffer.getComponent(sourceRef, TRANSFORM_COMPONENT_TYPE);
            if (sourceTransformComponent == null) {
                return;
            }
            Vector3d position = sourceTransformComponent.getPosition();
            playerRefComponent.getPacketHandler().writeNoCache(new DamageInfo(new com.hypixel.hytale.protocol.Vector3d(position.getX(), position.getY(), position.getZ()), damage.getAmount(), damageCause.toPacket()));
        }
    }

    public static class DamageAttackerTool
    extends DamageEventSystem {
        @Nonnull
        private static final Query<EntityStore> QUERY = AllLegacyLivingEntityTypesQuery.INSTANCE;

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
            LivingEntity sourceLivingEntity;
            Inventory sourceInventory;
            byte activeHotbarSlot;
            Damage.Source source;
            if (!damage.getCause().isDurabilityLoss() || !((source = damage.getSource()) instanceof Damage.EntitySource)) {
                return;
            }
            Damage.EntitySource entitySource = (Damage.EntitySource)source;
            Ref<EntityStore> sourceRef = entitySource.getRef();
            if (!sourceRef.isValid()) {
                return;
            }
            Entity sourceEntity = EntityUtils.getEntity(sourceRef, commandBuffer);
            if (sourceEntity instanceof LivingEntity && (activeHotbarSlot = (sourceInventory = (sourceLivingEntity = (LivingEntity)sourceEntity).getInventory()).getActiveHotbarSlot()) != -1) {
                sourceLivingEntity.decreaseItemStackDurability(sourceRef, sourceInventory.getItemInHand(), -1, sourceInventory.getActiveHotbarSlot(), commandBuffer);
            }
        }
    }

    public static class DamageStamina
    extends DamageEventSystem
    implements EntityStatsSystems.StatModifyingSystem {
        @Nonnull
        private static final Query<EntityStore> QUERY = Query.and(DamageDataComponent.getComponentType(), EntityStatMap.getComponentType());

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
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage event) {
        }

        @Override
        public void handleInternal(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
            EntityStatMap entityStatMapComponent = archetypeChunk.getComponent(index, EntityStatMap.getComponentType());
            assert (entityStatMapComponent != null);
            DamageDataComponent damageDataComponent = archetypeChunk.getComponent(index, DamageDataComponent.getComponentType());
            assert (damageDataComponent != null);
            if (damageDataComponent.getCurrentWielding() == null) {
                return;
            }
            WieldingInteraction.StaminaCost staminaCost = damageDataComponent.getCurrentWielding().getStaminaCost();
            if (staminaCost == null) {
                return;
            }
            Boolean isBlocked = damage.getMetaStore().getIfPresentMetaObject(Damage.BLOCKED);
            if (isBlocked != null && isBlocked.booleanValue()) {
                float staminaToConsume = staminaCost.computeStaminaAmountToConsume(damage.getInitialAmount(), entityStatMapComponent);
                Float multiplier = damage.getIfPresentMetaObject(Damage.STAMINA_DRAIN_MULTIPLIER);
                if (multiplier != null) {
                    staminaToConsume *= multiplier.floatValue();
                }
                entityStatMapComponent.subtractStatValue(DefaultEntityStatTypes.getStamina(), staminaToConsume);
            }
        }
    }

    public static class DamageArmor
    extends DamageEventSystem {
        @Nonnull
        private static final Query<EntityStore> QUERY = AllLegacyLivingEntityTypesQuery.INSTANCE;

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
            LivingEntity entity = (LivingEntity)EntityUtils.getEntity(index, archetypeChunk);
            assert (entity != null);
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            DamageCause damageCause = damage.getCause();
            if (damageCause.isDurabilityLoss()) {
                ItemContainer armor = entity.getInventory().getArmor();
                ShortArrayList armorPartIndexes = new ShortArrayList();
                armor.forEachWithMeta((slot, itemStack, _armorPartIndexes) -> {
                    if (!itemStack.isBroken()) {
                        _armorPartIndexes.add(slot);
                    }
                }, armorPartIndexes);
                if (!armorPartIndexes.isEmpty()) {
                    short slot2 = armorPartIndexes.getShort(RandomExtra.randomRange(armorPartIndexes.size()));
                    entity.decreaseItemStackDurability(ref, armor.getItemStack(slot2), -3, slot2, commandBuffer);
                }
            }
        }
    }

    public static class TrackLastDamage
    extends DamageEventSystem {
        @Nonnull
        private static final ComponentType<EntityStore, DamageDataComponent> DAMAGE_DATA_COMPONENT_TYPE = DamageDataComponent.getComponentType();
        @Nonnull
        private static final Query<EntityStore> QUERY = Query.and(AllLegacyLivingEntityTypesQuery.INSTANCE, DAMAGE_DATA_COMPONENT_TYPE);

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
            TimeResource timeResource = commandBuffer.getResource(TimeResource.getResourceType());
            DamageDataComponent damageDataComponent = archetypeChunk.getComponent(index, DAMAGE_DATA_COMPONENT_TYPE);
            assert (damageDataComponent != null);
            damageDataComponent.setLastDamageTime(timeResource.getNow());
        }
    }

    public static class PlayerDamageFilterSystem
    extends DamageEventSystem {
        @Nonnull
        private static final Query<EntityStore> QUERY = Player.getComponentType();

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getFilterDamageGroup();
        }

        @Override
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
            Damage.EntitySource entitySource;
            Ref<EntityStore> sourceRef;
            Damage.Source source;
            World world = store.getExternalData().getWorld();
            Player playerComponent = archetypeChunk.getComponent(index, Player.getComponentType());
            assert (playerComponent != null);
            if (playerComponent.hasSpawnProtection()) {
                damage.setCancelled(true);
                return;
            }
            if (!world.getWorldConfig().isPvpEnabled() && (source = damage.getSource()) instanceof Damage.EntitySource && (sourceRef = (entitySource = (Damage.EntitySource)source).getRef()).isValid() && commandBuffer.getComponent(sourceRef, Player.getComponentType()) != null) {
                damage.setCancelled(true);
                return;
            }
        }
    }

    public static class HitAnimation
    extends DamageEventSystem {
        @Nonnull
        private static final Query<EntityStore> QUERY = Query.and(Query.not(DeathComponent.getComponentType()), MovementStatesComponent.getComponentType());

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
            String selectedAnimationId;
            ModelComponent modelComponent = archetypeChunk.getComponent(index, ModelComponent.getComponentType());
            Model model = modelComponent != null ? modelComponent.getModel() : null;
            MovementStatesComponent movementStatesComponent = archetypeChunk.getComponent(index, MovementStatesComponent.getComponentType());
            assert (movementStatesComponent != null);
            MovementStates movementStates = movementStatesComponent.getMovementStates();
            if (damage.getAmount() <= 0.0f) {
                return;
            }
            String[] animationIds = Entity.DefaultAnimations.getHurtAnimationIds(movementStates, damage.getCause());
            if (model != null && (selectedAnimationId = model.getFirstBoundAnimationId(animationIds)) != null) {
                AnimationUtils.playAnimation(archetypeChunk.getReferenceTo(index), AnimationSlot.Status, selectedAnimationId, true, commandBuffer);
            }
        }
    }

    public static class FallDamageNPCs
    extends EntityTickingSystem<EntityStore> {
        static final float CURVE_MODIFIER = 0.58f;
        static final float CURVE_MULTIPLIER = 2.0f;
        public static final double MIN_DAMAGE = 10.0;

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getGatherDamageGroup();
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return NPCS_QUERY;
        }

        @Override
        public void tick(float dt, int systemIndex, @Nonnull Store<EntityStore> store) {
            World world = store.getExternalData().getWorld();
            if (!world.getWorldConfig().isFallDamageEnabled()) {
                return;
            }
            super.tick(dt, systemIndex, store);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            LivingEntity entity = (LivingEntity)EntityUtils.getEntity(index, archetypeChunk);
            assert (entity != null);
            MovementStatesComponent movementStatesComponent = archetypeChunk.getComponent(index, MovementStatesComponent.getComponentType());
            assert (movementStatesComponent != null);
            MovementStates movementStates = movementStatesComponent.getMovementStates();
            if (movementStates.onGround && entity.getCurrentFallDistance() > 0.0) {
                Velocity velocityComponent = archetypeChunk.getComponent(index, Velocity.getComponentType());
                assert (velocityComponent != null);
                double yVelocity = Math.abs(velocityComponent.getVelocity().getY());
                World world = commandBuffer.getExternalData().getWorld();
                int movementConfigIndex = world.getGameplayConfig().getPlayerConfig().getMovementConfigIndex();
                MovementConfig movementConfig = MovementConfig.getAssetMap().getAsset(movementConfigIndex);
                float minFallSpeedToEngageRoll = movementConfig.getMinFallSpeedToEngageRoll();
                if (yVelocity > (double)minFallSpeedToEngageRoll && !movementStates.inFluid) {
                    EntityStatMap entityStatMapComponent = archetypeChunk.getComponent(index, EntityStatMap.getComponentType());
                    assert (entityStatMapComponent != null);
                    double damagePercentage = Math.pow((double)0.58f * (yVelocity - (double)minFallSpeedToEngageRoll), 2.0) + 10.0;
                    EntityStatValue healthStatValue = entityStatMapComponent.get(DefaultEntityStatTypes.getHealth());
                    assert (healthStatValue != null);
                    float maxHealth = healthStatValue.getMax();
                    double healthModifier = (double)maxHealth / 100.0;
                    int damageInt = (int)Math.floor(healthModifier * damagePercentage);
                    if (movementStates.rolling) {
                        if (yVelocity <= (double)movementConfig.getMaxFallSpeedRollFullMitigation()) {
                            damageInt = 0;
                        } else if (yVelocity <= (double)movementConfig.getMaxFallSpeedToEngageRoll()) {
                            damageInt = (int)((double)damageInt * (1.0 - (double)movementConfig.getFallDamagePartialMitigationPercent() / 100.0));
                        }
                    }
                    if (damageInt > 0) {
                        assert (DamageCause.FALL != null);
                        Damage damage = new Damage(Damage.NULL_SOURCE, DamageCause.FALL, (float)damageInt);
                        DamageSystems.executeDamage(index, archetypeChunk, commandBuffer, damage);
                    }
                }
                entity.setCurrentFallDistance(0.0);
            }
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }
    }

    public static class FallDamagePlayers
    extends EntityTickingSystem<EntityStore> {
        static final float CURVE_MODIFIER = 0.58f;
        static final float CURVE_MULTIPLIER = 2.0f;
        public static final double MIN_DAMAGE = 10.0;
        @Nonnull
        private static final Query<EntityStore> QUERY = Query.and(EntityStatMap.getComponentType(), MovementStatesComponent.getComponentType(), EntityModule.get().getPlayerComponentType(), PlayerInput.getComponentType());
        @Nonnull
        private static final Set<Dependency<EntityStore>> DEPENDENCIES = Set.of(new SystemDependency(Order.BEFORE, PlayerSystems.ProcessPlayerInput.class));

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getGatherDamageGroup();
        }

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
        public void tick(float dt, int systemIndex, @Nonnull Store<EntityStore> store) {
            World world = store.getExternalData().getWorld();
            if (!world.getWorldConfig().isFallDamageEnabled()) {
                return;
            }
            super.tick(dt, systemIndex, store);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            PlayerInput playerInputComponent = archetypeChunk.getComponent(index, PlayerInput.getComponentType());
            assert (playerInputComponent != null);
            Velocity velocityComponent = archetypeChunk.getComponent(index, Velocity.getComponentType());
            assert (velocityComponent != null);
            double yVelocity = Math.abs(velocityComponent.getClientVelocity().getY());
            World world = commandBuffer.getExternalData().getWorld();
            PlayerConfig worldPlayerConfig = world.getGameplayConfig().getPlayerConfig();
            List<PlayerInput.InputUpdate> queue = playerInputComponent.getMovementUpdateQueue();
            block4: for (int i = 0; i < queue.size(); ++i) {
                PlayerInput.InputUpdate inputUpdate;
                PlayerInput.InputUpdate queueEntry = queue.get(i);
                Objects.requireNonNull(queueEntry);
                int n = 0;
                switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{PlayerInput.SetClientVelocity.class, PlayerInput.SetMovementStates.class}, (Object)inputUpdate, n)) {
                    case 0: {
                        PlayerInput.SetClientVelocity velocityEntry = (PlayerInput.SetClientVelocity)inputUpdate;
                        yVelocity = Math.abs(velocityEntry.getVelocity().y);
                        continue block4;
                    }
                    case 1: {
                        PlayerInput.SetMovementStates movementStatesEntry = (PlayerInput.SetMovementStates)inputUpdate;
                        Player playerComponent = archetypeChunk.getComponent(index, Player.getComponentType());
                        assert (playerComponent != null);
                        if (!movementStatesEntry.movementStates().onGround || !(playerComponent.getCurrentFallDistance() > 0.0)) continue block4;
                        int movementConfigIndex = worldPlayerConfig.getMovementConfigIndex();
                        MovementConfig movementConfig = MovementConfig.getAssetMap().getAsset(movementConfigIndex);
                        float minFallSpeedToEngageRoll = movementConfig.getMinFallSpeedToEngageRoll();
                        if (yVelocity > (double)minFallSpeedToEngageRoll && !movementStatesEntry.movementStates().inFluid) {
                            EntityStatMap entityStatMapComponent = archetypeChunk.getComponent(index, EntityStatMap.getComponentType());
                            assert (entityStatMapComponent != null);
                            double damagePercentage = Math.pow((double)0.58f * (yVelocity - (double)minFallSpeedToEngageRoll), 2.0) + 10.0;
                            EntityStatValue healthStatValue = entityStatMapComponent.get(DefaultEntityStatTypes.getHealth());
                            assert (healthStatValue != null);
                            float maxHealth = healthStatValue.getMax();
                            double healthModifier = (double)maxHealth / 100.0;
                            int damageInt = (int)Math.floor(healthModifier * damagePercentage);
                            if (movementStatesEntry.movementStates().rolling) {
                                if (yVelocity <= (double)movementConfig.getMaxFallSpeedRollFullMitigation()) {
                                    damageInt = 0;
                                } else if (yVelocity <= (double)movementConfig.getMaxFallSpeedToEngageRoll()) {
                                    damageInt = (int)((double)damageInt * (1.0 - (double)movementConfig.getFallDamagePartialMitigationPercent() / 100.0));
                                }
                            }
                            if (damageInt > 0) {
                                assert (DamageCause.FALL != null);
                                Damage damage = new Damage(Damage.NULL_SOURCE, DamageCause.FALL, (float)damageInt);
                                DamageSystems.executeDamage(index, archetypeChunk, commandBuffer, damage);
                            }
                        }
                        playerComponent.setCurrentFallDistance(0.0);
                        continue block4;
                    }
                }
            }
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }
    }

    public static class OutOfWorldDamage
    extends DelayedEntitySystem<EntityStore> {
        @Nonnull
        private static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();

        public OutOfWorldDamage() {
            super(1.0f);
        }

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getGatherDamageGroup();
        }

        @Override
        public Query<EntityStore> getQuery() {
            return TRANSFORM_COMPONENT_TYPE;
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            TransformComponent transformComponent = archetypeChunk.getComponent(index, TRANSFORM_COMPONENT_TYPE);
            assert (transformComponent != null);
            double posY = transformComponent.getPosition().getY();
            if (posY >= 0.0) {
                return;
            }
            boolean belowMinimum = posY < -32.0;
            Damage damage = new Damage(Damage.NULL_SOURCE, DamageCause.OUT_OF_WORLD, belowMinimum ? 2.1474836E9f : 50.0f);
            if (belowMinimum) {
                DeathComponent.tryAddComponent(commandBuffer, archetypeChunk.getReferenceTo(index), damage);
                return;
            }
            DamageSystems.executeDamage(index, archetypeChunk, commandBuffer, damage);
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }
    }

    public static class CanBreathe
    extends DelayedEntitySystem<EntityStore> {
        private static final float DAMAGE_AMOUNT_DROWNING = 10.0f;
        private static final float DAMAGE_AMOUNT_SUFFOCATION = 20.0f;
        @Nonnull
        private static final ComponentType<EntityStore, ModelComponent> MODEL_COMPONENT_TYPE = ModelComponent.getComponentType();
        @Nonnull
        private static final Query<EntityStore> QUERY = Query.and(AllLegacyLivingEntityTypesQuery.INSTANCE, EntityStatMap.getComponentType(), TransformComponent.getComponentType(), MODEL_COMPONENT_TYPE);

        public CanBreathe() {
            super(1.0f);
        }

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getGatherDamageGroup();
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            int fluidId;
            long packed;
            BlockMaterial material;
            Ref<EntityStore> ref;
            LivingEntity entity = (LivingEntity)EntityUtils.getEntity(index, archetypeChunk);
            assert (entity != null);
            EntityStatMap statMapComponent = archetypeChunk.getComponent(index, EntityStatMap.getComponentType());
            assert (statMapComponent != null);
            EntityStatValue oxygenStatValue = statMapComponent.get(DefaultEntityStatTypes.getOxygen());
            if (oxygenStatValue != null && !entity.canBreathe(ref = archetypeChunk.getReferenceTo(index), material = BlockMaterial.VALUES[MathUtil.unpackLeft(packed = LivingEntity.getPackedMaterialAndFluidAtBreathingHeight(ref, commandBuffer))], fluidId = MathUtil.unpackRight(packed), commandBuffer) && oxygenStatValue.get() <= oxygenStatValue.getMin()) {
                Damage damage;
                if (fluidId != 0) {
                    assert (DamageCause.DROWNING != null);
                    damage = new Damage(Damage.NULL_SOURCE, DamageCause.DROWNING, 10.0f);
                } else {
                    assert (DamageCause.SUFFOCATION != null);
                    damage = new Damage(Damage.NULL_SOURCE, DamageCause.SUFFOCATION, 20.0f);
                }
                DamageSystems.executeDamage(index, archetypeChunk, commandBuffer, damage);
            }
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return false;
        }
    }

    public static class ApplyDamage
    extends DamageEventSystem
    implements EntityStatsSystems.StatModifyingSystem {
        @Nonnull
        private static final Query<EntityStore> QUERY = EntityStatMap.getComponentType();
        @Nonnull
        private static final Set<Dependency<EntityStore>> DEPENDENCIES = Set.of(new SystemGroupDependency<EntityStore>(Order.AFTER, DamageModule.get().getGatherDamageGroup()), new SystemGroupDependency<EntityStore>(Order.AFTER, DamageModule.get().getFilterDamageGroup()), new SystemGroupDependency<EntityStore>(Order.BEFORE, DamageModule.get().getInspectDamageGroup()));

        @Override
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return DEPENDENCIES;
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
            EntityStatMap entityStatMapComponent = archetypeChunk.getComponent(index, EntityStatMap.getComponentType());
            assert (entityStatMapComponent != null);
            int healthStat = DefaultEntityStatTypes.getHealth();
            EntityStatValue healthValue = entityStatMapComponent.get(healthStat);
            Objects.requireNonNull(healthValue);
            boolean isDead = archetypeChunk.getArchetype().contains(DeathComponent.getComponentType());
            if (isDead) {
                damage.setCancelled(true);
                return;
            }
            damage.setAmount(Math.round(damage.getAmount()));
            float newValue = entityStatMapComponent.subtractStatValue(healthStat, damage.getAmount());
            if (newValue <= healthValue.getMin()) {
                DeathComponent.tryAddComponent(commandBuffer, archetypeChunk.getReferenceTo(index), damage);
            }
        }
    }

    public static class RecordLastCombat
    extends DamageEventSystem {
        @Nonnull
        private static final ComponentType<EntityStore, DamageDataComponent> DAMAGE_DATA_COMPONENT_TYPE = DamageDataComponent.getComponentType();
        @Nonnull
        private static final ResourceType<EntityStore, TimeResource> TIME_RESOURCE_TYPE = TimeResource.getResourceType();
        @Nonnull
        private static final Query<EntityStore> QUERY = DAMAGE_DATA_COMPONENT_TYPE;

        @Override
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
            DamageDataComponent damageDataComponent = archetypeChunk.getComponent(index, DAMAGE_DATA_COMPONENT_TYPE);
            assert (damageDataComponent != null);
            Instant timestamp = store.getResource(TIME_RESOURCE_TYPE).getNow();
            damageDataComponent.setLastCombatAction(timestamp);
            Damage.Source source = damage.getSource();
            if (source instanceof Damage.EntitySource) {
                Damage.EntitySource entitySource = (Damage.EntitySource)source;
                Ref<EntityStore> sourceRef = entitySource.getRef();
                if (!sourceRef.isValid()) {
                    return;
                }
                DamageDataComponent sourceDamageDataComponent = store.getComponent(sourceRef, DAMAGE_DATA_COMPONENT_TYPE);
                if (sourceDamageDataComponent != null) {
                    sourceDamageDataComponent.setLastCombatAction(timestamp);
                }
            }
        }

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getInspectDamageGroup();
        }
    }

    public static class ApplySoundEffects
    extends DamageEventSystem {
        @Nonnull
        private static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();
        @Nonnull
        private static final ComponentType<EntityStore, Player> PLAYER_COMPONENT_TYPE = Player.getComponentType();
        @Nonnull
        private static final Query<EntityStore> QUERY = TRANSFORM_COMPONENT_TYPE;

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
        }

        @Override
        public void handleInternal(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
            Vector3d targetPosition;
            Damage.EntitySource source;
            TransformComponent transformComponent = archetypeChunk.getComponent(index, TRANSFORM_COMPONENT_TYPE);
            assert (transformComponent != null);
            Player playerComponent = archetypeChunk.getComponent(index, PLAYER_COMPONENT_TYPE);
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            Damage.SoundEffect soundEffect = damage.getIfPresentMetaObject(Damage.IMPACT_SOUND_EFFECT);
            Damage.SoundEffect playerSoundEffect = damage.getIfPresentMetaObject(Damage.PLAYER_IMPACT_SOUND_EFFECT);
            if (soundEffect == null && playerSoundEffect == null) {
                return;
            }
            Damage.Source source2 = damage.getSource();
            Ref<EntityStore> sourceRef = source2 instanceof Damage.EntitySource ? ((source = (Damage.EntitySource)source2).getRef().isValid() ? source.getRef() : null) : null;
            Vector4d hitLocation = damage.getIfPresentMetaObject(Damage.HIT_LOCATION);
            Vector3d vector3d = targetPosition = hitLocation == null ? transformComponent.getPosition() : new Vector3d(hitLocation.x, hitLocation.y, hitLocation.z);
            if (soundEffect != null && soundEffect.getSoundEventIndex() != 0) {
                Predicate<Ref<EntityStore>> filter = sourceRef != null ? p -> !p.equals(sourceRef) : p -> true;
                SoundUtil.playSoundEvent3d(soundEffect.getSoundEventIndex(), targetPosition.x, targetPosition.y, targetPosition.z, filter, commandBuffer);
            }
            if (playerComponent != null && playerSoundEffect != null && playerSoundEffect.getSoundEventIndex() != 0) {
                SoundUtil.playSoundEvent3dToPlayer(ref, playerSoundEffect.getSoundEventIndex(), SoundCategory.SFX, targetPosition.x, targetPosition.y, targetPosition.z, commandBuffer);
            }
        }

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getInspectDamageGroup();
        }
    }

    public static class ApplyParticles
    extends DamageEventSystem {
        @Nonnull
        private static final ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> PLAYER_SPATIAL_RESOURCE_TYPE = EntityModule.get().getPlayerSpatialResourceType();
        @Nonnull
        private static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();
        @Nonnull
        private static final ComponentType<EntityStore, NetworkId> NETWORK_ID_COMPONENT_TYPE = NetworkId.getComponentType();
        @Nonnull
        private static final Query<EntityStore> QUERY = Query.and(TRANSFORM_COMPONENT_TYPE, NETWORK_ID_COMPONENT_TYPE);

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
            Object[] modelParticles;
            TransformComponent sourceTransformComponent;
            Damage.Particles particles = damage.getIfPresentMetaObject(Damage.IMPACT_PARTICLES);
            if (particles == null) {
                return;
            }
            Damage.Source source = damage.getSource();
            if (!(source instanceof Damage.EntitySource)) {
                return;
            }
            Damage.EntitySource sourceEntity = (Damage.EntitySource)source;
            Ref<EntityStore> sourceRef = sourceEntity.getRef();
            if (!sourceRef.isValid()) {
                return;
            }
            TransformComponent transformComponent = archetypeChunk.getComponent(index, TRANSFORM_COMPONENT_TYPE);
            assert (transformComponent != null);
            Vector4d hitLocation = damage.getIfPresentMetaObject(Damage.HIT_LOCATION);
            Vector3d targetPosition = hitLocation == null ? transformComponent.getPosition() : new Vector3d(hitLocation.x, hitLocation.y, hitLocation.z);
            boolean damageCanBePredicted = damage.getMetaStore().getMetaObject(Damage.CAN_BE_PREDICTED);
            double particlesViewDistance = particles.getViewDistance();
            Object[] worldParticles = particles.getWorldParticles();
            if (!Arrays.isNullOrEmpty(worldParticles) && (sourceTransformComponent = commandBuffer.getComponent(sourceRef, TransformComponent.getComponentType())) != null) {
                float angleBetween = TrigMathUtil.atan2(sourceTransformComponent.getPosition().x - targetPosition.x, sourceTransformComponent.getPosition().z - targetPosition.z);
                SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = commandBuffer.getResource(EntityModule.get().getPlayerSpatialResourceType());
                ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
                playerSpatialResource.getSpatialStructure().collect(targetPosition, particlesViewDistance, results);
                Ref<EntityStore> particleSource = damageCanBePredicted ? sourceRef : null;
                for (Object particle : worldParticles) {
                    ParticleUtil.spawnParticleEffect((WorldParticle)particle, targetPosition, angleBetween, 0.0f, 0.0f, particleSource, results, commandBuffer);
                }
            }
            if (!Arrays.isNullOrEmpty(modelParticles = particles.getModelParticles())) {
                com.hypixel.hytale.protocol.ModelParticle[] modelParticlesProtocol = new com.hypixel.hytale.protocol.ModelParticle[modelParticles.length];
                for (int j = 0; j < modelParticles.length; ++j) {
                    modelParticlesProtocol[j] = ((ModelParticle)modelParticles[j]).toPacket();
                }
                NetworkId networkIdComponent = archetypeChunk.getComponent(index, NETWORK_ID_COMPONENT_TYPE);
                assert (networkIdComponent != null);
                int targetNetworkId = networkIdComponent.getId();
                SpawnModelParticles packet = new SpawnModelParticles(targetNetworkId, modelParticlesProtocol);
                SpatialResource<Ref<EntityStore>, EntityStore> spatialResource = store.getResource(PLAYER_SPATIAL_RESOURCE_TYPE);
                SpatialStructure<Ref<EntityStore>> spatialStructure = spatialResource.getSpatialStructure();
                ObjectList results = SpatialResource.getThreadLocalReferenceList();
                spatialStructure.ordered(targetPosition, particlesViewDistance, results);
                for (Ref ref : results) {
                    if (damageCanBePredicted && ref.equals(sourceRef)) {
                        return;
                    }
                    PlayerRef playerRefComponent = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
                    if (playerRefComponent == null) continue;
                    playerRefComponent.getPacketHandler().write((Packet)packet);
                }
            }
        }

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getInspectDamageGroup();
        }
    }

    public static class FilterNPCWorldConfig
    extends DamageEventSystem {
        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getFilterDamageGroup();
        }

        @Override
        @Nullable
        public Query<EntityStore> getQuery() {
            return NPCS_QUERY;
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage event) {
            World world = store.getExternalData().getWorld();
            GameplayConfig gameplayConfig = world.getGameplayConfig();
            if (gameplayConfig.getCombatConfig().isNpcIncomingDamageDisabled()) {
                event.setCancelled(true);
                Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
                commandBuffer.tryRemoveComponent(ref, KnockbackComponent.getComponentType());
            }
        }
    }

    public static class FilterPlayerWorldConfig
    extends DamageEventSystem {
        private static final Query<EntityStore> QUERY = Query.and(AllLegacyLivingEntityTypesQuery.INSTANCE, Player.getComponentType());

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getFilterDamageGroup();
        }

        @Override
        @Nullable
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage event) {
            World world = store.getExternalData().getWorld();
            GameplayConfig gameplayConfig = world.getGameplayConfig();
            if (gameplayConfig.getCombatConfig().isPlayerIncomingDamageDisabled()) {
                event.setCancelled(true);
                Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
                commandBuffer.tryRemoveComponent(ref, KnockbackComponent.getComponentType());
            }
        }
    }

    public static class FilterUnkillable
    extends DamageEventSystem {
        public static boolean CAUSE_DESYNC;
        @Nonnull
        private static final Query<EntityStore> QUERY;

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getFilterDamageGroup();
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return QUERY;
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
            EffectControllerComponent entityEffectControllerComponent = archetypeChunk.getComponent(index, EffectControllerComponent.getComponentType());
            if (entityEffectControllerComponent != null && entityEffectControllerComponent.isInvulnerable()) {
                damage.setCancelled(true);
            }
            Archetype<EntityStore> archetype = archetypeChunk.getArchetype();
            boolean dead = archetype.contains(DeathComponent.getComponentType());
            boolean invulnerable = archetype.contains(Invulnerable.getComponentType());
            boolean intangible = archetype.contains(Intangible.getComponentType());
            if (dead || invulnerable || intangible || CAUSE_DESYNC) {
                damage.setCancelled(true);
            }
        }

        static {
            QUERY = AllLegacyLivingEntityTypesQuery.INSTANCE;
        }
    }
}

