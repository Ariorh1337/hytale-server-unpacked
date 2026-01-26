/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.commands.NPCMultiSelectCommandBase;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.RoleDebugFlags;
import java.util.AbstractCollection;
import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NPCDebugCommand
extends AbstractCommandCollection {
    public NPCDebugCommand() {
        super("debug", "server.commands.npc.debug.desc");
        this.addSubCommand(new ShowCommand());
        this.addSubCommand(new SetCommand());
        this.addSubCommand(new ToggleCommand());
        this.addSubCommand(new DefaultsCommand());
        this.addSubCommand(new ClearCommand());
        this.addSubCommand(new PresetsCommand());
    }

    private static void modifyFlags(@Nonnull CommandContext context, @Nonnull NPCEntity npc, @Nonnull Ref<EntityStore> ref, @Nonnull EnumSet<RoleDebugFlags> flags, @Nonnull BiFunction<EnumSet<RoleDebugFlags>, EnumSet<RoleDebugFlags>, EnumSet<RoleDebugFlags>> flagsModifier, @Nonnull Store<EntityStore> store) {
        EnumSet<RoleDebugFlags> newFlags = flagsModifier.apply(npc.getRoleDebugFlags(), flags);
        if (newFlags == null) {
            return;
        }
        NPCDebugCommand.safeSetRoleDebugFlags(npc, ref, newFlags, store);
        NPCDebugCommand.printNewFlags(npc, context, newFlags);
    }

    private static void safeSetRoleDebugFlags(@Nonnull NPCEntity npc, @Nonnull Ref<EntityStore> ref, @Nonnull EnumSet<RoleDebugFlags> flags, @Nonnull Store<EntityStore> store) {
        store.tryRemoveComponent(ref, Nameplate.getComponentType());
        npc.setRoleDebugFlags(flags);
    }

    private static void printNewFlags(@Nonnull NPCEntity npc, @Nonnull CommandContext context, @Nonnull EnumSet<RoleDebugFlags> newFlags) {
        String flags = NPCDebugCommand.getListOfFlags(newFlags).toString();
        context.sendMessage(Message.translation("server.commands.npc.debug.debugFlagsSet").param("role", npc.getRoleName()).param("flags", !flags.isEmpty() ? flags : "<None>"));
    }

    @Nonnull
    private static StringBuilder getListOfFlags(@Nonnull EnumSet<RoleDebugFlags> flags) {
        return RoleDebugFlags.getListOfFlags(flags, new StringBuilder());
    }

    public static class ShowCommand
    extends NPCMultiSelectCommandBase {
        public ShowCommand() {
            super("show", "server.commands.npc.debug.show.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull NPCEntity npc, @Nonnull World world, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref) {
            String flags = NPCDebugCommand.getListOfFlags(npc.getRoleDebugFlags()).toString();
            context.sendMessage(Message.translation("server.commands.npc.debug.currentFlags").param("role", npc.getRoleName()).param("flags", !flags.isEmpty() ? flags : "<None>"));
        }
    }

    public static class SetCommand
    extends NPCMultiSelectCommandBase {
        @Nonnull
        private final RequiredArg<String> flagsArg = this.withRequiredArg("flags", "server.commands.npc.debug.flags.desc", ArgTypes.STRING);

        public SetCommand() {
            super("set", "server.commands.npc.debug.set.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull NPCEntity npc, @Nonnull World world, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref) {
            String flagsString = (String)this.flagsArg.get(context);
            EnumSet<RoleDebugFlags> flags = RoleDebugFlags.getFlags(flagsString.split(","));
            NPCDebugCommand.modifyFlags(context, npc, ref, flags, (oldFlags, argFlags) -> argFlags, store);
        }
    }

    public static class ToggleCommand
    extends NPCMultiSelectCommandBase {
        @Nonnull
        private final RequiredArg<String> flagsArg = this.withRequiredArg("flags", "server.commands.npc.debug.flags.desc", ArgTypes.STRING);

        public ToggleCommand() {
            super("toggle", "server.commands.npc.debug.toggle.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull NPCEntity npc, @Nonnull World world, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref) {
            String flagsString = (String)this.flagsArg.get(context);
            EnumSet<RoleDebugFlags> flags = RoleDebugFlags.getFlags(flagsString.split(","));
            NPCDebugCommand.modifyFlags(context, npc, ref, flags, (oldFlags, argFlags) -> {
                if (argFlags.isEmpty()) {
                    return null;
                }
                Object newFlags = oldFlags.clone();
                for (RoleDebugFlags flag : argFlags) {
                    if (((AbstractCollection)newFlags).contains(flag)) {
                        ((AbstractCollection)newFlags).remove(flag);
                        continue;
                    }
                    ((AbstractCollection)newFlags).add(flag);
                }
                return newFlags;
            }, store);
        }
    }

    public static class DefaultsCommand
    extends NPCMultiSelectCommandBase {
        public DefaultsCommand() {
            super("defaults", "server.commands.npc.debug.defaults.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull NPCEntity npc, @Nonnull World world, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref) {
            NPCDebugCommand.safeSetRoleDebugFlags(npc, ref, RoleDebugFlags.getPreset("default"), store);
        }
    }

    public static class ClearCommand
    extends NPCMultiSelectCommandBase {
        public ClearCommand() {
            super("clear", "server.commands.npc.debug.clear.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull NPCEntity npc, @Nonnull World world, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref) {
            NPCDebugCommand.safeSetRoleDebugFlags(npc, ref, EnumSet.noneOf(RoleDebugFlags.class), store);
        }
    }

    public static class PresetsCommand
    extends AbstractCommand {
        @Nonnull
        private final OptionalArg<String> presetArg = this.withOptionalArg("preset", "server.commands.npc.debug.presets.preset.desc", ArgTypes.STRING);

        public PresetsCommand() {
            super("presets", "server.commands.npc.debug.presets.desc");
        }

        @Override
        @Nullable
        protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
            if (this.presetArg.provided(context)) {
                String presetName = (String)this.presetArg.get(context);
                if (presetName.isEmpty() || !RoleDebugFlags.havePreset(presetName)) {
                    context.sendMessage(Message.translation("server.commands.errors.npc.unknown_debug_preset").param("preset", presetName));
                    return null;
                }
                EnumSet<RoleDebugFlags> flags = RoleDebugFlags.getPreset(presetName);
                String flagString = NPCDebugCommand.getListOfFlags(flags).toString();
                context.sendMessage(Message.translation("server.commands.npc.debug.preset.info").param("preset", presetName).param("flags", !flagString.isEmpty() ? flagString : "<None>"));
                return null;
            }
            String flags = RoleDebugFlags.getListOfAllFlags(new StringBuilder()).toString();
            String presets = RoleDebugFlags.getListOfAllPresets(new StringBuilder()).toString();
            Message message = Message.translation("server.commands.npc.debug.presets.info").param("flags", flags).param("presets", presets);
            context.sendMessage(message);
            return null;
        }
    }
}

