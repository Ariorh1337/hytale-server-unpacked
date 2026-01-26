/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.scanners;

import com.hypixel.hytale.builtin.hytalegenerator.assets.scanners.ScannerAsset;
import com.hypixel.hytale.builtin.hytalegenerator.framework.interfaces.functions.BiDouble2DoubleFunction;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.BaseHeightReference;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.ColumnLinearScanner;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.logger.HytaleLogger;
import javax.annotation.Nonnull;

public class ColumnLinearScannerAsset
extends ScannerAsset {
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
        t.baseHeight = k;
    }, k -> k.baseHeight).add()).build();
    private int minY = 0;
    private int maxY = 1;
    private int resultCap = 1;
    private boolean topDownOrder = true;
    private boolean isRelativeToPosition = false;
    private String baseHeight = "";

    @Override
    @Nonnull
    public Scanner build(@Nonnull ScannerAsset.Argument argument) {
        if (super.skip()) {
            return Scanner.noScanner();
        }
        if (this.isRelativeToPosition) {
            return new ColumnLinearScanner(this.minY, this.maxY, this.resultCap, this.topDownOrder, true, null);
        }
        BaseHeightReference heightDataLayer = argument.referenceBundle.getLayerWithName(this.baseHeight, BaseHeightReference.class);
        if (heightDataLayer == null) {
            ((HytaleLogger.Api)HytaleLogger.getLogger().atConfig()).log("Couldn't find height data layer with name \"" + this.baseHeight + "\", defaulting to not using a bed.");
            return new ColumnLinearScanner(this.minY, this.maxY, this.resultCap, this.topDownOrder, false, null);
        }
        BiDouble2DoubleFunction baseHeightFunction = heightDataLayer.getHeightFunction();
        return new ColumnLinearScanner(this.minY, this.maxY, this.resultCap, this.topDownOrder, false, baseHeightFunction);
    }
}

