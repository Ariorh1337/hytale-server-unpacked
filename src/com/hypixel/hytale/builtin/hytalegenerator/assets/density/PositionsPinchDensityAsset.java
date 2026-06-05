package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.ConstantCurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.CurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.ListPositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.PositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.PositionsHorizontalPinchDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.PositionsPinchDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class PositionsPinchDensityAsset extends DensityAsset {
   @Nonnull
   public static final BuilderCodec<PositionsPinchDensityAsset> CODEC = BuilderCodec.builder(
         PositionsPinchDensityAsset.class, PositionsPinchDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("Positions", PositionProviderAsset.CODEC, true),
         (asset, value) -> asset.positionProviderAsset = value,
         asset -> asset.positionProviderAsset
      )
      .add()
      .append(new KeyedCodec<>("PinchCurve", CurveAsset.CODEC, true), (asset, value) -> asset.pinchCurveAsset = value, asset -> asset.pinchCurveAsset)
      .add()
      .<Double>append(new KeyedCodec<>("MaxDistance", Codec.DOUBLE, true), (asset, value) -> asset.maxDistance = value, asset -> asset.maxDistance)
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .append(new KeyedCodec<>("NormalizeDistance", Codec.BOOLEAN, true), (asset, value) -> asset.normalizeDistance = value, asset -> asset.normalizeDistance)
      .add()
      .append(new KeyedCodec<>("HorizontalPinch", Codec.BOOLEAN, false), (asset, value) -> asset.isHorizontal = value, asset -> asset.isHorizontal)
      .add()
      .append(new KeyedCodec<>("PositionsMinY", Codec.DOUBLE, false), (asset, value) -> asset.positionsMinY = value, asset -> asset.positionsMinY)
      .add()
      .append(new KeyedCodec<>("PositionsMaxY", Codec.DOUBLE, false), (asset, value) -> asset.positionsMaxY = value, asset -> asset.positionsMaxY)
      .add()
      .build();
   @Nonnull
   private PositionProviderAsset positionProviderAsset = ListPositionProviderAsset.INSTANCE;
   @Nonnull
   private CurveAsset pinchCurveAsset = new ConstantCurveAsset();
   private double maxDistance = 0.0;
   private boolean normalizeDistance = true;
   private boolean isHorizontal = true;
   private double positionsMinY = 0.0;
   private double positionsMaxY = 1.0E-6;

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantValueDensity(0.0);
      } else {
         return this.isHorizontal
            ? new PositionsHorizontalPinchDensity(
               this.buildFirstInput(argument),
               this.positionProviderAsset.build(new PositionProviderAsset.Argument(argument.parentSeed, argument.referenceBundle, argument.workerId)),
               this.pinchCurveAsset.build(),
               this.maxDistance,
               this.normalizeDistance,
               this.positionsMinY,
               this.positionsMaxY
            )
            : new PositionsPinchDensity(
               this.buildFirstInput(argument),
               this.positionProviderAsset.build(new PositionProviderAsset.Argument(argument.parentSeed, argument.referenceBundle, argument.workerId)),
               this.pinchCurveAsset.build(),
               this.maxDistance,
               this.normalizeDistance
            );
      }
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
      this.positionProviderAsset.cleanUp();
      this.pinchCurveAsset.cleanUp();
   }
}
