/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.client;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.map.Object2DoubleMapCodec;
import com.hypixel.hytale.codec.codecs.map.Object2FloatMapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.gameplay.CombatConfig;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.damage.DamageDataComponent;
import com.hypixel.hytale.server.core.entity.effect.ActiveEntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.ChargingInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageEffects;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.floats.Float2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleMaps;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatMaps;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WieldingInteraction
extends ChargingInteraction {
    public static final float WIELDING_INDEX = 0.0f;
    @Nonnull
    public static final BuilderCodec<WieldingInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(WieldingInteraction.class, WieldingInteraction::new, ChargingInteraction.ABSTRACT_CODEC).documentation("Interaction that blocks while the key is held and applies various modifiers while active.")).appendInherited(new KeyedCodec<String>("KnockbackModifiers", new Object2DoubleMapCodec<String>(Codec.STRING, Object2DoubleOpenHashMap::new)), (damageCalculator, map) -> {
        damageCalculator.knockbackModifiersRaw = map;
    }, damageCalculator -> damageCalculator.knockbackModifiersRaw, (damageCalculator, parent) -> {
        damageCalculator.knockbackModifiersRaw = parent.knockbackModifiersRaw;
    }).addValidator(DamageCause.VALIDATOR_CACHE.getMapKeyValidator()).add()).appendInherited(new KeyedCodec<String>("DamageModifiers", new Object2FloatMapCodec<String>(Codec.STRING, Object2FloatOpenHashMap::new)), (damageCalculator, map) -> {
        damageCalculator.damageModifiersRaw = map;
    }, damageCalculator -> damageCalculator.damageModifiersRaw, (damageCalculator, parent) -> {
        damageCalculator.damageModifiersRaw = parent.damageModifiersRaw;
    }).addValidator(DamageCause.VALIDATOR_CACHE.getMapKeyValidator()).add()).appendInherited(new KeyedCodec<AngledWielding>("AngledWielding", AngledWielding.CODEC), (i, o) -> {
        i.angledWielding = o;
    }, i -> i.angledWielding, (i, parent) -> {
        i.angledWielding = parent.angledWielding;
    }).add()).appendInherited(new KeyedCodec<String>("Next", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> {
        interaction.next = new Float2ObjectOpenHashMap();
        interaction.next.put(0.0f, s);
    }, interaction -> interaction.next != null ? (String)interaction.next.get(0.0f) : null, (interaction, parent) -> {
        interaction.next = parent.next;
    }).addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec<StaminaCost>("StaminaCost", StaminaCost.CODEC), (wieldingInteraction, staminaCost) -> {
        wieldingInteraction.staminaCost = staminaCost;
    }, wieldingInteraction -> wieldingInteraction.staminaCost, (wieldingInteraction, parent) -> {
        wieldingInteraction.staminaCost = parent.staminaCost;
    }).documentation("Configuration to define how stamina loss is computed.").add()).appendInherited(new KeyedCodec<DamageEffects>("BlockedEffects", DamageEffects.CODEC), (wieldingInteraction, interactionEffects) -> {
        wieldingInteraction.blockedEffects = interactionEffects;
    }, wieldingInteraction -> wieldingInteraction.blockedEffects, (wieldingInteraction, parent) -> {
        wieldingInteraction.blockedEffects = parent.blockedEffects;
    }).add()).appendInherited(new KeyedCodec("BlockedInteractions", RootInteraction.CHILD_ASSET_CODEC), (wieldingInteraction, s) -> {
        wieldingInteraction.blockedInteractions = s;
    }, wieldingInteraction -> wieldingInteraction.blockedInteractions, (wieldingInteraction, parent) -> {
        wieldingInteraction.blockedInteractions = parent.blockedInteractions;
    }).addValidatorLate(() -> RootInteraction.VALIDATOR_CACHE.getValidator().late()).add()).afterDecode(i -> {
        int index;
        i.allowIndefiniteHold = true;
        if (i.next != null && i.runTime > 0.0f) {
            i.next.put(i.runTime, (String)i.next.get(0.0f));
        }
        if (i.knockbackModifiersRaw != null) {
            i.knockbackModifiers = new Int2DoubleOpenHashMap();
            for (Object2DoubleMap.Entry entry : i.knockbackModifiersRaw.object2DoubleEntrySet()) {
                index = DamageCause.getAssetMap().getIndex((String)entry.getKey());
                i.knockbackModifiers.put(index, entry.getDoubleValue());
            }
        }
        if (i.damageModifiersRaw != null) {
            i.damageModifiers = new Int2FloatOpenHashMap();
            for (Object2FloatMap.Entry entry : i.damageModifiersRaw.object2FloatEntrySet()) {
                index = DamageCause.getAssetMap().getIndex((String)entry.getKey());
                i.damageModifiers.put(index, entry.getFloatValue());
            }
        }
    })).build();
    @Nullable
    protected Object2DoubleMap<String> knockbackModifiersRaw;
    @Nullable
    protected Object2FloatMap<String> damageModifiersRaw;
    protected AngledWielding angledWielding;
    protected StaminaCost staminaCost;
    protected DamageEffects blockedEffects;
    protected String blockedInteractions;
    @Nonnull
    protected transient Int2DoubleMap knockbackModifiers = Int2DoubleMaps.EMPTY_MAP;
    @Nonnull
    protected transient Int2FloatMap damageModifiers = Int2FloatMaps.EMPTY_MAP;

    @Nonnull
    public Int2DoubleMap getKnockbackModifiers() {
        return this.knockbackModifiers;
    }

    @Nonnull
    public Int2FloatMap getDamageModifiers() {
        return this.damageModifiers;
    }

    public AngledWielding getAngledWielding() {
        return this.angledWielding;
    }

    public DamageEffects getBlockedEffects() {
        return this.blockedEffects;
    }

    public StaminaCost getStaminaCost() {
        return this.staminaCost;
    }

    public String getBlockedInteractions() {
        return this.blockedInteractions;
    }

    @Override
    protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        Ref<EntityStore> ref = context.getEntity();
        World world = commandBuffer.getExternalData().getWorld();
        DamageDataComponent damageDataComponent = commandBuffer.getComponent(ref, DamageDataComponent.getComponentType());
        assert (damageDataComponent != null);
        if (Interaction.failed(context.getState().state)) {
            damageDataComponent.setCurrentWielding(null);
            return;
        }
        CombatConfig combatConfig = world.getGameplayConfig().getCombatConfig();
        EffectControllerComponent effectControllerComponent = commandBuffer.getComponent(ref, EffectControllerComponent.getComponentType());
        if (effectControllerComponent != null) {
            Int2ObjectMap<ActiveEntityEffect> activeEffects = effectControllerComponent.getActiveEffects();
            if (!firstRun && activeEffects.containsKey(combatConfig.getStaminaBrokenEffectIndex())) {
                damageDataComponent.setCurrentWielding(null);
                context.getState().state = InteractionState.Failed;
                if (context.hasLabels()) {
                    context.jump(context.getLabel(this.next != null ? this.next.size() : 0));
                }
                return;
            }
        }
        super.tick0(firstRun, time, type, context, cooldownHandler);
        if (firstRun && context.getState().state == InteractionState.NotFinished) {
            damageDataComponent.setCurrentWielding(this);
            return;
        }
        if (context.getState().state == InteractionState.Finished) {
            damageDataComponent.setCurrentWielding(null);
        }
    }

    @Override
    protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        Ref<EntityStore> ref = context.getEntity();
        World world = commandBuffer.getExternalData().getWorld();
        CombatConfig combatConfig = world.getGameplayConfig().getCombatConfig();
        EffectControllerComponent effectControllerComponent = commandBuffer.getComponent(ref, EffectControllerComponent.getComponentType());
        if (effectControllerComponent != null) {
            Int2ObjectMap<ActiveEntityEffect> activeEffects = effectControllerComponent.getActiveEffects();
            if (!firstRun && activeEffects.containsKey(combatConfig.getStaminaBrokenEffectIndex())) {
                context.getState().state = InteractionState.Failed;
                if (context.hasLabels()) {
                    context.jump(context.getLabel(this.next != null ? this.next.size() : 0));
                }
                return;
            }
        }
        super.simulateTick0(firstRun, time, type, context, cooldownHandler);
    }

    @Override
    public void handle(@Nonnull Ref<EntityStore> ref, boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context) {
        super.handle(ref, firstRun, time, type, context);
        if (context.getState().state != InteractionState.NotFinished) {
            CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
            assert (commandBuffer != null);
            DamageDataComponent damageDataComponent = commandBuffer.getComponent(ref, DamageDataComponent.getComponentType());
            assert (damageDataComponent != null);
            damageDataComponent.setCurrentWielding(null);
        }
    }

    @Override
    @Nonnull
    protected com.hypixel.hytale.protocol.Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.WieldingInteraction();
    }

    @Override
    protected void configurePacket(com.hypixel.hytale.protocol.Interaction packet) {
        super.configurePacket(packet);
        com.hypixel.hytale.protocol.WieldingInteraction p = (com.hypixel.hytale.protocol.WieldingInteraction)packet;
        if (this.blockedEffects != null) {
            p.blockedEffects = this.blockedEffects.toPacket();
        }
        if (this.angledWielding != null) {
            p.angledWielding = this.angledWielding.toPacket();
        }
        p.hasModifiers = this.damageModifiersRaw != null || this.knockbackModifiersRaw != null;
    }

    @Override
    @Nonnull
    public String toString() {
        return "WieldingInteraction{knockbackModifiers=" + String.valueOf(this.knockbackModifiersRaw) + ", damageModifiers=" + String.valueOf(this.damageModifiersRaw) + ", angledWielding=" + String.valueOf(this.angledWielding) + ", failed='" + this.failed + "', staminaCost=" + String.valueOf(this.staminaCost) + ", blockedEffects=" + String.valueOf(this.blockedEffects) + "} " + super.toString();
    }

    public static class AngledWielding
    implements NetworkSerializable<com.hypixel.hytale.protocol.AngledWielding> {
        public static final BuilderCodec<AngledWielding> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(AngledWielding.class, AngledWielding::new).appendInherited(new KeyedCodec<Float>("Angle", Codec.FLOAT), (o, i) -> {
            o.angleRad = i.floatValue() * ((float)Math.PI / 180);
        }, o -> Float.valueOf(o.angleRad * 57.295776f), (o, p) -> {
            o.angleRad = p.angleRad;
        }).add()).appendInherited(new KeyedCodec<Float>("AngleDistance", Codec.FLOAT), (o, i) -> {
            o.angleDistanceRad = i.floatValue() * ((float)Math.PI / 180);
        }, o -> Float.valueOf(o.angleDistanceRad * 57.295776f), (o, p) -> {
            o.angleDistanceRad = p.angleDistanceRad;
        }).add()).appendInherited(new KeyedCodec<String>("KnockbackModifiers", new Object2DoubleMapCodec<String>(Codec.STRING, Object2DoubleOpenHashMap::new)), (o, m) -> {
            o.knockbackModifiersRaw = m;
        }, o -> o.knockbackModifiersRaw, (o, p) -> {
            o.knockbackModifiersRaw = p.knockbackModifiersRaw;
        }).addValidator(DamageCause.VALIDATOR_CACHE.getMapKeyValidator()).add()).appendInherited(new KeyedCodec<String>("DamageModifiers", new Object2FloatMapCodec<String>(Codec.STRING, Object2FloatOpenHashMap::new)), (o, m) -> {
            o.damageModifiersRaw = m;
        }, o -> o.damageModifiersRaw, (o, p) -> {
            o.damageModifiersRaw = p.damageModifiersRaw;
        }).addValidator(DamageCause.VALIDATOR_CACHE.getMapKeyValidator()).add()).afterDecode(o -> {
            int index;
            if (o.knockbackModifiersRaw != null) {
                o.knockbackModifiers = new Int2DoubleOpenHashMap();
                for (Object2DoubleMap.Entry entry : o.knockbackModifiersRaw.object2DoubleEntrySet()) {
                    index = DamageCause.getAssetMap().getIndex((String)entry.getKey());
                    o.knockbackModifiers.put(index, entry.getDoubleValue());
                }
            }
            if (o.damageModifiersRaw != null) {
                o.damageModifiers = new Int2FloatOpenHashMap();
                for (Object2FloatMap.Entry entry : o.damageModifiersRaw.object2FloatEntrySet()) {
                    index = DamageCause.getAssetMap().getIndex((String)entry.getKey());
                    o.damageModifiers.put(index, entry.getFloatValue());
                }
            }
        })).build();
        protected float angleRad;
        protected float angleDistanceRad;
        @Nullable
        protected Object2DoubleMap<String> knockbackModifiersRaw;
        @Nullable
        protected Object2FloatMap<String> damageModifiersRaw;
        @Nonnull
        protected transient Int2DoubleMap knockbackModifiers = Int2DoubleMaps.EMPTY_MAP;
        @Nonnull
        protected transient Int2FloatMap damageModifiers = Int2FloatMaps.EMPTY_MAP;

        public double getAngleRad() {
            return this.angleRad;
        }

        public double getAngleDistanceRad() {
            return this.angleDistanceRad;
        }

        @Nonnull
        public Int2DoubleMap getKnockbackModifiers() {
            return this.knockbackModifiers;
        }

        @Nonnull
        public Int2FloatMap getDamageModifiers() {
            return this.damageModifiers;
        }

        @Override
        @Nonnull
        public com.hypixel.hytale.protocol.AngledWielding toPacket() {
            com.hypixel.hytale.protocol.AngledWielding packet = new com.hypixel.hytale.protocol.AngledWielding();
            packet.angleRad = this.angleRad;
            packet.angleDistanceRad = this.angleDistanceRad;
            packet.hasModifiers = this.damageModifiersRaw != null || this.knockbackModifiersRaw != null;
            return packet;
        }
    }

    public static class StaminaCost {
        public static final BuilderCodec<StaminaCost> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(StaminaCost.class, StaminaCost::new).append(new KeyedCodec<CostType>("CostType", new EnumCodec<CostType>(CostType.class)), (staminaCost, costType) -> {
            staminaCost.costType = costType;
        }, staminaCost -> staminaCost.costType).documentation("Define how the stamina loss is computed. Use MAX_HEALTH_PERCENTAGE to define how many % of the player's max health 1 stamina point is worth. Use DAMAGE define how much damage 1 stamina point is worth. Default value is MAX_HEALTH_PERCENTAGE.").add()).append(new KeyedCodec<Float>("Value", Codec.FLOAT), (staminaCost, aFloat) -> {
            staminaCost.value = aFloat.floatValue();
        }, staminaCost -> Float.valueOf(staminaCost.value)).addValidator(Validators.greaterThanOrEqual(Float.valueOf(0.0f))).documentation("The value to define how much a stamina point is worth. When CostType.MAX_HEALTH_PERCENTAGE, a ratio is expected, so for 4% of max health, the value expected here is 0.04. Default value is 0.04f").add()).build();
        private CostType costType = CostType.MAX_HEALTH_PERCENTAGE;
        private float value = 0.04f;

        public float computeStaminaAmountToConsume(float damageRaw, @Nonnull EntityStatMap entityStatMap) {
            return switch (this.costType.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> damageRaw / (this.value * entityStatMap.get(DefaultEntityStatTypes.getHealth()).getMax());
                case 1 -> damageRaw / this.value;
            };
        }

        @Nonnull
        public String toString() {
            return "StaminaCost{costType=" + String.valueOf((Object)this.costType) + ", value=" + this.value + "}";
        }

        static enum CostType {
            MAX_HEALTH_PERCENTAGE,
            DAMAGE;

        }
    }
}

