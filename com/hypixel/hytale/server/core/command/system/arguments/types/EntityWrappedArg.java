/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.arguments.types;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.Argument;
import com.hypixel.hytale.server.core.command.system.arguments.system.WrappedArg;
import com.hypixel.hytale.server.core.command.system.exceptions.GeneralCommandException;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityWrappedArg
extends WrappedArg<UUID> {
    @Nonnull
    private static final Message MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD = Message.translation("server.commands.errors.playerNotInWorld");

    public EntityWrappedArg(@Nonnull Argument<?, UUID> argument) {
        super(argument);
    }

    @Nullable
    public Ref<EntityStore> get(@Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull CommandContext context) {
        Ref<EntityStore> playerRef;
        World world = componentAccessor.getExternalData().getWorld();
        Ref<EntityStore> reference = this.getEntityDirectly(context, world);
        if (reference != null) {
            return reference;
        }
        Ref<EntityStore> ref = playerRef = context.isPlayer() ? context.senderAsPlayerRef() : null;
        if (playerRef == null) {
            throw new GeneralCommandException(Message.translation("server.commands.errors.playerOrArg").param("option", "entity"));
        }
        if (!playerRef.isValid()) {
            throw new GeneralCommandException(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
        }
        Ref<EntityStore> entityRef = TargetUtil.getTargetEntity(playerRef, componentAccessor);
        if (entityRef == null) {
            throw new GeneralCommandException(Message.translation("server.commands.errors.no_entity_in_view").param("option", "entity"));
        }
        return entityRef;
    }

    @Nullable
    public Ref<EntityStore> getEntityDirectly(@Nonnull CommandContext context, @Nonnull World world) {
        if (!this.provided(context)) {
            return null;
        }
        UUID uuid = (UUID)this.get(context);
        Ref<EntityStore> reference = world.getEntityStore().getRefFromUUID(uuid);
        if (reference == null) {
            throw new GeneralCommandException(Message.translation("server.commands.errors.targetNotFound").param("uuid", uuid.toString()));
        }
        return reference;
    }
}

