/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.memories.component;

import com.hypixel.hytale.builtin.adventure.memories.MemoriesPlugin;
import com.hypixel.hytale.builtin.adventure.memories.memories.Memory;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.Nonnull;

public class PlayerMemories
implements Component<EntityStore> {
    public static final BuilderCodec<PlayerMemories> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PlayerMemories.class, PlayerMemories::new).append(new KeyedCodec<Integer>("Capacity", Codec.INTEGER), (playerMemories, integer) -> {
        playerMemories.memoriesCapacity = integer;
    }, playerMemories -> playerMemories.memoriesCapacity).add()).append(new KeyedCodec<T[]>("Memories", new ArrayCodec<Memory>(Memory.CODEC, Memory[]::new)), (playerMemories, memories) -> {
        if (memories == null) {
            return;
        }
        Collections.addAll(playerMemories.memories, memories);
    }, playerMemories -> (Memory[])playerMemories.memories.toArray(Memory[]::new)).add()).build();
    private final Set<Memory> memories = new LinkedHashSet<Memory>();
    private int memoriesCapacity;

    public static ComponentType<EntityStore, PlayerMemories> getComponentType() {
        return MemoriesPlugin.get().getPlayerMemoriesComponentType();
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        PlayerMemories playerMemories = new PlayerMemories();
        playerMemories.memories.addAll(this.memories);
        playerMemories.memoriesCapacity = this.memoriesCapacity;
        return playerMemories;
    }

    public int getMemoriesCapacity() {
        return this.memoriesCapacity;
    }

    public void setMemoriesCapacity(int memoriesCapacity) {
        this.memoriesCapacity = memoriesCapacity;
    }

    public boolean recordMemory(Memory memory) {
        if (this.memories.size() >= this.memoriesCapacity) {
            return false;
        }
        return this.memories.add(memory);
    }

    public boolean hasMemories() {
        return !this.memories.isEmpty();
    }

    public boolean takeMemories(@Nonnull Set<Memory> outMemories) {
        boolean result = outMemories.addAll(this.memories);
        this.memories.clear();
        return result;
    }

    @Nonnull
    public Set<Memory> getRecordedMemories() {
        return Collections.unmodifiableSet(this.memories);
    }
}

