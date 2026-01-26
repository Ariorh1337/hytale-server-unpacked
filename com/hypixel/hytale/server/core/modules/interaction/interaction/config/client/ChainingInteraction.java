/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.client;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionSyncData;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.Collector;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.CollectorTag;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Label;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.OperationsBuilder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChainingInteraction
extends Interaction {
    @Nonnull
    public static final BuilderCodec<ChainingInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ChainingInteraction.class, ChainingInteraction::new, Interaction.ABSTRACT_CODEC).documentation("Runs one of the entries in `Next` based on how many times this interaction was run before the `ChainingAllowance` timer was reset.")).appendInherited(new KeyedCodec<Double>("ChainingAllowance", Codec.DOUBLE), (chainingInteraction, d) -> {
        chainingInteraction.chainingAllowance = d.floatValue();
    }, chainingInteraction -> chainingInteraction.chainingAllowance, (chainingInteraction, parent) -> {
        chainingInteraction.chainingAllowance = parent.chainingAllowance;
    }).documentation("Time in seconds that the user has to run this interaction again in order to hit the next chain entry.\nResets the timer each time the interaction is reached.").add()).appendInherited(new KeyedCodec<T[]>("Next", new ArrayCodec<String>(Interaction.CHILD_ASSET_CODEC, String[]::new)), (interaction, s) -> {
        interaction.next = s;
    }, interaction -> interaction.next, (interaction, parent) -> {
        interaction.next = parent.next;
    }).addValidator(Validators.nonNull()).addValidator(Validators.nonNullArrayElements()).addValidatorLate(() -> Interaction.VALIDATOR_CACHE.getArrayValidator().late()).add()).appendInherited(new KeyedCodec<String>("ChainId", Codec.STRING), (o, i) -> {
        o.chainId = i;
    }, o -> o.chainId, (o, p) -> {
        o.chainId = p.chainId;
    }).add()).appendInherited(new KeyedCodec("Flags", new MapCodec(CHILD_ASSET_CODEC, HashMap::new)), (o, i) -> {
        o.flags = i;
    }, o -> o.flags, (o, p) -> {
        o.flags = p.flags;
    }).addValidatorLate(() -> Interaction.VALIDATOR_CACHE.getMapValueValidator().late()).add()).afterDecode(o -> {
        if (o.flags != null) {
            o.sortedFlagKeys = (String[])o.flags.keySet().toArray(String[]::new);
            Object[] sortedFlagKeys = o.sortedFlagKeys;
            Arrays.sort(sortedFlagKeys);
            o.flagIndex = new Object2IntOpenHashMap<String>();
            for (int i = 0; i < sortedFlagKeys.length; ++i) {
                o.flagIndex.put((String)sortedFlagKeys[i], i);
            }
        }
    })).build();
    protected String chainId;
    protected float chainingAllowance;
    protected String[] next;
    @Nullable
    protected Map<String, String> flags;
    @Nullable
    protected Object2IntMap<String> flagIndex;
    private String[] sortedFlagKeys;

    @Override
    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Client;
    }

    @Override
    protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        InteractionSyncData clientState = context.getClientState();
        assert (clientState != null);
        InteractionSyncData state = context.getState();
        if (clientState.flagIndex != -1) {
            state.state = InteractionState.Finished;
            context.jump(context.getLabel(this.next.length + clientState.flagIndex));
            return;
        }
        if (clientState.chainingIndex == -1) {
            state.state = InteractionState.NotFinished;
            return;
        }
        state.state = InteractionState.Finished;
        context.jump(context.getLabel(clientState.chainingIndex));
    }

    @Override
    protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        if (!firstRun) {
            return;
        }
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        Ref<EntityStore> ref = context.getEntity();
        Data dataComponent = commandBuffer.getComponent(ref, Data.getComponentType());
        if (dataComponent == null) {
            return;
        }
        InteractionSyncData state = context.getState();
        String id = this.chainId == null ? this.id : this.chainId;
        Object2IntMap<String> map = this.chainId == null ? dataComponent.map : dataComponent.namedMap;
        int lastSequenceIndex = map.getInt(id);
        if (++lastSequenceIndex >= this.next.length) {
            lastSequenceIndex = 0;
        }
        if (this.chainingAllowance > 0.0f && dataComponent.getTimeSinceLastAttackInSeconds() > this.chainingAllowance) {
            lastSequenceIndex = 0;
        }
        map.put(id, lastSequenceIndex);
        state.chainingIndex = lastSequenceIndex;
        state.state = InteractionState.Finished;
        context.jump(context.getLabel(lastSequenceIndex));
        dataComponent.lastAttack = System.nanoTime();
    }

    @Override
    public void compile(@Nonnull OperationsBuilder builder) {
        int i;
        int len = this.next.length + (this.sortedFlagKeys != null ? this.sortedFlagKeys.length : 0);
        Label[] labels = new Label[len];
        for (int i2 = 0; i2 < labels.length; ++i2) {
            labels[i2] = builder.createUnresolvedLabel();
        }
        builder.addOperation(this, labels);
        Label end = builder.createUnresolvedLabel();
        for (i = 0; i < this.next.length; ++i) {
            builder.resolveLabel(labels[i]);
            Interaction interaction = Interaction.getInteractionOrUnknown(this.next[i]);
            interaction.compile(builder);
            builder.jump(end);
        }
        if (this.flags != null) {
            for (i = 0; i < this.sortedFlagKeys.length; ++i) {
                String flag = this.sortedFlagKeys[i];
                builder.resolveLabel(labels[this.next.length + i]);
                Interaction interaction = Interaction.getInteractionOrUnknown(this.flags.get(flag));
                interaction.compile(builder);
                builder.jump(end);
            }
        }
        builder.resolveLabel(end);
    }

    @Override
    public boolean walk(@Nonnull Collector collector, @Nonnull InteractionContext context) {
        for (int i = 0; i < this.next.length; ++i) {
            if (!InteractionManager.walkInteraction(collector, context, ChainingTag.of(i), this.next[i])) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nonnull
    protected com.hypixel.hytale.protocol.Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.ChainingInteraction();
    }

    @Override
    protected void configurePacket(com.hypixel.hytale.protocol.Interaction packet) {
        super.configurePacket(packet);
        com.hypixel.hytale.protocol.ChainingInteraction p = (com.hypixel.hytale.protocol.ChainingInteraction)packet;
        p.chainingAllowance = this.chainingAllowance;
        p.chainingNext = new int[this.next.length];
        int[] chainingNext = p.chainingNext;
        for (int i = 0; i < this.next.length; ++i) {
            chainingNext[i] = Interaction.getInteractionIdOrUnknown(this.next[i]);
        }
        if (this.flags != null) {
            p.flags = new Object2IntOpenHashMap<String>();
            for (Map.Entry<String, String> e : this.flags.entrySet()) {
                p.flags.put(e.getKey(), Interaction.getInteractionIdOrUnknown(e.getValue()));
            }
        }
        p.chainId = this.chainId;
    }

    @Override
    public boolean needsRemoteSync() {
        return true;
    }

    @Override
    @Nonnull
    public String toString() {
        return "ChainingInteraction{chainingAllowance=" + this.chainingAllowance + ", next=" + Arrays.toString(this.next) + "} " + super.toString();
    }

    public static class Data
    implements Component<EntityStore> {
        private final Object2IntMap<String> map = new Object2IntOpenHashMap<String>();
        private final Object2IntMap<String> namedMap = new Object2IntOpenHashMap<String>();
        private long lastAttack;

        public static ComponentType<EntityStore, Data> getComponentType() {
            return InteractionModule.get().getChainingDataComponent();
        }

        public float getTimeSinceLastAttackInSeconds() {
            if (this.lastAttack == 0L) {
                return 0.0f;
            }
            long diff = System.nanoTime() - this.lastAttack;
            return (float)diff / 1.0E9f;
        }

        @Nonnull
        public Object2IntMap<String> getNamedMap() {
            return this.namedMap;
        }

        @Override
        @Nonnull
        public Component<EntityStore> clone() {
            Data c = new Data();
            c.map.putAll(this.map);
            c.lastAttack = this.lastAttack;
            return c;
        }
    }

    private static class ChainingTag
    implements CollectorTag {
        private final int index;

        private ChainingTag(int index) {
            this.index = index;
        }

        public int getIndex() {
            return this.index;
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ChainingTag that = (ChainingTag)o;
            return this.index == that.index;
        }

        public int hashCode() {
            return this.index;
        }

        @Nonnull
        public String toString() {
            return "ChainingTag{index=" + this.index + "}";
        }

        @Nonnull
        public static ChainingTag of(int index) {
            return new ChainingTag(index);
        }
    }
}

