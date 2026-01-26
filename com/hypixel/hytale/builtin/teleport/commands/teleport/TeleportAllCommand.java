/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.teleport.commands.teleport;

import com.hypixel.hytale.builtin.teleport.components.TeleportHistory;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.Coord;
import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeFloat;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.permissions.HytalePermissions;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import java.util.Collection;
import javax.annotation.Nonnull;

public class TeleportAllCommand
extends CommandBase {
    @Nonnull
    private static final Message MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD = Message.translation("server.commands.errors.playerNotInWorld");
    @Nonnull
    private final RequiredArg<Coord> xArg = this.withRequiredArg("x", "server.commands.teleport.x.desc", ArgTypes.RELATIVE_DOUBLE_COORD);
    @Nonnull
    private final RequiredArg<Coord> yArg = this.withRequiredArg("y", "server.commands.teleport.y.desc", ArgTypes.RELATIVE_DOUBLE_COORD);
    @Nonnull
    private final RequiredArg<Coord> zArg = this.withRequiredArg("z", "server.commands.teleport.z.desc", ArgTypes.RELATIVE_DOUBLE_COORD);
    @Nonnull
    private final OptionalArg<RelativeFloat> yawArg = this.withOptionalArg("yaw", "server.commands.teleport.yaw.desc", ArgTypes.RELATIVE_FLOAT);
    @Nonnull
    private final OptionalArg<RelativeFloat> pitchArg = this.withOptionalArg("pitch", "server.commands.teleport.pitch.desc", ArgTypes.RELATIVE_FLOAT);
    @Nonnull
    private final OptionalArg<RelativeFloat> rollArg = this.withOptionalArg("roll", "server.commands.teleport.roll.desc", ArgTypes.RELATIVE_FLOAT);
    @Nonnull
    private final OptionalArg<World> worldArg = this.withOptionalArg("world", "server.commands.worldthread.arg.desc", ArgTypes.WORLD);

    public TeleportAllCommand() {
        super("all", "server.commands.tpall.desc");
        this.setPermissionGroup(null);
        this.requirePermission(HytalePermissions.fromCommand("teleport.all"));
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        World targetWorld;
        Coord relX = (Coord)this.xArg.get(context);
        Coord relY = (Coord)this.yArg.get(context);
        Coord relZ = (Coord)this.zArg.get(context);
        if (this.worldArg.provided(context)) {
            targetWorld = (World)this.worldArg.get(context);
        } else if (context.isPlayer()) {
            Ref<EntityStore> senderRef = context.senderAsPlayerRef();
            if (senderRef == null || !senderRef.isValid()) {
                context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
                return;
            }
            targetWorld = senderRef.getStore().getExternalData().getWorld();
        } else {
            context.sendMessage(Message.translation("server.commands.errors.playerOrArg").param("option", "world"));
            return;
        }
        targetWorld.execute(() -> {
            TransformComponent transformComponent;
            Store<EntityStore> senderStore;
            World senderWorld;
            Ref<EntityStore> senderRef;
            Store<EntityStore> store = targetWorld.getEntityStore().getStore();
            double baseX = 0.0;
            double baseY = 0.0;
            double baseZ = 0.0;
            if (context.isPlayer() && (senderRef = context.senderAsPlayerRef()) != null && senderRef.isValid() && (senderWorld = (senderStore = senderRef.getStore()).getExternalData().getWorld()) == targetWorld && (transformComponent = senderStore.getComponent(senderRef, TransformComponent.getComponentType())) != null) {
                Vector3d pos = transformComponent.getPosition();
                baseX = pos.getX();
                baseY = pos.getY();
                baseZ = pos.getZ();
            }
            double x = relX.resolveXZ(baseX);
            double z = relZ.resolveXZ(baseZ);
            double y = relY.resolveYAtWorldCoords(baseY, targetWorld, x, z);
            boolean hasRotation = this.yawArg.provided(context) || this.pitchArg.provided(context) || this.rollArg.provided(context);
            Collection<PlayerRef> playersToTeleport = targetWorld.getPlayerRefs();
            for (PlayerRef playerRef : playersToTeleport) {
                Ref<EntityStore> ref = playerRef.getReference();
                if (ref == null || !ref.isValid()) continue;
                TransformComponent transformComponent2 = store.getComponent(ref, TransformComponent.getComponentType());
                HeadRotation headRotationComponent = store.getComponent(ref, HeadRotation.getComponentType());
                if (transformComponent2 == null || headRotationComponent == null) continue;
                Vector3d previousPos = transformComponent2.getPosition().clone();
                Vector3f previousHeadRotation = headRotationComponent.getRotation().clone();
                Vector3f previousBodyRotation = transformComponent2.getRotation().clone();
                float yaw = this.yawArg.provided(context) ? ((RelativeFloat)this.yawArg.get(context)).resolve(previousHeadRotation.getYaw() * 57.295776f) * ((float)Math.PI / 180) : Float.NaN;
                float pitch = this.pitchArg.provided(context) ? ((RelativeFloat)this.pitchArg.get(context)).resolve(previousHeadRotation.getPitch() * 57.295776f) * ((float)Math.PI / 180) : Float.NaN;
                float roll = this.rollArg.provided(context) ? ((RelativeFloat)this.rollArg.get(context)).resolve(previousHeadRotation.getRoll() * 57.295776f) * ((float)Math.PI / 180) : Float.NaN;
                Teleport teleport = Teleport.createExact(new Vector3d(x, y, z), new Vector3f(previousBodyRotation.getPitch(), yaw, previousBodyRotation.getRoll()), new Vector3f(pitch, yaw, roll));
                store.addComponent(ref, Teleport.getComponentType(), teleport);
                Player playerComponent = store.getComponent(ref, Player.getComponentType());
                if (playerComponent == null) continue;
                PlayerRef playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());
                assert (playerRefComponent != null);
                TeleportHistory teleportHistoryComponent = store.ensureAndGetComponent(ref, TeleportHistory.getComponentType());
                teleportHistoryComponent.append(targetWorld, previousPos, previousHeadRotation, String.format("Teleport to (%s, %s, %s) by %s", x, y, z, context.sender().getDisplayName()));
                if (hasRotation) {
                    float displayYaw = Float.isNaN(yaw) ? previousHeadRotation.getYaw() * 57.295776f : yaw * 57.295776f;
                    float displayPitch = Float.isNaN(pitch) ? previousHeadRotation.getPitch() * 57.295776f : pitch * 57.295776f;
                    float displayRoll = Float.isNaN(roll) ? previousHeadRotation.getRoll() * 57.295776f : roll * 57.295776f;
                    NotificationUtil.sendNotification(playerRefComponent.getPacketHandler(), Message.translation("server.commands.teleport.teleportedWithLookNotification").param("x", x).param("y", y).param("z", z).param("yaw", displayYaw).param("pitch", displayPitch).param("roll", displayRoll).param("sender", context.sender().getDisplayName()), null, "teleportation");
                    continue;
                }
                NotificationUtil.sendNotification(playerRefComponent.getPacketHandler(), Message.translation("server.commands.teleport.teleportedToCoordinatesNotification").param("x", x).param("y", y).param("z", z).param("sender", context.sender().getDisplayName()), null, "teleportation");
            }
            if (hasRotation) {
                float displayYaw = this.yawArg.provided(context) ? ((RelativeFloat)this.yawArg.get(context)).getRawValue() : 0.0f;
                float displayPitch = this.pitchArg.provided(context) ? ((RelativeFloat)this.pitchArg.get(context)).getRawValue() : 0.0f;
                float displayRoll = this.rollArg.provided(context) ? ((RelativeFloat)this.rollArg.get(context)).getRawValue() : 0.0f;
                context.sendMessage(Message.translation("server.commands.teleport.teleportEveryoneWithLook").param("world", targetWorld.getName()).param("x", x).param("y", y).param("z", z).param("yaw", displayYaw).param("pitch", displayPitch).param("roll", displayRoll));
            } else {
                context.sendMessage(Message.translation("server.commands.teleport.teleportEveryone").param("world", targetWorld.getName()).param("x", x).param("y", y).param("z", z));
            }
        });
    }
}

