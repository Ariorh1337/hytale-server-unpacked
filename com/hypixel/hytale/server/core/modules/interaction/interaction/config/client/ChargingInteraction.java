/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.client;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.EnumMapCodec;
import com.hypixel.hytale.codec.codecs.map.Float2ObjectMapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.ChargingDelay;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionSyncData;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.damage.DamageDataComponent;
import com.hypixel.hytale.server.core.meta.MetaKey;
import com.hypixel.hytale.server.core.modules.interaction.IInteractionSimulationHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.Collector;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.CollectorTag;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.StringTag;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Label;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.OperationsBuilder;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.floats.Float2IntOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChargingInteraction
extends Interaction {
    @Nonnull
    public static final BuilderCodec<ChargingDelay> DELAY_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ChargingDelay.class, ChargingDelay::new).documentation("Configuration for delay when the user is attacked.\nThe delay will be between **MinDelay** when the incoming at **MinHealth** and **MaxDelay** when the incoming damage is at or above **MaxHealth**.")).appendInherited(new KeyedCodec<Float>("MinDelay", Codec.FLOAT), (o, i) -> {
        o.minDelay = i.floatValue();
    }, o -> Float.valueOf(o.minDelay), (o, p) -> {
        o.minDelay = p.minDelay;
    }).documentation("The smallest amount of delay that can be applied.").addValidator(Validators.nonNull()).addValidator(Validators.greaterThanOrEqual(Float.valueOf(0.0f))).add()).appendInherited(new KeyedCodec<Float>("MaxDelay", Codec.FLOAT), (o, i) -> {
        o.maxDelay = i.floatValue();
    }, o -> Float.valueOf(o.maxDelay), (o, p) -> {
        o.maxDelay = p.maxDelay;
    }).documentation("The largest amount of delay that can be applied.").addValidator(Validators.nonNull()).addValidator(Validators.greaterThanOrEqual(Float.valueOf(0.0f))).add()).appendInherited(new KeyedCodec<Float>("MaxTotalDelay", Codec.FLOAT), (o, i) -> {
        o.maxTotalDelay = i.floatValue();
    }, o -> Float.valueOf(o.maxTotalDelay), (o, p) -> {
        o.maxTotalDelay = p.maxTotalDelay;
    }).documentation("The max amount of delay applied during this interaction before any additional delay is ignored.").addValidator(Validators.nonNull()).addValidator(Validators.greaterThanOrEqual(Float.valueOf(0.0f))).add()).appendInherited(new KeyedCodec<Float>("MinHealth", Codec.FLOAT), (o, i) -> {
        o.minHealth = i.floatValue();
    }, o -> Float.valueOf(o.minHealth), (o, p) -> {
        o.minHealth = p.minHealth;
    }).documentation("The amount of health (as a percentage between 1.0 and 0.0) where if the user's health is below the value then the delay wont be applied.").addValidator(Validators.nonNull()).addValidator(Validators.greaterThanOrEqual(Float.valueOf(0.0f))).add()).appendInherited(new KeyedCodec<Float>("MaxHealth", Codec.FLOAT), (o, i) -> {
        o.maxHealth = i.floatValue();
    }, o -> Float.valueOf(o.maxHealth), (o, p) -> {
        o.maxHealth = p.maxHealth;
    }).documentation("The amount of health (as a percentage between 1.0 and 0.0) where if the user's health is above the value then the delay will be capped.").addValidator(Validators.nonNull()).addValidator(Validators.greaterThanOrEqual(Float.valueOf(0.0f))).add()).build();
    public static final BuilderCodec<ChargingInteraction> ABSTRACT_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.abstractBuilder(ChargingInteraction.class, Interaction.ABSTRACT_CODEC).appendInherited(new KeyedCodec<Boolean>("FailOnDamage", Codec.BOOLEAN), (interaction, s) -> {
        interaction.failOnDamage = s;
    }, interaction -> interaction.failOnDamage, (interaction, parent) -> {
        interaction.failOnDamage = parent.failOnDamage;
    }).documentation("Whether the interaction will be cancelled and the item removed when the entity takes damage").add()).appendInherited(new KeyedCodec<Boolean>("CancelOnOtherClick", Codec.BOOLEAN), (interaction, b) -> {
        interaction.cancelOnOtherClick = b;
    }, interaction -> interaction.cancelOnOtherClick, (interaction, parent) -> {
        interaction.cancelOnOtherClick = parent.cancelOnOtherClick;
    }).add()).appendInherited(new KeyedCodec("Forks", new EnumMapCodec(InteractionType.class, RootInteraction.CHILD_ASSET_CODEC)), (o, i) -> {
        o.forks = i;
    }, o -> o.forks, (o, p) -> {
        o.forks = p.forks;
    }).documentation("A collection of interactions to fork into when the input associated with the interaction type is used.\n\nFor example listing a `Primary` interaction type here with interactions will allow the user to press the input tied to the `Primary` interaction type whilst holding the input used to run the current interaction to run the specified interactions. e.g. Having a shield that you can hold `Secondary` to block and whilst blocking press `Primary` to shield bash.\n\nThis does not cancel the current interaction when triggered but the `CancelOnOtherClick` check will still run and may cancel the interaction.\n\nThe existing forks will continue to run even if this interaction ends.").addValidatorLate(() -> RootInteraction.VALIDATOR_CACHE.getMapValueValidator().late()).add()).afterDecode(interaction -> {
        float max = 0.0f;
        if (interaction.next != null) {
            FloatIterator iterator = interaction.next.keySet().iterator();
            while (iterator.hasNext()) {
                float nextFloat = iterator.nextFloat();
                if (!(nextFloat > max)) continue;
                max = nextFloat;
            }
            interaction.sortedKeys = interaction.next.keySet().toFloatArray();
            Arrays.sort(interaction.sortedKeys);
        }
        interaction.highestChargeValue = max;
    })).appendInherited(new KeyedCodec<String>("Failed", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> {
        interaction.failed = s;
    }, interaction -> interaction.failed, (interaction, parent) -> {
        interaction.failed = parent.failed;
    }).documentation("The interactions to run when this interaction fails.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).build();
    @Nonnull
    public static final BuilderCodec<ChargingInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ChargingInteraction.class, ChargingInteraction::new, ABSTRACT_CODEC).documentation("An interaction that holds until the key is released (or a time limit is reached) and executes different interactions based on how long the key was pressed.")).appendInherited(new KeyedCodec<Boolean>("AllowIndefiniteHold", Codec.BOOLEAN), (interaction, s) -> {
        interaction.allowIndefiniteHold = s;
    }, interaction -> interaction.allowIndefiniteHold, (interaction, parent) -> {
        interaction.allowIndefiniteHold = parent.allowIndefiniteHold;
    }).add()).appendInherited(new KeyedCodec<Boolean>("DisplayProgress", Codec.BOOLEAN), (interaction, s) -> {
        interaction.displayProgress = s;
    }, interaction -> interaction.displayProgress, (interaction, parent) -> {
        interaction.displayProgress = parent.displayProgress;
    }).add()).appendInherited(new KeyedCodec<String>("Next", new Float2ObjectMapCodec<String>(Interaction.CHILD_ASSET_CODEC, Float2ObjectOpenHashMap::new)), (interaction, s) -> {
        interaction.next = s;
    }, interaction -> interaction.next, (interaction, parent) -> {
        interaction.next = parent.next;
    }).addValidatorLate(() -> VALIDATOR_CACHE.getMapValueValidator().late()).add()).appendInherited(new KeyedCodec<Float>("MouseSensitivityAdjustmentTarget", Codec.FLOAT), (interaction, doubles) -> {
        interaction.mouseSensitivityAdjustmentTarget = doubles.floatValue();
    }, interaction -> Float.valueOf(interaction.mouseSensitivityAdjustmentTarget), (interaction, parent) -> {
        interaction.mouseSensitivityAdjustmentTarget = parent.mouseSensitivityAdjustmentTarget;
    }).documentation("What is the target modifier to apply to mouse sensitivity while this interaction is active.").addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(1.0f))).add()).appendInherited(new KeyedCodec<Float>("MouseSensitivityAdjustmentDuration", Codec.FLOAT), (interaction, doubles) -> {
        interaction.mouseSensitivityAdjustmentDuration = doubles.floatValue();
    }, interaction -> Float.valueOf(interaction.mouseSensitivityAdjustmentDuration), (interaction, parent) -> {
        interaction.mouseSensitivityAdjustmentDuration = parent.mouseSensitivityAdjustmentDuration;
    }).documentation("Override the global linear modifier adjustment with this as the time to go from 1.0 to 0.0.").add()).appendInherited(new KeyedCodec<ChargingDelay>("Delay", DELAY_CODEC), (o, i) -> {
        o.chargingDelay = i;
    }, o -> o.chargingDelay, (o, p) -> {
        o.chargingDelay = p.chargingDelay;
    }).documentation("Settings that allow for delaying the charging interaction on damage.").add()).build();
    private static final MetaKey<Object2IntMap<InteractionType>> FORK_COUNTS = Interaction.META_REGISTRY.registerMetaObject(i -> new Object2IntOpenHashMap());
    private static final MetaKey<InteractionChain> FORKED_CHAIN = Interaction.META_REGISTRY.registerMetaObject(i -> null);
    private static final float CHARGING_HELD = -1.0f;
    private static final float CHARGING_CANCELED = -2.0f;
    private static final StringTag TAG_FAILED = StringTag.of("Failed");
    protected boolean allowIndefiniteHold;
    protected boolean displayProgress = true;
    protected boolean cancelOnOtherClick = true;
    protected boolean failOnDamage;
    protected float mouseSensitivityAdjustmentTarget = 1.0f;
    protected float mouseSensitivityAdjustmentDuration = 1.0f;
    @Nullable
    protected String failed;
    @Nullable
    protected Float2ObjectMap<String> next;
    protected float[] sortedKeys;
    protected Map<InteractionType, String> forks;
    @Nullable
    protected ChargingDelay chargingDelay;
    protected float highestChargeValue;

    @Override
    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Client;
    }

    @Override
    protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        InteractionSyncData clientData = context.getClientState();
        if (context.getClientState().state == InteractionState.Failed && context.hasLabels()) {
            context.getState().state = InteractionState.Failed;
            context.jump(context.getLabel(this.next != null ? this.next.size() : 0));
            return;
        }
        if (clientData.forkCounts != null && this.forks != null) {
            Object2IntMap<InteractionType> serverForkCounts = context.getInstanceStore().getMetaObject(FORK_COUNTS);
            InteractionChain forked = context.getInstanceStore().getMetaObject(FORKED_CHAIN);
            if (forked != null && forked.getServerState() != InteractionState.NotFinished) {
                forked = null;
            }
            boolean matches = true;
            for (Map.Entry<InteractionType, Integer> e : clientData.forkCounts.entrySet()) {
                int serverCount = serverForkCounts.getInt((Object)e.getKey());
                String forkInteraction = this.forks.get((Object)e.getKey());
                if (forked == null && serverCount < e.getValue() && forkInteraction != null) {
                    InteractionContext forkContext = context.duplicate();
                    forked = context.fork(e.getKey(), forkContext, RootInteraction.getRootInteractionOrUnknown(forkInteraction), true);
                    context.getInstanceStore().putMetaObject(FORKED_CHAIN, forked);
                    serverForkCounts.put(e.getKey(), ++serverCount);
                }
                matches &= serverCount == e.getValue();
            }
            if (!matches) {
                context.getState().state = InteractionState.NotFinished;
                return;
            }
        }
        if (clientData.chargeValue == -1.0f) {
            context.getState().state = InteractionState.NotFinished;
            return;
        }
        if (clientData.chargeValue == -2.0f) {
            context.getState().state = InteractionState.Finished;
            return;
        }
        context.getState().state = InteractionState.Finished;
        float chargeValue = clientData.chargeValue;
        if (this.next == null) {
            return;
        }
        this.jumpToChargeValue(context, chargeValue);
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        Ref<EntityStore> ref = context.getEntity();
        DamageDataComponent damageDataComponent = commandBuffer.getComponent(ref, DamageDataComponent.getComponentType());
        assert (damageDataComponent != null);
        damageDataComponent.setLastChargeTime(commandBuffer.getResource(TimeResource.getResourceType()).getNow());
    }

    @Override
    protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        IInteractionSimulationHandler simulationHandler = context.getInteractionManager().getInteractionSimulationHandler();
        if (simulationHandler.isCharging(firstRun, time, type, context, ref, cooldownHandler) && (this.allowIndefiniteHold || time < this.highestChargeValue)) {
            if (this.failOnDamage && simulationHandler.shouldCancelCharging(firstRun, time, type, context, ref, cooldownHandler)) {
                context.getState().state = InteractionState.Failed;
                return;
            }
            context.getState().state = InteractionState.NotFinished;
        } else {
            float chargeValue;
            context.getState().state = InteractionState.Finished;
            context.getState().chargeValue = chargeValue = simulationHandler.getChargeValue(firstRun, time, type, context, ref, cooldownHandler);
            if (this.next == null) {
                return;
            }
            this.jumpToChargeValue(context, chargeValue);
        }
    }

    private void jumpToChargeValue(@Nonnull InteractionContext context, float chargeValue) {
        float closestDiff = 2.1474836E9f;
        int closestValue = -1;
        int index = 0;
        for (float e : this.sortedKeys) {
            if (chargeValue < e) {
                ++index;
                continue;
            }
            float diff = chargeValue - e;
            if (closestValue == -1 || diff < closestDiff) {
                closestDiff = diff;
                closestValue = index;
            }
            ++index;
        }
        if (closestValue != -1) {
            context.jump(context.getLabel(closestValue));
        }
    }

    @Override
    public void compile(@Nonnull OperationsBuilder builder) {
        int i;
        Label end = builder.createUnresolvedLabel();
        Label[] labels = new Label[(this.next != null ? this.next.size() : 0) + 1];
        for (i = 0; i < labels.length; ++i) {
            labels[i] = builder.createUnresolvedLabel();
        }
        builder.addOperation(this, labels);
        builder.jump(end);
        if (this.sortedKeys != null) {
            for (i = 0; i < this.sortedKeys.length; ++i) {
                float key = this.sortedKeys[i];
                builder.resolveLabel(labels[i]);
                Interaction interaction = Interaction.getInteractionOrUnknown((String)this.next.get(key));
                interaction.compile(builder);
                builder.jump(end);
            }
        }
        int failedIndex = this.sortedKeys != null ? this.sortedKeys.length : 0;
        builder.resolveLabel(labels[failedIndex]);
        if (this.failed != null) {
            Interaction interaction = Interaction.getInteractionOrUnknown(this.failed);
            interaction.compile(builder);
        }
        builder.resolveLabel(end);
    }

    @Override
    public boolean walk(@Nonnull Collector collector, @Nonnull InteractionContext context) {
        if (this.next != null) {
            for (Float2ObjectMap.Entry entry : this.next.float2ObjectEntrySet()) {
                if (!InteractionManager.walkInteraction(collector, context, ChargingTag.of(entry.getFloatKey()), (String)entry.getValue())) continue;
                return true;
            }
        }
        return this.failed != null && InteractionManager.walkInteraction(collector, context, TAG_FAILED, this.failed);
    }

    @Override
    @Nonnull
    protected com.hypixel.hytale.protocol.Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.ChargingInteraction();
    }

    @Override
    protected void configurePacket(com.hypixel.hytale.protocol.Interaction packet) {
        super.configurePacket(packet);
        com.hypixel.hytale.protocol.ChargingInteraction p = (com.hypixel.hytale.protocol.ChargingInteraction)packet;
        p.allowIndefiniteHold = this.allowIndefiniteHold;
        p.mouseSensitivityAdjustmentTarget = this.mouseSensitivityAdjustmentTarget;
        p.mouseSensitivityAdjustmentDuration = this.mouseSensitivityAdjustmentDuration;
        p.displayProgress = this.displayProgress;
        p.cancelOnOtherClick = this.cancelOnOtherClick;
        p.failOnDamage = this.failOnDamage;
        p.failed = Interaction.getInteractionIdOrUnknown(this.failed);
        p.chargingDelay = this.chargingDelay;
        if (this.next != null) {
            Float2IntOpenHashMap chargedNext = new Float2IntOpenHashMap();
            for (Float2ObjectMap.Entry entry : this.next.float2ObjectEntrySet()) {
                chargedNext.put(entry.getFloatKey(), Interaction.getInteractionIdOrUnknown((String)entry.getValue()));
            }
            p.chargedNext = chargedNext;
        }
        if (this.forks != null) {
            Object2IntOpenHashMap<InteractionType> intForks = new Object2IntOpenHashMap<InteractionType>();
            for (Map.Entry<InteractionType, String> entry : this.forks.entrySet()) {
                intForks.put(entry.getKey(), RootInteraction.getRootInteractionIdOrUnknown(entry.getValue()));
            }
            p.forks = intForks;
        }
    }

    @Override
    public boolean needsRemoteSync() {
        return true;
    }

    @Override
    @Nonnull
    public String toString() {
        return "ChargingInteraction{allowIndefiniteHold=" + this.allowIndefiniteHold + ", displayProgress=" + this.displayProgress + ", mouseSensitivityAdjustmentTarget=" + this.mouseSensitivityAdjustmentTarget + ", mouseSensitivityAdjustmentDuration=" + this.mouseSensitivityAdjustmentDuration + ", cancelOnOtherClick=" + this.cancelOnOtherClick + ", next=" + String.valueOf(this.next) + ", forks=" + String.valueOf(this.forks) + ", highestChargeValue=" + this.highestChargeValue + ", failOnDamage=" + this.failOnDamage + "} " + super.toString();
    }

    private static class ChargingTag
    implements CollectorTag {
        private final float seconds;

        private ChargingTag(float seconds) {
            this.seconds = seconds;
        }

        public double getSeconds() {
            return this.seconds;
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ChargingTag that = (ChargingTag)o;
            return Float.compare(that.seconds, this.seconds) == 0;
        }

        public int hashCode() {
            return this.seconds != 0.0f ? Float.floatToIntBits(this.seconds) : 0;
        }

        @Nonnull
        public String toString() {
            return "ChargingTag{seconds=" + this.seconds + "}";
        }

        @Nonnull
        public static ChargingTag of(float seconds) {
            return new ChargingTag(seconds);
        }
    }
}

