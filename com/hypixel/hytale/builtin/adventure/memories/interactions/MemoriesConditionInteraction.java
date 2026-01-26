/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.memories.interactions;

import com.hypixel.hytale.builtin.adventure.memories.MemoriesPlugin;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.Int2ObjectMapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.protocol.Interaction;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.Collector;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.CollectorTag;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.StringTag;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Label;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.OperationsBuilder;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MemoriesConditionInteraction
extends com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction {
    @Nonnull
    public static final BuilderCodec<MemoriesConditionInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(MemoriesConditionInteraction.class, MemoriesConditionInteraction::new, ABSTRACT_CODEC).appendInherited(new KeyedCodec<String>("Next", new Int2ObjectMapCodec<String>(com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction.CHILD_ASSET_CODEC, Int2ObjectOpenHashMap::new)), (o, v) -> {
        o.next = v;
    }, o -> o.next, (o, p) -> {
        o.next = p.next;
    }).documentation("The interaction to run if the player's memories level matches the key.").addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<String>("Failed", com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction.CHILD_ASSET_CODEC), (o, v) -> {
        o.failed = v;
    }, o -> o.failed, (o, p) -> {
        o.failed = p.failed;
    }).documentation("The interaction to run if the player's memories level does not match any key.").add()).afterDecode(o -> {
        o.levelToLabel.defaultReturnValue(-1);
        o.sortedKeys = o.next.keySet().toIntArray();
        Arrays.sort(o.sortedKeys);
        o.levelToLabel.clear();
        for (int i = 0; i < o.sortedKeys.length; ++i) {
            o.levelToLabel.put(o.sortedKeys[i], i);
        }
    })).build();
    @Nonnull
    private static final StringTag TAG_FAILED = StringTag.of("Failed");
    @Nonnull
    private Int2ObjectMap<String> next = Int2ObjectMaps.emptyMap();
    private transient int[] sortedKeys;
    @Nonnull
    private final Int2IntOpenHashMap levelToLabel = new Int2IntOpenHashMap();
    @Nullable
    private String failed;

    @Override
    protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        int memoriesLevel;
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        World world = commandBuffer.getExternalData().getWorld();
        context.getState().chainingIndex = memoriesLevel = MemoriesPlugin.get().getMemoriesLevel(world.getGameplayConfig());
        int labelIndex = this.levelToLabel.get(memoriesLevel);
        if (labelIndex == -1) {
            labelIndex = this.sortedKeys.length;
            context.getState().state = InteractionState.Failed;
        } else {
            context.getState().state = InteractionState.Finished;
        }
        context.jump(context.getLabel(labelIndex));
    }

    @Override
    protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        int memoriesLevel = context.getServerState().chainingIndex;
        int labelIndex = this.levelToLabel.get(memoriesLevel);
        if (labelIndex == -1) {
            labelIndex = this.sortedKeys.length;
            context.getState().state = InteractionState.Failed;
        } else {
            context.getState().state = InteractionState.Finished;
        }
        context.jump(context.getLabel(labelIndex));
    }

    @Override
    public void compile(@Nonnull OperationsBuilder builder) {
        int i;
        Label end = builder.createUnresolvedLabel();
        Label[] labels = new Label[this.next.size() + 1];
        for (i = 0; i < labels.length; ++i) {
            labels[i] = builder.createUnresolvedLabel();
        }
        builder.addOperation(this, labels);
        builder.jump(end);
        for (i = 0; i < this.sortedKeys.length; ++i) {
            int key = this.sortedKeys[i];
            builder.resolveLabel(labels[i]);
            com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction interaction = com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction.getInteractionOrUnknown((String)this.next.get(key));
            interaction.compile(builder);
            builder.jump(end);
        }
        int failedIndex = this.sortedKeys.length;
        builder.resolveLabel(labels[failedIndex]);
        if (this.failed != null) {
            com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction interaction = com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction.getInteractionOrUnknown(this.failed);
            interaction.compile(builder);
        }
        builder.resolveLabel(end);
    }

    @Override
    @Nonnull
    protected Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.MemoriesConditionInteraction();
    }

    @Override
    protected void configurePacket(Interaction packet) {
        super.configurePacket(packet);
        com.hypixel.hytale.protocol.MemoriesConditionInteraction p = (com.hypixel.hytale.protocol.MemoriesConditionInteraction)packet;
        p.memoriesNext = new Int2IntOpenHashMap(this.next.size());
        for (Int2ObjectMap.Entry entry : this.next.int2ObjectEntrySet()) {
            p.memoriesNext.put(entry.getIntKey(), com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction.getInteractionIdOrUnknown((String)entry.getValue()));
        }
        p.failed = com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction.getInteractionIdOrUnknown(this.failed);
    }

    @Override
    public boolean walk(@Nonnull Collector collector, @Nonnull InteractionContext context) {
        if (this.next != null) {
            for (Int2ObjectMap.Entry entry : this.next.int2ObjectEntrySet()) {
                if (!InteractionManager.walkInteraction(collector, context, new MemoriesTag(entry.getIntKey()), (String)entry.getValue())) continue;
                return true;
            }
        }
        if (this.failed != null) {
            return InteractionManager.walkInteraction(collector, context, TAG_FAILED, this.failed);
        }
        return false;
    }

    @Override
    public boolean needsRemoteSync() {
        return false;
    }

    @Override
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Server;
    }

    private record MemoriesTag(int memoryLevel) implements CollectorTag
    {
    }
}

