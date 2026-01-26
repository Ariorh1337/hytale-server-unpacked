/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.assets.patterns.ConstantPatternAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.patterns.PatternAsset;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.NotPattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class NotPatternAsset
extends PatternAsset {
    public static final BuilderCodec<NotPatternAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(NotPatternAsset.class, NotPatternAsset::new, PatternAsset.ABSTRACT_CODEC).append(new KeyedCodec("Pattern", PatternAsset.CODEC, true), (t, k) -> {
        t.patternAsset = k;
    }, k -> k.patternAsset).add()).build();
    private PatternAsset patternAsset = new ConstantPatternAsset();

    @Override
    @Nonnull
    public Pattern build(@Nonnull PatternAsset.Argument argument) {
        if (super.isSkipped()) {
            return Pattern.noPattern();
        }
        return new NotPattern(this.patternAsset.build(argument));
    }

    @Override
    public void cleanUp() {
        this.patternAsset.cleanUp();
    }
}

