/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning;

import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.Scope;
import com.hypixel.hytale.server.spawning.ISpawnable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ISpawnableWithModel
extends ISpawnable {
    @Nullable
    public String getSpawnModelName(ExecutionContext var1, Scope var2);

    @Nullable
    default public Scope createModifierScope(ExecutionContext executionContext) {
        throw new IllegalStateException("Call to createModifierScope not valid for ISpawnableWithModel");
    }

    public Scope createExecutionScope();

    public void markNeedsReload();

    public boolean isMemory(ExecutionContext var1, @Nullable Scope var2);

    public String getMemoriesCategory(ExecutionContext var1, @Nullable Scope var2);

    public String getMemoriesNameOverride(ExecutionContext var1, @Nullable Scope var2);

    @Nonnull
    public String getNameTranslationKey(ExecutionContext var1, @Nullable Scope var2);
}

