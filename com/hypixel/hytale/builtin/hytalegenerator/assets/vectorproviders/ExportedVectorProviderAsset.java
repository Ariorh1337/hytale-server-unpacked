/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders.ConstantVectorProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders.VectorProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.ConstantVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class ExportedVectorProviderAsset
extends VectorProviderAsset {
    @Nonnull
    public static final BuilderCodec<ExportedVectorProviderAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ExportedVectorProviderAsset.class, ExportedVectorProviderAsset::new, VectorProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec<Boolean>("SingleInstance", Codec.BOOLEAN, true), (asset, value) -> {
        asset.singleInstance = value;
    }, asset -> asset.singleInstance).add()).append(new KeyedCodec("VectorProvider", VectorProviderAsset.CODEC, true), (asset, value) -> {
        asset.vectorProviderAsset = value;
    }, value -> value.vectorProviderAsset).add()).build();
    private boolean singleInstance = false;
    private VectorProviderAsset vectorProviderAsset = new ConstantVectorProviderAsset();

    @Override
    public VectorProvider build(@Nonnull VectorProviderAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantVectorProvider(new Vector3d());
        }
        VectorProviderAsset.Exported exported = ExportedVectorProviderAsset.getExportedAsset(this.exportName);
        if (exported == null) {
            LoggerUtil.getLogger().warning("Couldn't find VectorProvider asset exported with name: '" + this.exportName + "'. Using empty Node instead.");
            return new ConstantVectorProvider(new Vector3d());
        }
        if (exported.isSingleInstance) {
            VectorProvider builtInstance = exported.threadInstances.get(argument.workerId);
            if (builtInstance == null) {
                builtInstance = this.vectorProviderAsset.build(argument);
                exported.threadInstances.put(argument.workerId, builtInstance);
            }
            return builtInstance;
        }
        return this.vectorProviderAsset.build(argument);
    }

    @Override
    public void cleanUp() {
        VectorProviderAsset.Exported exported = ExportedVectorProviderAsset.getExportedAsset(this.exportName);
        if (exported == null) {
            return;
        }
        exported.threadInstances.clear();
        this.vectorProviderAsset.cleanUp();
    }

    public boolean isSingleInstance() {
        return this.singleInstance;
    }
}

