/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class ExportedDensityAsset
extends DensityAsset {
    public static final BuilderCodec<ExportedDensityAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ExportedDensityAsset.class, ExportedDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<Boolean>("SingleInstance", Codec.BOOLEAN, false), (asset, value) -> {
        asset.singleInstance = value;
    }, asset -> asset.singleInstance).add()).build();
    private boolean singleInstance = false;

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped() || this.inputs().length == 0) {
            return new ConstantValueDensity(0.0);
        }
        DensityAsset.Exported exported = ExportedDensityAsset.getExportedAsset(this.exportName);
        if (exported == null) {
            LoggerUtil.getLogger().severe("Couldn't find Density asset exported with name: '" + this.exportName + "'. This could indicate a defect in the HytaleGenerator assets.");
            return this.firstInput().build(argument);
        }
        if (exported.singleInstance) {
            if (exported.builtInstance == null) {
                exported.builtInstance = this.firstInput().build(argument);
            }
            return exported.builtInstance;
        }
        return this.firstInput().build(argument);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
        DensityAsset.Exported exported = ExportedDensityAsset.getExportedAsset(this.exportName);
        if (exported == null) {
            return;
        }
        exported.builtInstance = null;
        for (DensityAsset input : this.inputs()) {
            input.cleanUp();
        }
    }

    public boolean isSingleInstance() {
        return this.singleInstance;
    }
}

