/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.scanners;

import com.hypixel.hytale.builtin.hytalegenerator.assets.scanners.OriginScannerAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.scanners.ScannerAsset;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.AreaScanner;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class AreaScannerAsset
extends ScannerAsset {
    public static final BuilderCodec<AreaScannerAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(AreaScannerAsset.class, AreaScannerAsset::new, ScannerAsset.ABSTRACT_CODEC).append(new KeyedCodec<Integer>("ResultCap", Codec.INTEGER, true), (t, k) -> {
        t.resultCap = k;
    }, k -> k.resultCap).addValidator(Validators.greaterThanOrEqual(0)).add()).append(new KeyedCodec<AreaScanner.ScanShape>("ScanShape", AreaScanner.ScanShape.CODEC, false), (t, k) -> {
        t.scanShape = k;
    }, t -> t.scanShape).add()).append(new KeyedCodec<Integer>("ScanRange", Codec.INTEGER, false), (t, k) -> {
        t.scanRange = k;
    }, t -> t.scanRange).addValidator(Validators.greaterThan(-1)).add()).append(new KeyedCodec("ChildScanner", ScannerAsset.CODEC, false), (t, k) -> {
        t.childScannerAsset = k;
    }, t -> t.childScannerAsset).add()).build();
    private int resultCap = 1;
    private AreaScanner.ScanShape scanShape = AreaScanner.ScanShape.CIRCLE;
    private int scanRange = 0;
    private ScannerAsset childScannerAsset = new OriginScannerAsset();

    @Override
    @Nonnull
    public Scanner build(@Nonnull ScannerAsset.Argument argument) {
        if (super.skip() || this.childScannerAsset == null) {
            return Scanner.noScanner();
        }
        return new AreaScanner(this.resultCap, this.scanShape, this.scanRange, this.childScannerAsset.build(argument));
    }

    @Override
    public void cleanUp() {
        this.childScannerAsset.cleanUp();
    }
}

