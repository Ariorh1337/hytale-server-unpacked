package com.hypixel.hytale.builtin.hytalegenerator.assets.scanners;

import com.hypixel.hytale.builtin.hytalegenerator.assets.framework.DecimalConstantsFrameworkAsset;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.EmptyScanner;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.deprecated.ColumnLinearScanner;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class ColumnLinearScannerAsset extends ScannerAsset {
   @Nonnull
   public static final BuilderCodec<ColumnLinearScannerAsset> CODEC = BuilderCodec.builder(
         ColumnLinearScannerAsset.class, ColumnLinearScannerAsset::new, ScannerAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("MinY", Codec.INTEGER, true), (asset, value) -> asset.minY = value, asset -> asset.minY)
      .add()
      .append(new KeyedCodec<>("MaxY", Codec.INTEGER, true), (asset, value) -> asset.maxY = value, asset -> asset.maxY)
      .add()
      .<Integer>append(new KeyedCodec<>("ResultCap", Codec.INTEGER, true), (asset, value) -> asset.resultCap = value, asset -> asset.resultCap)
      .addValidator(Validators.greaterThanOrEqual(0))
      .add()
      .append(new KeyedCodec<>("TopDownOrder", Codec.BOOLEAN, false), (asset, value) -> asset.topDownOrder = value, asset -> asset.topDownOrder)
      .add()
      .append(
         new KeyedCodec<>("RelativeToPosition", Codec.BOOLEAN, false),
         (asset, value) -> asset.isRelativeToPosition = value,
         asset -> asset.isRelativeToPosition
      )
      .add()
      .append(new KeyedCodec<>("BaseHeightName", Codec.STRING, false), (asset, value) -> asset.baseHeightName = value, asset -> asset.baseHeightName)
      .add()
      .build();
   private int minY = 0;
   private int maxY = 1;
   private int resultCap = 1;
   private boolean topDownOrder = true;
   private boolean isRelativeToPosition = false;
   private String baseHeightName = "";

   @Nonnull
   @Override
   public Scanner build(@Nonnull ScannerAsset.Argument argument) {
      if (super.skip()) {
         return EmptyScanner.INSTANCE;
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
