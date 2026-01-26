/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.commands;

import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.PrefabCopyException;
import com.hypixel.hytale.builtin.buildertools.PrototypePlayerBuilderToolSettings;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TempAssetIdUtil;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CopyCommand
extends AbstractPlayerCommand {
    @Nonnull
    private static final Message MESSAGE_BUILDER_TOOLS_COPY_CUT_NO_SELECTION = Message.translation("server.builderTools.copycut.noSelection");
    @Nonnull
    private final FlagArg noEntitiesFlag = this.withFlagArg("noEntities", "server.commands.copy.noEntities.desc");
    @Nonnull
    private final FlagArg entitiesOnlyFlag = this.withFlagArg("onlyEntities", "server.commands.copy.entitiesonly.desc");
    @Nonnull
    private final FlagArg emptyFlag = this.withFlagArg("empty", "server.commands.copy.empty.desc");
    @Nonnull
    private final FlagArg keepAnchorsFlag = this.withFlagArg("keepanchors", "server.commands.copy.keepanchors.desc");
    @Nonnull
    private final FlagArg playerAnchorFlag = this.withFlagArg("playerAnchor", "server.commands.copy.playerAnchor.desc");

    public CopyCommand() {
        super("copy", "server.commands.copy.desc");
        this.setPermissionGroup(GameMode.Creative);
        this.requirePermission("hytale.editor.selection.clipboard");
        this.addUsageVariant(new CopyRegionCommand());
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        if (!PrototypePlayerBuilderToolSettings.isOkayToDoCommandsOnSelection(ref, playerComponent, store)) {
            return;
        }
        BuilderToolsPlugin.BuilderState builderState = BuilderToolsPlugin.getState(playerComponent, playerRef);
        boolean entitiesOnly = (Boolean)this.entitiesOnlyFlag.get(context);
        boolean noEntities = (Boolean)this.noEntitiesFlag.get(context);
        int settings = 0;
        if (!entitiesOnly) {
            settings |= 8;
        }
        if (((Boolean)this.emptyFlag.get(context)).booleanValue()) {
            settings |= 4;
        }
        if (((Boolean)this.keepAnchorsFlag.get(context)).booleanValue()) {
            settings |= 0x40;
        }
        if (!noEntities || entitiesOnly) {
            settings |= 0x10;
        }
        int settingsFinal = settings;
        Vector3i playerAnchor = CopyCommand.getPlayerAnchor(ref, store, (Boolean)this.playerAnchorFlag.get(context));
        BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> {
            try {
                BlockSelection selection = builderState.getSelection();
                if (selection == null || !selection.hasSelectionBounds()) {
                    context.sendMessage(MESSAGE_BUILDER_TOOLS_COPY_CUT_NO_SELECTION);
                    return;
                }
                Vector3i min = selection.getSelectionMin();
                Vector3i max = selection.getSelectionMax();
                builderState.copyOrCut((Ref<EntityStore>)r, min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ(), settingsFinal, playerAnchor, (ComponentAccessor<EntityStore>)componentAccessor);
            }
            catch (PrefabCopyException e) {
                context.sendMessage(Message.translation("server.builderTools.copycut.copyFailedReason").param("reason", e.getMessage()));
            }
        });
    }

    @Nullable
    private static Vector3i getPlayerAnchor(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, boolean usePlayerAnchor) {
        if (!usePlayerAnchor) {
            return null;
        }
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null) {
            return null;
        }
        Vector3d position = transformComponent.getPosition();
        return new Vector3i(MathUtil.floor(position.getX()), MathUtil.floor(position.getY()), MathUtil.floor(position.getZ()));
    }

    public static void copySelection(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
        assert (playerRefComponent != null);
        CopyCommand.copySelection(ref, componentAccessor, BuilderToolsPlugin.getState(playerComponent, playerRefComponent), 24);
    }

    public static void copySelection(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull BuilderToolsPlugin.BuilderState builderState, int settings) {
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
        assert (playerRefComponent != null);
        BuilderToolsPlugin.addToQueue(playerComponent, playerRefComponent, (r, s, c) -> {
            try {
                BlockSelection selection = builderState.getSelection();
                if (selection == null || !selection.hasSelectionBounds()) {
                    playerComponent.sendMessage(MESSAGE_BUILDER_TOOLS_COPY_CUT_NO_SELECTION);
                    return;
                }
                Vector3i min = selection.getSelectionMin();
                Vector3i max = selection.getSelectionMax();
                builderState.copyOrCut((Ref<EntityStore>)r, min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ(), settings, (ComponentAccessor<EntityStore>)c);
            }
            catch (PrefabCopyException e) {
                playerComponent.sendMessage(Message.translation("server.builderTools.copycut.copyFailedReason").param("reason", e.getMessage()));
            }
        });
    }

    private static class CopyRegionCommand
    extends AbstractPlayerCommand {
        @Nonnull
        private final RequiredArg<Integer> xMinArg = this.withRequiredArg("xMin", "server.commands.copy.xMin.desc", ArgTypes.INTEGER);
        @Nonnull
        private final RequiredArg<Integer> yMinArg = this.withRequiredArg("yMin", "server.commands.copy.yMin.desc", ArgTypes.INTEGER);
        @Nonnull
        private final RequiredArg<Integer> zMinArg = this.withRequiredArg("zMin", "server.commands.copy.zMin.desc", ArgTypes.INTEGER);
        @Nonnull
        private final RequiredArg<Integer> xMaxArg = this.withRequiredArg("xMax", "server.commands.copy.xMax.desc", ArgTypes.INTEGER);
        @Nonnull
        private final RequiredArg<Integer> yMaxArg = this.withRequiredArg("yMax", "server.commands.copy.yMax.desc", ArgTypes.INTEGER);
        @Nonnull
        private final RequiredArg<Integer> zMaxArg = this.withRequiredArg("zMax", "server.commands.copy.zMax.desc", ArgTypes.INTEGER);
        @Nonnull
        private final FlagArg noEntitiesFlag = this.withFlagArg("noEntities", "server.commands.copy.noEntities.desc");
        @Nonnull
        private final FlagArg entitiesOnlyFlag = this.withFlagArg("onlyEntities", "server.commands.copy.entitiesonly.desc");
        @Nonnull
        private final FlagArg emptyFlag = this.withFlagArg("empty", "server.commands.copy.empty.desc");
        @Nonnull
        private final FlagArg keepAnchorsFlag = this.withFlagArg("keepanchors", "server.commands.copy.keepanchors.desc");
        @Nonnull
        private final FlagArg playerAnchorFlag = this.withFlagArg("playerAnchor", "server.commands.copy.playerAnchor.desc");

        public CopyRegionCommand() {
            super("server.commands.copy.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            if (!PrototypePlayerBuilderToolSettings.isOkayToDoCommandsOnSelection(ref, playerComponent, store)) {
                return;
            }
            BuilderToolsPlugin.BuilderState builderState = BuilderToolsPlugin.getState(playerComponent, playerRef);
            boolean entitiesOnly = (Boolean)this.entitiesOnlyFlag.get(context);
            boolean noEntities = (Boolean)this.noEntitiesFlag.get(context);
            int settings = 0;
            if (!entitiesOnly) {
                settings |= 8;
            }
            if (((Boolean)this.emptyFlag.get(context)).booleanValue()) {
                settings |= 4;
            }
            if (((Boolean)this.keepAnchorsFlag.get(context)).booleanValue()) {
                settings |= 0x40;
            }
            if (!noEntities || entitiesOnly) {
                settings |= 0x10;
            }
            int xMin = (Integer)this.xMinArg.get(context);
            int yMin = (Integer)this.yMinArg.get(context);
            int zMin = (Integer)this.zMinArg.get(context);
            int xMax = (Integer)this.xMaxArg.get(context);
            int yMax = (Integer)this.yMaxArg.get(context);
            int zMax = (Integer)this.zMaxArg.get(context);
            int copySettings = settings;
            Vector3i playerAnchor = CopyCommand.getPlayerAnchor(ref, store, (Boolean)this.playerAnchorFlag.get(context));
            BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> {
                try {
                    builderState.copyOrCut((Ref<EntityStore>)r, xMin, yMin, zMin, xMax, yMax, zMax, copySettings, playerAnchor, (ComponentAccessor<EntityStore>)componentAccessor);
                }
                catch (PrefabCopyException e) {
                    context.sendMessage(Message.translation("server.builderTools.copycut.copyFailedReason").param("reason", e.getMessage()));
                    SoundUtil.playSoundEvent2d(r, TempAssetIdUtil.getSoundEventIndex("CREATE_ERROR"), SoundCategory.UI, componentAccessor);
                }
            });
        }
    }
}

