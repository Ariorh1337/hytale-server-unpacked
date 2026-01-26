/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.scanners;

import com.hypixel.hytale.builtin.hytalegenerator.assets.scanners.ScannerAsset;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.OriginScanner;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class OriginScannerAsset
extends ScannerAsset {
    public static final BuilderCodec<OriginScannerAsset> CODEC = BuilderCodec.builder(OriginScannerAsset.class, OriginScannerAsset::new, ScannerAsset.ABSTRACT_CODEC).build();

    @Override
    @Nonnull
    public Scanner build(@Nonnull ScannerAsset.Argument argument) {
        if (super.skip()) {
            return Scanner.noScanner();
        }
        return OriginScanner.getInstance();
    }
}

