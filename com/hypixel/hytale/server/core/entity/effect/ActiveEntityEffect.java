/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.entity.effect;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.protocol.ChangeStatBehaviour;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCalculatorSystems;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageCalculator;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageEffects;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ActiveEntityEffect
implements Damage.Source {
    @Nonnull
    public static final BuilderCodec<ActiveEntityEffect> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ActiveEntityEffect.class, ActiveEntityEffect::new).append(new KeyedCodec<String>("EntityEffectId", Codec.STRING), (entityEffect, x) -> {
        entityEffect.entityEffectId = x;
    }, entityEffect -> entityEffect.entityEffectId).add()).append(new KeyedCodec<Float>("InitialDuration", Codec.FLOAT), (entityEffect, x) -> {
        entityEffect.initialDuration = x.floatValue();
    }, entityEffect -> Float.valueOf(entityEffect.initialDuration)).add()).append(new KeyedCodec<Float>("RemainingDuration", Codec.FLOAT), (entityEffect, x) -> {
        entityEffect.remainingDuration = x.floatValue();
    }, entityEffect -> Float.valueOf(entityEffect.remainingDuration)).add()).append(new KeyedCodec<Float>("SinceLastDamage", Codec.FLOAT), (entityEffect, x) -> {
        entityEffect.sinceLastDamage = x.floatValue();
    }, entityEffect -> Float.valueOf(entityEffect.sinceLastDamage)).add()).append(new KeyedCodec<Boolean>("HasBeenDamaged", Codec.BOOLEAN), (entityEffect, x) -> {
        entityEffect.hasBeenDamaged = x;
    }, entityEffect -> entityEffect.hasBeenDamaged).add()).append(new KeyedCodec<DamageCalculatorSystems.Sequence>("SequentialHits", DamageCalculatorSystems.Sequence.CODEC), (entityEffect, x) -> {
        entityEffect.sequentialHits = x;
    }, entityEffect -> entityEffect.sequentialHits).add()).append(new KeyedCodec<Boolean>("Infinite", Codec.BOOLEAN), (entityEffect, aBoolean) -> {
        entityEffect.infinite = aBoolean;
    }, entityEffect -> entityEffect.infinite).add()).append(new KeyedCodec<Boolean>("Debuff", Codec.BOOLEAN), (entityEffect, aBoolean) -> {
        entityEffect.debuff = aBoolean;
    }, entityEffect -> entityEffect.debuff).add()).append(new KeyedCodec<String>("StatusEffectIcon", Codec.STRING), (entityEffect, aString) -> {
        entityEffect.statusEffectIcon = aString;
    }, entityEffect -> entityEffect.statusEffectIcon).add()).append(new KeyedCodec<Boolean>("Invulnerable", Codec.BOOLEAN), (entityEffect, aBoolean) -> {
        entityEffect.invulnerable = aBoolean;
    }, entityEffect -> entityEffect.invulnerable).add()).build();
    private static final float DEFAULT_DURATION = 1.0f;
    @Nonnull
    private static final Message MESSAGE_GENERAL_DAMAGE_CAUSES_UNKNOWN = Message.translation("server.general.damageCauses.unknown");
    protected String entityEffectId;
    protected int entityEffectIndex;
    protected float initialDuration;
    protected float remainingDuration;
    protected boolean infinite;
    protected boolean debuff;
    @Nullable
    protected String statusEffectIcon;
    private float sinceLastDamage;
    private boolean hasBeenDamaged;
    protected boolean invulnerable;
    private DamageCalculatorSystems.Sequence sequentialHits;

    public ActiveEntityEffect() {
    }

    public ActiveEntityEffect(@Nonnull String entityEffectId, int entityEffectIndex, float initialDuration, float remainingDuration, boolean infinite, boolean debuff, @Nullable String statusEffectIcon, float sinceLastDamage, boolean hasBeenDamaged, @Nonnull DamageCalculatorSystems.Sequence sequentialHits, boolean invulnerable) {
        this.entityEffectId = entityEffectId;
        this.entityEffectIndex = entityEffectIndex;
        this.initialDuration = initialDuration;
        this.remainingDuration = remainingDuration;
        this.infinite = infinite;
        this.debuff = debuff;
        this.statusEffectIcon = statusEffectIcon;
        this.sinceLastDamage = sinceLastDamage;
        this.hasBeenDamaged = hasBeenDamaged;
        this.sequentialHits = sequentialHits;
        this.invulnerable = invulnerable;
    }

    public ActiveEntityEffect(@Nonnull String entityEffectId, int entityEffectIndex, float duration, boolean debuff, @Nullable String statusEffectIcon, boolean invulnerable) {
        this(entityEffectId, entityEffectIndex, duration, duration, false, debuff, statusEffectIcon, 0.0f, false, new DamageCalculatorSystems.Sequence(), invulnerable);
    }

    public ActiveEntityEffect(@Nonnull String entityEffectId, int entityEffectIndex, boolean infinite, boolean invulnerable) {
        this(entityEffectId, entityEffectIndex, 1.0f, 1.0f, infinite, false, "", 0.0f, false, new DamageCalculatorSystems.Sequence(), invulnerable);
    }

    public void tick(@Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> ref, @Nonnull EntityEffect entityEffect, @Nonnull EntityStatMap entityStatMapComponent, float dt) {
        int cyclesToRun = this.calculateCyclesToRun(entityEffect, dt);
        this.tickDamage(commandBuffer, ref, entityEffect, cyclesToRun);
        ActiveEntityEffect.tickStatChanges(commandBuffer, ref, entityEffect, entityStatMapComponent, cyclesToRun);
        if (!this.infinite) {
            this.remainingDuration -= dt;
        }
    }

    private int calculateCyclesToRun(@Nonnull EntityEffect entityEffect, float dt) {
        int cycles = 0;
        float damageCalculatorCooldown = entityEffect.getDamageCalculatorCooldown();
        if (damageCalculatorCooldown > 0.0f) {
            this.sinceLastDamage += dt;
            cycles = MathUtil.fastFloor(this.sinceLastDamage / damageCalculatorCooldown);
            this.sinceLastDamage %= damageCalculatorCooldown;
        } else if (!this.hasBeenDamaged) {
            cycles = 1;
            this.hasBeenDamaged = true;
        }
        return cycles;
    }

    private static void tickStatChanges(@Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> ref, @Nonnull EntityEffect entityEffect, @Nonnull EntityStatMap entityStatMapComponent, int cyclesToRun) {
        Int2FloatMap entityStats = entityEffect.getEntityStats();
        if (entityStats == null) {
            return;
        }
        if (cyclesToRun <= 0) {
            return;
        }
        DamageEffects statModifierEffects = entityEffect.getStatModifierEffects();
        if (statModifierEffects != null) {
            statModifierEffects.spawnAtEntity(commandBuffer, ref);
        }
        entityStatMapComponent.processStatChanges(EntityStatMap.Predictable.ALL, entityStats, entityEffect.getValueType(), ChangeStatBehaviour.Add);
    }

    private void tickDamage(@Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> ref, @Nonnull EntityEffect entityEffect, int cyclesToRun) {
        Damage[] hits;
        DamageCalculator damageCalculator = entityEffect.getDamageCalculator();
        if (damageCalculator == null) {
            return;
        }
        if (cyclesToRun <= 0) {
            return;
        }
        Object2FloatMap<DamageCause> relativeDamage = damageCalculator.calculateDamage(this.initialDuration);
        if (relativeDamage == null || relativeDamage.isEmpty()) {
            return;
        }
        World world = commandBuffer.getExternalData().getWorld();
        DamageEffects damageEffects = entityEffect.getDamageEffects();
        for (Damage damageEvent : hits = DamageCalculatorSystems.queueDamageCalculator(world, relativeDamage, ref, commandBuffer, this, null)) {
            DamageCalculatorSystems.DamageSequence damageSequence = new DamageCalculatorSystems.DamageSequence(this.sequentialHits, damageCalculator);
            damageEvent.putMetaObject(DamageCalculatorSystems.DAMAGE_SEQUENCE, damageSequence);
            if (damageEffects != null) {
                damageEffects.addToDamage(damageEvent);
            }
            commandBuffer.invoke(ref, damageEvent);
        }
    }

    public int getEntityEffectIndex() {
        return this.entityEffectIndex;
    }

    public float getInitialDuration() {
        return this.initialDuration;
    }

    public float getRemainingDuration() {
        return this.remainingDuration;
    }

    public boolean isInfinite() {
        return this.infinite;
    }

    public boolean isDebuff() {
        return this.debuff;
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    @Override
    @Nonnull
    public Message getDeathMessage(@Nonnull Damage info, @Nonnull Ref<EntityStore> targetRef, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        Message damageCauseMessage;
        EntityEffect entityEffect = EntityEffect.getAssetMap().getAsset(this.entityEffectIndex);
        if (entityEffect != null) {
            String locale = entityEffect.getLocale();
            String reason = locale != null ? locale : entityEffect.getId().toLowerCase(Locale.ROOT);
            damageCauseMessage = Message.translation("server.general.damageCauses." + reason);
        } else {
            damageCauseMessage = MESSAGE_GENERAL_DAMAGE_CAUSES_UNKNOWN;
        }
        return Message.translation("server.general.killedBy").param("damageSource", damageCauseMessage);
    }

    @Nonnull
    public String toString() {
        return "ActiveEntityEffect{entityEffectIndex='" + this.entityEffectIndex + "', initialDuration=" + this.initialDuration + ", remainingDuration=" + this.remainingDuration + ", damageCooldown=" + this.sinceLastDamage + ", hasBeenDamaged=" + this.hasBeenDamaged + ", sequentialHits=" + String.valueOf(this.sequentialHits) + ", infinite=" + this.infinite + ", debuff=" + this.debuff + ", statusEffectIcon=" + this.statusEffectIcon + "}";
    }
}

