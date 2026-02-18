/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.scanners;

import com.hypixel.hytale.builtin.hytalegenerator.assets.framework.DecimalConstantsFrameworkAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.scanners.ScannerAsset;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.ColumnLinearScanner;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class ColumnLinearScannerAsset
extends ScannerAsset {
    @Nonnull
    public static final BuilderCodec<ColumnLinearScannerAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ColumnLinearScannerAsset.class, ColumnLinearScannerAsset::new, ScannerAsset.ABSTRACT_CODEC).append(new KeyedCodec<Integer>("MinY", Codec.INTEGER, true), (t, k) -> {
        t.minY = k;
    }, k -> k.minY).add()).append(new KeyedCodec<Integer>("MaxY", Codec.INTEGER, true), (t, k) -> {
        t.maxY = k;
    }, k -> k.maxY).add()).append(new KeyedCodec<Integer>("ResultCap", Codec.INTEGER, true), (t, k) -> {
        t.resultCap = k;
    }, k -> k.resultCap).addValidator(Validators.greaterThanOrEqual(0)).add()).append(new KeyedCodec<Boolean>("TopDownOrder", Codec.BOOLEAN, false), (t, k) -> {
        t.topDownOrder = k;
    }, k -> k.topDownOrder).add()).append(new KeyedCodec<Boolean>("RelativeToPosition", Codec.BOOLEAN, false), (t, k) -> {
        t.isRelativeToPosition = k;
    }, k -> k.isRelativeToPosition).add()).append(new KeyedCodec<String>("BaseHeightName", Codec.STRING, false), (t, k) -> {
        t.baseHeightName = k;
    }, k -> k.baseHeightName).add()).build();
    private int minY = 0;
    private int maxY = 1;
    private int resultCap = 1;
    private boolean topDownOrder = true;
    private boolean isRelativeToPosition = false;
    private String baseHeightName = "";

    @Override
    @Nonnull
    public Scanner build(@Nonnull ScannerAsset.Argument argument) {
        if (super.skip()) {
            return Scanner.noScanner();
        }
        if (this.isRelativeToPosition) {
            return new ColumnLinearScanner(this.minY, this.maxY, this.resultCap, this.topDownOrder, true, 0.0);
        }
        Double baseHeight = DecimalConstantsFrameworkAsset.Entries.get(this.baseHeightName, argument.referenceBundle);
        if (baseHeight == null) {
            baseHeight = 0.0;
        }
        return new ColumnLinearScanner(this.minY, this.maxY, this.resultCap, this.topDownOrder, false, baseHeight);
    }
}

