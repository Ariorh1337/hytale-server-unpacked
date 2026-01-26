/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.commands;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.PrototypePlayerBuilderToolSettings;
import com.hypixel.hytale.builtin.buildertools.utils.Material;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockPattern;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReplaceCommand
extends AbstractPlayerCommand {
    @Nonnull
    private final RequiredArg<BlockPattern> toArg = this.withRequiredArg("to", "server.commands.replace.toBlock.desc", ArgTypes.BLOCK_PATTERN);
    @Nonnull
    private final FlagArg substringSwapFlag = this.withFlagArg("substringSwap", "server.commands.replace.substringSwap.desc");
    @Nonnull
    private final FlagArg regexFlag = this.withFlagArg("regex", "server.commands.replace.regex.desc");

    public ReplaceCommand() {
        super("replace", "server.commands.replace.desc");
        this.setPermissionGroup(GameMode.Creative);
        this.addUsageVariant(new ReplaceFromToCommand());
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        ReplaceCommand.executeReplace(context, store, ref, playerRef, null, (BlockPattern)this.toArg.get(context), (Boolean)this.substringSwapFlag.get(context), (Boolean)this.regexFlag.get(context));
    }

    private static void executeReplace(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nullable String fromValue, @Nonnull BlockPattern toPattern, boolean substringSwap, boolean regex) {
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        if (!PrototypePlayerBuilderToolSettings.isOkayToDoCommandsOnSelection(ref, playerComponent, store)) {
            return;
        }
        if (toPattern == null || toPattern.isEmpty()) {
            context.sendMessage(Message.translation("server.builderTools.invalidBlockType").param("name", "").param("key", ""));
            return;
        }
        String toValue = toPattern.toString();
        Material fromMaterial = fromValue != null ? Material.fromKey(fromValue) : null;
        Material toMaterial = Material.fromPattern(toPattern, ThreadLocalRandom.current());
        if (toMaterial.isFluid() && !substringSwap && !regex) {
            if (fromMaterial == null) {
                context.sendMessage(Message.translation("server.commands.replace.fromRequired"));
                return;
            }
            BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.replace((Ref<EntityStore>)r, fromMaterial, toMaterial, (ComponentAccessor<EntityStore>)componentAccessor));
            context.sendMessage(Message.translation("server.builderTools.replace.replacementBlockDone").param("from", fromValue).param("to", toValue));
            return;
        }
        if (fromMaterial != null && fromMaterial.isFluid()) {
            BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.replace((Ref<EntityStore>)r, fromMaterial, toMaterial, (ComponentAccessor<EntityStore>)componentAccessor));
            context.sendMessage(Message.translation("server.builderTools.replace.replacementBlockDone").param("from", fromValue).param("to", toValue));
            return;
        }
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        if (fromValue == null && !substringSwap && !regex) {
            BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.replace((Ref<EntityStore>)r, null, toPattern, (ComponentAccessor<EntityStore>)componentAccessor));
            context.sendMessage(Message.translation("server.builderTools.replace.replacementAllDone").param("to", toValue));
            return;
        }
        if (fromValue == null) {
            context.sendMessage(Message.translation("server.commands.replace.fromRequired"));
            return;
        }
        if (regex) {
            Pattern pattern;
            try {
                pattern = Pattern.compile(fromValue);
            }
            catch (PatternSyntaxException e) {
                context.sendMessage(Message.translation("server.commands.replace.invalidRegex").param("error", e.getMessage()));
                return;
            }
            BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> {
                s.replace((Ref<EntityStore>)r, value -> {
                    String valueKey = ((BlockType)assetMap.getAsset(value)).getId();
                    return pattern.matcher(valueKey).matches();
                }, toPattern, (ComponentAccessor<EntityStore>)componentAccessor);
                context.sendMessage(Message.translation("server.commands.replace.success").param("regex", fromValue).param("replacement", toValue));
            });
            return;
        }
        if (fromMaterial == null) {
            context.sendMessage(Message.translation("server.builderTools.invalidBlockType").param("name", fromValue).param("key", fromValue));
            return;
        }
        if (substringSwap) {
            String[] blockKeys = fromValue.split(",");
            Int2IntArrayMap swapMap = new Int2IntArrayMap();
            block4: for (int blockId = 0; blockId < assetMap.getAssetCount(); ++blockId) {
                BlockType blockType = assetMap.getAsset(blockId);
                String blockKeyStr = blockType.getId();
                for (String from : blockKeys) {
                    String replacedKey;
                    if (!blockKeyStr.contains(from.trim())) continue;
                    try {
                        replacedKey = blockKeyStr.replace(from.trim(), toValue);
                    }
                    catch (Exception e) {
                        continue;
                    }
                    int index = assetMap.getIndex(replacedKey);
                    if (index == Integer.MIN_VALUE) continue;
                    swapMap.put(blockId, index);
                    continue block4;
                }
            }
            if (!swapMap.isEmpty()) {
                BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.replace((Ref<EntityStore>)r, value -> swapMap.getOrDefault(value, value), (ComponentAccessor<EntityStore>)componentAccessor));
                context.sendMessage(Message.translation("server.builderTools.replace.replacementDone").param("nb", swapMap.size()).param("to", toValue));
            } else {
                context.sendMessage(Message.translation("server.commands.replace.noMatchingBlocks").param("blockType", fromValue));
            }
            return;
        }
        int fromBlockId = fromMaterial.getBlockId();
        BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.replace((Ref<EntityStore>)r, block -> block == fromBlockId, toPattern, (ComponentAccessor<EntityStore>)componentAccessor));
        context.sendMessage(Message.translation("server.builderTools.replace.replacementBlockDone").param("from", fromValue).param("to", toValue));
    }

    private static class ReplaceFromToCommand
    extends AbstractPlayerCommand {
        @Nonnull
        private final RequiredArg<String> fromArg = this.withRequiredArg("from", "server.commands.replace.from.desc", ArgTypes.STRING);
        @Nonnull
        private final RequiredArg<BlockPattern> toArg = this.withRequiredArg("to", "server.commands.replace.toBlock.desc", ArgTypes.BLOCK_PATTERN);
        @Nonnull
        private final FlagArg substringSwapFlag = this.withFlagArg("substringSwap", "server.commands.replace.substringSwap.desc");
        @Nonnull
        private final FlagArg regexFlag = this.withFlagArg("regex", "server.commands.replace.regex.desc");

        public ReplaceFromToCommand() {
            super("server.commands.replace.desc");
            this.setPermissionGroup(GameMode.Creative);
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            ReplaceCommand.executeReplace(context, store, ref, playerRef, (String)this.fromArg.get(context), (BlockPattern)this.toArg.get(context), (Boolean)this.substringSwapFlag.get(context), (Boolean)this.regexFlag.get(context));
        }
    }
}

