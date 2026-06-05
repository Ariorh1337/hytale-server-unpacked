package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.ConstantCurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.CurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.CylinderDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.RotatorDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class CylinderDensityAsset extends DensityAsset {
   @Nonnull
   public static final BuilderCodec<CylinderDensityAsset> CODEC = BuilderCodec.builder(
         CylinderDensityAsset.class, CylinderDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("RadialCurve", CurveAsset.CODEC, true), (asset, value) -> asset.radialCurveAsset = value, asset -> asset.radialCurveAsset)
      .add()
      .append(new KeyedCodec<>("AxialCurve", CurveAsset.CODEC, true), (asset, value) -> asset.axialCurveAsset = value, asset -> asset.axialCurveAsset)
      .add()
      .append(new KeyedCodec<>("NewYAxis", Vector3dUtil.CODEC, false), (asset, value) -> {
         if (value.length() != 0.0) {
            asset.newYAxis = value;
         }
      }, asset -> asset.newYAxis)
      .add()
      .append(new KeyedCodec<>("Spin", Codec.DOUBLE, false), (asset, value) -> asset.spinAngle = value, asset -> asset.spinAngle)
      .add()
      .build();
   private CurveAsset radialCurveAsset = new ConstantCurveAsset();
   private CurveAsset axialCurveAsset = new ConstantCurveAsset();
   @Nonnull
   private Vector3d newYAxis = new Vector3d(0.0, 1.0, 0.0);
   private double spinAngle;

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      if (!this.isSkipped() && this.axialCurveAsset != null && this.radialCurveAsset != null) {
         CylinderDensity cylinder = new CylinderDensity(this.radialCurveAsset.build(), this.axialCurveAsset.build());
         return new RotatorDensity(cylinder, this.newYAxis, this.spinAngle);
      } else {
         return new ConstantValueDensity(0.0);
      }
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
      this.radialCurveAsset.cleanUp();
      this.axialCurveAsset.cleanUp();
   }
}
