/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.scanners;

import com.hypixel.hytale.builtin.hytalegenerator.assets.scanners.ScannerAsset;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import javax.annotation.Nonnull;

public class ImportedScannerAsset
extends ScannerAsset {
    public static final BuilderCodec<ImportedScannerAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ImportedScannerAsset.class, ImportedScannerAsset::new, ScannerAsset.ABSTRACT_CODEC).append(new KeyedCodec<String>("Name", Codec.STRING, false), (t, k) -> {
        t.name = k;
    }, k -> k.name).add()).build();
    private String name = "";

    @Override
    public Scanner build(@Nonnull ScannerAsset.Argument argument) {
        if (super.skip()) {
            return Scanner.noScanner();
        }
        if (this.name == null || this.name.isEmpty()) {
            ((HytaleLogger.Api)HytaleLogger.getLogger().atWarning()).log("An exported Pattern with the name does not exist: " + this.name);
            return Scanner.noScanner();
        }
        ScannerAsset exportedAsset = ScannerAsset.getExportedAsset(this.name);
        if (exportedAsset == null) {
            return Scanner.noScanner();
        }
        return exportedAsset.build(argument);
    }
}

