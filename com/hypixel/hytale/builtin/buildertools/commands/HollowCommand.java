/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.commands;

import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.PrototypePlayerBuilderToolSettings;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class HollowCommand
extends AbstractPlayerCommand {
    @Nonnull
    private final DefaultArg<String> blockTypeArg = this.withDefaultArg("blockType", "server.commands.hollow.blockType.desc", ArgTypes.BLOCK_TYPE_KEY, "Empty", "Air");
    @Nonnull
    private final DefaultArg<Integer> thicknessArg = (DefaultArg)this.withDefaultArg("thickness", "server.commands.hollow.thickness.desc", ArgTypes.INTEGER, Integer.valueOf(1), "Thickness of 1").addValidator(Validators.range(1, 128));
    @Nonnull
    private final FlagArg floorArg = (FlagArg)this.withFlagArg("floor", "server.commands.hollow.floor.desc").addAliases("bottom");
    @Nonnull
    private final FlagArg roofArg = (FlagArg)this.withFlagArg("roof", "server.commands.hollow.roof.desc").addAliases("ceiling", "top");
    @Nonnull
    private final FlagArg perimeterArg = (FlagArg)this.withFlagArg("perimeter", "server.commands.hollow.perimeter.desc").addAliases("all");

    public HollowCommand() {
        super("hollow", "server.commands.hollow.desc");
        this.setPermissionGroup(GameMode.Creative);
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        if (!PrototypePlayerBuilderToolSettings.isOkayToDoCommandsOnSelection(ref, playerComponent, store)) {
            return;
        }
        int blockTypeIndex = BlockType.getAssetMap().getIndex((String)this.blockTypeArg.get(context));
        Boolean floor = (Boolean)this.floorArg.get(context);
        Boolean roof = (Boolean)this.roofArg.get(context);
        Boolean perimeter = (Boolean)this.perimeterArg.get(context);
        BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.hollow((Ref<EntityStore>)r, blockTypeIndex, (Integer)this.thicknessArg.get(context), roof != false || perimeter != false, floor != false || perimeter != false, (ComponentAccessor<EntityStore>)componentAccessor));
    }
}

