/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.permissions.commands.op;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;

public class OpSelfCommand
extends AbstractPlayerCommand {
    @Nonnull
    private static final Message MESSAGE_COMMANDS_OP_ADDED = Message.translation("server.commands.op.self.added");
    @Nonnull
    private static final Message MESSAGE_COMMANDS_OP_REMOVED = Message.translation("server.commands.op.self.removed");
    @Nonnull
    private static final Message MESSAGE_COMMANDS_NON_VANILLA_PERMISSIONS = Message.translation("server.commands.op.self.nonVanillaPermissions");
    @Nonnull
    private static final Message MESSAGE_COMMANDS_SINGLEPLAYER_OWNER_REQ = Message.translation("server.commands.op.self.singleplayerOwnerReq");

    public OpSelfCommand() {
        super("self", "server.commands.op.self.desc");
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        if (PermissionsModule.get().areProvidersTampered()) {
            playerRef.sendMessage(MESSAGE_COMMANDS_NON_VANILLA_PERMISSIONS);
            return;
        }
        if (Constants.SINGLEPLAYER && !SingleplayerModule.isOwner(playerRef)) {
            playerRef.sendMessage(MESSAGE_COMMANDS_SINGLEPLAYER_OWNER_REQ);
            return;
        }
        if (!Constants.SINGLEPLAYER && !Constants.ALLOWS_SELF_OP_COMMAND) {
            playerRef.sendMessage(Message.translation("server.commands.op.self.multiplayerTip").param("uuidCommand", "uuid").param("permissionFile", "permissions.json").param("launchArg", "--allow-op"));
            return;
        }
        UUID uuid = playerRef.getUuid();
        PermissionsModule perms = PermissionsModule.get();
        String opGroup = "OP";
        Set<String> groups = perms.getGroupsForUser(uuid);
        if (groups.contains("OP")) {
            perms.removeUserFromGroup(uuid, "OP");
            context.sendMessage(MESSAGE_COMMANDS_OP_REMOVED);
        } else {
            perms.addUserToGroup(uuid, "OP");
            context.sendMessage(MESSAGE_COMMANDS_OP_ADDED);
        }
    }
}

