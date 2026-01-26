/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.assets.patterns.ConstantPatternAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.patterns.PatternAsset;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.WallPattern;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.List;
import javax.annotation.Nonnull;

public class WallPatternAsset
extends PatternAsset {
    public static final BuilderCodec<WallPatternAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(WallPatternAsset.class, WallPatternAsset::new, PatternAsset.ABSTRACT_CODEC).append(new KeyedCodec("Wall", PatternAsset.CODEC, true), (t, k) -> {
        t.wall = k;
    }, k -> k.wall).add()).append(new KeyedCodec("Origin", PatternAsset.CODEC, true), (t, k) -> {
        t.origin = k;
    }, k -> k.origin).add()).append(new KeyedCodec<Boolean>("RequireAllDirections", Codec.BOOLEAN, false), (t, k) -> {
        t.matchAll = k;
    }, k -> k.matchAll).add()).append(new KeyedCodec<T[]>("Directions", new ArrayCodec<WallPattern.WallDirection>(WallPattern.WallDirection.CODEC, WallPattern.WallDirection[]::new), true), (t, k) -> {
        t.directions = k;
    }, k -> k.directions).add()).build();
    private PatternAsset wall = new ConstantPatternAsset();
    private PatternAsset origin = new ConstantPatternAsset();
    private WallPattern.WallDirection[] directions = new WallPattern.WallDirection[0];
    private boolean matchAll = false;

    @Override
    @Nonnull
    public Pattern build(@Nonnull PatternAsset.Argument argument) {
        if (super.isSkipped()) {
            return Pattern.noPattern();
        }
        Pattern wallPattern = this.wall.build(argument);
        Pattern originPattern = this.origin.build(argument);
        return new WallPattern(wallPattern, originPattern, List.of(this.directions), this.matchAll);
    }

    @Override
    public void cleanUp() {
        this.wall.cleanUp();
        this.origin.cleanUp();
    }
}

