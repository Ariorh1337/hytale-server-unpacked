/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.props;

import com.hypixel.hytale.builtin.hytalegenerator.assets.props.PropAsset;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import javax.annotation.Nonnull;

public class ImportedPropAsset
extends PropAsset {
    public static final BuilderCodec<ImportedPropAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ImportedPropAsset.class, ImportedPropAsset::new, PropAsset.ABSTRACT_CODEC).append(new KeyedCodec<String>("Name", Codec.STRING, true), (asset, v) -> {
        asset.name = v;
    }, asset -> asset.name).add()).build();
    private String name = "";

    @Override
    public Prop build(@Nonnull PropAsset.Argument argument) {
        if (super.skip()) {
            return Prop.noProp();
        }
        if (this.name == null || this.name.isEmpty()) {
            ((HytaleLogger.Api)HytaleLogger.getLogger().atWarning()).log("An exported Pattern with the name does not exist: " + this.name);
            return Prop.noProp();
        }
        PropAsset exportedAsset = PropAsset.getExportedAsset(this.name);
        if (exportedAsset == null) {
            return Prop.noProp();
        }
        return exportedAsset.build(argument);
    }
}

