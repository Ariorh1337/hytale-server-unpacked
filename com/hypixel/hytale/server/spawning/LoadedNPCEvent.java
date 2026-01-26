/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning;

import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.server.npc.asset.builder.BuilderInfo;
import com.hypixel.hytale.server.spawning.ISpawnableWithModel;
import java.util.Objects;
import javax.annotation.Nonnull;

public class LoadedNPCEvent
implements IEvent<Void> {
    private BuilderInfo builderInfo;

    public LoadedNPCEvent(@Nonnull BuilderInfo builderInfo) {
        Objects.requireNonNull(builderInfo, "builderInfo can't be null for event");
        if (!(builderInfo.getBuilder() instanceof ISpawnableWithModel)) {
            throw new IllegalArgumentException("BuilderInfo builder must be spawnable for event");
        }
        this.builderInfo = builderInfo;
    }

    public BuilderInfo getBuilderInfo() {
        return this.builderInfo;
    }

    @Nonnull
    public String toString() {
        return "LoadedNPCEvent{builderInfo=" + String.valueOf(this.builderInfo) + "}";
    }
}

