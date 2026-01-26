/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.saveandload;

import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.BrushConfig;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.BrushConfigCommandExecutor;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.system.SequenceBrushOperation;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;

public class LoadBrushConfigOperation
extends SequenceBrushOperation {
    public static final BuilderCodec<LoadBrushConfigOperation> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(LoadBrushConfigOperation.class, LoadBrushConfigOperation::new).append(new KeyedCodec<String>("StoredName", Codec.STRING), (op, val) -> {
        op.variableNameArg = val;
    }, op -> op.variableNameArg).documentation("The name to store the snapshot of this brush config under").add()).append(new KeyedCodec<T[]>("ParametersToLoad", new ArrayCodec<BrushConfig.DataSettingFlags>(new EnumCodec<BrushConfig.DataSettingFlags>(BrushConfig.DataSettingFlags.class), BrushConfig.DataSettingFlags[]::new)), (op, val) -> {
        op.dataSettingFlagArg = val != null ? Arrays.asList(val) : List.of();
    }, op -> op.dataSettingFlagArg.toArray(new BrushConfig.DataSettingFlags[0])).documentation("A list of the different parameters to load from the stored config").add()).documentation("Restore a saved brush config snapshot")).build();
    @Nonnull
    public String variableNameArg = "Undefined";
    @Nonnull
    public List<BrushConfig.DataSettingFlags> dataSettingFlagArg = List.of();

    public LoadBrushConfigOperation() {
        super("Load Brush Config Snapshot", "Restore a saved brush config snapshot", false);
    }

    @Override
    public void modifyBrushConfig(@Nonnull Ref<EntityStore> ref, @Nonnull BrushConfig brushConfig, @Nonnull BrushConfigCommandExecutor brushConfigCommandExecutor, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        brushConfigCommandExecutor.loadBrushConfigSnapshot(this.variableNameArg, (BrushConfig.DataSettingFlags[])this.dataSettingFlagArg.toArray(BrushConfig.DataSettingFlags[]::new));
    }
}

