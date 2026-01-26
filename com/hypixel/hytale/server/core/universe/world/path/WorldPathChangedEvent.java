/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.path;

import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.server.core.universe.world.path.WorldPath;
import java.util.Objects;
import javax.annotation.Nonnull;

public class WorldPathChangedEvent
implements IEvent<Void> {
    private WorldPath worldPath;

    public WorldPathChangedEvent(WorldPath worldPath) {
        Objects.requireNonNull(worldPath, "World path must not be null in an event");
        this.worldPath = worldPath;
    }

    public WorldPath getWorldPath() {
        return this.worldPath;
    }

    @Nonnull
    public String toString() {
        return "WorldPathChangedEvent{worldPath=" + String.valueOf(this.worldPath) + "}";
    }
}

