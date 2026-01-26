/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.util.TrigMathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector4d;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntitySnapshot;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.knockback.KnockbackComponent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import com.hypixel.hytale.server.core.meta.DynamicMetaStore;
import com.hypixel.hytale.server.core.meta.MetaKey;
import com.hypixel.hytale.server.core.modules.debug.DebugUtils;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCalculatorSystems;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.Collector;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.none.SelectInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageCalculator;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageClass;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageEffects;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.Knockback;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.TargetEntityEffect;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Label;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.OperationsBuilder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DamageEntityInteraction
extends Interaction {
    @Nonnull
    public static final BuilderCodec<DamageEntityInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(DamageEntityInteraction.class, DamageEntityInteraction::new, Interaction.ABSTRACT_CODEC).documentation("Damages the target entity.")).appendInherited(new KeyedCodec<DamageCalculator>("DamageCalculator", DamageCalculator.CODEC), (i, a) -> {
        i.damageCalculator = a;
    }, i -> i.damageCalculator, (i, parent) -> {
        i.damageCalculator = parent.damageCalculator;
    }).add()).appendInherited(new KeyedCodec<DamageEffects>("DamageEffects", DamageEffects.CODEC), (i, o) -> {
        i.damageEffects = o;
    }, i -> i.damageEffects, (i, parent) -> {
        i.damageEffects = parent.damageEffects;
    }).add()).appendInherited(new KeyedCodec<T[]>("AngledDamage", new ArrayCodec<AngledDamage>(AngledDamage.CODEC, AngledDamage[]::new)), (i, o) -> {
        i.angledDamage = o;
    }, i -> i.angledDamage, (i, parent) -> {
        i.angledDamage = parent.angledDamage;
    }).add()).appendInherited(new KeyedCodec("TargetedDamage", new MapCodec<TargetedDamage, HashMap>(TargetedDamage.CODEC, HashMap::new)), (i, o) -> {
        i.targetedDamage = o;
    }, i -> i.targetedDamage, (i, parent) -> {
        i.targetedDamage = parent.targetedDamage;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<T[]>("EntityStatsOnHit", new ArrayCodec<EntityStatOnHit>(EntityStatOnHit.CODEC, EntityStatOnHit[]::new)), (damageEntityInteraction, entityStatOnHit) -> {
        damageEntityInteraction.entityStatsOnHit = entityStatOnHit;
    }, damageEntityInteraction -> damageEntityInteraction.entityStatsOnHit, (damageEntityInteraction, parent) -> {
        damageEntityInteraction.entityStatsOnHit = parent.entityStatsOnHit;
    }).documentation("EntityStats to apply based on the hits resulting from this interaction.").add()).appendInherited(new KeyedCodec<String>("Next", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> {
        interaction.next = s;
    }, interaction -> interaction.next, (interaction, parent) -> {
        interaction.next = parent.next;
    }).documentation("The interactions to run when this interaction succeeds.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec<String>("Failed", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> {
        interaction.failed = s;
    }, interaction -> interaction.failed, (interaction, parent) -> {
        interaction.failed = parent.failed;
    }).documentation("The interactions to run when this interaction fails.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec<String>("Blocked", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> {
        interaction.blocked = s;
    }, interaction -> interaction.blocked, (interaction, parent) -> {
        interaction.blocked = parent.blocked;
    }).documentation("The interactions to run when this interaction fails.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).afterDecode(o -> {
        o.sortedTargetDamageKeys = (String[])o.targetedDamage.keySet().toArray(String[]::new);
        Object[] keys = o.sortedTargetDamageKeys;
        Arrays.sort(keys);
        int i = 0;
        while (i < keys.length) {
            Object k = keys[i];
            o.targetedDamage.get((Object)k).index = i++;
        }
    })).build();
    private static final int FAILED_LABEL_INDEX = 0;
    private static final int SUCCESS_LABEL_INDEX = 1;
    private static final int BLOCKED_LABEL_INDEX = 2;
    private static final int ANGLED_LABEL_OFFSET = 3;
    public static final int ARMOR_RESISTANCE_FLAT_MODIFIER = 0;
    public static final int ARMOR_RESISTANCE_MULTIPLIER_MODIFIER = 1;
    private static final MetaKey<DamageCalculatorSystems.Sequence> SEQUENTIAL_HITS = META_REGISTRY.registerMetaObject(i -> new DamageCalculatorSystems.Sequence());
    private static final MetaKey<Integer> NEXT_INDEX = META_REGISTRY.registerMetaObject();
    private static final MetaKey<Damage[]> QUEUED_DAMAGE = META_REGISTRY.registerMetaObject();
    protected DamageCalculator damageCalculator;
    @Nullable
    protected DamageEffects damageEffects;
    protected AngledDamage[] angledDamage;
    protected EntityStatOnHit[] entityStatsOnHit;
    protected Map<String, TargetedDamage> targetedDamage = Collections.emptyMap();
    protected String[] sortedTargetDamageKeys;
    @Nullable
    protected String next;
    @Nullable
    protected String blocked;
    @Nullable
    protected String failed;

    @Override
    protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        Ref<EntityStore> targetRef = context.getTargetEntity();
        if (targetRef == null || !targetRef.isValid() || !context.getEntity().isValid()) {
            context.jump(context.getLabel(0));
            context.getState().nextLabel = 0;
            context.getState().state = InteractionState.Failed;
            return;
        }
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        if (this.processDamage(context, context.getInstanceStore().getIfPresentMetaObject(QUEUED_DAMAGE))) {
            return;
        }
        Ref<EntityStore> ref = context.getOwningEntity();
        Vector4d hit = context.getMetaStore().getMetaObject(Interaction.HIT_LOCATION);
        Damage.EntitySource source = new Damage.EntitySource(ref);
        this.attemptEntityDamage0(source, context, context.getEntity(), targetRef, hit);
        if (SelectInteraction.SHOW_VISUAL_DEBUG && hit != null) {
            DebugUtils.addSphere(commandBuffer.getExternalData().getWorld(), new Vector3d(hit.x, hit.y, hit.z), new Vector3f(1.0f, 0.0f, 0.0f), 0.2f, 5.0f);
        }
    }

    @Override
    protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        this.tick0(firstRun, time, type, context, cooldownHandler);
    }

    private boolean processDamage(@Nonnull InteractionContext context, @Nullable Damage[] queuedDamage) {
        if (queuedDamage == null) {
            return false;
        }
        boolean failed = true;
        boolean blocked = false;
        for (Damage queue : queuedDamage) {
            if (!queue.isCancelled()) {
                failed = false;
            }
            if (!queue.getMetaObject(Damage.BLOCKED).booleanValue()) continue;
            blocked = true;
        }
        if (failed) {
            context.jump(context.getLabel(0));
            context.getState().nextLabel = 0;
            context.getState().state = InteractionState.Failed;
        } else if (blocked) {
            context.jump(context.getLabel(2));
            context.getState().nextLabel = 2;
            context.getState().state = InteractionState.Finished;
        } else {
            int index;
            context.getState().nextLabel = index = context.getInstanceStore().getMetaObject(NEXT_INDEX).intValue();
            context.jump(context.getLabel(index));
            context.getState().state = InteractionState.Finished;
        }
        return true;
    }

    @Override
    public void compile(@Nonnull OperationsBuilder builder) {
        Label[] labels = new Label[3 + (this.angledDamage != null ? this.angledDamage.length : 0) + this.targetedDamage.size()];
        builder.addOperation(this, labels);
        Label endLabel = builder.createUnresolvedLabel();
        labels[0] = builder.createLabel();
        if (this.failed != null) {
            Interaction.getInteractionOrUnknown(this.failed).compile(builder);
        }
        builder.jump(endLabel);
        labels[1] = builder.createLabel();
        if (this.next != null) {
            Interaction.getInteractionOrUnknown(this.next).compile(builder);
        }
        builder.jump(endLabel);
        labels[2] = builder.createLabel();
        if (this.blocked != null) {
            Interaction.getInteractionOrUnknown(this.blocked).compile(builder);
        }
        builder.jump(endLabel);
        int offset = 3;
        if (this.angledDamage != null) {
            for (AngledDamage damage : this.angledDamage) {
                labels[offset++] = builder.createLabel();
                String next = damage.next;
                if (next == null) {
                    next = this.next;
                }
                if (next != null) {
                    Interaction.getInteractionOrUnknown(next).compile(builder);
                }
                builder.jump(endLabel);
            }
        }
        if (!this.targetedDamage.isEmpty()) {
            for (String k : this.sortedTargetDamageKeys) {
                TargetedDamage entry = this.targetedDamage.get(k);
                labels[offset++] = builder.createLabel();
                String next = entry.next;
                if (next == null) {
                    next = this.next;
                }
                if (next != null) {
                    Interaction.getInteractionOrUnknown(next).compile(builder);
                }
                builder.jump(endLabel);
            }
        }
        builder.resolveLabel(endLabel);
    }

    @Override
    public boolean walk(@Nonnull Collector collector, @Nonnull InteractionContext context) {
        return false;
    }

    @Override
    @Nonnull
    protected com.hypixel.hytale.protocol.Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.DamageEntityInteraction();
    }

    @Override
    protected void configurePacket(com.hypixel.hytale.protocol.Interaction packet) {
        int i;
        super.configurePacket(packet);
        com.hypixel.hytale.protocol.DamageEntityInteraction p = (com.hypixel.hytale.protocol.DamageEntityInteraction)packet;
        p.damageEffects = this.damageEffects != null ? this.damageEffects.toPacket() : null;
        p.next = Interaction.getInteractionIdOrUnknown(this.next);
        p.failed = Interaction.getInteractionIdOrUnknown(this.failed);
        p.blocked = Interaction.getInteractionIdOrUnknown(this.blocked);
        if (this.angledDamage != null) {
            p.angledDamage = new com.hypixel.hytale.protocol.AngledDamage[this.angledDamage.length];
            for (i = 0; i < this.angledDamage.length; ++i) {
                p.angledDamage[i] = this.angledDamage[i].toAngledDamagePacket();
            }
        }
        if (this.entityStatsOnHit != null) {
            p.entityStatsOnHit = new com.hypixel.hytale.protocol.EntityStatOnHit[this.entityStatsOnHit.length];
            for (i = 0; i < this.entityStatsOnHit.length; ++i) {
                p.entityStatsOnHit[i] = this.entityStatsOnHit[i].toPacket();
            }
        }
        p.targetedDamage = new Object2ObjectOpenHashMap<String, com.hypixel.hytale.protocol.TargetedDamage>();
        for (Map.Entry<String, TargetedDamage> e : this.targetedDamage.entrySet()) {
            p.targetedDamage.put(e.getKey(), e.getValue().toTargetedDamagePacket());
        }
    }

    @Override
    public boolean needsRemoteSync() {
        return true;
    }

    @Override
    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.None;
    }

    private void attemptEntityDamage0(@Nonnull Damage.Source source, @Nonnull InteractionContext context, @Nonnull Ref<EntityStore> attackerRef, @Nonnull Ref<EntityStore> targetRef, @Nullable Vector4d hit) {
        TargetedDamage entry;
        String hitDetail;
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        DamageCalculator damageCalculator = this.damageCalculator;
        DamageEffects damageEffects = this.damageEffects;
        EntitySnapshot targetSnapshot = context.getSnapshot(targetRef, commandBuffer);
        EntitySnapshot attackerSnapshot = context.getSnapshot(attackerRef, commandBuffer);
        Vector3d targetPos = targetSnapshot.getPosition();
        Vector3d attackerPos = attackerSnapshot.getPosition();
        float angleBetween = TrigMathUtil.atan2(attackerPos.x - targetPos.x, attackerPos.z - targetPos.z);
        int nextLabel = 1;
        if (this.angledDamage != null) {
            float angleBetweenRotation = MathUtil.wrapAngle(angleBetween + (float)Math.PI - targetSnapshot.getBodyRotation().getYaw());
            for (int i = 0; i < this.angledDamage.length; ++i) {
                AngledDamage angledDamage = this.angledDamage[i];
                if (!(Math.abs(MathUtil.compareAngle(angleBetweenRotation, angledDamage.angleRad)) < (double)angledDamage.angleDistanceRad)) continue;
                damageCalculator = angledDamage.damageCalculator == null ? damageCalculator : angledDamage.damageCalculator;
                damageEffects = angledDamage.damageEffects == null ? damageEffects : angledDamage.damageEffects;
                nextLabel = 3 + i;
                break;
            }
        }
        if ((hitDetail = (String)context.getMetaStore().getIfPresentMetaObject(HIT_DETAIL)) != null && (entry = this.targetedDamage.get(hitDetail)) != null) {
            damageCalculator = entry.damageCalculator == null ? damageCalculator : entry.damageCalculator;
            damageEffects = entry.damageEffects == null ? damageEffects : entry.damageEffects;
            nextLabel = entry.index;
        }
        context.getInstanceStore().putMetaObject(NEXT_INDEX, nextLabel);
        if (damageCalculator == null) {
            return;
        }
        DynamicMetaStore<Interaction> metaStore = context.getMetaStore().getMetaObject(SelectInteraction.SELECT_META_STORE);
        DamageCalculatorSystems.Sequence sequentialHits = metaStore == null ? new DamageCalculatorSystems.Sequence() : metaStore.getMetaObject(SEQUENTIAL_HITS);
        Object2FloatMap<DamageCause> damage = damageCalculator.calculateDamage(this.getRunTime());
        HeadRotation attackerHeadRotationComponent = commandBuffer.getComponent(attackerRef, HeadRotation.getComponentType());
        Vector3f attackerDirection = attackerHeadRotationComponent != null ? attackerHeadRotationComponent.getRotation() : Vector3f.ZERO;
        if (damage != null && !damage.isEmpty()) {
            Player attackerPlayerComponent;
            double[] knockbackMultiplier = new double[]{1.0};
            float[] armorDamageModifiers = new float[]{0.0f, 1.0f};
            DamageEntityInteraction.calculateKnockbackAndArmorModifiers(damageCalculator.getDamageClass(), damage, targetRef, attackerRef, armorDamageModifiers, knockbackMultiplier, commandBuffer);
            KnockbackComponent knockbackComponent = null;
            if (damageEffects != null && damageEffects.getKnockback() != null) {
                knockbackComponent = commandBuffer.getComponent(targetRef, KnockbackComponent.getComponentType());
                if (knockbackComponent == null) {
                    knockbackComponent = new KnockbackComponent();
                    commandBuffer.putComponent(targetRef, KnockbackComponent.getComponentType(), knockbackComponent);
                }
                Knockback knockback = damageEffects.getKnockback();
                knockbackComponent.setVelocity(knockback.calculateVector(attackerPos, attackerDirection.getYaw(), targetPos).scale(knockbackMultiplier[0]));
                knockbackComponent.setVelocityType(knockback.getVelocityType());
                knockbackComponent.setVelocityConfig(knockback.getVelocityConfig());
                knockbackComponent.setDuration(knockback.getDuration());
            }
            ItemStack itemInHand = (attackerPlayerComponent = commandBuffer.getComponent(attackerRef, Player.getComponentType())) == null || attackerPlayerComponent.canApplyItemStackPenalties(attackerRef, commandBuffer) ? context.getHeldItem() : null;
            Damage[] hits = DamageCalculatorSystems.queueDamageCalculator(commandBuffer.getExternalData().getWorld(), damage, targetRef, context.getCommandBuffer(), source, itemInHand);
            if (hits.length > 0) {
                Damage firstDamage = hits[0];
                DamageCalculatorSystems.DamageSequence seq = new DamageCalculatorSystems.DamageSequence(sequentialHits, damageCalculator);
                seq.setEntityStatOnHit(this.entityStatsOnHit);
                firstDamage.putMetaObject(DamageCalculatorSystems.DAMAGE_SEQUENCE, seq);
                if (damageEffects != null) {
                    damageEffects.addToDamage(firstDamage);
                }
                for (Damage damageEvent : hits) {
                    if (knockbackComponent != null) {
                        damageEvent.putMetaObject(Damage.KNOCKBACK_COMPONENT, knockbackComponent);
                    }
                    float damageValue = damageEvent.getAmount();
                    damageEvent.setAmount((damageValue += armorDamageModifiers[0]) * Math.max(0.0f, armorDamageModifiers[1]));
                    if (hit != null) {
                        damageEvent.putMetaObject(Damage.HIT_LOCATION, hit);
                        float hitAngleRad = TrigMathUtil.atan2(attackerPos.x - hit.x, attackerPos.z - hit.z);
                        hitAngleRad = MathUtil.wrapAngle(hitAngleRad - attackerDirection.getYaw());
                        float hitAngleDeg = hitAngleRad * 57.295776f;
                        damageEvent.putMetaObject(Damage.HIT_ANGLE, Float.valueOf(hitAngleDeg));
                    }
                    commandBuffer.invoke(targetRef, damageEvent);
                }
                this.processDamage(context, hits);
            }
            context.getInstanceStore().putMetaObject(QUEUED_DAMAGE, hits);
        }
    }

    private static void calculateKnockbackAndArmorModifiers(@Nonnull DamageClass damageClass, @Nonnull Object2FloatMap<DamageCause> damage, @Nonnull Ref<EntityStore> targetRef, @Nonnull Ref<EntityStore> attackerRef, float[] armorDamageModifiers, double[] knockbackMultiplier, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        Entity entity;
        EffectControllerComponent effectControllerComponent = componentAccessor.getComponent(targetRef, EffectControllerComponent.getComponentType());
        if (effectControllerComponent != null) {
            knockbackMultiplier[0] = IntStream.of(effectControllerComponent.getActiveEffectIndexes()).mapToObj(i -> EntityEffect.getAssetStore().getAssetMap().getAsset(i)).filter(effect -> effect != null && effect.getApplicationEffects() != null).mapToDouble(effect -> effect.getApplicationEffects().getKnockbackMultiplier()).reduce(1.0, (a, b) -> a * b);
        }
        if (!((entity = EntityUtils.getEntity(attackerRef, componentAccessor)) instanceof LivingEntity)) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity)entity;
        Inventory inventory = livingEntity.getInventory();
        if (inventory == null) {
            return;
        }
        ItemContainer armorContainer = inventory.getArmor();
        if (armorContainer == null) {
            return;
        }
        float knockbackEnhancementModifier = 1.0f;
        for (short i2 = 0; i2 < armorContainer.getCapacity(); i2 = (short)(i2 + 1)) {
            Item item;
            ItemStack itemStack = armorContainer.getItemStack(i2);
            if (itemStack == null || itemStack.isEmpty() || (item = itemStack.getItem()).getArmor() == null) continue;
            Map<DamageCause, StaticModifier[]> armorDamageEnhancementMap = item.getArmor().getDamageEnhancementValues();
            for (DamageCause damageCause : damage.keySet()) {
                Map<DamageCause, Float> knockbackEnhancements;
                StaticModifier[] armorDamageEnhancementValue;
                if (armorDamageEnhancementMap != null && (armorDamageEnhancementValue = armorDamageEnhancementMap.get(damageCause)) != null) {
                    for (StaticModifier staticModifier : armorDamageEnhancementValue) {
                        if (staticModifier.getCalculationType() == StaticModifier.CalculationType.ADDITIVE) {
                            armorDamageModifiers[0] = armorDamageModifiers[0] + staticModifier.getAmount();
                            continue;
                        }
                        armorDamageModifiers[1] = armorDamageModifiers[1] + staticModifier.getAmount();
                    }
                }
                if ((knockbackEnhancements = item.getArmor().getKnockbackEnhancements()) == null) continue;
                knockbackEnhancementModifier += knockbackEnhancements.get(damageCause).floatValue();
            }
            StaticModifier[] damageClassModifier = item.getArmor().getDamageClassEnhancement().get((Object)damageClass);
            if (damageClassModifier == null) continue;
            for (StaticModifier modifier : damageClassModifier) {
                if (modifier.getCalculationType() == StaticModifier.CalculationType.ADDITIVE) {
                    armorDamageModifiers[0] = armorDamageModifiers[0] + modifier.getAmount();
                    continue;
                }
                armorDamageModifiers[1] = armorDamageModifiers[1] + modifier.getAmount();
            }
        }
        knockbackMultiplier[0] = knockbackMultiplier[0] * (double)knockbackEnhancementModifier;
    }

    public static class AngledDamage
    extends TargetedDamage {
        public static final BuilderCodec<AngledDamage> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(AngledDamage.class, AngledDamage::new, TargetedDamage.CODEC).appendInherited(new KeyedCodec<Float>("Angle", Codec.FLOAT), (o, i) -> {
            o.angleRad = i.floatValue() * ((float)Math.PI / 180);
        }, o -> Float.valueOf(o.angleRad * 57.295776f), (o, p) -> {
            o.angleRad = p.angleRad;
        }).add()).appendInherited(new KeyedCodec<Float>("AngleDistance", Codec.FLOAT), (o, i) -> {
            o.angleDistanceRad = i.floatValue() * ((float)Math.PI / 180);
        }, o -> Float.valueOf(o.angleDistanceRad * 57.295776f), (o, p) -> {
            o.angleDistanceRad = p.angleDistanceRad;
        }).add()).build();
        protected float angleRad;
        protected float angleDistanceRad;

        @Nonnull
        public com.hypixel.hytale.protocol.AngledDamage toAngledDamagePacket() {
            com.hypixel.hytale.protocol.DamageEffects damageEffectsPacket = this.damageEffects == null ? null : this.damageEffects.toPacket();
            return new com.hypixel.hytale.protocol.AngledDamage(this.angleRad, this.angleDistanceRad, damageEffectsPacket, Interaction.getInteractionIdOrUnknown(this.next));
        }

        @Override
        @Nonnull
        public String toString() {
            return "AngledDamage{angleRad=" + this.angleRad + ", angleDistanceRad=" + this.angleDistanceRad + "} " + super.toString();
        }
    }

    public static class TargetedDamage {
        public static final BuilderCodec<TargetedDamage> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(TargetedDamage.class, TargetedDamage::new).appendInherited(new KeyedCodec<DamageCalculator>("DamageCalculator", DamageCalculator.CODEC), (i, a) -> {
            i.damageCalculator = a;
        }, i -> i.damageCalculator, (i, parent) -> {
            i.damageCalculator = parent.damageCalculator;
        }).add()).appendInherited(new KeyedCodec("TargetEntityEffects", new MapCodec<TargetEntityEffect, HashMap>(TargetEntityEffect.CODEC, HashMap::new)), (i, map) -> {
            i.targetEntityEffects = map;
        }, i -> i.targetEntityEffects, (i, parent) -> {
            i.targetEntityEffects = parent.targetEntityEffects;
        }).add()).appendInherited(new KeyedCodec<DamageEffects>("DamageEffects", DamageEffects.CODEC), (i, o) -> {
            i.damageEffects = o;
        }, i -> i.damageEffects, (i, parent) -> {
            i.damageEffects = parent.damageEffects;
        }).add()).appendInherited(new KeyedCodec<String>("Next", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> {
            interaction.next = s;
        }, interaction -> interaction.next, (interaction, parent) -> {
            interaction.next = parent.next;
        }).documentation("The interactions to run when this interaction succeeds.").addValidatorLate(() -> Interaction.VALIDATOR_CACHE.getValidator().late()).add()).build();
        protected int index;
        protected DamageCalculator damageCalculator;
        protected Map<String, TargetEntityEffect> targetEntityEffects;
        protected DamageEffects damageEffects;
        @Nullable
        protected String next;

        @Nonnull
        public com.hypixel.hytale.protocol.TargetedDamage toTargetedDamagePacket() {
            return new com.hypixel.hytale.protocol.TargetedDamage(this.index, this.damageEffects.toPacket(), Interaction.getInteractionIdOrUnknown(this.next));
        }

        @Nonnull
        public String toString() {
            return "TargetedDamage{damageCalculator=" + String.valueOf(this.damageCalculator) + ", targetEntityEffects=" + String.valueOf(this.targetEntityEffects) + ", damageEffects=" + String.valueOf(this.damageEffects) + ", next='" + this.next + "'}";
        }
    }

    public static class EntityStatOnHit
    implements NetworkSerializable<com.hypixel.hytale.protocol.EntityStatOnHit> {
        public static final BuilderCodec<EntityStatOnHit> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(EntityStatOnHit.class, EntityStatOnHit::new).appendInherited(new KeyedCodec<String>("EntityStatId", Codec.STRING), (entityStatOnHitInteraction, s) -> {
            entityStatOnHitInteraction.entityStatId = s;
        }, entityStatOnHitInteraction -> entityStatOnHitInteraction.entityStatId, (entityStatOnHitInteraction, parent) -> {
            entityStatOnHitInteraction.entityStatId = parent.entityStatId;
        }).documentation("The id of the EntityStat that will be affected by the interaction.").addValidator(Validators.nonNull()).addValidator(EntityStatType.VALIDATOR_CACHE.getValidator()).add()).appendInherited(new KeyedCodec<Float>("Amount", Codec.FLOAT), (entityStatOnHitInteraction, integer) -> {
            entityStatOnHitInteraction.amount = integer.floatValue();
        }, entityStatOnHitInteraction -> Float.valueOf(entityStatOnHitInteraction.amount), (entityStatOnHitInteraction, parent) -> {
            entityStatOnHitInteraction.amount = parent.amount;
        }).documentation("The base amount for a single entity hit.").add()).appendInherited(new KeyedCodec<float[]>("MultipliersPerEntitiesHit", Codec.FLOAT_ARRAY), (entityStatOnHitInteraction, doubles) -> {
            entityStatOnHitInteraction.multipliersPerEntitiesHit = doubles;
        }, entityStatOnHitInteraction -> entityStatOnHitInteraction.multipliersPerEntitiesHit, (entityStatOnHitInteraction, parent) -> {
            entityStatOnHitInteraction.multipliersPerEntitiesHit = parent.multipliersPerEntitiesHit;
        }).documentation("An array of multipliers corresponding to how much the amount should be multiplied by for each entity hit.").addValidator(Validators.nonEmptyFloatArray()).add()).appendInherited(new KeyedCodec<Float>("MultiplierPerExtraEntityHit", Codec.FLOAT), (entityStatOnHitInteraction, aDouble) -> {
            entityStatOnHitInteraction.multiplierPerExtraEntityHit = aDouble.floatValue();
        }, entityStatOnHitInteraction -> Float.valueOf(entityStatOnHitInteraction.multiplierPerExtraEntityHit), (entityStatOnHitInteraction, parent) -> {
            entityStatOnHitInteraction.multiplierPerExtraEntityHit = parent.multiplierPerExtraEntityHit;
        }).documentation("When the number of entity hit is higher than the number of multipliers defined, the amount will be multiplied by this multiplier for each extra entity hit.").add()).afterDecode(entityStatOnHitInteraction -> {
            if (entityStatOnHitInteraction.entityStatId == null) {
                return;
            }
            entityStatOnHitInteraction.entityStatIndex = EntityStatType.getAssetMap().getIndex(entityStatOnHitInteraction.entityStatId);
        })).build();
        public static final float[] DEFAULT_MULTIPLIERS_PER_ENTITIES_HIT = new float[]{1.0f, 0.6f, 0.4f, 0.2f, 0.1f};
        public static final float DEFAULT_MULTIPLIER_PER_EXTRA_ENTITY_HIT = 0.05f;
        protected String entityStatId;
        protected float amount;
        protected float[] multipliersPerEntitiesHit = DEFAULT_MULTIPLIERS_PER_ENTITIES_HIT;
        protected float multiplierPerExtraEntityHit = 0.05f;
        private int entityStatIndex;

        public void processEntityStatsOnHit(int hits, @Nonnull EntityStatMap statMap) {
            if (hits == 0) {
                return;
            }
            float multiplier = hits <= this.multipliersPerEntitiesHit.length ? this.multipliersPerEntitiesHit[hits - 1] : this.multiplierPerExtraEntityHit;
            statMap.addStatValue(EntityStatMap.Predictable.SELF, this.entityStatIndex, multiplier * this.amount);
        }

        @Nonnull
        public String toString() {
            return "EntityStatOnHit{entityStatId='" + this.entityStatId + "', amount=" + this.amount + ", multipliersPerEntitiesHit=" + Arrays.toString(this.multipliersPerEntitiesHit) + ", multiplierPerExtraEntityHit=" + this.multiplierPerExtraEntityHit + ", entityStatIndex=" + this.entityStatIndex + "}";
        }

        @Override
        @Nonnull
        public com.hypixel.hytale.protocol.EntityStatOnHit toPacket() {
            return new com.hypixel.hytale.protocol.EntityStatOnHit(this.entityStatIndex, this.amount, this.multipliersPerEntitiesHit, this.multiplierPerExtraEntityHit);
        }
    }
}

