/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential;

import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.BrushConfig;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.BrushConfigCommandExecutor;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.BrushConfigEditStore;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.system.SequenceBrushOperation;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.BuilderTool;
import com.hypixel.hytale.server.core.codec.LayerEntryCodec;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockPattern;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LayerOperation
extends SequenceBrushOperation {
    public static final BuilderCodec<LayerOperation> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(LayerOperation.class, LayerOperation::new).append(new KeyedCodec<T[]>("Layers", new ArrayCodec<LayerEntryCodec>(LayerEntryCodec.CODEC, LayerEntryCodec[]::new)), (op, val) -> {
        op.layerArgs = val != null ? new ArrayList<LayerEntryCodec>(Arrays.asList(val)) : List.of();
    }, op -> op.layerArgs != null ? op.layerArgs.toArray(new LayerEntryCodec[0]) : new LayerEntryCodec[]{}).documentation("The layers to set").add()).documentation("Replace blocks according to the specified layers in terms of their depth from the nearest air block")).build();
    private List<LayerEntryCodec> layerArgs = new ArrayList<LayerEntryCodec>();

    public LayerOperation() {
        super("Layer", "Replace blocks according to the specified layers in terms of their depth from the nearest air block", true);
    }

    @Override
    public boolean modifyBlocks(Ref<EntityStore> ref, BrushConfig brushConfig, BrushConfigCommandExecutor brushConfigCommandExecutor, @Nonnull BrushConfigEditStore edit, int x, int y, int z, ComponentAccessor<EntityStore> componentAccessor) {
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
        assert (playerRefComponent != null);
        BuilderToolsPlugin.BuilderState builderState = BuilderToolsPlugin.getState(playerComponent, playerRefComponent);
        if (edit.getBlock(x, y, z) <= 0) {
            return true;
        }
        Map<String, Object> toolArgs = this.getToolArgs(ref, componentAccessor);
        ArrayList<Pair<Integer, String>> layers = new ArrayList<Pair<Integer, String>>();
        int maxDepth = 0;
        for (LayerEntryCodec layer : this.layerArgs) {
            if (layer.isSkip()) continue;
            maxDepth += layer.getDepth().intValue();
            layers.add(Pair.of(layer.getDepth(), this.resolveBlockPattern(layer, toolArgs, brushConfig)));
        }
        WorldChunk chunk = edit.getAccessor().getChunk(ChunkUtil.indexChunkFromBlock(x, z));
        builderState.layer(x, y, z, layers, maxDepth, Vector3i.DOWN, chunk, edit.getBefore(), edit.getAfter());
        return true;
    }

    private String resolveBlockPattern(LayerEntryCodec entry, @Nullable Map<String, Object> toolArgs, BrushConfig brushConfig) {
        if (entry.isUseToolArg()) {
            if (toolArgs == null || !toolArgs.containsKey(entry.getMaterial())) {
                brushConfig.setErrorFlag("Layer: Tool arg '" + entry.getMaterial() + "' not found");
                return "";
            }
            Object argValue = toolArgs.get(entry.getMaterial());
            if (argValue instanceof BlockPattern) {
                BlockPattern blockPattern = (BlockPattern)argValue;
                return blockPattern.toString();
            }
            brushConfig.setErrorFlag("Layer: Tool arg '" + entry.getMaterial() + "' is not a Block type");
            return "";
        }
        return entry.getMaterial();
    }

    @Nullable
    private Map<String, Object> getToolArgs(Ref<EntityStore> ref, ComponentAccessor<EntityStore> componentAccessor) {
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        if (playerComponent == null) {
            return null;
        }
        BuilderTool builderTool = BuilderTool.getActiveBuilderTool(playerComponent);
        if (builderTool == null) {
            return null;
        }
        ItemStack itemStack = playerComponent.getInventory().getItemInHand();
        if (itemStack == null) {
            return null;
        }
        BuilderTool.ArgData argData = builderTool.getItemArgData(itemStack);
        return argData.tool();
    }

    @Override
    public void modifyBrushConfig(@Nonnull Ref<EntityStore> ref, @Nonnull BrushConfig brushConfig, @Nonnull BrushConfigCommandExecutor brushConfigCommandExecutor, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
    }
}

