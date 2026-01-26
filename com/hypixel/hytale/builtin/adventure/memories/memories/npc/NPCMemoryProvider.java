/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.memories.memories.npc;

import com.hypixel.hytale.builtin.adventure.memories.MemoriesPlugin;
import com.hypixel.hytale.builtin.adventure.memories.memories.Memory;
import com.hypixel.hytale.builtin.adventure.memories.memories.MemoryProvider;
import com.hypixel.hytale.builtin.adventure.memories.memories.npc.NPCMemory;
import com.hypixel.hytale.logger.sentry.SkipSentryException;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderInfo;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.Scope;
import com.hypixel.hytale.server.spawning.ISpawnableWithModel;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NPCMemoryProvider
extends MemoryProvider<NPCMemory> {
    public static final double DEFAULT_RADIUS = 10.0;

    public NPCMemoryProvider() {
        super("NPC", NPCMemory.CODEC, 10.0);
    }

    @Override
    @Nonnull
    public Map<String, Set<Memory>> getAllMemories() {
        Object2ObjectOpenHashMap<String, Set<Memory>> allMemories = new Object2ObjectOpenHashMap<String, Set<Memory>>();
        Int2ObjectMap<BuilderInfo> allBuilders = NPCPlugin.get().getBuilderManager().getAllBuilders();
        for (BuilderInfo builderInfo : allBuilders.values()) {
            try {
                String category;
                Builder<?> builder = builderInfo.getBuilder();
                if (!builder.isSpawnable() || builder.isDeprecated() || !builderInfo.isValid() || !NPCMemoryProvider.isMemory(builder) || (category = NPCMemoryProvider.getCategory(builder)) == null) continue;
                String memoriesNameOverride = NPCMemoryProvider.getMemoriesNameOverride(builder);
                String translationKey = NPCMemoryProvider.getNPCNameTranslationKey(builder);
                NPCMemory memory = memoriesNameOverride != null && !memoriesNameOverride.isEmpty() ? new NPCMemory(memoriesNameOverride, translationKey, true) : new NPCMemory(builderInfo.getKeyName(), translationKey, false);
                allMemories.computeIfAbsent(category, s -> new HashSet()).add(memory);
            }
            catch (SkipSentryException e) {
                MemoriesPlugin.get().getLogger().at(Level.SEVERE).log(e.getMessage());
            }
        }
        return allMemories;
    }

    @Nullable
    private static String getCategory(@Nonnull Builder<?> builder) {
        if (builder instanceof ISpawnableWithModel) {
            ISpawnableWithModel spawnableWithModel = (ISpawnableWithModel)((Object)builder);
            ExecutionContext executionContext = new ExecutionContext();
            executionContext.setScope(spawnableWithModel.createExecutionScope());
            Scope modifierScope = spawnableWithModel.createModifierScope(executionContext);
            return spawnableWithModel.getMemoriesCategory(executionContext, modifierScope);
        }
        return "Other";
    }

    private static boolean isMemory(@Nonnull Builder<?> builder) {
        if (builder instanceof ISpawnableWithModel) {
            ISpawnableWithModel spawnableWithModel = (ISpawnableWithModel)((Object)builder);
            ExecutionContext executionContext = new ExecutionContext();
            executionContext.setScope(spawnableWithModel.createExecutionScope());
            Scope modifierScope = spawnableWithModel.createModifierScope(executionContext);
            return spawnableWithModel.isMemory(executionContext, modifierScope);
        }
        return false;
    }

    @Nullable
    private static String getMemoriesNameOverride(@Nonnull Builder<?> builder) {
        if (builder instanceof ISpawnableWithModel) {
            ISpawnableWithModel spawnableWithModel = (ISpawnableWithModel)((Object)builder);
            ExecutionContext executionContext = new ExecutionContext();
            executionContext.setScope(spawnableWithModel.createExecutionScope());
            Scope modifierScope = spawnableWithModel.createModifierScope(executionContext);
            return spawnableWithModel.getMemoriesNameOverride(executionContext, modifierScope);
        }
        return null;
    }

    @Nonnull
    private static String getNPCNameTranslationKey(@Nonnull Builder<?> builder) {
        if (builder instanceof ISpawnableWithModel) {
            ISpawnableWithModel spawnableWithModel = (ISpawnableWithModel)((Object)builder);
            ExecutionContext executionContext = new ExecutionContext();
            executionContext.setScope(spawnableWithModel.createExecutionScope());
            Scope modifierScope = spawnableWithModel.createModifierScope(executionContext);
            return spawnableWithModel.getNameTranslationKey(executionContext, modifierScope);
        }
        throw new SkipSentryException(new IllegalStateException("Cannot get translation key for a non spawnable NPC role!"));
    }
}

