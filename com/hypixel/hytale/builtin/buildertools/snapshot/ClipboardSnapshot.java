/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.snapshot;

import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.snapshot.SelectionSnapshot;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ClipboardSnapshot<T extends SelectionSnapshot<?>>
extends SelectionSnapshot<T> {
    @Nullable
    public T restoreClipboard(Ref<EntityStore> var1, Player var2, World var3, BuilderToolsPlugin.BuilderState var4, ComponentAccessor<EntityStore> var5);

    @Override
    default public T restore(Ref<EntityStore> ref, @Nonnull Player player, World world, ComponentAccessor<EntityStore> componentAccessor) {
        PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
        if (!1.$assertionsDisabled && playerRefComponent == null) {
            throw new AssertionError();
        }
        BuilderToolsPlugin.BuilderState state = BuilderToolsPlugin.getState(player, playerRefComponent);
        if (state == null) {
            return null;
        }
        return this.restoreClipboard(ref, player, world, state, componentAccessor);
    }

    static {
        if (1.$assertionsDisabled) {
            // empty if block
        }
    }
}

