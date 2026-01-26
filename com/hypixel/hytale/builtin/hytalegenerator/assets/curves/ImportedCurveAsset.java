/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.curves;

import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.CurveAsset;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;

public class ImportedCurveAsset
extends CurveAsset {
    public static final BuilderCodec<ImportedCurveAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ImportedCurveAsset.class, ImportedCurveAsset::new, CurveAsset.ABSTRACT_CODEC).append(new KeyedCodec<String>("Name", Codec.STRING, true), (t, k) -> {
        t.name = k;
    }, k -> k.name).add()).build();
    private String name;

    @Override
    public Double2DoubleFunction build() {
        if (this.name == null || this.name.isEmpty()) {
            ((HytaleLogger.Api)HytaleLogger.getLogger().atWarning()).log("An exported Curve with the name does not exist: " + this.name);
            return in -> 0.0;
        }
        CurveAsset exportedAsset = CurveAsset.getExportedAsset(this.name);
        if (exportedAsset == null) {
            return in -> 0.0;
        }
        return exportedAsset.build();
    }

    @Override
    public void cleanUp() {
    }
}

