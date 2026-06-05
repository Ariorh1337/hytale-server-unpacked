package com.hypixel.hytale.builtin.hytalegenerator.assets.scanners;

import com.hypixel.hytale.builtin.hytalegenerator.scanners.EmptyScanner;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.deprecated.AreaScanner;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class AreaScannerAsset extends ScannerAsset {
   @Nonnull
   public static final BuilderCodec<AreaScannerAsset> CODEC = BuilderCodec.builder(AreaScannerAsset.class, AreaScannerAsset::new, ScannerAsset.ABSTRACT_CODEC)
      .append(new KeyedCodec<>("ResultCap", Codec.INTEGER, true), (asset, value) -> asset.resultCap = value, asset -> asset.resultCap)
      .addValidator(Validators.greaterThanOrEqual(0))
      .add()
      .append(new KeyedCodec<>("ScanShape", AreaScanner.ScanShape.CODEC, false), (asset, value) -> asset.scanShape = value, asset -> asset.scanShape)
      .add()
      .<Integer>append(new KeyedCodec<>("ScanRange", Codec.INTEGER, false), (asset, value) -> asset.scanRange = value, asset -> asset.scanRange)
      .addValidator(Validators.greaterThan(-1))
      .add()
      .append(new KeyedCodec<>("ChildScanner", ScannerAsset.CODEC, false), (asset, value) -> asset.childScannerAsset = value, asset -> asset.childScannerAsset)
      .add()
      .build();
   private int resultCap = 1;
   private AreaScanner.ScanShape scanShape = AreaScanner.ScanShape.CIRCLE;
   private int scanRange = 0;
   private ScannerAsset childScannerAsset = new DirectScannerAsset();

   @Nonnull
   @Override
   public Scanner build(@Nonnull ScannerAsset.Argument argument) {
      return !super.skip() && this.childScannerAsset != null
         ? new AreaScanner(this.resultCap, this.scanShape, this.scanRange, this.childScannerAsset.build(argument))
         : EmptyScanner.INSTANCE;
   }

   @Override
   public void cleanUp() {
      this.childScannerAsset.cleanUp();
   }
}
