/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.assets.patterns.PatternAsset;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import javax.annotation.Nonnull;

public class ImportedPatternAsset
extends PatternAsset {
    public static final BuilderCodec<ImportedPatternAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ImportedPatternAsset.class, ImportedPatternAsset::new, PatternAsset.ABSTRACT_CODEC).append(new KeyedCodec<String>("Name", Codec.STRING, true), (t, k) -> {
        t.name = k;
    }, k -> k.name).add()).build();
    private String name = "";

    @Override
    public Pattern build(@Nonnull PatternAsset.Argument argument) {
        if (super.isSkipped()) {
            return Pattern.noPattern();
        }
        if (this.name == null || this.name.isEmpty()) {
            ((HytaleLogger.Api)HytaleLogger.getLogger().atWarning()).log("An exported Pattern with the name does not exist: " + this.name);
            return Pattern.noPattern();
        }
        PatternAsset exportedAsset = PatternAsset.getExportedAsset(this.name);
        if (exportedAsset == null) {
            return Pattern.noPattern();
        }
        return exportedAsset.build(argument);
    }
}

