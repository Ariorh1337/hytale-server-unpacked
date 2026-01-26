/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.teleport.components;

import com.hypixel.hytale.builtin.teleport.TeleportPlugin;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.annotation.Nonnull;

public class TeleportHistory
implements Component<EntityStore> {
    private static final int MAX_TELEPORT_HISTORY = 100;
    private static final Message MESSAGE_COMMANDS_TELEPORT_NOT_FURTHER = Message.translation("server.commands.teleport.notFurther");
    private static final Message MESSAGE_COMMANDS_TELEPORT_WORLD_NOT_LOADED = Message.translation("server.commands.teleport.worldNotLoaded");
    @Nonnull
    private final Deque<Waypoint> back = new ArrayDeque<Waypoint>();
    @Nonnull
    private final Deque<Waypoint> forward = new ArrayDeque<Waypoint>();

    @Nonnull
    public static ComponentType<EntityStore, TeleportHistory> getComponentType() {
        return TeleportPlugin.get().getTeleportHistoryComponentType();
    }

    public void forward(@Nonnull Ref<EntityStore> ref, int count) {
        Store<EntityStore> store = ref.getStore();
        TeleportHistory.go(store, ref, this.forward, this.back, count, true);
    }

    public void back(@Nonnull Ref<EntityStore> ref, int count) {
        Store<EntityStore> store = ref.getStore();
        TeleportHistory.go(store, ref, this.back, this.forward, count, false);
    }

    public int getForwardSize() {
        return this.forward.size();
    }

    public int getBackSize() {
        return this.back.size();
    }

    private static void go(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull Deque<Waypoint> from, @Nonnull Deque<Waypoint> to, int count, boolean isForward) {
        if (count <= 0) {
            throw new IllegalArgumentException(String.valueOf(count));
        }
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        assert (playerRef != null);
        Waypoint point = null;
        for (int i = 0; i < count; ++i) {
            if (from.isEmpty()) {
                if (point != null) break;
                playerRef.sendMessage(MESSAGE_COMMANDS_TELEPORT_NOT_FURTHER);
                return;
            }
            point = from.pop();
            to.push(point);
        }
        if (point == null) {
            throw new NullPointerException(to.toString());
        }
        World targetWorld = Universe.get().getWorld(point.world);
        if (targetWorld == null) {
            playerRef.sendMessage(MESSAGE_COMMANDS_TELEPORT_WORLD_NOT_LOADED);
        } else {
            to.push(point);
            Teleport teleportComponent = Teleport.createForPlayer(targetWorld, point.position, point.rotation);
            store.addComponent(ref, Teleport.getComponentType(), teleportComponent);
            Vector3d pos = point.position;
            int remainingInDirection = from.size();
            int totalInOtherDirection = to.size() - 1;
            if (point.message != null && !point.message.isEmpty()) {
                playerRef.sendMessage((isForward ? Message.translation("server.commands.teleport.teleportedForwardToWaypoint") : Message.translation("server.commands.teleport.teleportedBackToWaypoint")).param("name", point.message).param("x", pos.getX()).param("y", pos.getY()).param("z", pos.getZ()).param("remaining", remainingInDirection).param("otherDirection", totalInOtherDirection));
            } else {
                playerRef.sendMessage((isForward ? Message.translation("server.commands.teleport.teleportedForwardToCoordinates") : Message.translation("server.commands.teleport.teleportedBackToCoordinates")).param("x", pos.getX()).param("y", pos.getY()).param("z", pos.getZ()).param("remaining", remainingInDirection).param("otherDirection", totalInOtherDirection));
            }
        }
    }

    public void append(@Nonnull World world, @Nonnull Vector3d pos, @Nonnull Vector3f rotation, @Nonnull String key) {
        this.back.push(new Waypoint(world.getName(), pos, rotation, key));
        this.forward.clear();
        while (this.back.size() > 100) {
            this.back.removeLast();
        }
    }

    @Nonnull
    public String toString() {
        return "TeleportHistory{back=" + String.valueOf(this.back) + ", forward=" + String.valueOf(this.forward) + "}";
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        TeleportHistory cloned = new TeleportHistory();
        cloned.back.addAll(this.back);
        cloned.forward.addAll(this.forward);
        return cloned;
    }

    public static class Waypoint {
        private final String world;
        private final Vector3d position;
        private final Vector3f rotation;
        private final String message;

        public Waypoint(@Nonnull String world, @Nonnull Vector3d position, @Nonnull Vector3f rotation, @Nonnull String message) {
            this.world = world;
            this.position = position;
            this.rotation = rotation;
            this.message = message;
        }

        @Nonnull
        public String toString() {
            return "Waypoint{world='" + this.world + "', position=" + String.valueOf(this.position) + ", rotation=" + String.valueOf(this.rotation) + ", message='" + this.message + "'}";
        }
    }
}

