package com.hypixel.hytale.builtin.hytalegenerator.assets.curves;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import javax.annotation.Nonnull;

public class FloorCurveAsset extends CurveAsset {
   @Nonnull
   public static final BuilderCodec<FloorCurveAsset> CODEC = BuilderCodec.builder(FloorCurveAsset.class, FloorCurveAsset::new, CurveAsset.ABSTRACT_CODEC)
      .append(new KeyedCodec<>("Curve", CurveAsset.CODEC, true), (asset, value) -> asset.curveAsset = value, asset -> asset.curveAsset)
      .add()
      .append(new KeyedCodec<>("Floor", Codec.DOUBLE, true), (asset, value) -> asset.limit = value, asset -> asset.limit)
      .add()
      .build();
   private CurveAsset curveAsset = new ConstantCurveAsset();
   private double limit;

   @Nonnull
   @Override
   public Double2DoubleFunction build() {
      if (this.curveAsset == null) {
         return in -> 0.0;
      }

      Double2DoubleFunction curve = this.curveAsset.build();
      return in -> Math.max(this.limit, curve.applyAsDouble(in));
   }

   @Override
   public void cleanUp() {
      this.curveAsset.cleanUp();
   }
}
