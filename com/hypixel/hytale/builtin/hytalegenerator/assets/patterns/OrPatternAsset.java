/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.assets.patterns.PatternAsset;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.OrPattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import javax.annotation.Nonnull;

public class OrPatternAsset
extends PatternAsset {
    public static final BuilderCodec<OrPatternAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(OrPatternAsset.class, OrPatternAsset::new, PatternAsset.ABSTRACT_CODEC).append(new KeyedCodec<T[]>("Patterns", new ArrayCodec(PatternAsset.CODEC, PatternAsset[]::new), true), (t, k) -> {
        t.patternAssets = k;
    }, k -> k.patternAssets).add()).build();
    private PatternAsset[] patternAssets = new PatternAsset[0];

    @Override
    @Nonnull
    public Pattern build(@Nonnull PatternAsset.Argument argument) {
        if (super.isSkipped()) {
            return Pattern.noPattern();
        }
        ArrayList<Pattern> patterns = new ArrayList<Pattern>(this.patternAssets.length);
        for (PatternAsset asset : this.patternAssets) {
            if (asset.isSkipped()) continue;
            patterns.add(asset.build(argument));
        }
        return new OrPattern(patterns);
    }

    @Override
    public void cleanUp() {
        for (PatternAsset patternAsset : this.patternAssets) {
            patternAsset.cleanUp();
        }
    }
}

