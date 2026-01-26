/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.assets.patterns.ConstantPatternAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.patterns.PatternAsset;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.CuboidPattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3i;
import javax.annotation.Nonnull;

public class CuboidPatternAsset
extends PatternAsset {
    public static final BuilderCodec<CuboidPatternAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CuboidPatternAsset.class, CuboidPatternAsset::new, PatternAsset.ABSTRACT_CODEC).append(new KeyedCodec("SubPattern", PatternAsset.CODEC, true), (t, k) -> {
        t.subPatternAsset = k;
    }, k -> k.subPatternAsset).add()).append(new KeyedCodec<Vector3i>("Min", Vector3i.CODEC, true), (t, k) -> {
        t.min = k;
    }, k -> k.min).add()).append(new KeyedCodec<Vector3i>("Max", Vector3i.CODEC, true), (t, k) -> {
        t.max = k;
    }, k -> k.max).add()).build();
    private PatternAsset subPatternAsset = new ConstantPatternAsset();
    private Vector3i min = new Vector3i();
    private Vector3i max = new Vector3i();

    @Override
    @Nonnull
    public Pattern build(@Nonnull PatternAsset.Argument argument) {
        if (super.isSkipped()) {
            return Pattern.noPattern();
        }
        Pattern subPattern = this.subPatternAsset.build(argument);
        return new CuboidPattern(subPattern, this.min, this.max);
    }

    @Override
    public void cleanUp() {
        this.subPatternAsset.cleanUp();
    }
}

