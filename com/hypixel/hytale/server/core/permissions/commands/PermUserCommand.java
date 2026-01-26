/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.permissions.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.permissions.provider.PermissionProvider;
import com.hypixel.hytale.server.core.util.message.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class PermUserCommand
extends AbstractCommandCollection {
    public PermUserCommand() {
        super("user", "server.commands.perm.user.desc");
        this.addSubCommand(new PermUserListCommand());
        this.addSubCommand(new PermUserAddCommand());
        this.addSubCommand(new PermUserRemoveCommand());
        this.addSubCommand(new PermUserGroupCommand());
    }

    private static class PermUserListCommand
    extends CommandBase {
        @Nonnull
        private final RequiredArg<UUID> uuidArg = this.withRequiredArg("uuid", "server.commands.perm.user.list.uuid.desc", ArgTypes.UUID);

        public PermUserListCommand() {
            super("list", "server.commands.perm.user.list.desc");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            UUID uuid = (UUID)this.uuidArg.get(context);
            for (PermissionProvider permissionProvider : PermissionsModule.get().getProviders()) {
                Message header = Message.raw(permissionProvider.getName());
                Set<Message> userPermissions = permissionProvider.getUserPermissions(uuid).stream().map(Message::raw).collect(Collectors.toSet());
                context.sendMessage(MessageFormat.list(header, userPermissions));
            }
        }
    }

    private static class PermUserAddCommand
    extends CommandBase {
        @Nonnull
        private final RequiredArg<UUID> uuidArg = this.withRequiredArg("uuid", "server.commands.perm.user.add.uuid.desc", ArgTypes.UUID);
        @Nonnull
        private final RequiredArg<List<String>> permissionsArg = this.withListRequiredArg("permissions", "server.commands.perm.user.add.permissions.desc", ArgTypes.STRING);

        public PermUserAddCommand() {
            super("add", "server.commands.perm.user.add.desc");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            UUID uuid = (UUID)this.uuidArg.get(context);
            HashSet<String> permissions = new HashSet<String>((Collection)this.permissionsArg.get(context));
            PermissionsModule.get().addUserPermission(uuid, permissions);
            context.sendMessage(Message.translation("server.commands.perm.permAddedToUser").param("uuid", uuid.toString()));
        }
    }

    private static class PermUserRemoveCommand
    extends CommandBase {
        @Nonnull
        private final RequiredArg<UUID> uuidArg = this.withRequiredArg("uuid", "server.commands.perm.user.remove.uuid.desc", ArgTypes.UUID);
        @Nonnull
        private final RequiredArg<List<String>> permissionsArg = this.withListRequiredArg("permissions", "server.commands.perm.user.remove.permissions.desc", ArgTypes.STRING);

        public PermUserRemoveCommand() {
            super("remove", "server.commands.perm.user.remove.desc");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            UUID uuid = (UUID)this.uuidArg.get(context);
            HashSet<String> permissions = new HashSet<String>((Collection)this.permissionsArg.get(context));
            PermissionsModule.get().removeUserPermission(uuid, permissions);
            context.sendMessage(Message.translation("server.commands.perm.permRemovedFromUser").param("uuid", uuid.toString()));
        }
    }

    private static class PermUserGroupCommand
    extends AbstractCommandCollection {
        public PermUserGroupCommand() {
            super("group", "server.commands.perm.user.group.desc");
            this.addSubCommand(new PermUserGroupListCommand());
            this.addSubCommand(new PermUserGroupAddCommand());
            this.addSubCommand(new PermUserGroupRemoveCommand());
        }

        private static class PermUserGroupListCommand
        extends CommandBase {
            @Nonnull
            private final RequiredArg<UUID> uuidArg = this.withRequiredArg("uuid", "server.commands.perm.user.group.list.uuid.desc", ArgTypes.UUID);

            public PermUserGroupListCommand() {
                super("list", "server.commands.perm.user.group.list.desc");
            }

            @Override
            protected void executeSync(@Nonnull CommandContext context) {
                UUID uuid = (UUID)this.uuidArg.get(context);
                for (PermissionProvider permissionProvider : PermissionsModule.get().getProviders()) {
                    Message header = Message.raw(permissionProvider.getName());
                    Set<Message> groups = permissionProvider.getGroupsForUser(uuid).stream().map(Message::raw).collect(Collectors.toSet());
                    context.sendMessage(MessageFormat.list(header, groups));
                }
            }
        }

        private static class PermUserGroupAddCommand
        extends CommandBase {
            @Nonnull
            private final RequiredArg<UUID> uuidArg = this.withRequiredArg("uuid", "server.commands.perm.user.group.add.uuid.desc", ArgTypes.UUID);
            @Nonnull
            private final RequiredArg<String> groupArg = this.withRequiredArg("group", "server.commands.perm.user.group.add.group.desc", ArgTypes.STRING);

            public PermUserGroupAddCommand() {
                super("add", "server.commands.perm.user.group.add.desc");
            }

            @Override
            protected void executeSync(@Nonnull CommandContext context) {
                UUID uuid = (UUID)this.uuidArg.get(context);
                String group = (String)this.groupArg.get(context);
                PermissionsModule.get().addUserToGroup(uuid, group);
                context.sendMessage(Message.translation("server.commands.perm.userAddedToGroup").param("uuid", uuid.toString()).param("group", group));
            }
        }

        private static class PermUserGroupRemoveCommand
        extends CommandBase {
            @Nonnull
            private final RequiredArg<UUID> uuidArg = this.withRequiredArg("uuid", "server.commands.perm.user.group.remove.uuid.desc", ArgTypes.UUID);
            @Nonnull
            private final RequiredArg<String> groupArg = this.withRequiredArg("group", "server.commands.perm.user.group.remove.group.desc", ArgTypes.STRING);

            public PermUserGroupRemoveCommand() {
                super("remove", "server.commands.perm.user.group.remove.desc");
            }

            @Override
            protected void executeSync(@Nonnull CommandContext context) {
                UUID uuid = (UUID)this.uuidArg.get(context);
                String group = (String)this.groupArg.get(context);
                PermissionsModule.get().removeUserFromGroup(uuid, group);
                context.sendMessage(Message.translation("server.commands.perm.userRemovedFromGroup").param("uuid", uuid.toString()).param("group", group));
            }
        }
    }
}

