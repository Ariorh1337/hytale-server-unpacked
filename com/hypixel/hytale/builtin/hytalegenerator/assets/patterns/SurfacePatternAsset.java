/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.assets.patterns.ConstantPatternAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.patterns.PatternAsset;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.AndPattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.OrPattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.SurfacePattern;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import java.util.ArrayList;
import javax.annotation.Nonnull;

public class SurfacePatternAsset
extends PatternAsset {
    public static final BuilderCodec<SurfacePatternAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SurfacePatternAsset.class, SurfacePatternAsset::new, PatternAsset.ABSTRACT_CODEC).append(new KeyedCodec("Surface", PatternAsset.CODEC, true), (t, k) -> {
        t.surface = k;
    }, k -> k.surface).add()).append(new KeyedCodec("Medium", PatternAsset.CODEC, true), (t, k) -> {
        t.origin = k;
    }, k -> k.origin).add()).append(new KeyedCodec<Double>("SurfaceRadius", Codec.DOUBLE, false), (t, k) -> {
        t.surfaceRadius = k;
    }, k -> k.surfaceRadius).addValidator(Validators.greaterThanOrEqual(0.0)).add()).append(new KeyedCodec<Double>("MediumRadius", Codec.DOUBLE, false), (t, k) -> {
        t.originRadius = k;
    }, k -> k.originRadius).addValidator(Validators.greaterThanOrEqual(0.0)).add()).append(new KeyedCodec<Integer>("SurfaceGap", Codec.INTEGER, false), (t, k) -> {
        t.surfaceGap = k;
    }, k -> k.surfaceGap).addValidator(Validators.greaterThanOrEqual(0)).add()).append(new KeyedCodec<Integer>("MediumGap", Codec.INTEGER, false), (t, k) -> {
        t.originGap = k;
    }, k -> k.originGap).addValidator(Validators.greaterThanOrEqual(0)).add()).append(new KeyedCodec<Boolean>("RequireAllFacings", Codec.BOOLEAN, false), (t, k) -> {
        t.requireAllFacings = k;
    }, k -> k.requireAllFacings).add()).append(new KeyedCodec<T[]>("Facings", new ArrayCodec<SurfacePattern.Facing>(SurfacePattern.Facing.CODEC, SurfacePattern.Facing[]::new), true), (t, k) -> {
        t.facings = k;
    }, k -> k.facings).add()).build();
    private PatternAsset surface = new ConstantPatternAsset();
    private PatternAsset origin = new ConstantPatternAsset();
    private double surfaceRadius = 0.0;
    private double originRadius = 0.0;
    private int surfaceGap = 0;
    private int originGap = 0;
    private SurfacePattern.Facing[] facings = new SurfacePattern.Facing[0];
    private boolean requireAllFacings = false;

    @Override
    @Nonnull
    public Pattern build(@Nonnull PatternAsset.Argument argument) {
        if (super.isSkipped()) {
            return Pattern.noPattern();
        }
        Pattern floorPattern = this.surface.build(argument);
        Pattern originPattern = this.origin.build(argument);
        ArrayList<Pattern> patterns = new ArrayList<Pattern>(this.facings.length);
        for (SurfacePattern.Facing s : this.facings) {
            SurfacePattern pattern = new SurfacePattern(floorPattern, originPattern, this.surfaceRadius, this.originRadius, s, this.surfaceGap, this.originGap);
            patterns.add(pattern);
        }
        if (this.requireAllFacings) {
            return new AndPattern(patterns);
        }
        return new OrPattern(patterns);
    }

    @Override
    public void cleanUp() {
        this.surface.cleanUp();
        this.origin.cleanUp();
    }
}

